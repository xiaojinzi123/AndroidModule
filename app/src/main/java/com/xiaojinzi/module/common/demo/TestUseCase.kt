package com.xiaojinzi.module.common.demo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

interface TestUseCase {

    val nameFlow: Flow<String>
    val passwordFlow: Flow<String>
    // 是否可以继续下一步, 换句话说就是登录按钮是否可以点击
    // 这里的逻辑是, 当 name 和 password 都不为空的时候才可以点击
    val canNextFlow: Flow<Boolean>
    // 是否可以重置
    val canResetFlow: Flow<Boolean>

}

class TestUseCaseImpl: TestUseCase {

    override val nameFlow = MutableStateFlow(value = "")

    override val passwordFlow = MutableStateFlow(value = "")

    override val canNextFlow = combine(
        nameFlow, passwordFlow
    ) { name, password ->
        name.isNotEmpty() && password.isNotEmpty()
    }

    override val canResetFlow = combine(
        nameFlow, passwordFlow
    ) { name, password ->
        name.isNotEmpty() || password.isNotEmpty()
    }

}