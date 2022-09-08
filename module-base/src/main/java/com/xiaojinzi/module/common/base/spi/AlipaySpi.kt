package com.xiaojinzi.module.common.base.spi

import android.content.Context
import androidx.annotation.UiContext

interface AlipaySpi {

    companion object {
        const val TAG = "AlipayService"
    }

    /**
     * 支付
     * @param orderInfo 订单信息
     */
    suspend fun pay(@UiContext context: Context, orderInfo: String)

}