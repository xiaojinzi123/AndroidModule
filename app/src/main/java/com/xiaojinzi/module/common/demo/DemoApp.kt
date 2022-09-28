package com.xiaojinzi.module.common.demo

import android.app.Application
import com.xiaojinzi.support.init.AppInstance

class DemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AppInstance.app = this
    }

}