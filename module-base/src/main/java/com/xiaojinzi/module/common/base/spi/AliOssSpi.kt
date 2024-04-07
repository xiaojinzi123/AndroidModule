package com.xiaojinzi.module.common.base.spi


interface AliOssSpi : FileUploadSpi {

    companion object {

        const val TAG = "AliOssSpi"

    }

    /**
     * 初始化
     */
    suspend fun init(
        endpoint: String,
        bucket: String,
    )

}