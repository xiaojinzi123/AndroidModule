package com.xiaojinzi.module.common.image.crop.view

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
        useCase.imageOffsetObservableDto
    ) { containerRatiom, imageOffset ->
        imageOffset.div(operand = containerRatiom)
    }

    val cropRectInitObservableVo = combine(
        viewContainerSizeObservableVo,
        useCase.cropRectObservableDto
    ) { viewContainerSize, cropRect ->
        Rect(
            left = cropRect.left * viewContainerSize.width / ImageCropUseCase.CONTAINER_SIZE,
            right = cropRect.right * viewContainerSize.width / ImageCropUseCase.CONTAINER_SIZE,
            top = cropRect.top * viewContainerSize.height / ImageCropUseCase.CONTAINER_SIZE,
            bottom = cropRect.bottom * viewContainerSize.height / ImageCropUseCase.CONTAINER_SIZE,
        )
    }

}