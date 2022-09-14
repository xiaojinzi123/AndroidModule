package com.xiaojinzi.module.common.base.support

import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.common.base.spi.*

/**
 * 一些通用模块的服务
 */
object CommonServices {

    /**
     * SharedPreference 的服务
     */
    val appInfoAdapterSpi: CommonAppInfoAdapterSpi?
        get() = ServiceManager.get(CommonAppInfoAdapterSpi::class.java)

    /**
     * SharedPreference 的服务
     */
    val spService: SPSpi
        get() = ServiceManager.requiredGet(SPSpi::class.java)

    /**
     * FFmpeg 的服务
     */
    val ffmpegSpi: FFmpegSpi?
        get() = ServiceManager.get(FFmpegSpi::class.java)

    /**
     * Bugly 的服务
     */
    val buglySpi: BuglySpi?
        get() = ServiceManager.get(BuglySpi::class.java)

    /**
     * 微信 SDK 的服务
     */
    val wxSpi: WxSdkSpi?
        get() = ServiceManager.get(WxSdkSpi::class.java)

    /**
     * 微信登录 的服务
     */
    val wxLoginSpi: WXLoginSpi?
        get() = ServiceManager.get(WXLoginSpi::class.java)

    /**
     * 微信支付 的服务
     */
    val wxPaySpi: WXPaySpi?
        get() = ServiceManager.get(WXPaySpi::class.java)

    /**
     * 支付宝支付 的服务
     */
    val alipaySpi: AlipaySpi?
        get() = ServiceManager.get(AlipaySpi::class.java)

    /**
     * 腾讯 Cos 对象存储 的服务
     */
    val txCosSpi: TxCosSpi?
        get() = ServiceManager.get(TxCosSpi::class.java)

    /**
     * 腾讯 Cos 对象存储的 凭证信息获取 的服务
     */
    val txCosCredentialGetService: TxCosCredentialGetService?
        get() = ServiceManager.get(TxCosCredentialGetService::class.java)

}