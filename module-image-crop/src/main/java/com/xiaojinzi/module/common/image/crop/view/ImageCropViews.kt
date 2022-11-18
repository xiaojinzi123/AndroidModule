package com.xiaojinzi.module.common.image.crop.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.common.image.crop.domain.ImageCropUseCase
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.nothing
import kotlin.math.min

private const val TAG = "ImageCropViews"

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

private val TestImage = ImageCropUseCase.ImageList[3]

@Composable
private fun test() {
    // 目标的裁剪比例
    val targetRatio: Float = 3f / 8f
    var controllerViewRect by remember {
        mutableStateOf(
            Rect(
                topLeft = Offset.Zero,
                bottomRight = Offset.Zero,
            )
        )
    }
    val controllerViewRectForAnim by animateRectAsState(
        targetValue = controllerViewRect
    )
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .fillMaxWidth()
                .weight(weight = 1f, fill = true)
                .background(color = Color(color = 0xFFE91E63))
                .drawWithContent {
                    this.drawContent()
                    if (!controllerViewRectForAnim.isEmpty) {
                        // 绘制四条边
                        run {
                            this.drawLine(
                                color = Color.White,
                                start = controllerViewRectForAnim.topLeft,
                                end = controllerViewRectForAnim.topRight,
                                strokeWidth = BORDER_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = controllerViewRectForAnim.topRight,
                                end = controllerViewRectForAnim.bottomRight,
                                strokeWidth = BORDER_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = controllerViewRectForAnim.bottomRight,
                                end = controllerViewRectForAnim.bottomLeft,
                                strokeWidth = BORDER_LINE_WIDTH,
                            )
                            this.drawLine(
                                color = Color.White,
                                start = controllerViewRectForAnim.bottomLeft,
                                end = controllerViewRectForAnim.topLeft,
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
                                        x = controllerViewRectForAnim.left - CONTROL_OFFSET1,
                                        y = controllerViewRectForAnim.top,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.left + CONTROL_LINE_LENGTH_CORNER,
                                        y = controllerViewRectForAnim.top,
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.left,
                                        y = controllerViewRectForAnim.top - CONTROL_OFFSET1
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.left,
                                        y = controllerViewRectForAnim.top + CONTROL_LINE_LENGTH_CORNER
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                            }
                            // 右上角
                            run {
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.right + CONTROL_OFFSET1,
                                        y = controllerViewRectForAnim.top,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.right - CONTROL_LINE_LENGTH_CORNER,
                                        y = controllerViewRectForAnim.top,
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.right,
                                        y = controllerViewRectForAnim.top - CONTROL_OFFSET1,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.right,
                                        y = controllerViewRectForAnim.top + CONTROL_LINE_LENGTH_CORNER
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                            }
                            // 右下角
                            run {
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.right + CONTROL_OFFSET1,
                                        y = controllerViewRectForAnim.bottom,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.right - CONTROL_LINE_LENGTH_CORNER,
                                        y = controllerViewRectForAnim.bottom,
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.right,
                                        y = controllerViewRectForAnim.bottom + CONTROL_OFFSET1
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.right,
                                        y = controllerViewRectForAnim.bottom - CONTROL_LINE_LENGTH_CORNER
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                            }
                            // 左下角
                            run {
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.left - CONTROL_OFFSET1,
                                        y = controllerViewRectForAnim.bottom,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.left + CONTROL_LINE_LENGTH_CORNER,
                                        y = controllerViewRectForAnim.bottom,
                                    ),
                                    strokeWidth = CONTROL_LINE_WIDTH,
                                )
                                this.drawLine(
                                    color = Color.White,
                                    start = Offset(
                                        x = controllerViewRectForAnim.left,
                                        y = controllerViewRectForAnim.bottom + CONTROL_OFFSET1,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.left,
                                        y = controllerViewRectForAnim.bottom - CONTROL_LINE_LENGTH_CORNER,
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
                                    x = controllerViewRectForAnim.left - CONTROL_OFFSET2,
                                    y = controllerViewRectForAnim.centerLeft.y - CONTROL_LINE_LENGTH_CENTER / 2f,
                                ),
                                end = Offset(
                                    x = controllerViewRectForAnim.left - CONTROL_OFFSET2,
                                    y = controllerViewRectForAnim.centerLeft.y + CONTROL_LINE_LENGTH_CENTER / 2f,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            // top 的
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRectForAnim.topCenter.x - CONTROL_LINE_LENGTH_CENTER / 2f,
                                    y = controllerViewRectForAnim.top - CONTROL_OFFSET2,
                                ),
                                end = Offset(
                                    x = controllerViewRectForAnim.topCenter.x + CONTROL_LINE_LENGTH_CENTER / 2f,
                                    y = controllerViewRectForAnim.top - CONTROL_OFFSET2,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            // right 的
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRectForAnim.right + CONTROL_OFFSET2,
                                    y = controllerViewRectForAnim.centerRight.y - CONTROL_LINE_LENGTH_CENTER / 2f,
                                ),
                                end = Offset(
                                    x = controllerViewRectForAnim.right + CONTROL_OFFSET2,
                                    y = controllerViewRectForAnim.centerRight.y + CONTROL_LINE_LENGTH_CENTER / 2f,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                            // bottom 的
                            this.drawLine(
                                color = Color.White,
                                start = Offset(
                                    x = controllerViewRectForAnim.bottomCenter.x - CONTROL_LINE_LENGTH_CENTER / 2f,
                                    y = controllerViewRectForAnim.bottom + CONTROL_OFFSET2,
                                ),
                                end = Offset(
                                    x = controllerViewRectForAnim.bottomCenter.x + CONTROL_LINE_LENGTH_CENTER / 2f,
                                    y = controllerViewRectForAnim.bottom + CONTROL_OFFSET2,
                                ),
                                strokeWidth = CONTROL_LINE_WIDTH,
                            )
                        }
                        // 画九宫格的白线
                        run {
                            val eachWidth = controllerViewRectForAnim.width / (SUBLINE_COUNT + 1)
                            val eachHeight = controllerViewRectForAnim.height / (SUBLINE_COUNT + 1)
                            repeat(times = SUBLINE_COUNT) { index ->
                                this.drawLine(
                                    color = SUBLINE_COLOR,
                                    start = Offset(
                                        x = controllerViewRectForAnim.left,
                                        y = controllerViewRectForAnim.top + eachHeight * (index + 1),
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.right,
                                        y = controllerViewRectForAnim.top + eachHeight * (index + 1),
                                    ),
                                    strokeWidth = SUBLINE_WIDTH,
                                )
                                this.drawLine(
                                    color = SUBLINE_COLOR,
                                    start = Offset(
                                        x = controllerViewRectForAnim.left + eachWidth * (index + 1),
                                        y = controllerViewRectForAnim.top,
                                    ),
                                    end = Offset(
                                        x = controllerViewRectForAnim.left + eachWidth * (index + 1),
                                        y = controllerViewRectForAnim.bottom,
                                    ),
                                    strokeWidth = SUBLINE_WIDTH,
                                )
                            }
                        }
                    }
                }
                .onSizeChanged { size ->
                    // 矩形的位置大小
                    val tempRatio: Float = (size.width.toFloat() / size.height)
                    controllerViewRect = when {
                        tempRatio == targetRatio -> {
                            Rect(
                                topLeft = Offset.Zero,
                                bottomRight = Offset(
                                    x = size.width.toFloat(),
                                    y = size.height.toFloat(),
                                )
                            )
                        }
                        tempRatio < targetRatio -> {
                            val offset = (size.height - size.width.toFloat() / targetRatio) / 2f
                            // 固定宽
                            Rect(
                                topLeft = Offset(
                                    x = 0f,
                                    y = 0f + offset,
                                ),
                                bottomRight = Offset(
                                    x = size.width.toFloat(),
                                    y = size.height.toFloat() - offset,
                                ),
                            )
                        }
                        else -> {
                            val offset = (size.width - size.height.toFloat() * targetRatio) / 2f
                            // 固定高
                            Rect(
                                topLeft = Offset(
                                    x = 0f + offset,
                                    y = 0f,
                                ),
                                bottomRight = Offset(
                                    x = size.width - offset,
                                    y = size.height.toFloat(),
                                )
                            )
                        }
                    }
                }
                .nothing(),
            contentAlignment = Alignment.Center,
        ) {
            var scale by remember { mutableStateOf(1f) }
            LogSupport.d(
                tag = TAG,
                content = "scale = $scale"
            )
            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                scale *= zoomChange
            }
            Image(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val newConstraints = if (
                            (controllerViewRectForAnim.width / controllerViewRectForAnim.height) < TestImage.ratio
                        ) {
                            Constraints.fixed(
                                width = (controllerViewRectForAnim.height * TestImage.ratio).toInt(),
                                height = controllerViewRectForAnim.height.toInt(),
                            )
                        } else {
                            Constraints.fixed(
                                width = controllerViewRectForAnim.width.toInt(),
                                height = (controllerViewRectForAnim.width / TestImage.ratio).toInt(),
                            )
                        }
                        val placeable = measurable.measure(
                            constraints = newConstraints
                        )
                        this.layout(width = placeable.width, height = placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .fillMaxSize()
                    .graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                    }
                    .transformable(state = state)
                    .nothing(),
                painter = painterResource(id = TestImage.rsd),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ImageCropView() {
    val vm: ImageCropViewModel = viewModel()
    val containerRatio by vm.containerRatioObservableVo.collectAsState(initial = null)
    val targetImage by vm.targetImageInitData.valueStateFlow.collectAsState(initial = null)
    val targetImageScale by vm.targetImageScaleObservableDto.collectAsState(initial = 1f)
    val targetImageScaleAnim by animateFloatAsState(targetValue = targetImageScale)
    val targetImageOffset by vm.imageOffsetObservableVo.collectAsState(initial = Offset.Zero)
    val widthHeightPercent by vm.imageInitWidthHeightPercentObservableVo.collectAsState(initial = 0f to 0f)
    val cropRect by vm.cropRectInitObservableVo.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(color = 0xFF191919))
            .nothing(),
    ) {
        Spacer(
            modifier = Modifier
                .statusBarsPadding()
                .height(height = 50.dp)
                .nothing()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
                .nothing(),
            contentAlignment = Alignment.Center,
        ) {
            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                vm.applyImageScaleAndMove(
                    scaleOffset = zoomChange,
                    moveOffset = containerRatio
                        ?.let {
                            offsetChange.times(operand = it)
                        } ?: Offset.Zero,
                )
            }
            Box(
                modifier = Modifier
                    .onKeyEvent {
                        println("xxxxxxxxxxxxxxxxxxxxxx")
                        false
                    }
                    .layout { measurable, constraints ->
                        // 取出宽和高比较小的一个
                        val targetSize = min(
                            a = constraints.maxWidth,
                            b = constraints.maxHeight,
                        )
                        val placeable = measurable.measure(
                            constraints = Constraints.fixed(
                                width = targetSize, height = targetSize,
                            )
                        )
                        this.layout(width = placeable.width, height = placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .background(color = Color.Red)
                    .drawWithContent {
                        this.drawContent()
                        cropRect?.let { cropRect ->
                            if (!cropRect.isEmpty) {
                                // 绘制四条边
                                run {
                                    this.drawLine(
                                        color = Color.White,
                                        start = cropRect.topLeft,
                                        end = cropRect.topRight,
                                        strokeWidth = BORDER_LINE_WIDTH,
                                    )
                                    this.drawLine(
                                        color = Color.White,
                                        start = cropRect.topRight,
                                        end = cropRect.bottomRight,
                                        strokeWidth = BORDER_LINE_WIDTH,
                                    )
                                    this.drawLine(
                                        color = Color.White,
                                        start = cropRect.bottomRight,
                                        end = cropRect.bottomLeft,
                                        strokeWidth = BORDER_LINE_WIDTH,
                                    )
                                    this.drawLine(
                                        color = Color.White,
                                        start = cropRect.bottomLeft,
                                        end = cropRect.topLeft,
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
                                                x = cropRect.left - CONTROL_OFFSET1,
                                                y = cropRect.top,
                                            ),
                                            end = Offset(
                                                x = cropRect.left + CONTROL_LINE_LENGTH_CORNER,
                                                y = cropRect.top,
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.left,
                                                y = cropRect.top - CONTROL_OFFSET1
                                            ),
                                            end = Offset(
                                                x = cropRect.left,
                                                y = cropRect.top + CONTROL_LINE_LENGTH_CORNER
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                    }
                                    // 右上角
                                    run {
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.right + CONTROL_OFFSET1,
                                                y = cropRect.top,
                                            ),
                                            end = Offset(
                                                x = cropRect.right - CONTROL_LINE_LENGTH_CORNER,
                                                y = cropRect.top,
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.right,
                                                y = cropRect.top - CONTROL_OFFSET1,
                                            ),
                                            end = Offset(
                                                x = cropRect.right,
                                                y = cropRect.top + CONTROL_LINE_LENGTH_CORNER
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                    }
                                    // 右下角
                                    run {
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.right + CONTROL_OFFSET1,
                                                y = cropRect.bottom,
                                            ),
                                            end = Offset(
                                                x = cropRect.right - CONTROL_LINE_LENGTH_CORNER,
                                                y = cropRect.bottom,
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.right,
                                                y = cropRect.bottom + CONTROL_OFFSET1
                                            ),
                                            end = Offset(
                                                x = cropRect.right,
                                                y = cropRect.bottom - CONTROL_LINE_LENGTH_CORNER
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                    }
                                    // 左下角
                                    run {
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.left - CONTROL_OFFSET1,
                                                y = cropRect.bottom,
                                            ),
                                            end = Offset(
                                                x = cropRect.left + CONTROL_LINE_LENGTH_CORNER,
                                                y = cropRect.bottom,
                                            ),
                                            strokeWidth = CONTROL_LINE_WIDTH,
                                        )
                                        this.drawLine(
                                            color = Color.White,
                                            start = Offset(
                                                x = cropRect.left,
                                                y = cropRect.bottom + CONTROL_OFFSET1,
                                            ),
                                            end = Offset(
                                                x = cropRect.left,
                                                y = cropRect.bottom - CONTROL_LINE_LENGTH_CORNER,
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
                                            x = cropRect.left - CONTROL_OFFSET2,
                                            y = cropRect.centerLeft.y - CONTROL_LINE_LENGTH_CENTER / 2f,
                                        ),
                                        end = Offset(
                                            x = cropRect.left - CONTROL_OFFSET2,
                                            y = cropRect.centerLeft.y + CONTROL_LINE_LENGTH_CENTER / 2f,
                                        ),
                                        strokeWidth = CONTROL_LINE_WIDTH,
                                    )
                                    // top 的
                                    this.drawLine(
                                        color = Color.White,
                                        start = Offset(
                                            x = cropRect.topCenter.x - CONTROL_LINE_LENGTH_CENTER / 2f,
                                            y = cropRect.top - CONTROL_OFFSET2,
                                        ),
                                        end = Offset(
                                            x = cropRect.topCenter.x + CONTROL_LINE_LENGTH_CENTER / 2f,
                                            y = cropRect.top - CONTROL_OFFSET2,
                                        ),
                                        strokeWidth = CONTROL_LINE_WIDTH,
                                    )
                                    // right 的
                                    this.drawLine(
                                        color = Color.White,
                                        start = Offset(
                                            x = cropRect.right + CONTROL_OFFSET2,
                                            y = cropRect.centerRight.y - CONTROL_LINE_LENGTH_CENTER / 2f,
                                        ),
                                        end = Offset(
                                            x = cropRect.right + CONTROL_OFFSET2,
                                            y = cropRect.centerRight.y + CONTROL_LINE_LENGTH_CENTER / 2f,
                                        ),
                                        strokeWidth = CONTROL_LINE_WIDTH,
                                    )
                                    // bottom 的
                                    this.drawLine(
                                        color = Color.White,
                                        start = Offset(
                                            x = cropRect.bottomCenter.x - CONTROL_LINE_LENGTH_CENTER / 2f,
                                            y = cropRect.bottom + CONTROL_OFFSET2,
                                        ),
                                        end = Offset(
                                            x = cropRect.bottomCenter.x + CONTROL_LINE_LENGTH_CENTER / 2f,
                                            y = cropRect.bottom + CONTROL_OFFSET2,
                                        ),
                                        strokeWidth = CONTROL_LINE_WIDTH,
                                    )
                                }
                                // 画九宫格的白线
                                run {
                                    val eachWidth = cropRect.width / (SUBLINE_COUNT + 1)
                                    val eachHeight = cropRect.height / (SUBLINE_COUNT + 1)
                                    repeat(times = SUBLINE_COUNT) { index ->
                                        this.drawLine(
                                            color = SUBLINE_COLOR,
                                            start = Offset(
                                                x = cropRect.left,
                                                y = cropRect.top + eachHeight * (index + 1),
                                            ),
                                            end = Offset(
                                                x = cropRect.right,
                                                y = cropRect.top + eachHeight * (index + 1),
                                            ),
                                            strokeWidth = SUBLINE_WIDTH,
                                        )
                                        this.drawLine(
                                            color = SUBLINE_COLOR,
                                            start = Offset(
                                                x = cropRect.left + eachWidth * (index + 1),
                                                y = cropRect.top,
                                            ),
                                            end = Offset(
                                                x = cropRect.left + eachWidth * (index + 1),
                                                y = cropRect.bottom,
                                            ),
                                            strokeWidth = SUBLINE_WIDTH,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // .transformable(state = state)
                    .onSizeChanged {
                        vm.viewContainerSizeObservableVo.add(
                            value = it
                        )
                    }
                    .nothing(),
            ) {
                targetImage?.let { targetImage ->

                    Image(
                        modifier = Modifier
                            .onKeyEvent {
                                println("xxxxxxxxxxxxxxxxxxxxxx")
                                false
                            }
                            .align(alignment = Alignment.Center)
                            .fillMaxWidth(fraction = widthHeightPercent.first)
                            .fillMaxHeight(fraction = widthHeightPercent.second)
                            .graphicsLayer {
                                this.scaleX = targetImageScaleAnim
                                this.scaleY = targetImageScaleAnim
                                this.translationX = targetImageOffset.x
                                this.translationY = targetImageOffset.y
                            }
                            .nothing(),
                        painter = painterResource(id = targetImage.rsd),
                        contentDescription = null,
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .height(height = 200.dp)
                .nothing()
        )
    }
}

@Composable
fun ImageCropWrap() {
    ImageCropView()
}

@Preview
@Composable
private fun ImageCropViewPreview() {
    ImageCropView()
}