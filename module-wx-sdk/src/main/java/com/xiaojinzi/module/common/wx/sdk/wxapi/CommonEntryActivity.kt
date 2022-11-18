package com.xiaojinzi.module.common.wx.sdk.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.xiaojinzi.lib.common.res.wx.sdk.WXLoginErrorDto
import com.xiaojinzi.lib.common.res.wx.sdk.WXLoginSuccessDto
import com.xiaojinzi.module.common.wx.sdk.WXApi
import com.xiaojinzi.support.ktx.EventPublisher

open class CommonEntryActivity : Activity(), IWXAPIEventHandler {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            // 授权登录
            ConstantsAPI.COMMAND_SENDAUTH -> {
                val sendAuthResp = baseResp as? SendAuth.Resp
                when (sendAuthResp?.errCode) {
                    0 -> {
                        EventPublisher.eventObservable.tryEmit(
                            value = WXLoginSuccessDto(
                                code = sendAuthResp.code,
                            ),
                        )
                    }
                    else -> {
                        EventPublisher.eventObservable.tryEmit(
                            value = WXLoginErrorDto(),
                        )
                    }
                }
            }
            else -> {
                EventPublisher.eventObservable.tryEmit(
                    value = WXLoginErrorDto(),
                )
            }
        }
        finish()
    }
}