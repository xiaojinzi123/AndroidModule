package com.xiaojinzi.module.common.base.spi

import androidx.annotation.Keep
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import com.xiaojinzi.support.ktx.newUUid
import com.xiaojinzi.support.ktx.resumeExceptionIgnoreException
import com.xiaojinzi.support.ktx.resumeIgnoreException
import com.xiaojinzi.support.ktx.LogSupport
import hu.akarnokd.kotlin.flow.toList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * 上传文件时候, 有些需要额外的信息支持
 */
@Keep
sealed class FileUploadTaskExtendDto

@Keep
data class FileUploadTaskDto(
    val uuid: String = newUUid(),
    val targetFile: File,
    val extend: FileUploadTaskExtendDto? = null,
)

@Keep
data class FileUploadProgressDto(
    val task: FileUploadTaskDto,
    val progress: Float,
)

@Keep
data class FileUploadCompleteDto(
    val task: FileUploadTaskDto,
    val url: String,
)

@Keep
data class FileUploadFailDto(
    val task: FileUploadTaskDto,
    val error: Exception,
)

/**
 * 文件上传的服务发现.
 * 具体的实现类, 最好实现 [FileUploadServiceBaseImpl] 来简化开发
 */
interface FileUploadSpi {

    companion object {
        const val TAG = "FileUploadSpi"
    }

    /**
     * 所有任务的进度
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    val progressObservableDto: Flow<FileUploadProgressDto>

    /**
     * 完成的任务
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    val completeObservableDto: Flow<FileUploadCompleteDto>

    /**
     * 失败的任务
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    val failObservableDto: Flow<FileUploadFailDto>

    /**
     * 取消的任务
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    val cancelObservableDto: Flow<FileUploadTaskDto>

    /**
     * 订阅进度, 特定 ID
     */
    fun subscribeSingleProgress(uid: String): Flow<Float>

    /**
     * 订阅进度, 特定 ID 集合
     */
    fun subscribeCombineProgress(uidList: List<String>): Flow<Float>

    /**
     * 提交一个上传任务, 返回一个 uuid, 用于后续的查询
     */
    fun submitTask(task: FileUploadTaskDto): String

    /**
     * 取消任务
     */
    suspend fun cancelTask(uid: String)

    /**
     * 取消任务
     */
    suspend fun cancelTasks(uidList: List<String>)

    /**
     * 上传单个任务
     */
    suspend fun upload(task: FileUploadTaskDto): FileUploadCompleteDto

    /**
     * 上传多个文件任务
     */
    suspend fun uploadList(tasks: List<FileUploadTaskDto>): List<FileUploadCompleteDto> {
        return withContext(context = Dispatchers.IO) {
            tasks
                .map { task ->
                    async {
                        upload(task = task)
                    }
                }
                .awaitAll()
        }
    }

    /**
     * 简单的上传
     */
    suspend fun simpleUpload(file: File): String {
        return upload(
            FileUploadTaskDto(targetFile = file)
        ).url
    }

    /**
     * 简单的上传
     */
    suspend fun simpleUploadList(
        files: List<File>,
        onProgress: (progress: Float) -> Unit = {},
    ): List<String> {
        val tempScope = MainScope()
        val tasks = files.map {
            FileUploadTaskDto(
                targetFile = it
            )
        }
        subscribeCombineProgress(uidList = tasks.map { it.uuid })
            .onEach {
                onProgress.invoke(it)
            }
            .launchIn(scope = tempScope)
        return try {
            uploadList(tasks = tasks)
                .map { it.url }
        } catch (e: Exception) {
            throw e
        } finally {
            tempScope.cancel()
        }
    }

}

abstract class FileUploadServiceBaseImpl : FileUploadSpi {

    override val progressObservableDto = NormalMutableSharedFlow<FileUploadProgressDto>()

    final override val completeObservableDto = NormalMutableSharedFlow<FileUploadCompleteDto>()

    final override val failObservableDto = NormalMutableSharedFlow<FileUploadFailDto>()

    final override val cancelObservableDto = NormalMutableSharedFlow<FileUploadTaskDto>()

    /**
     * 任务的 map
     */
    private val taskMap: ConcurrentHashMap<String, FileUploadTaskDto> =
        ConcurrentHashMap<String, FileUploadTaskDto>()

    fun postProgress(uid: String, progress: Float) {
        LogSupport.d(
            tag = FileUploadSpi.TAG,
            content = "postProgress: uid = $uid, progress = $progress",
        )
        taskMap[uid]?.let { task ->
            progressObservableDto.tryEmit(value = FileUploadProgressDto(task, progress))
        }
    }

    fun postComplete(uid: String, url: String) {
        LogSupport.d(
            tag = FileUploadSpi.TAG,
            content = "postComplete: uid = $uid, url = $url",
        )
        taskMap.remove(uid)?.let { task ->
            completeObservableDto.tryEmit(
                value = FileUploadCompleteDto(task, url)
            ).apply {
                LogSupport.d(
                    tag = FileUploadSpi.TAG,
                    content = "postComplete: uid = $uid, url = $url, result = $this",
                )
            }
        }
    }

    fun postFail(uid: String, error: Exception) {
        LogSupport.d(
            tag = FileUploadSpi.TAG,
            content = "postFail: uid = $uid, error = ${error.message}",
        )
        taskMap.remove(uid)?.let { task ->
            failObservableDto.tryEmit(value = FileUploadFailDto(task, error)).apply {
                LogSupport.d(
                    tag = FileUploadSpi.TAG,
                    content = "postFail: uid = $uid, error = ${error.message}, result = $this",
                )
            }
        }
    }

    fun postCancel(uid: String) {
        LogSupport.d(
            tag = FileUploadSpi.TAG,
            content = "postCancel: uid = $uid",
        )
        taskMap.remove(uid)?.let { task ->
            cancelObservableDto.tryEmit(value = task).apply {
                LogSupport.d(
                    tag = FileUploadSpi.TAG,
                    content = "postCancel: uid = $uid, result = $this",
                )
            }
        }
    }

    override suspend fun upload(task: FileUploadTaskDto): FileUploadCompleteDto {
        return withContext(context = Dispatchers.IO) {
            suspendCancellableCoroutine { cot ->
                val tempScope = MainScope()
                completeObservableDto
                    .filter {
                        (it.task == task).apply {
                            LogSupport.d(
                                tag = FileUploadSpi.TAG,
                                content = "upload completeObservableDto: task = $task, it.task = ${it.task}, result = $this",
                            )
                        }
                    }
                    .onEach {
                        cot.resumeIgnoreException(value = it)
                        tempScope.cancel()
                    }
                    .flowOn(context = Dispatchers.Default)
                    .launchIn(scope = tempScope)
                failObservableDto
                    .filter {
                        (it.task == task).apply {
                            LogSupport.d(
                                tag = FileUploadSpi.TAG,
                                content = "upload failObservableDto: task = $task, it.task = ${it.task}, result = $this",
                            )
                        }
                    }
                    .onEach {
                        cot.resumeExceptionIgnoreException(
                            exception = it.error,
                        )
                        tempScope.cancel()
                    }
                    .flowOn(context = Dispatchers.Default)
                    .launchIn(scope = tempScope)
                cancelObservableDto
                    .filter {
                        (it == task).apply {
                            LogSupport.d(
                                tag = FileUploadSpi.TAG,
                                content = "upload cancelObservableDto: task = $task, it = $it, result = $this",
                            )
                        }
                    }
                    .onEach {
                        cot.resumeExceptionIgnoreException(
                            exception = Exception("canceled"),
                        )
                        tempScope.cancel()
                    }
                    .flowOn(context = Dispatchers.Default)
                    .launchIn(scope = tempScope)
                // 取消的时候执行
                cot.invokeOnCancellation {
                    tempScope.cancel()
                    postCancel(uid = task.uuid)
                }
                // 提交上传的任务
                prepareDoUpload(task = task)
            }
        }.apply {
            LogSupport.d(
                tag = FileUploadSpi.TAG,
                content = "upload success: task = $task, result = $this",
            )
        }
    }

    override fun subscribeSingleProgress(uid: String): Flow<Float> {
        return progressObservableDto
            .filter {
                it.task.uuid == uid
            }
            .map { it.progress }
            .onStart { emit(0f) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun subscribeCombineProgress(uidList: List<String>): Flow<Float> {
        return uidList
            .map { subscribeSingleProgress(uid = it) }
            .asFlow()
            .toList()
            .flatMapLatest { flowList ->
                combine(flows = flowList) { progressList ->
                    val totalCompleteProgress =
                        progressList.reduceOrNull { acc, progress -> acc + progress } ?: 0f
                    val totalProgress = progressList.size.toFloat()
                    totalCompleteProgress / totalProgress
                }
            }
    }

    /**
     * 执行上传
     */
    abstract fun doUpload(task: FileUploadTaskDto)

    override suspend fun cancelTask(uid: String) {
        cancelTasks(uidList = listOf(element = uid))
    }

    /**
     * 执行上传
     */
    private fun prepareDoUpload(task: FileUploadTaskDto) {
        taskMap[task.uuid] = task
        doUpload(task = task)
    }

    override fun submitTask(task: FileUploadTaskDto): String {
        prepareDoUpload(task = task)
        return task.uuid
    }

    init {

        completeObservableDto
            .onEach {
                LogSupport.d(
                    tag = FileUploadSpi.TAG,
                    content = "${FileUploadSpi.TAG} init subscribe completeObservableDto, value: $it"
                )
            }
            .flowOn(context = Dispatchers.Default)
            .launchIn(scope = AppScope)

        failObservableDto
            .onEach {
                LogSupport.d(
                    tag = FileUploadSpi.TAG,
                    content = "${FileUploadSpi.TAG} init subscribe failObservableDto, value: $it"
                )
            }
            .flowOn(context = Dispatchers.Default)
            .launchIn(scope = AppScope)

        cancelObservableDto
            .onEach {
                LogSupport.d(
                    tag = FileUploadSpi.TAG,
                    content = "${FileUploadSpi.TAG} init subscribe failObservableDto, value: $it"
                )
            }
            .flowOn(context = Dispatchers.Default)
            .launchIn(scope = AppScope)

    }

}