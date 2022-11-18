package com.xiaojinzi.module.common.image.crop.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.common.base.CommonRouterConfig
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.compose.StateBar
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.support.ktx.translateStatusBar

@RouterAnno(
    hostAndPath = CommonRouterConfig.SUPPORT_IMAGE_CROP,
)
class ImageCropAct : BaseAct<ImageCropViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initOnceUseViewModel {
        }

        setContent {
            StateBar {
                ImageCropWrap()
            }
        }

    }

}