package com.xiaojinzi.lib.common.res.wx.sdk

import androidx.annotation.Keep

@Keep
sealed class WXLoginDto

@Keep
data class WXLoginSuccessDto(
    // 微信返回临时使用的 code
    val code: String,
): WXLoginDto()

@Keep
data class WXLoginErrorDto(
    val error: Exception = Exception(),
): WXLoginDto()
