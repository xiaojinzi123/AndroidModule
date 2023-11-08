package com.xiaojinzi.module.common.base.spi

import androidx.annotation.Keep


interface TxCosSpi: FileUploadSpi {

    companion object {

        const val TAG = "TxCosSpi"

        @Keep
        data class FileUploadTaskExtendDtoImpl(
            val subPath: String,
        ) : FileUploadTaskExtendDto()

    }

    /**
     * 初始化
     */
    suspend fun init(
        region: String,
        bucket: String,
        defaultSubPath: String,
    )

}