package com.xiaojinzi.module.common.base.router

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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

@RouterAnno(
    hostAndPath = CommonRouterConfig.SYSTEM_ACTION_MANAGE_OVERLAY_PERMISSION
)
fun manageOverlayPermission(request: RouterRequest): Intent {
    return Intent().apply {
        this.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        this.data = Uri.parse("package:" + app.packageName)
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

