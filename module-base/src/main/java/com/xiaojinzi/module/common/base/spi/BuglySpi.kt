package com.xiaojinzi.module.common.base.spi

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
     * 没有异常
     */
    fun tryPostCatchException(e: Throwable) {
        try {
            postCatchException(e)
        } catch (ignore: Throwable) {
            // ignore
        }
    }

    /**
     * 设置渠道信息
     */
    fun setChannel(channel: String)

    /**
     * 设置 userId
     */
    fun setUserId(userId: String)

}