package com.schpro.project.core.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.schpro.project.R
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.data.models.User
import com.schpro.project.databinding.DialogTaskBinding

class TaskDialog constructor(
    private val context: Context
) {

    private lateinit var binding: DialogTaskBinding
    private lateinit var dialog: AlertDialog
    private lateinit var anggotaAdapter: BaseRecyclerViewAdapter<User>

    private var anggotaList = mutableListOf<User>()

    init {
        initDialog()
        initData()
    }

    private fun initDialog() {
        binding = DialogTaskBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = MaterialAlertDialogBuilder(context, R.style.TransparentDialog)
        dialogBuilder.setView(binding.root)
        dialog = dialogBuilder.create()

        binding.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        rvAnggota()
    }

    private fun rvAnggota() {
        anggotaAdapter = object : BaseRecyclerViewAdapter<User>(
            R.layout.item_anggota,
            bind = { item, holder, count, adapter ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_username).text = item.username

                    findViewById<View>(R.id.tv_role).visibility = View.GONE
                    findViewById<View>(R.id.img_delete).visibility = View.VISIBLE

                    findViewById<View>(R.id.img_delete).setOnClickListener {
                        anggotaList.remove(item)
                        adapter.submitList(anggotaList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        ) {}.apply {
            submitList(anggotaList)
        }

        binding.rvAnggota.apply {
            adapter = anggotaAdapter
        }
    }

    private fun initData() {

    }

    fun setButtonAction(title: String, action: () -> Unit) {
        binding.btnDone.text = title
        binding.btnDone.setOnClickListener {
            action.invoke()
        }
    }

    fun show() {
        dialog.show()
    }
}