package com.xiaojinzi.module.common.base.spi

import com.xiaojinzi.lib.common.res.wx.sdk.WXLoginDto
import com.xiaojinzi.support.annotation.MaybeCannotReturn
import com.xiaojinzi.support.annotation.NoError

/**
 * 微信登录的 spi
 */
interface WXLoginSpi {

    /**
     * 微信登录
     */
    @NoError
    @MaybeCannotReturn
    suspend fun login(): WXLoginDto

}