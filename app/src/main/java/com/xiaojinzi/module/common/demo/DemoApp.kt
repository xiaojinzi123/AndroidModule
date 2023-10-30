package com.xiaojinzi.module.common.demo

import android.app.Application
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.component.impl.service.serviceRequired
import com.xiaojinzi.module.common.base.spi.SPSpi
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.LogSupport
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

        flow1
            .onEach {
                delay(2000)
                LogSupport.d(
                    tag = "test",
                    content = "flow1.value = $it",
                )
            }
            .launchIn(scope = AppScope)

        AppScope.launch(context = ErrorIgnoreContext) {

            while (isActive) {

                delay(1000)
                flow1.tryEmit(value = counter.incrementAndGet()).apply {
                    LogSupport.d(
                        tag = "test",
                        content = "flow1.tryEmit = $this",
                    )
                }

            }

        }


    }

}