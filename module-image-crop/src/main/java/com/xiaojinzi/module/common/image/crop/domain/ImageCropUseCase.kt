package com.xiaojinzi.module.common.image.crop.domain

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.xiaojinzi.module.common.image.crop.R
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Keep
data class ImageVo(
    @DrawableRes
    val rsd: Int,
    val width: Int,
    val height: Int,
    val ratio: Float = width.toFloat() / height,
) // 占位
{

    val isWider: Boolean
        get() = width > height

    val isHigher: Boolean
        get() = height > width

}

/**
 * 所有数据都在一个 10000 * 10000 的容器中.
 * 这是一个虚拟的容器, 就是为了表示出所有的数据
 * UI 层根据这些数据, 对 UI 进行渲染
 * UI 层也会提供一个 1:1 的绘制容器, 对这个虚拟容器进行绘制
 * 这里解释下为什么要弄一个虚拟容器的概念：因为可以让 UI 层不影响真正的数据层
 * 比如横竖屏切换了, 菜单挤了原先的 View 的绘制容器等等, 都不会出现问题
 */
interface ImageCropUseCase : BaseUseCase {

    companion object {

        const val CONTAINER_SIZE = 10000f

        val ImageList = listOf(
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
            ImageVo(
                rsd = R.drawable.test4,
                width = 1238,
                height = 4000,
            ),
        )

        val TestImage = ImageList[2]

    }

    val targetImageInitData: InitOnceData<ImageVo>

    /**
     * 目标图片的比例
     */
    val targetImageRatioInitData: InitOnceData<Float>

    /**
     * 图片的初始位置, 在 10000 * 10000 的容器中.
     */
    val initImageRectObservableDto: Flow<Rect>

    /**
     * 图片的缩放比
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val targetImageScaleObservableDto: Flow<Float>

    /**
     * 图片的偏移量
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val imageOffsetObservableDto: Flow<Offset>

    /**
     * cropBorder 的位置, 在 10000 * 10000 的容器中.
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val cropRectObservableDto: Flow<Rect>

    /**
     * 应用图片的缩放和移动
     */
    fun applyImageScaleAndMove(
        scaleOffset: Float,
        moveOffset: Offset,
    )

    /**
     * 调整
     */
    fun adjust()

}

class ImageCropUseCaseImpl : BaseUseCaseImpl(), ImageCropUseCase {

    override val targetImageInitData: InitOnceData<ImageVo> = MutableInitOnceData(
        initValue = ImageCropUseCase.TestImage,
    )

    override val targetImageRatioInitData = targetImageInitData.initOnceData(
        scope = scope,
        valueCheck = { value ->
            if (value <= 0f) {
                throw IllegalArgumentException("targetImageRatio must > 0")
            }
        },
    ) {
        it.ratio
    }

    override val initImageRectObservableDto = targetImageRatioInitData
        .valueStateFlow
        .map { ratio ->
            // 得到目标图的默认宽和高
            val (width, height) = if (ratio == 1f) { // 如果是正方形的
                ImageCropUseCase.CONTAINER_SIZE to ImageCropUseCase.CONTAINER_SIZE
            } else if (ratio > 1f) { // 如果是宽图
                ImageCropUseCase.CONTAINER_SIZE to ImageCropUseCase.CONTAINER_SIZE / ratio
            } else { // 如果是长图
                ImageCropUseCase.CONTAINER_SIZE * ratio to ImageCropUseCase.CONTAINER_SIZE
            }
            val widthOffset = ImageCropUseCase.CONTAINER_SIZE - width
            val heightOffset = ImageCropUseCase.CONTAINER_SIZE - height
            Rect(
                left = widthOffset / 2f,
                top = heightOffset / 2f,
                right = ImageCropUseCase.CONTAINER_SIZE - widthOffset / 2f,
                bottom = ImageCropUseCase.CONTAINER_SIZE - heightOffset / 2f,
            )
        }
        .mutableSharedStateIn(
            scope = scope,
        )

    override val targetImageScaleObservableDto = MutableSharedStateFlow(
        initValue = 1f
    )

    override val cropRectObservableDto = initImageRectObservableDto
        .mutableSharedStateIn(
            scope = scope,
        )

    override val imageOffsetObservableDto = MutableSharedStateFlow(initValue = Offset.Zero)

    override fun applyImageScaleAndMove(
        scaleOffset: Float,
        moveOffset: Offset,
    ) {
        scope.launch(context = ErrorIgnoreContext) {
            val oldImageScale = targetImageScaleObservableDto.first()
            val oldImageOffset = imageOffsetObservableDto.first()
            targetImageScaleObservableDto.emit(
                oldImageScale * scaleOffset
            )
            imageOffsetObservableDto.emit(
                value = oldImageOffset + moveOffset
            )
        }
    }

    override fun adjust() {
        scope.launch(context = ErrorIgnoreContext) {
            // 先处理大小, 如果图片比裁剪的框框小了, 则缩放一下
            val originImagePosition = initImageRectObservableDto.first()
            val imageScale = targetImageScaleObservableDto.first()
            val cropRect = cropRectObservableDto.first()
            val minScaleWidth = cropRect.width / originImagePosition.width
            val minScaleHeight = cropRect.height / originImagePosition.height
            if (imageScale < minScaleWidth || imageScale < minScaleHeight) {
                targetImageScaleObservableDto.emit(
                    value = maxOf(minScaleWidth, minScaleHeight)
                )
            }
        }
    }

}