package com.xiaojinzi.module.common.wx.sdk.spi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.common.res.share.PlatformShareInfoDto
import com.xiaojinzi.lib.common.res.share.ShareType
import com.xiaojinzi.module.common.base.spi.ShareSpi
import com.xiaojinzi.module.common.wx.sdk.WXApi
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext
import com.xiaojinzi.support.ktx.notSupportError


@SuppressLint("UnspecifiedRegisterReceiverFlag")
@ServiceAnno(
    value = [ShareSpi::class, ShareSpi::class],
    name = [PlatformShareInfoDto.PLATFORM_WX_CHAT, PlatformShareInfoDto.PLATFORM_WX_STATE],
    autoInit = true,
)
class ShareServiceImplForWX : ShareSpi {

    override suspend fun share(shareInfo: PlatformShareInfoDto) {
        val scene = when (shareInfo.platform) {
            PlatformShareInfoDto.PLATFORM_WX_CHAT -> SendMessageToWX.Req.WXSceneSession
            PlatformShareInfoDto.PLATFORM_WX_STATE -> SendMessageToWX.Req.WXSceneTimeline
            else -> notSupportError()
        }
        val req = SendMessageToWX.Req().apply {
            this.transaction = System.currentTimeMillis().toString()
            this.message = WXMediaMessage().apply {
                this.mediaObject = when (shareInfo.core.shareType) {
                    ShareType.Link -> {
                        WXWebpageObject().apply {
                            this.webpageUrl = shareInfo.core.link
                        }
                    }

                    ShareType.Image -> {
                        shareInfo.core.imageBitmap?.let {
                            WXImageObject(it)
                        }
                    }

                    else -> notSupportError()
                }
                this.title = shareInfo.core.title?.contentWithContext() ?: ""
                this.description = shareInfo.core.description?.contentWithContext() ?: ""
                when {

                    shareInfo.core.thumbImageBitmap != null -> {
                        shareInfo.core.thumbImageBitmap?.let {
                            this.setThumbImage(it)
                        }
                    }

                    shareInfo.core.thumbImageRsd != null -> {
                        shareInfo.core.thumbImageRsd?.let {
                            this.setThumbImage(
                                BitmapFactory.decodeResource(
                                    app.resources,
                                    it,
                                )
                            )
                        }
                    }

                }
            }
            this.scene = scene
        }
        //调用 api 接口，发送数据到微信
        WXApi.api.sendReq(req).apply {
            LogSupport.d(
                tag = ShareSpi.TAG,
                content = "wx share, isSuccess = $this, shareInfo = $shareInfo"
            )
        }
    }

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        WXApi.appId?.let { appId ->
                            // 将该 app 注册到微信
                            WXApi.api.registerApp(appId);
                        }
                    }
                },
                IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP),
                Context.RECEIVER_NOT_EXPORTED,
            )
        } else {
            app.registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        WXApi.appId?.let { appId ->
                            // 将该 app 注册到微信
                            WXApi.api.registerApp(appId);
                        }
                    }
                },
                IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP),
            )
        }

    }

}