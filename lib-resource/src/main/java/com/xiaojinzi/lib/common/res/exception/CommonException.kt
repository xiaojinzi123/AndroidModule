package com.xiaojinzi.lib.common.res.exception

import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.toStringItemDto

/**
 * 表示业务异常
 */
open class CommonBusinessException(
    val messageRsd: Int? = null,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {

    val messageStringItem: StringItemDto?
        get() {
            return messageRsd?.toStringItemDto() ?: message?.toStringItemDto()
        }

}

/**
 * 表示不用管的异常
 */
class CommonIgnoreException(
    cause: Throwable? = null,
) : Exception(cause)
