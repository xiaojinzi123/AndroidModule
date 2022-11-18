package com.xiaojinzi.module.common.wx.sdk.spi

import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.common.res.wx.sdk.WXLoginDto
import com.xiaojinzi.module.common.base.spi.WXLoginSpi
import com.xiaojinzi.module.common.wx.sdk.WXApi
import com.xiaojinzi.support.ktx.EventPublisher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull

@ServiceAnno(WXLoginSpi::class)
class WXLoginSpiImpl : WXLoginSpi {

    override suspend fun login(): WXLoginDto {
        val req = SendAuth.Req().apply {
            this.scope = "snsapi_userinfo"
            this.state = "wechat_sdk_demo_test"
        }
        WXApi.api.sendReq(req)
        return EventPublisher.eventObservable
            .mapNotNull { it as? WXLoginDto }
            .first()
    }

}