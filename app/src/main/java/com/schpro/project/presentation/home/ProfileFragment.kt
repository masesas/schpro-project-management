package com.schpro.project.presentation.home

import com.schpro.project.core.base.BaseFragment
import com.schpro.project.databinding.FragmentProfileBinding
import com.schpro.project.presentation.home.viewModel.ProfileViewModel
import com.schpro.project.presentation.overview.OverviewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(FragmentProfileBinding::inflate) {

    override fun initViews() {
        viewModel.getSession { user ->
            if (user != null) {
                binding.tvEmail.text = user.email
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout {
                OverviewActivity.callingIntent(requireActivity())
            }
        }
    }

    override fun getViewModelClass() = ProfileViewModel::class.java
}