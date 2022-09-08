package com.xiaojinzi.module.common.wx.sdk.spi

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.WxSdkSpi
import com.xiaojinzi.module.common.wx.sdk.WXApi

@ServiceAnno(WxSdkSpi::class)
class WxSdkSpiImpl: WxSdkSpi {

    override suspend fun init(appId: String) {

        WXApi.appId = appId

    }

}