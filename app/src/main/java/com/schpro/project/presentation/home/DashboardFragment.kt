package com.schpro.project.presentation.home

import android.annotation.SuppressLint
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.data.models.Roles
import com.schpro.project.databinding.FragmentDashboardBinding
import com.schpro.project.presentation.home.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<FragmentDashboardBinding, HomeViewModel>(FragmentDashboardBinding::inflate) {

    override fun initViews() {
        observer()
    }

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java

    @SuppressLint("SetTextI18n")
    private fun observer() {
        viewModel.getSession { user ->
            if (user != null) {
                val role = user.role
                binding.tvHi.text = "Hi, ${user.username} ${user.role.optionalName}"

                if (role == Roles.ProjectManager) {
                    binding.containerCount.tvTitleCount1.text = "Total Project"
                    binding.containerCount.tvTitleCount3.text = "Complete"
                    binding.containerCount.imgCount2.setImageResource(R.drawable.ic_on_going_manager)
                } else {

                }
            }
        }

        binding.containerCount.tvCount1.text = 0.toString()
        binding.containerCount.tvCount2.text = 0.toString()
        binding.containerCount.tvCount3.text = 0.toString()
    }
}