package com.xiaojinzi.module.common.base.spi

import com.xiaojinzi.lib.common.res.share.PlatformShareInfoDto

/**
 * 分享的 Service
 */
interface ShareSpi {

    companion object {
        const val TAG = "ShareService"
    }

    /**
     * 分享
     */
    suspend fun share(shareInfo: PlatformShareInfoDto)

}