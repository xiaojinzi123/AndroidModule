package com.xiaojinzi.module.common.ali.pay.spi

import android.content.Context
import com.alipay.sdk.app.PayTask
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.common.base.spi.AlipaySpi
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.support.ktx.notSupportError
import com.xiaojinzi.support.util.LogSupport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ServiceAnno(AlipaySpi::class)
class AlipayServiceImpl : AlipaySpi {

    override suspend fun pay(context: Context, orderInfo: String) {

        withContext(context = Dispatchers.IO) {
            val act = context.getActivity() ?: notSupportError()
            val alipay = PayTask(act)
            val resultMap = alipay.payV2(orderInfo, true)
            LogSupport.d(
                tag = AlipaySpi.TAG,
                content = "resultMap = $resultMap",
            )
            val isSuccess = resultMap["resultStatus"] == "9000"
            if (!isSuccess) {
                notSupportError()
            }
        }

    }

}