package com.xiaojinzi.module.common.bugly1.spi

import android.app.Application
import com.tencent.bugly.library.Bugly
import com.tencent.bugly.library.BuglyAppVersionMode
import com.tencent.bugly.library.BuglyBuilder
import com.tencent.bugly.library.BuglyLogLevel
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.Bugly1Spi

@ServiceAnno(Bugly1Spi::class)
class Bugly1SpiImpl : Bugly1Spi {

    override suspend fun init(
        app: Application,
        isDebugMode: Boolean,
        appId: String,
        appKey: String,
        userId: String?,
        appChannel: String?,
        buildNumber: String?,
    ) {
        Bugly.init(
            app,
            BuglyBuilder(appId, appKey).apply {
                this.debugMode = isDebugMode
                this.logLevel = BuglyLogLevel.LEVEL_OFF
                this.userId = userId
                this.appChannel = appChannel
                this.buildNumber = buildNumber
                this.appVersionType = if (isDebugMode) {
                    BuglyAppVersionMode.DEBUG
                } else {
                    BuglyAppVersionMode.RELEASE
                }
            }
        )
    }

    override suspend fun testJavaCrash() {
        Bugly.testCrash(Bugly.JAVA_CRASH)
    }

    override fun postCatchException(
        e: Throwable,
        extraMsg: String?,
        extraData: ByteArray?,
        enableAllThreadStack: Boolean
    ) {
        Bugly.handleCatchException(
            Thread.currentThread(), e, extraMsg, extraData, true,
        )
    }


}