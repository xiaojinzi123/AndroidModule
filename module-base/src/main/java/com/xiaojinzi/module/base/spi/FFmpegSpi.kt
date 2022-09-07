package com.xiaojinzi.module.base.spi

interface FFmpegSpi {

    companion object {
        const val TAG = "FFmpegService"
    }

    /**
     * 执行命令
     */
    suspend fun executeCommand(command: String)

}