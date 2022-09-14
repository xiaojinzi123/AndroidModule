package com.xiaojinzi.module.common.tx.cos

import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials
import com.tencent.qcloud.core.auth.SessionQCloudCredentials
import com.xiaojinzi.module.common.base.support.CommonServices
import com.xiaojinzi.support.ktx.notSupportError
import kotlinx.coroutines.runBlocking


class MySessionCredentialProvider : BasicLifecycleCredentialProvider() {

    override fun fetchNewCredentials(): QCloudLifecycleCredentials {

        val info = runBlocking {
            CommonServices.txCosCredentialGetService?.getInfo()
        } ?: notSupportError()

        // 最后返回临时密钥信息对象
        return SessionQCloudCredentials(
            info.secretId, info.secretKey,
            info.sessionToken, info.startTime, info.expiredTime
        )

    }

}