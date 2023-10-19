package com.schpro.project.core.widget

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.schpro.project.R
import com.schpro.project.databinding.DialogKonfirmasiBinding

fun confirmationDialog(
    context: Context,
    actionOk: (AlertDialog) -> Unit,
    actionCancel: (() -> Unit)? = null
) {
    val binding = DialogKonfirmasiBinding.inflate(LayoutInflater.from(context))
    val builder = MaterialAlertDialogBuilder(context, R.style.TransparentDialog)
    builder.setView(binding.root)
    val dialog = builder.create()

    binding.btnKonfirmasi.setOnClickListener {
        actionOk.invoke(dialog)
    }
    binding.btnBatal.setOnClickListener {
        if (actionCancel != null) {
            actionCancel.invoke()
        } else {
            dialog.dismiss()
        }
    }

    dialog.show()
}