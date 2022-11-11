package com.xiaojinzi.module.common.demo.module.main.domain

import com.xiaojinzi.module.common.base.usecase.CommonBaseUseCase
import com.xiaojinzi.module.common.base.usecase.CommonBaseUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCase
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCaseImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface MainUseCase : CommonBaseUseCase, CommonUseCase {
}

class MainUseCaseImpl(
    private val commonUseCase: CommonUseCase = CommonUseCaseImpl(),
) : CommonBaseUseCaseImpl(),
    CommonUseCase by commonUseCase,
    MainUseCase {

    override fun destroy() {
        super.destroy()
        commonUseCase.destroy()
    }

    override suspend fun initData() {
        super.initData()
        delay(timeMillis = 2000)
    }

    init {
    }

}