package com.schpro.project.presentation.auth

import com.schpro.project.BuildConfig
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toast
import com.schpro.project.data.models.request.LoginRequest
import com.schpro.project.databinding.FragmentLoginBinding
import com.schpro.project.presentation.auth.viewModel.LoginViewModel
import com.schpro.project.presentation.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate) {

    override fun initViews() {
        components()
        observer()
        //debugMode()
        backPressed(true)
    }

    override fun getViewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java

    private fun components() {
        binding.imgBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnMasuk.setOnClickListener {
            if (validation()) {
                viewModel
                    .login(
                        LoginRequest(
                            binding.etEmail.text.toString(),
                            binding.etPassword.text.toString()
                        )
                    )
            }
        }
    }

    private fun observer() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showProgressDialog()
                is UiState.Success -> {
                    hideProgressDialog()
                    HomeActivity.callingIntent(requireContext())
                }

                is UiState.Failure -> {
                    hideProgressDialog()
                    state.message?.run {
                        notify(this)
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {
        val binding = binding!!
        if (
            binding.etEmail.text.isNullOrEmpty()
            || binding.etPassword.text.isNullOrEmpty()
        ) {
            toast("Lengkapi email dan password")
            return false
        }

        return true
    }

    private fun debugMode() {
        if (BuildConfig.DEBUG) {
            binding.etEmail.setText("khesa.oto@gmail.com")
            binding.etPassword.setText("123456789")
        }
    }
}