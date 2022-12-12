package com.xiaojinzi.module.common.base.spi

import com.google.gson.Gson
import com.xiaojinzi.module.common.base.support.CommonServices
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.notSupportError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * SharedPreferences 的持久化的接口
 */
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

// ==================== 下面是 SPSpi 接口及其实现的一个扩展. 方便使用 ====================

interface SpPersistenceConverter<T1, T2> {

    fun converter(value: T1): T2

    fun parse(value: T2): T1

}

inline fun <reified T> spPersistenceGetValue(
    key: String,
): T? {
    return (when (T::class) {
        Boolean::class -> {
            CommonServices.spService!!.getBool(
                key = key,
            )
        }
        String::class -> {
            CommonServices.spService!!.getString(
                key = key,
            )
        }
        Int::class -> {
            CommonServices.spService!!.getInt(
                key = key,
            )
        }
        Long::class -> {
            CommonServices.spService!!.getLong(
                key = key,
            )
        }
        else -> notSupportError()
    }) as T?
}

inline fun <reified T> spPersistenceSetValue(
    key: String,
    value: T?
) {
    when (T::class) {
        Boolean::class -> {
            CommonServices.spService!!.putBool(
                key = key,
                value = value as Boolean?,
            )
        }
        String::class -> {
            CommonServices.spService!!.putString(
                key = key,
                value = value as String?,
            )
        }
        Int::class -> {
            CommonServices.spService!!.putInt(
                key = key,
                value = value as Int?,
            )
        }
        Long::class -> {
            CommonServices.spService!!.putLong(
                key = key,
                value = value as Long?,
            )
        }
        else -> notSupportError()
    }
}

inline fun <reified T> MutableSharedStateFlow<T>.spPersistence(
    scope: CoroutineScope = AppScope,
    allowStateInitialized: Boolean = false,
    key: String,
    def: T,
): MutableSharedStateFlow<T> {
    val targetObservable = this
    if (!allowStateInitialized && targetObservable.isInit) {
        notSupportError()
    }
    scope.launch {
        val targetValue = spPersistenceGetValue<T>(
            key = key,
        ) ?: def
        targetObservable.emit(value = targetValue)
        targetObservable
            .onEach {
                spPersistenceSetValue<T>(
                    key = key,
                    value = it
                )
            }
            .collect()
    }
    return this
}

inline fun <T, reified V> MutableSharedStateFlow<T>.spConverterPersistence(
    scope: CoroutineScope = AppScope,
    allowStateInitialized: Boolean = false,
    key: String,
    def: T,
    spPersistenceConverter: SpPersistenceConverter<T, V>,
): MutableSharedStateFlow<T> {
    val targetObservable = this
    if (!allowStateInitialized && targetObservable.isInit) {
        notSupportError()
    }
    scope.launch {
        val targetValue = spPersistenceGetValue<V>(
            key = key,
        )?.let {
            spPersistenceConverter.parse(value = it)
        } ?: def
        targetObservable.emit(value = targetValue)
        targetObservable
            .onEach {
                spPersistenceSetValue<V>(
                    key = key,
                    value = it?.run {
                        spPersistenceConverter.converter(value = it)
                    }
                )
            }
            .collect()
    }
    return this
}

inline fun <reified T> MutableSharedStateFlow<T>.spObjectConverterPersistence(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T,
): MutableSharedStateFlow<T> {
    return spConverterPersistence(
        scope = scope,
        key = key,
        def = def,
        spPersistenceConverter = object : SpPersistenceConverter<T, String> {
            override fun converter(value: T): String {
                return Gson().toJson(value)
            }

            override fun parse(value: String): T {
                return Gson().fromJson(value, T::class.java)
            }
        }
    )
}