package com.schpro.project.core.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.schpro.project.R
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.extension.datePicker
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.adapter.DropdownModel
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Status
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import com.schpro.project.databinding.DialogTaskBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDialog constructor(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val userSession: User? = null
) {

    private lateinit var dialog: AlertDialog
    private var dropdownMember = mutableListOf<DropdownModel<User>>()
    private var selectedAnggota = mutableListOf<User>()
    private var loadedTask = Task()

    private val simpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    private val binding: DialogTaskBinding by lazy {
        DialogTaskBinding.inflate(LayoutInflater.from(context))
    }

    private val selectedAnggotaAdapter by lazy {
        object : BaseRecyclerViewAdapter<User>(R.layout.item_anggota,
            bind = { item, holder, count, adapter ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_username).text = item.username

                    findViewById<View>(R.id.tv_role).visibility = View.GONE
                    if (userSession != null && userSession.role == Roles.ProjectTeam) {
                        findViewById<View>(R.id.img_delete).visibility = View.GONE
                    } else {
                        findViewById<View>(R.id.img_delete).visibility = View.VISIBLE
                    }


                    findViewById<View>(R.id.img_delete).setOnClickListener {
                        selectedAnggota.remove(item)
                        adapter.submitList(selectedAnggota)
                        adapter.notifyDataSetChanged()
                    }
                }
            }) {}.apply {
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

        binding.etStatus.visibility = View.GONE
        binding.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.chooseMember.setOnClickListener {
            DropdownBottomSheet(
                context, dropdownMember
            ) {
                if (!selectedAnggota.contains(it)) {
                    selectedAnggota.add(it)
                    selectedAnggotaAdapter.submitList(selectedAnggota)
                    selectedAnggotaAdapter.notifyDataSetChanged()
                } else {
                    context.toast("Anggota telah ditambahkan")
                }
            }.setTitle("Pilih Anggota").showActionButton(false).show()
        }

        binding.etTenggatTask.setOnClickListener {
            binding.etTenggatTask.datePicker(fragmentManager)
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
        loadedTask = task
        selectedAnggota.clear()
        selectedAnggota.addAll(task.members)
        binding.etStatus.visibility = View.VISIBLE

        binding.etTitle.setText(task.title)
        binding.etDeskripsi.setText(task.description)
        binding.etTenggatTask.setText(task.tenggatTask)
        binding.etStatus.setText(task.status.optionalName)

        selectedAnggotaAdapter.submitList(selectedAnggota)
        selectedAnggotaAdapter.notifyDataSetChanged()

        binding.btnDone.visibility = if (task.status == Status.Done) View.GONE
        else View.VISIBLE

    }

    fun setAnggotaList(anggota: List<User>) {
        dropdownMember = anggota.map {
            DropdownModel(it, valueBuilder = { user -> user.username }, subTitleBuilder = { "" })
        }.toMutableList()
    }

    fun hideChooseAnggota() {
        binding.chooseMember.visibility = View.GONE
    }

    fun addSelectedAnggota(user: List<User>) {
        selectedAnggota.clear()
        selectedAnggota.addAll(user)

        selectedAnggotaAdapter.submitList(selectedAnggota)
        selectedAnggotaAdapter.notifyDataSetChanged()
    }

    fun setButtonAction(buttonText: String, action: (AlertDialog, Task) -> Unit) {
        binding.btnDone.text = buttonText
        binding.btnDone.setOnClickListener {
            if (binding.etTitle.text.toString().isEmpty() || binding.etDeskripsi.text.toString()
                    .isEmpty() || binding.etTenggatTask.text.toString()
                    .isEmpty() || selectedAnggota.isEmpty()
            ) {
                context.toast("Lengkapi semua field")
                return@setOnClickListener
            }

            loadedTask.apply {
                title = binding.etTitle.text.toString()
                description = binding.etDeskripsi.text.toString()
                tenggatTask = binding.etTenggatTask.text.toString()
                members = selectedAnggota
            }
            action.invoke(dialog, loadedTask)
        }
    }

    fun dismiss() = if (dialog.isShowing) dialog.dismiss() else null

    fun clearFields(clearAnggota: Boolean = false) {
        binding.etTitle.setText("")
        binding.etTenggatTask.setText("")
        binding.etDeskripsi.setText("")
        binding.etStatus.setText("")

        if (clearAnggota) {
            selectedAnggota.clear()
            selectedAnggotaAdapter.submitList(selectedAnggota)
            selectedAnggotaAdapter.notifyDataSetChanged()
        }
    }

    fun disabledFields() {
        binding.etTitle.isEnabled = false
        binding.etTenggatTask.isEnabled = false
        binding.etDeskripsi.isEnabled = false
        binding.etStatus.isEnabled = false
    }

    fun show() {
        dialog.show()
    }
}