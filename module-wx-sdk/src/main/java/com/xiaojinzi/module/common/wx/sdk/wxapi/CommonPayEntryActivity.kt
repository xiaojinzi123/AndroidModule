package com.xiaojinzi.module.common.wx.sdk.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.xiaojinzi.module.common.wx.sdk.WXApi
import com.xiaojinzi.support.ktx.EventPublisher

open class CommonPayEntryActivity : Activity(), IWXAPIEventHandler {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(
            TextView(this).apply {
                this.text = "哈哈哈哈哈"
            }
        )*/
        try {
            val intent = intent
            WXApi.api.handleIntent(intent, this)
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        try {
            WXApi.api.handleIntent(intent, this)
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onReq(baseReq: BaseReq?) {
        // empty
    }

    override fun onResp(baseResp: BaseResp?) {
        when (baseResp?.type) {
            ConstantsAPI.COMMAND_PAY_BY_WX -> {
                when (baseResp.errCode) {
                    0 -> {
                        // 支付成功
                        EventPublisher.eventObservable.tryEmit(
                            value = "wx支付成功"
                        )
                    }
                    else -> {
                        // 支付失败
                        EventPublisher.eventObservable.tryEmit(
                            value = "wx支付失败"
                        )
                    }
                }
            }
            else -> {
                // 支付失败
                EventPublisher.eventObservable.tryEmit(
                    value = "wx支付失败"
                )
            }
        }
        finish()
    }
}