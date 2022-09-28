package com.xiaojinzi.lib.common.res.exception

/**
 * 表示业务异常
 */
open class CommonBusinessException(
    val messageRsd: Int? = null,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * 表示不用管的异常
 */
class CommonIgnoreException(
    cause: Throwable? = null,
) : Exception(cause)
