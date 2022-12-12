package com.xiaojinzi.module.common.base.spi

import android.content.Context
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.support.ktx.app

interface SPSpi {

    companion object {
        const val DEFAULT_FILE_NAME = "SP_DEFAULT"
    }

    fun contains(fileName: String = DEFAULT_FILE_NAME, key: String): Boolean

    fun getString(
        fileName: String = DEFAULT_FILE_NAME,
        key: String,
        defValue: String? = null
    ): String?

    fun putString(fileName: String = DEFAULT_FILE_NAME, key: String, value: String?)

    fun getBool(
        fileName: String = DEFAULT_FILE_NAME,
        key: String,
        defValue: Boolean? = null
    ): Boolean?

    fun putBool(fileName: String = DEFAULT_FILE_NAME, key: String, value: Boolean?)

    fun getInt(fileName: String = DEFAULT_FILE_NAME, key: String, defValue: Int? = null): Int?

    fun putInt(fileName: String = DEFAULT_FILE_NAME, key: String, value: Int?)

    fun getLong(fileName: String = DEFAULT_FILE_NAME, key: String, defValue: Long? = null): Long?

    fun putLong(fileName: String = DEFAULT_FILE_NAME, key: String, value: Long?)

    /**
     * 移除所有
     */
    fun removeAll(fileName: String = DEFAULT_FILE_NAME)

}