package com.xiaojinzi.module.common.wx.sdk

import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.xiaojinzi.support.ktx.app

object WXApi {

    // 微信开放平台的 APP_ID
    var appId: String? = null

    val api: IWXAPI by lazy {
        val targetAppId: String = appId ?: throw RuntimeException("请先设置 appId")
        WXAPIFactory.createWXAPI(app, targetAppId, true).apply {
            this.registerApp(targetAppId)
        }
    }


}