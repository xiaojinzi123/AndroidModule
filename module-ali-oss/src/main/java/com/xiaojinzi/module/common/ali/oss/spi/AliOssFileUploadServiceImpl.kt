package com.xiaojinzi.module.common.ali.oss.spi

import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.AliOssSpi
import com.xiaojinzi.module.common.base.spi.FileUploadServiceBaseImpl
import com.xiaojinzi.module.common.base.spi.FileUploadTaskDto
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.extension
import com.xiaojinzi.support.ktx.newUUid
import java.util.concurrent.ConcurrentHashMap

@ServiceAnno(
    value = [AliOssSpi::class]
)
class AliOssFileUploadServiceImpl : FileUploadServiceBaseImpl(), AliOssSpi {

    private val taskMap: ConcurrentHashMap<String, OSSAsyncTask<PutObjectResult>> =
        ConcurrentHashMap()

    private var endpoint: String = ""
    private var bucket: String = ""

    override suspend fun init(endpoint: String, bucket: String) {
        this.endpoint = endpoint
        this.bucket = bucket
    }

    override fun doUpload(task: FileUploadTaskDto) {

        val fileKey = "${
            if (task.extendPrefix.isEmpty()) {
                ""
            } else {
                "${task.extendPrefix}_"
            }
        }${newUUid()}.${task.targetFile.name.extension}"

        val request = PutObjectRequest(
            bucket,
            fileKey,
            task.targetFile.path,
        ).apply {
            this.setProgressCallback { _, currentSize, totalSize ->
                postProgress(
                    uid = task.uuid,
                    progress = if (totalSize <= 0) {
                        0f
                    } else {
                        (currentSize.toFloat() / totalSize)
                    },
                )
            }
        }

        val credentialProvider: OSSCredentialProvider = object : OSSFederationCredentialProvider() {
            override fun getFederationToken(): OSSFederationToken {
                // 从您的服务器中获取Token。
                return OSSFederationToken(
                    "STS.NTGjwL7wHcGsy82MMq2Mb6wfL",
                    "CtAnW5airamRuSZErz4aa96ByXZTcE7Jf4YupSARF4Sk",
                    "CAISvgJ1q6Ft5B2yfSjIr5fyIc342qhp1IWYex6DqU0kPsJO2bLNrjz2IHhMeXdsAOkbsvwylWBZ6P8TlqBiStpGSAnNddMoPX+/WID4MeT7oMWQweEuqv/MQBq+aXPS2MvVfJ+KLrf0ceusbFbpjzJ6xaCAGxypQ12iN+/i6/clFKN1ODO1dj1bHtxbCxJ/ocsBTxvrOO2qLwThjxi7biMqmHIl2TovtP3mn5bNskWA1QWr8IJP+dSteKrDRtJ3IZJyX+2y2OFLbafb2EZSkUMSrPoo0PUdpGuf44DNUgcIsg/nIuuR8dAqJRdhdk2qynT/eQ2Xf5RazQ+z/+yfo34lVWrlhiB+0j9O6n9lCTHY18e4SMdq9k63pvSlHLebnf1KL/z1Gi1kFXQPhLEOJ35IaHXuToXDnvSiUkZM3vbtuMkagAEwIZysmx9gozZHC6l9JzyCN3vvRAXoQh0g5jSjRoFM3bdlv25v/f4Hh0YIEbQK946M3ruNKTTiuJnW+SdfnroLQgT19gzkX1yBDGyLGYzajBUisF3oizw4QmvTcljW14Pk8Fvzs6UuGYDs7bU8y8iidYCJjdv2nIhlSKBlQ8xJ/yAA",
                    "2024-04-07T10:34:49Z"
                )
            }
        }
        val ossClient = OSSClient(app, endpoint, credentialProvider)

        ossClient.asyncPutObject(
            request, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest, result: PutObjectResult) {
                    request.progressCallback = null
                }

                override fun onFailure(
                    request: PutObjectRequest,
                    clientException: ClientException?,
                    serviceException: ServiceException?
                ) {
                    request.progressCallback = null
                }
            }
        ).apply {
            taskMap[task.uuid] = this
        }

    }

    override suspend fun cancelTasks(uidList: List<String>) {
        uidList.forEach { uid ->
            postCancel(uid = uid)
            taskMap[uid]?.cancel()
        }
    }
}