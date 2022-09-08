package com.xiaojinzi.module.common.wx.sdk.spi

import android.content.Context
import com.tencent.mm.opensdk.modelpay.PayReq
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.WXPaySpi
import com.xiaojinzi.module.common.wx.sdk.WXApi
import com.xiaojinzi.support.ktx.notSupportError
import com.xiaojinzi.support.util.EventPublisher
import com.xiaojinzi.support.util.LogSupport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ServiceAnno(WXPaySpi::class)
class WXPayServiceImpl : WXPaySpi {

    override suspend fun pay(
        context: Context,
        appId: String,
        partnerId: String,
        prepayId: String,
        packageValue: String,
        nonceStr: String,
        timeStamp: String,
        sign: String
    ) {
        withContext(context = Dispatchers.Main) {
            LogSupport.d(
                tag = WXPaySpi.TAG,
                content = "开始微信支付"
            )
            val request = PayReq()
            request.appId = appId
            request.partnerId = partnerId
            request.prepayId = prepayId
            request.packageValue = packageValue
            request.nonceStr = nonceStr
            request.timeStamp = timeStamp
            request.sign = sign
            LogSupport.d(
                tag = WXPaySpi.TAG,
                content = "创建一个 job 准备等待结果"
            )
            val resultJob = async(context = Dispatchers.IO) {
                EventPublisher
                    .eventObservable
                    .map { it as? String }
                    .filter { it == "wx支付成功" || it == "wx支付失败" }
                    .first()
            }
            LogSupport.d(
                tag = WXPaySpi.TAG,
                content = "准备发送微信支付请求"
            )
            WXApi.api.sendReq(request)
            LogSupport.d(
                tag = WXPaySpi.TAG,
                content = "开始等待微信支付结果"
            )
            when (resultJob.await()) {
                "wx支付失败" -> notSupportError()
            }
            LogSupport.d(
                tag = WXPaySpi.TAG,
                content = "微信支付成功"
            )
        }
    }

}