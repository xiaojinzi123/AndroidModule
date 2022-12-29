package com.xiaojinzi.module.common.base.spi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.sharedStateIn
import kotlinx.coroutines.flow.Flow

interface NetworkSpi {

    /**
     * 订阅网络是否链接
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isNetworkConnectedEventDto: Flow<Boolean>

    /**
     * 订阅网络是否链接
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isNetworkConnectedStateDto: Flow<Boolean>

    /**
     * 当前网络是否链接
     */
    val isNetworkConnected: Boolean

}

@ServiceAnno(NetworkSpi::class)
class NetworkSpiImpl : NetworkSpi {

    override val isNetworkConnectedEventDto = NormalMutableSharedFlow<Boolean>()

    override val isNetworkConnectedStateDto = isNetworkConnectedEventDto.sharedStateIn(
        initValue = false,
        scope = AppScope,
    )

    override val isNetworkConnected: Boolean
        get() = isNetworkConnectedStateDto.value

    init {

        val systemService =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val networkRequest = NetworkRequest.Builder()
                .build()
            systemService.registerNetworkCallback(
                networkRequest,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        isNetworkConnectedEventDto.tryEmit(value = true)
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        isNetworkConnectedEventDto.tryEmit(value = false)
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        isNetworkConnectedEventDto.tryEmit(value = false)
                    }

                })
        } else {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            class NetworkBroadcastReceiver : BroadcastReceiver() {
                override fun onReceive(
                    context: Context?,
                    intent: Intent?
                ) {
                    var isConnected = false
                    val activeNetworkInfo = systemService.activeNetworkInfo
                    if (activeNetworkInfo != null) {
                        isConnected = activeNetworkInfo.isAvailable
                    }
                    isNetworkConnectedEventDto.tryEmit(value = isConnected)
                }
            }
            app.registerReceiver(NetworkBroadcastReceiver(), intentFilter)
        }

    }

}