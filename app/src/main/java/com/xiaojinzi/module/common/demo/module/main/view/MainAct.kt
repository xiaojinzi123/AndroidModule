package com.xiaojinzi.module.common.demo.module.main.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.common.base.CommonRouterConfig
import com.xiaojinzi.module.common.base.usecase.CommonContentView
import com.xiaojinzi.module.common.base.view.CommonActivity
import kotlinx.coroutines.launch

class MainAct : CommonActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CommonContentView<MainViewModel>() {
                Text(
                    text = "ceasdasedqwe",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFFFF00FF),
                        fontWeight = FontWeight.Normal,
                    ),
                    textAlign = TextAlign.Start,
                )
            }
        }

        lifecycleScope.launch {

            Router
                .with(context = mContext)
                .hostAndPath(hostAndPath = CommonRouterConfig.SUPPORT_IMAGE_CROP)
                .forward()

        }

    }

}