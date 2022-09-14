package com.xiaojinzi.module.common.tx.cos

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged

@ModuleAppAnno
class TxCosModuleApplication : IApplicationLifecycle, IModuleNotifyChanged {

    override fun onModuleChanged(app: Application) {
    }

    override fun onCreate(app: Application) {
    }

    override fun onDestroy() {
    }

}