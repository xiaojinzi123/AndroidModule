package com.xiaojinzi.module.common.base.spi

import android.app.Application
import android.content.Context

/**
 * 接入文档：
 * https://bugly.tds.qq.com/docs/sdk/android
 */
interface Bugly1Spi {

    /**
     * 初始化
     */
    fun init(
        app: Application,
        isDebugMode: Boolean = false,
        appId: String,
        appKey: String,
        userId: String? = null,
        appChannel: String? = null,
        buildNumber: String? = null,
    )

    /**
     * 设置自定义数据
     */
    fun putUserData(
        context: Context,
        key: String,
        value: String,
    )

    /**
     * 测试崩溃
     */
    fun testJavaCrash()

    /**
     * 测试崩溃
     */
    fun testAnrCrash()

    /**
     * 测试崩溃
     */
    fun testNativeCrash()

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