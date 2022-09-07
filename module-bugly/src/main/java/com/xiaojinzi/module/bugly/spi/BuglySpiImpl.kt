package com.xiaojinzi.module.bugly.spi

import com.tencent.bugly.crashreport.CrashReport
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.spi.BuglySpi
import com.xiaojinzi.support.ktx.app

@ServiceAnno(BuglySpi::class)
class BuglySpiImpl : BuglySpi {

    override suspend fun init(appId: String, isDebugMode: Boolean) {
        CrashReport.initCrashReport(app, appId, isDebugMode)
    }

    override suspend fun testJavaCrash() {
        CrashReport.testJavaCrash()
    }

    override fun postCatchException(e: Throwable) {
        CrashReport.postCatchedException(e)
    }

    override fun setChannel(channel: String) {
        CrashReport.setAppChannel(app, channel)
    }

    override fun setUserId(channel: String) {
        CrashReport.setUserId(app, channel)
    }

}