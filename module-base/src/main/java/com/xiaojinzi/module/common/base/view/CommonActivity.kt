package com.xiaojinzi.module.common.base.view

import android.os.Bundle
import com.xiaojinzi.component.Component
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel

/**
 * 通用的 Module 的 Activity
 * 1. 实现 Component 的注入
 * 2. 默认的跳转的结束动画
 */
open class CommonActivity<VM : BaseViewModel> : BaseAct<VM>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Component.inject(this)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            com.xiaojinzi.common.lib.res.R.anim.none,
            com.xiaojinzi.common.lib.res.R.anim.bottom_out
        )
    }

}