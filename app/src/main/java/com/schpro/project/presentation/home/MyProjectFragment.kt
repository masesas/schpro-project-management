package com.schpro.project.presentation.home

import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toast
import com.schpro.project.data.models.Project
import com.schpro.project.databinding.FragmentMyProjectBinding
import com.schpro.project.presentation.detailProject.DetailProjectActivity
import com.schpro.project.presentation.home.viewModel.HomeViewModel
import com.schpro.project.presentation.home.viewModel.MyProjectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProjectFragment :
    BaseFragment<FragmentMyProjectBinding, MyProjectViewModel>(FragmentMyProjectBinding::inflate) {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private var projectList = mutableListOf<Project>()
    private val projectAdapter by lazy {
        object : BaseRecyclerViewAdapter<Project>(
            R.layout.item_project,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<TextView>(R.id.tv_pm_name).text =
                        item.projectManager?.username ?: ""
                    findViewById<TextView>(R.id.tv_project_due).text = item.dueDate

                    findViewById<View>(R.id.btn_detail).setOnClickListener {
                        DetailProjectActivity.callingIntent(
                            requireContext(),
                            DetailProjectActivity.Companion.Args(
                                projectId = item.id
                            )
                        )
                    }
                }
            }
        ) {}.apply {
            submitList(projectList)
        }
    }

    override fun initViews() {
        components()
        observe()
    }

    override fun getViewModelClass() = MyProjectViewModel::class.java

    private fun components() {
        rvProject()
    }

    private fun observe() {
        homeViewModel.getSession { user ->
            if (user != null) {
                viewModel.getProjectList(user)
            }
        }

        viewModel.projectList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    //showProgress()
                }

                is UiState.Success -> {
                    hideProgress()
                    projectList = state.data.toMutableList()
                    projectAdapter.submitList(projectList)
                    projectAdapter.notifyDataSetChanged()
                }

                is UiState.Failure -> {
                    toast(state.message)
                }
            }
        }
    }

    private fun rvProject() = binding.recyclerview.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = projectAdapter
    }
}