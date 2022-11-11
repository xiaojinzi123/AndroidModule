package com.xiaojinzi.module.common.base

import android.annotation.SuppressLint
import android.provider.Settings

// ------------------------------------ 公共的 ------------------------------------

object CommonRouterConfig {

    const val PLACEHOLDER = "TODO/TODO"

    private const val HOST_SUPPORT = "support"
    const val SUPPORT_IMAGE_CROP = "$HOST_SUPPORT/imageCrop"

    private const val HOST_SYSTEM = "system"
    const val SYSTEM_APP_DETAIL = "$HOST_SYSTEM/appDetail"
    const val SYSTEM_APP_MARKET = "$HOST_SYSTEM/appMarket"
    const val SYSTEM_ACTION_ACCESSIBILITY_SETTINGS =
        "$HOST_SYSTEM/${Settings.ACTION_ACCESSIBILITY_SETTINGS}"

    @SuppressLint("InlinedApi")
    const val SYSTEM_ACTION_MANAGE_OVERLAY_PERMISSION =
        "$HOST_SYSTEM/${Settings.ACTION_MANAGE_OVERLAY_PERMISSION}"

    const val SYSTEM_IMAGE_PICKER = "$HOST_SYSTEM/imagePicker"
    const val SYSTEM_VIDEO_PICKER = "$HOST_SYSTEM/videoPicker"

}
