package com.xiaojinzi.module.common.base

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

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

    @RequiresApi(Build.VERSION_CODES.M)
    const val SYSTEM_ACTION_MANAGE_OVERLAY_PERMISSION =
        "$HOST_SYSTEM/${Settings.ACTION_MANAGE_OVERLAY_PERMISSION}"

    @RequiresApi(Build.VERSION_CODES.O)
    const val SYSTEM_ACTION_APP_NOTIFICATION_PERMISSION =
        "$HOST_SYSTEM/${Settings.ACTION_APP_NOTIFICATION_SETTINGS}"

    const val SYSTEM_IMAGE_PICKER = "$HOST_SYSTEM/imagePicker"
    const val SYSTEM_VIDEO_PICKER = "$HOST_SYSTEM/videoPicker"
    const val SYSTEM_TAKE_PHONE = "$HOST_SYSTEM/takePhone"

}
