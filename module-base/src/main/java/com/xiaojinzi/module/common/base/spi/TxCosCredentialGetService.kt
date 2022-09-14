package com.xiaojinzi.module.common.base.spi

import com.xiaojinzi.lib.common.res.tx.cos.TxCosCredentialInfoDto

interface TxCosCredentialGetService {

    /**
     * 获取临时凭证
     */
    suspend fun getInfo(): TxCosCredentialInfoDto

}