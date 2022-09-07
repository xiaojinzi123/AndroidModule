package com.xiaojinzi.module.base.spi

interface BuglySpi {

    /**
     * 初始化
     */
    suspend fun init(appId: String, isDebugMode: Boolean)

    /**
     * 测试崩溃
     */
    suspend fun testJavaCrash()

    /**
     * post 抓住了的异常
     */
    fun postCatchException(e: Throwable)

    /**
     * 设置渠道信息
     */
    fun setChannel(channel: String)

    /**
     * 设置 userId
     */
    fun setUserId(userId: String)

}