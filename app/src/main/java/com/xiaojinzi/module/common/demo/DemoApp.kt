package com.xiaojinzi.module.common.demo

import android.app.Application
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.support.init.AppInstance

class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppInstance.app = this

        Component.init(
            isDebug = BuildConfig.DEBUG,
            config = Config
                .with(this)
                .optimizeInit(true)
                .autoRegisterModule(true)
                .build()
        )

    }

}