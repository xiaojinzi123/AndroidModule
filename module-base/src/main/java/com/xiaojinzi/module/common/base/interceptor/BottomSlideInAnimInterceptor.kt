package com.xiaojinzi.module.common.base.interceptor

import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.component.impl.RouterInterceptor.Chain
import com.xiaojinzi.module.base.R

open class BottomSlideInAnimInterceptor : RouterInterceptor {
  @Throws(Exception::class)
  override
  fun intercept(chain: Chain) {
    val originRequest = chain.request()
    val rawActivity = originRequest.rawActivity
    val originAfterAction = originRequest.afterStartAction
    val request = originRequest
        .toBuilder()
        .afterStartAction {
            originAfterAction?.invoke()
            rawActivity?.overridePendingTransition(com.xiaojinzi.lib.res.R.anim.bottom_in, com.xiaojinzi.lib.res.R.anim.none)
        }
        .build()
    chain.proceed(request)
  }
}