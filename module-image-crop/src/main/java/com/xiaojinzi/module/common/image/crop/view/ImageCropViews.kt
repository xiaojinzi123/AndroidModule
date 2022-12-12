package com.xiaojinzi.module.common.image.crop.view

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.support.ktx.nothing
import kotlin.math.min

private const val TAG = "ImageCropViews"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ImageCropView() {
    val vm: ImageCropViewModel = viewModel()
    val containerRatio by vm.containerRatioObservableVo.collectAsState(initial = null)
    val targetImage by vm.targetImageInitData.valueStateFlow.collectAsState(initial = null)
    val targetImageScale by vm.targetImageScaleObservableDto.collectAsState(initial = 1f)
    val targetImageScaleAnim by animateFloatAsState(targetValue = targetImageScale)
    val widthHeightPercent by vm.imageInitWidthHeightPercentObservableVo.collectAsState(initial = 0f to 0f)
    val targetImageOffset by vm.imageOffsetObservableVo.collectAsState(initial = Offset.Zero)
    val cropRect by vm.cropRectInitObservableVo.collectAsState(initial = null)
    val cropTouchDetectionRectList by vm.cropTouchDetectionRectListObservableVo.collectAsState(
        initial = emptyList()
    )
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
                .background(color = Color(color = 0x66343C57))
                .padding(horizontal = 24.dp, vertical = 0.dp)
                .nothing(),
            contentAlignment = Alignment.Center,
        ) {
            // 按下时候的裁剪区域
            var cropDownRect: Rect? by remember {
                mutableStateOf(null)
            }
            var cropDownTouchDetectionVo: TouchDetectionVo? by remember {
                mutableStateOf(null)
            }
            var cropDownPosition: Offset? by remember {
                mutableStateOf(null)
            }
            Box(
                modifier = Modifier
                    // 一个正方形的限制
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
                                    val eachWidth = cropRect.width / (SUB_LINE_COUNT + 1)
                                    val eachHeight = cropRect.height / (SUB_LINE_COUNT + 1)
                                    repeat(times = SUB_LINE_COUNT) { index ->
                                        this.drawLine(
                                            color = SUB_LINE_COLOR,
                                            start = Offset(
                                                x = cropRect.left,
                                                y = cropRect.top + eachHeight * (index + 1),
                                            ),
                                            end = Offset(
                                                x = cropRect.right,
                                                y = cropRect.top + eachHeight * (index + 1),
                                            ),
                                            strokeWidth = SUB_LINE_WIDTH,
                                        )
                                        this.drawLine(
                                            color = SUB_LINE_COLOR,
                                            start = Offset(
                                                x = cropRect.left + eachWidth * (index + 1),
                                                y = cropRect.top,
                                            ),
                                            end = Offset(
                                                x = cropRect.left + eachWidth * (index + 1),
                                                y = cropRect.bottom,
                                            ),
                                            strokeWidth = SUB_LINE_WIDTH,
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
                    // 监听手势
                    .pointerInteropFilter { motionEvent ->
                        when (motionEvent.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                cropDownRect = cropRect
                                cropDownPosition = Offset(
                                    x = motionEvent.x,
                                    y = motionEvent.y,
                                )
                                cropDownTouchDetectionVo = cropTouchDetectionRectList
                                    .find {
                                        it.rect.contains(
                                            offset = Offset(
                                                x = motionEvent.x,
                                                y = motionEvent.y,
                                            )
                                        )
                                    }
                                cropDownTouchDetectionVo != null
                            }
                            MotionEvent.ACTION_MOVE -> {

                                println("cropTouchDetectionRectList = $cropTouchDetectionRectList, cropDownPosition = $cropDownPosition, motionEvent = ${motionEvent.x},${motionEvent.y}")

                                val diffX = motionEvent.x - cropDownPosition!!.x
                                val diffY = motionEvent.y - cropDownPosition!!.y

                                val targetContainerRatio = containerRatio!!
                                cropDownRect?.let { downCropRect ->
                                    cropDownTouchDetectionVo?.let { downTouchDetectionVo ->
                                        val newRect = downCropRect.copy(
                                            left = downTouchDetectionVo.leftCompute.invoke(
                                                downCropRect.left,
                                                diffX,
                                            ),
                                            top = downTouchDetectionVo.topCompute.invoke(
                                                downCropRect.top,
                                                diffY,
                                            ),
                                            right = downTouchDetectionVo.rightCompute.invoke(
                                                downCropRect.right,
                                                diffX,
                                            ),
                                            bottom = downTouchDetectionVo.bottomCompute.invoke(
                                                downCropRect.bottom,
                                                diffY,
                                            ),
                                        )
                                        val resultRect = newRect.copy(
                                            left = newRect.left * targetContainerRatio,
                                            top = newRect.top * targetContainerRatio,
                                            right = newRect.right * targetContainerRatio,
                                            bottom = newRect.bottom * targetContainerRatio,
                                        )
                                        vm.setNewCropRect(
                                            newRect = resultRect
                                        )
                                    }
                                }

                                true
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                cropDownPosition = null
                                cropDownRect = null
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                    .nothing(),
            ) {
                targetImage?.let { targetImage ->

                    var imageFirstDownPosition: Offset? by remember {
                        mutableStateOf(null)
                    }

                    var imageSecondDownPosition: Offset? by remember {
                        mutableStateOf(null)
                    }

                    Image(
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .fillMaxWidth(fraction = widthHeightPercent.first)
                            .fillMaxHeight(fraction = widthHeightPercent.second)
                            .graphicsLayer {
                                this.scaleX = targetImageScaleAnim
                                this.scaleY = targetImageScaleAnim
                                this.translationX = targetImageOffset.x
                                this.translationY = targetImageOffset.y
                            }
                            .pointerInteropFilter { motionEvent ->
                                when (motionEvent.actionMasked) {
                                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                                        when (motionEvent.pointerCount) {
                                            1 -> imageFirstDownPosition = Offset(
                                                x = motionEvent.x,
                                                y = motionEvent.y,
                                            )
                                            2 -> imageSecondDownPosition = Offset(
                                                x = motionEvent.x,
                                                y = motionEvent.y,
                                            )
                                        }
                                    }
                                    MotionEvent.ACTION_MOVE -> { // 只计算第一个手指的移动
                                        val targetContainerRatio = containerRatio!!
                                        val diffX =
                                            motionEvent.x - imageFirstDownPosition!!.x
                                        val diffY =
                                            motionEvent.y - imageFirstDownPosition!!.y
                                        val newOffset = Offset(
                                            x = targetImageOffset.x + diffX,
                                            y = targetImageOffset.y + diffY,
                                        ).times(operand = targetContainerRatio)
                                        vm.setNewTargetImageOffset(
                                            newOffset = newOffset,
                                        )
                                    }
                                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                                        when (motionEvent.pointerCount) {
                                            1 -> imageFirstDownPosition = null
                                            2 -> imageSecondDownPosition = null
                                        }
                                    }
                                }
                                true
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
                .height(height = 100.dp)
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