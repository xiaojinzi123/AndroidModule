package com.xiaojinzi.module.common.ffmpeg.spi

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.FFmpegSpi
import com.xiaojinzi.support.ktx.resumeExceptionIgnoreException
import com.xiaojinzi.support.ktx.resumeIgnoreException
import com.xiaojinzi.support.ktx.LogSupport
import io.microshow.rxffmpeg.RxFFmpegInvoke
import kotlin.coroutines.suspendCoroutine

@ServiceAnno(FFmpegSpi::class)
class FFmpegSpiImpl : FFmpegSpi {

    override suspend fun executeCommand(command: String) {
        val commands = command.split(" ").toTypedArray()
        suspendCoroutine<Unit> { cot ->
            RxFFmpegInvoke.getInstance()
                .runCommand(commands, object : RxFFmpegInvoke.IFFmpegListener {
                    override fun onFinish() {
                        LogSupport.d(tag = FFmpegSpi.TAG, content = "onFinish")
                        cot.resumeIgnoreException(value = Unit)
                    }

                    override fun onProgress(progress: Int, progressTime: Long) {
                        LogSupport.d(tag = FFmpegSpi.TAG, content = "onProgress")
                    }

                    override fun onCancel() {
                        LogSupport.d(tag = FFmpegSpi.TAG, content = "onCancel")
                    }

                    override fun onError(message: String?) {
                        LogSupport.d(tag = FFmpegSpi.TAG, content = "onError")
                        cot.resumeExceptionIgnoreException(Exception(message))
                    }
                })
        }
    }

}