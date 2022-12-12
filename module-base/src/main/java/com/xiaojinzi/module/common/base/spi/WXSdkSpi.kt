package com.xiaojinzi.module.common.base.spi

import com.xiaojinzi.support.annotation.NoError

interface WXSdkSpi {

    /**
     * 初始化
     */
    suspend fun init(appId: String)

    /**
     * 打开微信
     *
     * @return 是否打开成功
     */
    @NoError
    suspend fun openWx(): Boolean

}