package com.xiaojinzi.module.common.image.crop.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.common.image.crop.R
import com.xiaojinzi.support.ktx.nothing


@Keep
private data class ImageVo(
    @DrawableRes
    val rsd: Int,
    val width: Int,
    val height: Int,
    val ratio: Float = width.toFloat() / height,
)

private val ImageList = listOf(
    ImageVo(
        rsd = R.drawable.test1,
        width = 3024,
        height = 4032,
    ),
    ImageVo(
        rsd = R.drawable.test2,
        width = 4608,
        height = 3456,
    ),
    ImageVo(
        rsd = R.drawable.test3,
        width = 3000,
        height = 4000,
    ),
)

private val TestImage = ImageList[2]

private const val CONTROL_LINE_WIDTH = 8f

// 是线的宽度的一半
private const val CONTROL_OFFSET1 = CONTROL_LINE_WIDTH / 2f

// 是线的宽度的 1/4
private const val CONTROL_OFFSET2 = CONTROL_LINE_WIDTH * 3f / 7f

private const val CONTROL_LINE_LENGTH_CORNER = 60f
private const val CONTROL_LINE_LENGTH_CENTER = 100f

private const val BORDER_LINE_WIDTH = 2f

// 辅助线
private const val SUBLINE_COUNT = 2

// 辅助线的宽度
private const val SUBLINE_WIDTH = 1f
private val SUBLINE_COLOR = Color.White

@Composable
private fun EditDataView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(color = 0xFF191919))
            .nothing(),
    ) {
        Spacer(
            modifier = Modifier
                .statusBarsPadding()
                .height(height = 100.dp)
                .nothing()
        )
        Image(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .fillMaxWidth()
                .aspectRatio(ratio = TestImage.ratio)
                .drawWithContent {
                    this.drawContent()
                    // 矩形的位置大小
                    val controllerViewRect = Rect(
                        topLeft = Offset.Zero,
                        bottomRight = Offset(
                            x = size.width,
                            y = size.height,
                        )
                    )
                    // 绘制四条边
                    run {
                        this.drawLine(
                            color = Color.White,
                            start = controllerViewRect.topLeft,
                            end = controllerViewRect.topRight,
                            strokeWidth = BORDER_LINE_WIDTH,
                        )
                        this.drawLine(
                            color = Color.White,
                            start = controllerViewRect.topRight,
                            end = controllerViewRect.bottomRight,
                            strokeWidth = BORDER_LINE_WIDTH,
                        )
                        this.drawLine(
                            color = Color.White,
                            start = controllerViewRect.bottomRight,
                            end = controllerViewRect.bottomLeft,
                            strokeWidth = BORDER_LINE_WIDTH,
                        )
                        this.drawLine(
                            color = Color.White,
                            start = controllerViewRect.bottomLeft,
                            end = controllerViewRect.topLeft,
                            strokeWidth = BORDER_LINE_WIDTH,
                        )
                    }
                    // 四个角的
                    run {
                        // 左上角
                        run {
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.left - CONTROL_OFFSET1,
                                    y = controllerViewRect.top,
                                ),
                                end = Offset(
                                    x = controllerViewRect.left + CONTROL_LINE_LENGTH_CORNER,
                                    y = controllerViewRect.top,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.left,
                                    y = controllerViewRect.top - CONTROL_OFFSET1
                                ),
                                end = Offset(
                                    x = controllerViewRect.left,
                                    y = controllerViewRect.top + CONTROL_LINE_LENGTH_CORNER
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                        }
                        // 右上角
                        run {
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.right + CONTROL_OFFSET1,
                                    y = controllerViewRect.top,
                                ),
                                end = Offset(
                                    x = controllerViewRect.right - CONTROL_LINE_LENGTH_CORNER,
                                    y = controllerViewRect.top,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.right,
                                    y = controllerViewRect.top - CONTROL_OFFSET1,
                                ),
                                end = Offset(
                                    x = controllerViewRect.right,
                                    y = controllerViewRect.top + CONTROL_LINE_LENGTH_CORNER
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                        }
                        // 右下角
                        run {
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.right + CONTROL_OFFSET1,
                                    y = controllerViewRect.bottom,
                                ),
                                end = Offset(
                                    x = controllerViewRect.right - CONTROL_LINE_LENGTH_CORNER,
                                    y = controllerViewRect.bottom,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.right,
                                    y = controllerViewRect.bottom + CONTROL_OFFSET1
                                ),
                                end = Offset(
                                    x = controllerViewRect.right,
                                    y = controllerViewRect.bottom - CONTROL_LINE_LENGTH_CORNER
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                        }
                        // 左下角
                        run {
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.left - CONTROL_OFFSET1,
                                    y = controllerViewRect.bottom,
                                ),
                                end = Offset(
                                    x = controllerViewRect.left + CONTROL_LINE_LENGTH_CORNER,
                                    y = controllerViewRect.bottom,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRect.left,
                                    y = controllerViewRect.bottom + CONTROL_OFFSET1,
                                ),
                                end = Offset(
                                    x = controllerViewRect.left,
                                    y = controllerViewRect.bottom - CONTROL_LINE_LENGTH_CORNER,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                        }
                    }
                    // 四个方向中间的横线
                    run {
                        // left 的
                        this.drawLine(
                            color = Color.White,
                            start = Offset(
                                x = controllerViewRect.left - CONTROL_OFFSET2,
                                y = controllerViewRect.centerLeft.y - CONTROL_LINE_LENGTH_CENTER / 2f,
                            ),
                            end = Offset(
                                x = controllerViewRect.left - CONTROL_OFFSET2,
                                y = controllerViewRect.centerLeft.y + CONTROL_LINE_LENGTH_CENTER / 2f,
                            ),
                            strokeWidth = CONTROL_LINE_WIDTH,
                        )
                        // top 的
                        this.drawLine(
                            color = Color.White,
                            start = Offset(
                                x = controllerViewRect.topCenter.x - CONTROL_LINE_LENGTH_CENTER / 2f,
                                y = controllerViewRect.top - CONTROL_OFFSET2,
                            ),
                            end = Offset(
                                x = controllerViewRect.topCenter.x + CONTROL_LINE_LENGTH_CENTER / 2f,
                                y = controllerViewRect.top - CONTROL_OFFSET2,
                            ),
                            strokeWidth = CONTROL_LINE_WIDTH,
                        )
                        // right 的
                        this.drawLine(
                            color = Color.White,
                            start = Offset(
                                x = controllerViewRect.right + CONTROL_OFFSET2,
                                y = controllerViewRect.centerRight.y - CONTROL_LINE_LENGTH_CENTER / 2f,
                            ),
                            end = Offset(
                                x = controllerViewRect.right + CONTROL_OFFSET2,
                                y = controllerViewRect.centerRight.y + CONTROL_LINE_LENGTH_CENTER / 2f,
                            ),
                            strokeWidth = CONTROL_LINE_WIDTH,
                        )
                        // bottom 的
                        this.drawLine(
                            color = Color.White,
                            start = Offset(
                                x = controllerViewRect.bottomCenter.x - CONTROL_LINE_LENGTH_CENTER / 2f,
                                y = controllerViewRect.bottom + CONTROL_OFFSET2,
                            ),
                            end = Offset(
                                x = controllerViewRect.bottomCenter.x + CONTROL_LINE_LENGTH_CENTER / 2f,
                                y = controllerViewRect.bottom + CONTROL_OFFSET2,
                            ),
                            strokeWidth = CONTROL_LINE_WIDTH,
                        )
                    }
                    // 画九宫格的白线
                    run {
                        val eachWidth = controllerViewRect.width / (SUBLINE_COUNT + 1)
                        val eachHeight = controllerViewRect.height / (SUBLINE_COUNT + 1)
                        repeat(times = SUBLINE_COUNT) { index ->
                            this.drawLine(
                                color = SUBLINE_COLOR,
                                start = Offset(
                                    x = controllerViewRect.left,
                                    y = controllerViewRect.top + eachHeight * (index + 1),
                                ),
                                end = Offset(
                                    x = controllerViewRect.right,
                                    y = controllerViewRect.top + eachHeight * (index + 1),
                                ),
                                strokeWidth = SUBLINE_WIDTH,
                            )
                            this.drawLine(
                                color = SUBLINE_COLOR,
                                start = Offset(
                                    x = controllerViewRect.left + eachWidth * (index + 1),
                                    y = controllerViewRect.top,
                                ),
                                end = Offset(
                                    x = controllerViewRect.left + eachWidth * (index + 1),
                                    y = controllerViewRect.bottom,
                                ),
                                strokeWidth = SUBLINE_WIDTH,
                            )
                        }
                    }
                }
                .nothing(),
            painter = painterResource(id = TestImage.rsd),
            contentDescription = null,
        )
    }
}

@Composable
fun EditDataViewWrap() {
    EditDataView()
}

@Preview
@Composable
private fun EditDataViewPreview() {
    EditDataView()
}