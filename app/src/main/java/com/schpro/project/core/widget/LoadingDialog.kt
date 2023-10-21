package com.schpro.project.core.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.schpro.project.R
import com.schpro.project.databinding.DialogLoadingBinding

class LoadingDialog(
    private val context: Context
) {

    private val binding by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(context))
    }
    private lateinit var dialog: AlertDialog

    init {
        initDialog()
    }

    private fun initDialog() {
        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        builder.setView(binding.root)
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setMessage(message: String = "Memuat ...") {
        binding.tvMessage.text = message
    }

    fun dismiss() = if (dialog.isShowing) dialog.dismiss() else null

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }
}