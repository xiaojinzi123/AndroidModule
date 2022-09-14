package com.xiaojinzi.module.common.tx.cos.spi

import com.tencent.cos.xml.CosXmlService
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.transfer.COSXMLUploadTask
import com.tencent.cos.xml.transfer.COSXMLUploadTask.COSXMLUploadTaskResult
import com.tencent.cos.xml.transfer.TransferConfig
import com.tencent.cos.xml.transfer.TransferManager
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.FileUploadServiceBaseImpl
import com.xiaojinzi.module.common.base.spi.FileUploadTaskDto
import com.xiaojinzi.module.common.base.spi.TxCosSpi
import com.xiaojinzi.module.common.tx.cos.MySessionCredentialProvider
import com.xiaojinzi.support.ktx.*
import com.xiaojinzi.support.util.LogSupport
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.ConcurrentHashMap

@ServiceAnno(
    value = [TxCosSpi::class]
)
class TxCosFileUploadServiceImpl : FileUploadServiceBaseImpl(), TxCosSpi {

    private val TAG = "TxCosFileUploadServiceImpl"

    // 存储桶所在地域简称，例如广州地区是 ap-guangzhou
    private var region: String? = null
    private var bucket: String? = null

    // 创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
    private var serviceConfig: CosXmlServiceConfig? = null

    // 初始化 COS Service，获取实例
    private var cosXmlService: CosXmlService? = null

    private val transferConfig = TransferConfig.Builder().build()

    // 初始化 TransferManager
    private var transferManager: TransferManager? = null

    private val taskMap: ConcurrentHashMap<String, COSXMLUploadTask> = ConcurrentHashMap()

    override suspend fun init(region: String, bucket: String) {
        this.region = region
        this.bucket = bucket
        serviceConfig = CosXmlServiceConfig.Builder()
            .setRegion(region)
            // 使用 HTTPS 请求, 默认为 HTTP 请求
            .isHttps(true)
            .builder()
        cosXmlService = CosXmlService(
            app,
            serviceConfig,
            MySessionCredentialProvider(),
        )
        transferManager = TransferManager(
            cosXmlService,
            transferConfig
        )
    }

    override fun doUpload(task: FileUploadTaskDto) {

        // 存储桶名称，由 bucketname-appid 组成，appid必须填入，可以在COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket

        if (region == null || bucket == null || serviceConfig == null || cosXmlService == null || transferManager == null) {
            notSupportError()
        }

        val cosPath = "app/${newUUid()}.${task.targetFile.name.extension}" // 对象在存储桶中的位置标识符，即称对象键

        val srcPath: String = task.targetFile.path

        // 若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
        val uploadId: String? = null

        // 上传文件
        val cosXmlUploadTask = transferManager!!.upload(
            bucket, cosPath,
            srcPath, uploadId
        ).apply {
            taskMap[task.uuid] = this
        }

        //设置上传进度回调
        cosXmlUploadTask.setCosXmlProgressListener { complete, target ->
            postProgress(
                uuid = task.uuid,
                progress = if (target <= 0) {
                    0f
                } else {
                    (complete.toFloat() / target)
                },
            )
        }

        // 设置返回结果回调
        cosXmlUploadTask.setCosXmlResultListener(object : CosXmlResultListener {

            override fun onSuccess(request: CosXmlRequest, result: CosXmlResult) {
                val uploadResult = result as COSXMLUploadTaskResult
                LogSupport.d(
                    tag = TAG,
                    content = "上传成功: ${uploadResult.accessUrl}"
                )
                postComplete(
                    uid = task.uuid,
                    url = uploadResult.accessUrl,
                )
            }

            // 如果您使用 kotlin 语言来调用，请注意回调方法中的异常是可空的，否则不会回调 onFail 方法，即：
            // clientException 的类型为 CosXmlClientException?，serviceException 的类型为 CosXmlServiceException?
            override fun onFail(
                request: CosXmlRequest,
                clientException: CosXmlClientException?,
                serviceException: CosXmlServiceException?,
            ) {
                /*if (clientException != null) {
                    clientException.printStackTrace()
                } else {
                    serviceException!!.printStackTrace()
                }*/
                postFail(
                    uid = task.uuid,
                    error = (clientException ?: serviceException
                    ?: Exception("unknown error")).apply {
                        LogSupport.d(
                            tag = TAG,
                            content = "上传失败: ${this.message}"
                        )
                    },
                )
            }

        })
        // 设置任务状态回调, 可以查看任务过程
        cosXmlUploadTask.setTransferStateListener { state ->
            // todo notify transfer state
        }

    }

    override suspend fun cancelTasks(uidList: List<String>) {
        uidList.forEach { uid ->
            postCancel(uid = uid)
            taskMap[uid]?.cancel()
        }
    }

    init {

        completeObservableDto
            .onEach {
                LogSupport.d(
                    tag = TxCosSpi.TAG,
                    content = "$TAG init subscribe completeObservableDTO, value: $it"
                )
                taskMap.remove(it.task.uuid)
            }
            .launchIn(scope = AppScope)

        failObservableDto
            .onEach {
                LogSupport.d(
                    tag = TxCosSpi.TAG,
                    content = "$TAG init subscribe failObservableDTO, value: $it"
                )
                taskMap.remove(it.task.uuid)
            }
            .launchIn(scope = AppScope)

        cancelObservableDto
            .onEach { task ->
                LogSupport.d(
                    tag = TxCosSpi.TAG,
                    content = "$TAG init subscribe cancelObservableDTO, value: $task"
                )
                taskMap.remove(task.uuid)?.cancel()
            }
            .launchIn(scope = AppScope)

    }

}