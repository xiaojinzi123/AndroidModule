package com.xiaojinzi.module.common.base.interceptor

import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.component.impl.RouterInterceptor.Chain
import com.xiaojinzi.component.impl.RouterResult

open class BottomSlideInAnimInterceptor : RouterInterceptor {

    override suspend fun intercept(chain: Chain): RouterResult {

        val originRequest = chain.request()
        val rawActivity = originRequest.rawAliveActivity
        val originAfterAction = originRequest.afterStartActivityAction
        val request = originRequest
            .toBuilder()
            .afterStartActivityAction {
                originAfterAction?.invoke()
                rawActivity?.overridePendingTransition(
                    com.xiaojinzi.lib.common.res.R.anim.bottom_in,
                    com.xiaojinzi.lib.common.res.R.anim.none
                )
            }
            .build()
        return chain.proceed(request)

    }

}