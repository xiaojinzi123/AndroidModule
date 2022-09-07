package com.xiaojinzi.module.base.support

import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.base.spi.BuglySpi
import com.xiaojinzi.module.base.spi.FFmpegSpi
import com.xiaojinzi.module.base.spi.SPSpi

/**
 * 一些通用模块的服务
 */
object CommonServices {

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

}