package com.xiaojinzi.module.ffmpeg

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged
import io.microshow.rxffmpeg.RxFFmpegInvoke

/**
 * 使用的项目地址：https://github.com/microshow/RxFFmpeg
 */
@ModuleAppAnno
class FFmpegModuleApplication: IApplicationLifecycle, IModuleNotifyChanged {

    override
    fun onCreate(application: Application) {
        RxFFmpegInvoke.getInstance().setDebug(true)
    }

    override
    fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {
    }

}