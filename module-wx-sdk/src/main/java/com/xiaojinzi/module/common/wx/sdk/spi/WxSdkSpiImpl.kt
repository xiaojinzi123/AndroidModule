package com.xiaojinzi.module.common.wx.sdk.spi

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.WxSdkSpi
import com.xiaojinzi.module.common.wx.sdk.WXApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ServiceAnno(WxSdkSpi::class)
class WxSdkSpiImpl : WxSdkSpi {

    override suspend fun init(appId: String) {
        WXApi.appId = appId
    }

    override suspend fun openWx(): Boolean {
        return withContext(context = Dispatchers.Main) {
            try {
                WXApi.api.openWXApp()
            } catch (e: Exception) {
                false
            }
        }
    }

}