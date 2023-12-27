package com.xiaojinzi.module.common.demo.module.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.common.base.CommonRouterConfig
import com.xiaojinzi.module.common.base.support.CommonServices
import com.xiaojinzi.module.common.base.usecase.CommonContentView
import com.xiaojinzi.module.common.base.view.CommonActivity
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.copyFileTo
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.translateStatusBar
import kotlinx.coroutines.launch
import java.io.File

class MainAct : CommonActivity<MainViewModel>() {

    private var uri : Uri? = null

    private val targetImage = MutableSharedStateFlow<File?>(
        initValue = null,
    )

    private val AppCoilImageLoader = ImageLoader
        .Builder(context = app)
        .components {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                this.add(ImageDecoderDecoder.Factory())
            } else {
                this.add(GifDecoder.Factory())
            }
            this.add(VideoFrameDecoder.Factory())
        }
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val targetImage by targetImage.collectAsState(initial = null)
            CommonContentView<MainViewModel>() {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .nothing(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                ) {

                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f, fill = true)
                            .nothing(),
                        imageLoader = AppCoilImageLoader,
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(
                                data = targetImage,
                            )
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                    )

                    Button(
                        onClick = {
                            uri = FileProvider.getUriForFile(
                                this@MainAct,
                                app.packageName + ".fileProvider",
                                File(app.cacheDir, "${System.currentTimeMillis()}.jpg"),
                            )
                            Router
                                .with(context = this@MainAct)
                                .hostAndPath(hostAndPath = CommonRouterConfig.SYSTEM_TAKE_PHONE)
                                .requestCode(requestCode = 123)
                                .putParcelable(key = "output", value = uri)
                                .forward()
                        }
                    ) {
                        Text(text = "点我拍照")
                    }

                    Spacer(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .height(height = 20.dp)
                            .nothing()
                    )

                    Button(
                        onClick = {
                            CommonServices
                                .bugly1Spi
                                ?.testAnrCrash()
                        }
                    ) {
                        Text(text = "testJavaCrash")
                    }

                    Spacer(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .height(height = 20.dp)
                            .nothing()
                    )

                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogSupport.d(
            tag = "xxxxx",
            content = "resultCode = $resultCode",
        )
        if (requestCode == 123 && resultCode == RESULT_OK) {
            targetImage.value = kotlin.runCatching {
                uri?.copyFileTo(
                    File(app.cacheDir, "${System.currentTimeMillis()}.jpg")
                )
            }.getOrNull()
        }
    }

}