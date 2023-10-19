package com.schpro.project.presentation.auth

import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.databinding.FragmentRegistSuccessBinding
import com.schpro.project.presentation.auth.viewModel.RegistSuccessViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistSuccessFragment :
    BaseFragment<FragmentRegistSuccessBinding, RegistSuccessViewModel>(FragmentRegistSuccessBinding::inflate) {

    override fun initViews() {
        binding.btnLogin.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
            Navigation.findNavController(binding.root).navigate(R.id.fragment_login)
        }
        backPressed()
    }

    override fun getViewModelClass() = RegistSuccessViewModel::class.java
}