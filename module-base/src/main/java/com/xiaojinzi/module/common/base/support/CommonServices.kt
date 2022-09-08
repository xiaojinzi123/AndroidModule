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

}