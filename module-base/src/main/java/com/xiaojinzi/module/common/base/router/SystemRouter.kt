package com.xiaojinzi.module.common.base.router

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.impl.RouterRequest
import com.xiaojinzi.component.support.ParameterSupport
import com.xiaojinzi.module.common.base.CommonRouterConfig
import com.xiaojinzi.support.ktx.app

/**
 * 系统 App 详情
 *
 * @param request
 * @return
 */
@RouterAnno(hostAndPath = CommonRouterConfig.SYSTEM_APP_DETAIL)
fun appDetail(request: RouterRequest): Intent {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:" + request.rawContext!!.packageName)
    return intent
}

@RouterAnno(
    regex = "^(http|https)(.*)",
    desc = "系统浏览器",
)
fun systemBrowser(request: RouterRequest): Intent {
    return Intent().apply {
        action = Intent.ACTION_VIEW
        data = request.uri
    }
}

@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_APP_MARKET
)
fun toAppMarket(request: RouterRequest): Intent {
    val packageName: String? = ParameterSupport.getString(request.bundle, "packageName")
    val intent = Intent(Intent.ACTION_VIEW)
    if (!packageName.isNullOrEmpty()) {
        intent.setPackage(packageName)
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.parse("market://details?id=" + app.packageName)
    return intent
}

@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_ACTION_ACCESSIBILITY_SETTINGS
)
fun accessibilitySettings(request: RouterRequest): Intent {
    return Intent().apply {
        this.action = Settings.ACTION_ACCESSIBILITY_SETTINGS
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_ACTION_MANAGE_OVERLAY_PERMISSION
)
fun manageOverlayPermission(request: RouterRequest): Intent {
    return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
        // this.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        this.data = Uri.parse("package:" + app.packageName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.putExtra(
                Settings.EXTRA_APP_PACKAGE,
                app.packageName,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_ACTION_APP_NOTIFICATION_PERMISSION
)
fun appNotificationPermission(request: RouterRequest): Intent {
    return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        this.putExtra(
            Settings.EXTRA_APP_PACKAGE,
            app.packageName,
        )
    }
}

@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_IMAGE_PICKER
)
fun imagePicker(request: RouterRequest): Intent {
    val isSelectMultiple: Boolean =
        ParameterSupport.getBoolean(request.bundle, "isSelectMultiple") ?: false
    return Intent.createChooser(
        Intent().apply {
            this.type = "image/*"
            this.action = Intent.ACTION_GET_CONTENT
            if (isSelectMultiple) {
                this.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        },
        "Select Image"
    )
}

@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_VIDEO_PICKER
)
fun videoPicker(request: RouterRequest): Intent {
    val isSelectMultiple: Boolean =
        ParameterSupport.getBoolean(request.bundle, "isSelectMultiple") ?: false
    return Intent.createChooser(
        Intent().apply {
            this.type = "video/*"
            this.action = Intent.ACTION_GET_CONTENT
            if (isSelectMultiple) {
                this.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        },
        "Select Video"
    )
}

@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_TAKE_PHONE
)
fun takePhoto(request: RouterRequest): Intent {
    val outputUri = ParameterSupport.getParcelable<Uri>(
        bundle = request.bundle,
        key = "output",
        defaultValue = null,
    ) ?: throw IllegalArgumentException("output can not be null")
    return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        this.putExtra(
            MediaStore.EXTRA_OUTPUT, outputUri,
        )
    }
}

