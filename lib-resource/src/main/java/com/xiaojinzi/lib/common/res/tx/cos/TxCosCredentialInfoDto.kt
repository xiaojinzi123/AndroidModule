package com.xiaojinzi.lib.common.res.tx.cos

import androidx.annotation.Keep
import com.xiaojinzi.support.annotation.ModelDto

@Keep
@ModelDto
data class TxCosCredentialInfoDto(
    val secretId: String,
    val secretKey: String,
    val sessionToken: String,
    val startTime: Long,
    val expiredTime: Long,
)