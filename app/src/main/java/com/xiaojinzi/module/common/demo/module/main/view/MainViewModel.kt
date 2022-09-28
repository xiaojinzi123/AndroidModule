package com.xiaojinzi.module.common.demo.module.main.view

import com.xiaojinzi.module.common.demo.module.main.domain.MainUseCase
import com.xiaojinzi.module.common.demo.module.main.domain.MainUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel

class MainViewModel(
    private val useCase: MainUseCase = MainUseCaseImpl(),
): BaseViewModel(), MainUseCase by useCase {
}