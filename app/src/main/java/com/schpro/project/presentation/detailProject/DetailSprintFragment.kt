package com.schpro.project.presentation.detailProject

import com.schpro.project.core.base.BaseFragment
import com.schpro.project.databinding.FragmentDetailSprintBinding
import com.schpro.project.presentation.detailProject.viewModel.DetailSprintViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSprintFragment :
    BaseFragment<FragmentDetailSprintBinding, DetailSprintViewModel>(FragmentDetailSprintBinding::inflate) {
    override fun initViews() {

    }

    override fun getViewModelClass() = DetailSprintViewModel::class.java
}