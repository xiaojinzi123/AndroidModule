package com.xiaojinzi.module.common.base.spi

import android.app.Application

interface Bugly1Spi {

    /**
     * 初始化
     */
    suspend fun init(
        app: Application,
        isDebugMode: Boolean = false,
        appId: String,
        appKey: String,
        userId: String? = null,
        appChannel: String? = null,
        buildNumber: String? = null,
    )

    /**
     * 测试崩溃
     */
    suspend fun testJavaCrash()

    /**
     * post 抓住了的异常
     */
    fun postCatchException(
        e: Throwable,
        extraMsg: String? = null,
        extraData: ByteArray? = null,
        enableAllThreadStack: Boolean = true,
    )

}