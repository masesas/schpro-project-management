package com.schpro.project.presentation.detailProject

import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.TaskDialog
import com.schpro.project.databinding.FragmentDetailSprintBinding
import com.schpro.project.presentation.detailProject.viewModel.DetailSprintViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSprintFragment :
    BaseFragment<FragmentDetailSprintBinding, DetailSprintViewModel>(FragmentDetailSprintBinding::inflate) {

    private val args: DetailSprintFragmentArgs by navArgs()
    private val taskDialog: TaskDialog by lazy {
        TaskDialog(requireContext()).apply {
            setButtonAction("Submit") { dialog, task ->

            }
        }
    }

    override fun initViews() {
        components()
        observe()
    }

    override fun getViewModelClass() = DetailSprintViewModel::class.java

    private fun components() {
        args.sprint.run {
            binding.tvTitle.text = title
            binding.tvDate.text = date
        }

        binding.btnAddTask.setOnClickListener {
            taskDialog.show()
        }

        binding.tabsContainer.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        toast("${tab.position} ${tab.text.toString()}")
                    }

                    1 -> {
                        toast("${tab.position} ${tab.text.toString()}")
                    }

                    2 -> {
                        toast("${tab.position} ${tab.text.toString()}")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun observe() {

    }
}