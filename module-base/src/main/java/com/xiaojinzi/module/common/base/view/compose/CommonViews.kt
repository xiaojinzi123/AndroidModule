package com.xiaojinzi.module.common.base.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xiaojinzi.support.ktx.nothing

@Composable
fun CommonInitDataView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(com.xiaojinzi.lib.common.res.R.raw.common_res_lottie_loading1)
        )
        LottieAnimation(
            modifier = Modifier
                .widthIn(max = 80.dp)
                .nothing(),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
    }
}

@Composable
fun CommonErrorDataView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(height = 35.dp).nothing())
        Image(
            modifier = Modifier
                .size(size = 138.dp)
                .nothing(),
            painter = painterResource(id = com.xiaojinzi.lib.common.res.R.drawable.res_error1),
            contentDescription = null
        )
        Text(
            text = stringResource(id = com.xiaojinzi.lib.common.res.R.string.res_str_desc1),
            style = TextStyle(
                color = Color(0xFFABA5AB),
                lineHeight = 20.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}