package com.xiaojinzi.module.common.base.usecase

import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.lib.common.res.exception.CommonBusinessException
import com.xiaojinzi.module.common.base.view.CommonLoadingDialog
import com.xiaojinzi.module.common.base.view.compose.CommonAlertDialog
import com.xiaojinzi.module.common.base.view.compose.CommonErrorDataView
import com.xiaojinzi.module.common.base.view.compose.CommonInitDataView
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.NoError
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCase
import com.xiaojinzi.support.architecture.mvvm1.TipBean
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.compose.util.clickableNoRipple
import com.xiaojinzi.support.ktx.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// expect fun test(): String

@Keep
enum class ViewState {
    STATE_INIT,
    STATE_LOADING,
    STATE_ERROR,
    STATE_SUCCESS,
}

@Keep
enum class PageInitState {

    INIT_NORMAL,
    INIT_LOADING,
    INIT_SUCCESS,
    INIT_ERROR,
    ;

    fun toViewState(): ViewState {
        return when (this) {
            INIT_NORMAL -> ViewState.STATE_INIT
            INIT_LOADING -> ViewState.STATE_LOADING
            INIT_SUCCESS -> ViewState.STATE_SUCCESS
            INIT_ERROR -> ViewState.STATE_ERROR
        }
    }

}

@Keep
data class ShowConfirmDialog(
    val title: StringItemDto? = null,
    val content: StringItemDto? = null,
    val negative: StringItemDto? = null,
    val positive: StringItemDto? = null,
)

/**
 * 对话框的返回类型
 */
enum class ConfirmDialogResultType {
    CONFIRM,
    CANCEL,
}

interface CommonDialogUseCase : BaseUseCase {

    /**
     * 显示确认对话框的
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val confirmDialogObservableDto: MutableSharedStateFlow<ShowConfirmDialog?>

    /**
     * 确认的事件
     */
    @HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
    val confirmDialogResultEventObservableDto: MutableSharedFlow<ConfirmDialogResultType>

    /**
     * 确认对话框
     */
    suspend fun confirmDialog(
        title: StringItemDto? = null,
        content: StringItemDto,
        negative: StringItemDto? = null,
        positive: StringItemDto? = null,
    ): ConfirmDialogResultType {
        // 显示对话框
        confirmDialogObservableDto.emit(
            value = ShowConfirmDialog(
                title = title,
                content = content,
                negative = negative,
                positive = positive,
            )
        )
        return confirmDialogResultEventObservableDto.first()
    }

    suspend fun confirmDialogOrError(
        title: StringItemDto? = null,
        content: StringItemDto,
        negative: StringItemDto? = null,
        positive: StringItemDto? = null,
    ) {
        confirmDialog(
            title = title,
            content = content,
            negative = negative,
            positive = positive,
        ).apply {
            if (this != ConfirmDialogResultType.CONFIRM) {
                throw CommonBusinessException()
            }
        }
    }

    fun postConfirmDialog(
        title: StringItemDto? = null,
        content: StringItemDto,
        negative: StringItemDto? = null,
        positive: StringItemDto? = null,
    )

}

class CommonDialogUseCaseImpl : BaseUseCaseImpl(), CommonDialogUseCase {

    override val confirmDialogObservableDto =
        MutableSharedStateFlow<ShowConfirmDialog?>(initValue = null)

    override val confirmDialogResultEventObservableDto =
        NormalMutableSharedFlow<ConfirmDialogResultType>()

    override fun postConfirmDialog(
        title: StringItemDto?,
        content: StringItemDto,
        negative: StringItemDto?,
        positive: StringItemDto?
    ) {
        scope.launch(context = ErrorIgnoreContext) {
            confirmDialogObservableDto.emit(
                value = ShowConfirmDialog(
                    title = title,
                    content = content,
                    negative = negative,
                    positive = positive,
                )
            )
        }
    }

    init {
        confirmDialogResultEventObservableDto
            .onEach {
                confirmDialogObservableDto.emit(value = null)
            }
            .launchIn(scope = scope)
    }

}

interface CommonBaseUseCase : CommonDialogUseCase {

    companion object {
        const val TAG = "CommonBaseUseCase"
    }

    /**
     * 页面状态
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val pageInitStateObservableDto: MutableSharedStateFlow<PageInitState>

    /**
     * 初始化数据
     */
    @Throws(Exception::class)
    suspend fun initData()

    /**
     * 尝试初始化
     */
    fun retryInit()

    /**
     * 执行任务, 自带 Loadding
     * 需要自己的 usecase 实现 [CommonUseCase] 接口
     */
    @NoError
    fun executeJobWithLoading(job: suspend () -> Unit)

    @Throws(Exception::class)
    suspend fun blockingExecuteJobWithLoading(job: suspend () -> Unit)

    /**
     * 错误处理
     */
    fun errorHandle(error: Exception) {
        // empty
    }

}

open class CommonBaseUseCaseImpl(
    private val commonDialogUseCase: CommonDialogUseCase = CommonDialogUseCaseImpl(),
) : BaseUseCaseImpl(),
    CommonDialogUseCase by commonDialogUseCase,
    CommonBaseUseCase {

    override val pageInitStateObservableDto =
        MutableSharedStateFlow(initValue = PageInitState.INIT_NORMAL)

    @Throws(Exception::class)
    override suspend fun initData() {
    }

    final override fun retryInit() {
        scope.launch(context = ErrorIgnoreContext) {
            try {
                pageInitStateObservableDto.value = PageInitState.INIT_LOADING
                timeAtLeast {
                    initData()
                }
                pageInitStateObservableDto.emit(
                    value = PageInitState.INIT_SUCCESS
                )
            } catch (e: Exception) {
                pageInitStateObservableDto.emit(
                    value = PageInitState.INIT_ERROR
                )
            }
        }
    }

    @NoError
    override fun executeJobWithLoading(job: suspend () -> Unit) {
        scope.launch(ErrorIgnoreContext) {
            blockingExecuteJobWithLoading(job = job)
        }
    }

    override suspend fun blockingExecuteJobWithLoading(job: suspend () -> Unit) {
        val commonUseCase = this as? CommonUseCase
        try {
            commonUseCase?.showLoading(isShow = true)
            timeAtLeast {
                job.invoke()
            }
        } catch (error: Exception) {
            errorHandle(
                error = error,
            )
        } finally {
            commonUseCase?.showLoading(isShow = false)
        }
    }

    override fun destroy() {
        super.destroy()
        commonDialogUseCase.destroy()
    }

    init {
        retryInit()
    }

}

@Composable
inline fun <reified VM : ViewModel> CommonContentView(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .nothing(),
    needInit: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    initView: @Composable (modifier: Modifier) -> Unit = { innerModifier ->
        CommonInitDataView(
            modifier = innerModifier,
        )
    },
    errorView: @Composable (modifier: Modifier) -> Unit = { innerModifier ->
        CommonErrorDataView(
            modifier = innerModifier,
        )
    },
    noinline loadingDialog: @Composable (isShow: Boolean) -> Unit = { isShow ->
        val context = LocalContext.current
        val dialog by remember {
            mutableStateOf(value = CommonLoadingDialog(context = context))
        }
        if (isShow && !dialog.isShowing) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
        DisposableEffect(key1 = dialog) {
            onDispose {
                dialog.dismiss()
            }
        }
    },
    alertDialog: @Composable (
        dialogContent: ShowConfirmDialog,
        onDismissClick: () -> Unit,
        onConfirmClick: () -> Unit,
    ) -> Unit = { dialogContent, onDismissClick, onConfirmClick ->
        CommonAlertDialog(
            title = dialogContent.title,
            text = dialogContent.content,
            cancelText = dialogContent.negative,
            confirmText = dialogContent.positive,
            onDismissClick = onDismissClick,
            onConfirmClick = onConfirmClick,
        )
    },
    crossinline tipEvent: (tipContent: TipBean) -> Unit = { tipContent ->
        Toast.makeText(
            app,
            tipContent.content.contentWithContext(context = app),
            tipContent.toastLength,
        ).show()
    },
    noinline content: @Composable BoxScope.(vm: VM) -> Unit,
) {
    val vm: VM = viewModel()
    when (vm) {
        is CommonUseCase -> {
            val isShowLoading by vm.isLoadingObservable.collectAsState(initial = false)
            loadingDialog(
                isShow = isShowLoading
            )
        }
    }
    val viewState = when (vm) {
        is CommonBaseUseCase -> {
            val pageInitState by vm.pageInitStateObservableDto.collectAsState(initial = PageInitState.INIT_NORMAL)
            pageInitState.toViewState()
        }
        else -> {
            ViewState.STATE_SUCCESS
        }
    }
    val dialogContent = when (vm) {
        is CommonBaseUseCase -> {
            val dialogContent by vm.confirmDialogObservableDto.collectAsState(initial = null)
            dialogContent
        }
        else -> {
            null
        }
    }
    val context = LocalContext.current
    // 对 ui 控制的一些监听
    LaunchedEffect(key1 = Unit) {
        when (vm) {
            is CommonUseCase -> {
                vm.activityFinishEventObservable
                    .onEach {
                        context.tryFinishActivity()
                    }
                    .launchIn(scope = this)
                vm.tipObservable
                    .onEach { tipContent ->
                        tipEvent(tipContent)
                    }
                    .launchIn(scope = this)
            }
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        if (needInit) {
            when (viewState) {
                ViewState.STATE_INIT, ViewState.STATE_LOADING -> {
                    initView(
                        modifier = Modifier
                            .fillMaxSize()
                            .nothing(),
                    )
                }
                ViewState.STATE_ERROR -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickableNoRipple {
                                (vm as? CommonBaseUseCase)?.retryInit()
                            }
                            .nothing(),
                        contentAlignment = Alignment.Center,
                    ) {
                        errorView(
                            modifier = Modifier
                                .fillMaxSize()
                                .nothing(),
                        )
                    }
                }
                ViewState.STATE_SUCCESS -> {
                    content(vm = vm)
                }
            }
        } else {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                content(vm = vm)
            }
        }
    }
    dialogContent?.let {
        alertDialog(
            dialogContent = it,
            onDismissClick = {
                (vm as? CommonBaseUseCase)?.confirmDialogResultEventObservableDto?.tryEmit(
                    value = ConfirmDialogResultType.CANCEL
                )
            },
        ) {
            (vm as? CommonBaseUseCase)?.confirmDialogResultEventObservableDto?.tryEmit(
                value = ConfirmDialogResultType.CONFIRM
            )
        }
    }
}