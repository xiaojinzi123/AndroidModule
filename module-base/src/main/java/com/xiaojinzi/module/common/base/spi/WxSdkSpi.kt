package com.xiaojinzi.module.common.base.spi

interface WxSdkSpi {

    /**
     * 初始化
     */
    suspend fun init(appId: String)

}