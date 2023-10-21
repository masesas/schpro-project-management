package com.schpro.project.core.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.schpro.project.R
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.adapter.DropdownModel
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import com.schpro.project.databinding.DialogTaskBinding

class TaskDialog constructor(
    private val context: Context
) {

    private lateinit var dialog: AlertDialog

    private var dropdownMember = mutableListOf<DropdownModel<User>>()
    private var selectedAnggota = mutableListOf<User>()

    private val binding: DialogTaskBinding by lazy {
        DialogTaskBinding.inflate(LayoutInflater.from(context))
    }

    private val selectedAnggotaAdapter by lazy {
        object : BaseRecyclerViewAdapter<User>(
            R.layout.item_anggota,
            bind = { item, holder, count, adapter ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_username).text = item.username

                    findViewById<View>(R.id.tv_role).visibility = View.GONE
                    findViewById<View>(R.id.img_delete).visibility = View.VISIBLE

                    findViewById<View>(R.id.img_delete).setOnClickListener {
                        selectedAnggota.remove(item)
                        adapter.submitList(selectedAnggota)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        ) {}.apply {
            submitList(selectedAnggota)
        }
    }

    init {
        initDialog()
    }

    private fun initDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(context, R.style.TransparentDialog)
        dialogBuilder.setView(binding.root)
        dialog = dialogBuilder.create()

        binding.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.chooseMember.setOnClickListener {
            DropdownBottomSheet(
                context,
                dropdownMember
            ) {
                if (!selectedAnggota.contains(it)) {
                    selectedAnggota.add(it)
                    selectedAnggotaAdapter.submitList(selectedAnggota)
                    selectedAnggotaAdapter.notifyDataSetChanged()
                } else {
                    context.toast("Anggota telah ditambahkan")
                }
            }
                .setTitle("Pilih Anggota")
                .showActionButton(false)
                .show()
        }

        rvAnggota()
    }

    private fun rvAnggota() {
        binding.rvAnggota.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectedAnggotaAdapter
        }
    }

    fun setTaskData(task: Task) {
        selectedAnggota.clear()
        selectedAnggota.addAll(task.members)

        binding.etTitle.setText(task.title)
        binding.etDeskripsi.setText(task.description)
        binding.etTenggatTask.setText(task.tenggatTask)
    }

    fun setAnggotaList(anggota: List<User>) {
        dropdownMember = anggota.map {
            DropdownModel(
                it,
                valueBuilder = { user -> user.username },
                subTitleBuilder = { "" }
            )
        }.toMutableList()
    }

    fun setButtonAction(title: String, action: (AlertDialog, Task) -> Unit) {
        binding.btnDone.text = title
        binding.btnDone.setOnClickListener {
            action.invoke(
                dialog, Task(
                    title = binding.etTitle.text.toString(),
                    description = binding.etDeskripsi.text.toString(),
                    tenggatTask = binding.etTenggatTask.text.toString(),
                    members = selectedAnggota
                )
            )
        }
    }

    fun show() {
        binding.etTitle.setText("")
        binding.etTenggatTask.setText("")
        binding.etDeskripsi.setText("")
        selectedAnggota.clear()

        dialog.show()
    }
}