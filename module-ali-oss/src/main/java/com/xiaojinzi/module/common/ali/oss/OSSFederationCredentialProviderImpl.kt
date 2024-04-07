package com.xiaojinzi.module.common.ali.oss

import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken
import com.xiaojinzi.module.common.base.support.CommonServices
import com.xiaojinzi.support.ktx.notSupportError
import kotlinx.coroutines.runBlocking

class OSSFederationCredentialProviderImpl : OSSFederationCredentialProvider() {
    override fun getFederationToken(): OSSFederationToken {

        val info = runBlocking {
            CommonServices.aliOssCredentialGetService?.getInfo()
        } ?: notSupportError()

        // 最后返回临时密钥信息对象
        return OSSFederationToken(
            info.accessKeyId, info.accessKeySecret,
            info.securityToken, info.expiration,
        )

    }

}