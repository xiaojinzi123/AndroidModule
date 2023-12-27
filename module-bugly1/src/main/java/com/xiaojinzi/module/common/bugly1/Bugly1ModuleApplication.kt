package com.xiaojinzi.module.common.bugly1

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged

@ModuleAppAnno
class Bugly1ModuleApplication: IApplicationLifecycle, IModuleNotifyChanged {

    override
    fun onCreate(application: Application) {
    }

    override
    fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {
    }

}