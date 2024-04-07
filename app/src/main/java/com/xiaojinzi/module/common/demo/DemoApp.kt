package com.xiaojinzi.module.common.demo

import android.app.Application
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.component.impl.service.serviceRequired
import com.xiaojinzi.module.common.base.spi.SPSpi
import com.xiaojinzi.module.common.base.support.CommonServices
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.ktx.LogSupport
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicInteger

class DemoApp : Application() {

    private val flow1 = MutableSharedFlow<Int>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val counter = AtomicInteger()

    override fun onCreate() {
        super.onCreate()
        AppInstance.app = this

        LogSupport.logAble = true

        Component.init(
            application = this,
            isDebug = true,
            config = Config.Builder()
                .optimizeInit(true)
                .autoRegisterModule(true)
                .build()
        )

        SPSpi::class.serviceRequired()

        CommonServices
            .bugly1Spi
            ?.init(
                app = this,
                isDebugMode = true,
                appId = "d602a40137",
                appKey = "4a76894a-b292-44e3-b5c8-86c81249548f",
            )

    }

}