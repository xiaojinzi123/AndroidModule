package com.xiaojinzi.module.common.base.support

import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.common.base.spi.AliOssCredentialGetService
import com.xiaojinzi.module.common.base.spi.AliOssSpi
import com.xiaojinzi.module.common.base.spi.AlipaySpi
import com.xiaojinzi.module.common.base.spi.Bugly1Spi
import com.xiaojinzi.module.common.base.spi.BuglySpi
import com.xiaojinzi.module.common.base.spi.CommonAppInfoAdapterSpi
import com.xiaojinzi.module.common.base.spi.FFmpegSpi
import com.xiaojinzi.module.common.base.spi.NetworkSpi
import com.xiaojinzi.module.common.base.spi.SPSpi
import com.xiaojinzi.module.common.base.spi.TxCosCredentialGetService
import com.xiaojinzi.module.common.base.spi.TxCosSpi
import com.xiaojinzi.module.common.base.spi.WXLoginSpi
import com.xiaojinzi.module.common.base.spi.WXPaySpi
import com.xiaojinzi.module.common.base.spi.WXSdkSpi

/**
 * 一些通用模块的服务
 */
object CommonServices {

    /**
     * SharedPreference 的服务
     */
    val appInfoAdapterSpi: CommonAppInfoAdapterSpi?
        get() = ServiceManager.get(CommonAppInfoAdapterSpi::class)

    /**
     * 网络的服务
     */
    val networkSpi: NetworkSpi?
        get() = ServiceManager.get(NetworkSpi::class)

    /**
     * SharedPreference 的服务
     */
    val spService: SPSpi?
        get() = ServiceManager.get(SPSpi::class)

    /**
     * FFmpeg 的服务
     */
    val ffmpegSpi: FFmpegSpi?
        get() = ServiceManager.get(FFmpegSpi::class)

    /**
     * Bugly 的服务
     */
    val buglySpi: BuglySpi?
        get() = ServiceManager.get(BuglySpi::class)

    /**
     * Bugly 的服务
     */
    val bugly1Spi: Bugly1Spi?
        get() = ServiceManager.get(Bugly1Spi::class)

    /**
     * 微信 SDK 的服务
     */
    val wxSpi: WXSdkSpi?
        get() = ServiceManager.get(WXSdkSpi::class)

    /**
     * 微信登录 的服务
     */
    val wxLoginSpi: WXLoginSpi?
        get() = ServiceManager.get(WXLoginSpi::class)

    /**
     * 微信支付 的服务
     */
    val wxPaySpi: WXPaySpi?
        get() = ServiceManager.get(WXPaySpi::class)

    /**
     * 支付宝支付 的服务
     */
    val alipaySpi: AlipaySpi?
        get() = ServiceManager.get(AlipaySpi::class)

    /**
     * 腾讯 Cos 对象存储 的服务
     */
    val txCosSpi: TxCosSpi?
        get() = ServiceManager.get(TxCosSpi::class)

    /**
     * 腾讯 Cos 对象存储的 凭证信息获取 的服务
     */
    val txCosCredentialGetService: TxCosCredentialGetService?
        get() = ServiceManager.get(TxCosCredentialGetService::class)

    /**
     * 阿里 oss 对象存储 的服务
     */
    val aliOssSpi: AliOssSpi?
        get() = ServiceManager.get(AliOssSpi::class)

    /**
     * 阿里 oss 对象存储的 凭证信息获取 的服务
     */
    val aliOssCredentialGetService: AliOssCredentialGetService?
        get() = ServiceManager.get(AliOssCredentialGetService::class)

}