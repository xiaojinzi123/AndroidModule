package com.xiaojinzi.module.common.base.spi

import com.xiaojinzi.lib.common.res.tx.cos.AliOssCredentialInfoDto

interface AliOssCredentialGetService {

    /**
     * 获取临时凭证
     */
    suspend fun getInfo(): AliOssCredentialInfoDto

}