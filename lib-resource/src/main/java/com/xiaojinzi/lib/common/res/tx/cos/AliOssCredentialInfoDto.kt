package com.xiaojinzi.lib.common.res.tx.cos

import androidx.annotation.Keep
import com.xiaojinzi.support.annotation.ModelDto

@Keep
@ModelDto
data class AliOssCredentialInfoDto(
    val securityToken: String,
    val accessKeyId: String,
    val accessKeySecret: String,
    val expiration: String
)