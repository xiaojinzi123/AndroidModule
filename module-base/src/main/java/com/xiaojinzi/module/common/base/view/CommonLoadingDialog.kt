package com.xiaojinzi.module.common.base.view

import android.content.Context
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.xiaojinzi.module.common.base.databinding.CommonBaseLoadingDialogBinding
import com.xiaojinzi.support.ktx.viewBindings

class CommonLoadingDialog(context: Context) : AppCompatDialog(context) {

    private val viewBinding: CommonBaseLoadingDialogBinding by context.viewBindings()

    init {
        setCancelable(false)
        window?.setDimAmount(0f)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.attributes = window?.attributes?.apply {
            this.width = WindowManager.LayoutParams.WRAP_CONTENT
            this.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        setContentView(viewBinding.root)
    }

}