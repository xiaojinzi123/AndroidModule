package com.xiaojinzi.module.common.image.crop.view

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewModelScope
import com.xiaojinzi.module.common.image.crop.domain.ImageCropUseCase
import com.xiaojinzi.module.common.image.crop.domain.ImageCropUseCaseImpl
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.ktx.CacheSharedStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

// 线条的宽度
const val CONTROL_LINE_WIDTH = 8f

// 是线的宽度的一半
const val CONTROL_OFFSET1 = CONTROL_LINE_WIDTH / 2f

// 是线的宽度的 3/7
const val CONTROL_OFFSET2 = CONTROL_LINE_WIDTH * 3f / 7f

// 角落的线的长度
const val CONTROL_LINE_LENGTH_CORNER = 60f

// 中间的线的长度
const val CONTROL_LINE_LENGTH_CENTER = 100f

// 角落的触摸区域的 size
const val CONTROL_TOUCH_SIZE = CONTROL_LINE_LENGTH_CORNER + 80f

// 四条边的控制块的触碰区域的宽度
const val CONTROL_CENTER_TOUCH_WIDTH = CONTROL_LINE_LENGTH_CENTER + 20f

// 四条边的控制块的触碰区域的高度
const val CONTROL_CENTER_TOUCH_HEIGHT = 160f

const val BORDER_LINE_WIDTH = 2f

// 辅助线
const val SUB_LINE_COUNT = 2

// 辅助线的宽度
const val SUB_LINE_WIDTH = 1f

val SUB_LINE_COLOR = Color.White

data class TouchDetectionVo(
    val leftCompute: (originX: Float, diffX: Float) -> Float = { originX, _ -> originX },
    val topCompute: (originY: Float, diffY: Float) -> Float = { originY, _ -> originY },
    val rightCompute: (originX: Float, diffX: Float) -> Float = { originX, _ -> originX },
    val bottomCompute: (originY: Float, diffY: Float) -> Float = { originY, _ -> originY },
    val rect: Rect,
)

class ImageCropViewModel(
    private val useCase: ImageCropUseCase = ImageCropUseCaseImpl(),
) : BaseViewModel(), ImageCropUseCase by useCase {

    /**
     * UI 的容器的真实大小
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val viewContainerSizeObservableVo = CacheSharedStateFlow<IntSize>(
        scope = viewModelScope,
    ) {
        if (it.width != it.height) {
            throw IllegalArgumentException("width is not equal to height, width = ${it.width}, height = ${it.height}")
        }
    }

    /**
     * 虚拟容器和 View 容器的比例
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val containerRatioObservableVo = viewContainerSizeObservableVo
        .map {
            ImageCropUseCase.CONTAINER_SIZE / it.width
        }

    /**
     * 图片宽度的初始比例, 和容器的比例
     */
    val imageInitWidthHeightPercentObservableVo = useCase
        .initImageRectObservableDto
        .map {
            it.width / ImageCropUseCase.CONTAINER_SIZE to it.height / ImageCropUseCase.CONTAINER_SIZE
        }

    /**
     * 图片的偏移量
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val imageOffsetObservableVo: Flow<Offset> = combine(
        containerRatioObservableVo,
        useCase.imageOffsetObservableDto,
    ) { containerRatio, imageOffset ->
        imageOffset.div(operand = containerRatio)
    }

    /**
     * 视图上的裁剪的区域
     */
    val cropRectInitObservableVo = combine(
        containerRatioObservableVo,
        useCase.cropRectObservableDto,
    ) { containerRatio, cropRect ->
        Rect(
            left = cropRect.left / containerRatio,
            right = cropRect.right / containerRatio,
            top = cropRect.top / containerRatio,
            bottom = cropRect.bottom / containerRatio,
        )
    }

    /**
     * 裁剪区域的几个控制点的触摸区域
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val cropTouchDetectionRectListObservableVo = cropRectInitObservableVo.map { cropRect ->
        listOf(
            // left
            TouchDetectionVo(
                leftCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.left - CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                    top = cropRect.centerLeft.y - CONTROL_CENTER_TOUCH_WIDTH / 2f,
                    right = cropRect.left + CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                    bottom = cropRect.centerLeft.y + CONTROL_CENTER_TOUCH_WIDTH / 2f,
                )
            ),
            // top
            TouchDetectionVo(
                topCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.topCenter.x - CONTROL_CENTER_TOUCH_WIDTH / 2f,
                    top = cropRect.top - CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                    right = cropRect.topCenter.x + CONTROL_CENTER_TOUCH_WIDTH / 2f,
                    bottom = cropRect.top + CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                )
            ),
            // right
            TouchDetectionVo(
                rightCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.right - CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                    top = cropRect.centerRight.y - CONTROL_CENTER_TOUCH_WIDTH / 2f,
                    right = cropRect.right + CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                    bottom = cropRect.centerRight.y + CONTROL_CENTER_TOUCH_WIDTH / 2f,
                )
            ),
            // bottom
            TouchDetectionVo(
                bottomCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.bottomCenter.x - CONTROL_CENTER_TOUCH_WIDTH / 2f,
                    top = cropRect.bottom - CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                    right = cropRect.bottomCenter.x + CONTROL_CENTER_TOUCH_WIDTH / 2f,
                    bottom = cropRect.bottom + CONTROL_CENTER_TOUCH_HEIGHT / 2f,
                )
            ),
            // 左上角
            TouchDetectionVo(
                leftCompute = { originX, diffX -> originX + diffX },
                topCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.left - CONTROL_TOUCH_SIZE / 2f,
                    top = cropRect.top - CONTROL_TOUCH_SIZE / 2f,
                    right = cropRect.left + CONTROL_TOUCH_SIZE / 2f,
                    bottom = cropRect.top + CONTROL_TOUCH_SIZE / 2f,
                ),
            ),
            // 右边上角
            TouchDetectionVo(
                topCompute = { originY, diffY -> originY + diffY },
                rightCompute = { originX, diffX -> originX + diffX },
                rect = Rect(
                    left = cropRect.right - CONTROL_TOUCH_SIZE / 2f,
                    top = cropRect.top - CONTROL_TOUCH_SIZE / 2f,
                    right = cropRect.right + CONTROL_TOUCH_SIZE / 2f,
                    bottom = cropRect.top + CONTROL_TOUCH_SIZE / 2f,
                ),
            ),
            // 左下角
            TouchDetectionVo(
                leftCompute = { originX, diffX -> originX + diffX },
                bottomCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.left - CONTROL_TOUCH_SIZE / 2f,
                    top = cropRect.bottom - CONTROL_TOUCH_SIZE / 2f,
                    right = cropRect.left + CONTROL_TOUCH_SIZE / 2f,
                    bottom = cropRect.bottom + CONTROL_TOUCH_SIZE / 2f,
                ),
            ),
            // 右边下角
            TouchDetectionVo(
                rightCompute = { originX, diffX -> originX + diffX },
                bottomCompute = { originY, diffY -> originY + diffY },
                rect = Rect(
                    left = cropRect.right - CONTROL_TOUCH_SIZE / 2f,
                    top = cropRect.bottom - CONTROL_TOUCH_SIZE / 2f,
                    right = cropRect.right + CONTROL_TOUCH_SIZE / 2f,
                    bottom = cropRect.bottom + CONTROL_TOUCH_SIZE / 2f,
                ),
            ),
        )
    }

}