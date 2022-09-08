package com.xiaojinzi.module.common.base.spi

import android.content.Context
import androidx.annotation.UiContext

interface WXPaySpi {

    companion object {
        const val TAG = "wxPayService"
    }

    /**
     * 支付
     */
    suspend fun pay(
        @UiContext context: Context,
        appId: String,
        partnerId: String,
        prepayId: String,
        packageValue: String,
        nonceStr: String,
        timeStamp: String,
        sign: String,
    )

}