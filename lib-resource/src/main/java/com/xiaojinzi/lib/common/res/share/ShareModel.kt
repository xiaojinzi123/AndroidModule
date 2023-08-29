package com.xiaojinzi.lib.common.res.share

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.notSupportError
import kotlinx.parcelize.Parcelize

enum class ShareType {
    // 分享链接,
    Link,

    // 图片分享
    Image,
}

@Keep
@Parcelize
data class ShareInfoDto(
    // 分享的类型
    val shareType: ShareType,
    // 标题
    val title: StringItemDto? = null,
    // 描述
    val description: StringItemDto? = null,
    // 分享链接需要用到的链接
    val link: String? = null,
    // 分享图片用到的图片
    val imageBitmap: Bitmap? = null,
    // 缩略图图片 Id
    @DrawableRes
    val thumbImageRsd: Int? = null,
    // 缩略图图片
    val thumbImageBitmap: Bitmap? = null,
) : Parcelable {
    init {
        when (shareType) {
            ShareType.Link -> {
                if (link.isNullOrEmpty()) {
                    notSupportError(message = "link is empty")
                }
            }

            ShareType.Image -> {
                if (imageBitmap == null) {
                    notSupportError(message = "imageBitmap is null")
                }
            }
        }
    }
}

@Keep
@Parcelize
data class ViewShareInfoDto(
    val title: StringItemDto? = null,
    val core: ShareInfoDto,
) : Parcelable

@Keep
data class PlatformShareInfoDto(
    // 平台
    val platform: String,
    val core: ShareInfoDto,
) {

    companion object {

        // 微信会话
        const val PLATFORM_WX_CHAT = "wxChat"

        // 微信朋友圈
        const val PLATFORM_WX_STATE = "wxState"

        // QQ会话
        const val PLATFORM_QQ_CHAT = "qqChat"

        // QQ空间
        const val PLATFORM_QQ_STATE = "qqState"

    }

}