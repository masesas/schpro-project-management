package com.schpro.project.presentation.home

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.DropdownBottomSheet
import com.schpro.project.core.widget.adapter.DropdownModel
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.User
import com.schpro.project.databinding.FragmentAddProjectBinding
import com.schpro.project.presentation.home.viewModel.AddProjectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProjectFragment :
    BaseFragment<FragmentAddProjectBinding, AddProjectViewModel>(FragmentAddProjectBinding::inflate) {

    private var userSession = User()
    private var dropdownMember = mutableListOf<DropdownModel<User>>()
    private var loadedAnggota = mutableListOf<User>()
    private var selectedAnggota = mutableListOf<User>()
    private var projectParams = Project()

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

    override fun initViews() {
        components()
        observe()
    }

    override fun getViewModelClass(): Class<AddProjectViewModel> = AddProjectViewModel::class.java

    private fun observe() {
        viewModel.getSession { user ->
            if (user != null) {
                projectParams.apply {
                    byUser = user
                }

                viewModel.getUserProjectTeam()
            } else {
                toast("Login tidak valid, silahkan logout dan login kembali")
            }
        }

        viewModel.saveProject.observe(viewLifecycleOwner) { state ->
            state?.let {
                when (it) {
                    is UiState.Loading -> showProgress()
                    is UiState.Success -> {
                        if (projectParams.id.isEmpty()) {
                            clearForm()
                        }
                        hideProgress()
                        toast(it.data.second)
                    }

                    is UiState.Failure -> toast(it.message)
                }
            }
        }

        viewModel.usersProjectTeam.observe(viewLifecycleOwner) {
            loadedAnggota = it.toMutableList()
            dropdownMember = loadedAnggota.map { anggota ->
                DropdownModel(
                    anggota,
                    valueBuilder = { user -> user.username },
                    subTitleBuilder = { "" }
                )
            }.toMutableList()
        }
    }

    private fun components() {
        projectParams = Project()
        binding.chooseMember.setOnClickListener {
            DropdownBottomSheet(
                requireContext(),
                dropdownMember
            ) {
                if (!selectedAnggota.contains(it)) {
                    selectedAnggota.add(it)
                    selectedAnggotaAdapter.submitList(selectedAnggota)
                    selectedAnggotaAdapter.notifyDataSetChanged()
                } else {
                    toast("Anggota telah ditambahkan")
                }
            }
                .setTitle("Pilih Anggota")
                .showActionButton(false)
                .show()
        }

        binding.btnSave.setOnClickListener {
            if (validation()) {
                projectParams.apply {
                    title = binding.etTitle.text.toString().trim()
                    description = binding.etDescription.text.toString().trim()
                    dueDate = binding.etDueDate.text.toString().trim()
                    members = selectedAnggota
                }

                viewModel.saveProject(projectParams)
            }
        }

        rvSelectedAnggota()
    }

    private fun rvSelectedAnggota() {
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectedAnggotaAdapter
        }
    }

    private fun validation(): Boolean {
        if (
            binding.etTitle.text.toString().isEmpty()
            || binding.etDescription.text.toString().isEmpty()
            || binding.etDueDate.text.toString().isEmpty()
            || selectedAnggota.isEmpty()
        ) {
            toast("Lengkapi semua field")
            return false
        }

        return true
    }

    private fun clearForm() {
        binding.etTitle.setText("")
        binding.etDescription.setText("")
        binding.etDueDate.setText("")
        selectedAnggota = mutableListOf()

        selectedAnggotaAdapter.submitList(selectedAnggota)
        selectedAnggotaAdapter.notifyDataSetChanged()
    }
}