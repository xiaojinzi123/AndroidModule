package com.xiaojinzi.module.common.wx.sdk

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged

/**
 * 使用的项目地址：https://github.com/microshow/RxFFmpeg
 */
@ModuleAppAnno
class WxSdkModuleApplication: IApplicationLifecycle, IModuleNotifyChanged {

    override
    fun onCreate(application: Application) {
    }

    override
    fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {
    }

}