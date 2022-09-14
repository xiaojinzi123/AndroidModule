package com.xiaojinzi.module.common.base.spi

interface TxCosSpi: FileUploadSpi {

    companion object {
        const val TAG = "TxCosSpi"
    }

    /**
     * 初始化
     */
    suspend fun initOss(
        region: String,
        bucket: String,
    )

}