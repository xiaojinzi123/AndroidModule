package com.xiaojinzi.module.common.base.view.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.compose.util.contentWithComposable
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.toStringItemDto

@Composable
fun CommonAlertDialog(
    cancelText: StringItemDto? = null,
    confirmText: StringItemDto? = null,
    title: StringItemDto? = null,
    text: StringItemDto? = null,
    onDismissClick: () -> Unit = {},
    onConfirmClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissClick.invoke()
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick
            ) {
                Text(
                    text = (confirmText
                        ?: com.xiaojinzi.lib.common.res.R.string.common_res_str_confirm.toStringItemDto()).contentWithComposable(),
                    textAlign = TextAlign.Center,
                )
            }
        },
        dismissButton = cancelText?.let {
            {
                TextButton(
                    onClick = onDismissClick
                ) {
                    Text(
                        text = it.contentWithComposable(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .nothing(),
                text = (title
                    ?: com.xiaojinzi.lib.common.res.R.string.common_res_str_tip.toStringItemDto()).contentWithComposable(),
                textAlign = TextAlign.Start,
            )
        },
        text = text?.let {
            {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nothing(),
                    text = text.contentWithComposable(),
                    textAlign = TextAlign.Start,
                )
            }
        }
    )
}