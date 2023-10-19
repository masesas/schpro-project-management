package com.schpro.project.presentation.auth

import androidx.navigation.Navigation
import com.schpro.project.BuildConfig
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.harusIsi
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.DropdownBottomSheet
import com.schpro.project.core.widget.adapter.DropdownModel
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.request.RegistrasiRequest
import com.schpro.project.databinding.FragmentRegistrasiBinding
import com.schpro.project.presentation.auth.viewModel.RegistrasiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrasiFragment :
    BaseFragment<FragmentRegistrasiBinding, RegistrasiViewModel>(FragmentRegistrasiBinding::inflate) {

    override fun initViews() {
        components()
        observer()
        debugMode()
    }

    override fun getViewModelClass(): Class<RegistrasiViewModel> = RegistrasiViewModel::class.java

    private fun observer() {
        this.viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    showProgress()
                }

                is UiState.Success -> {
                    Navigation.findNavController(binding.root).popBackStack()
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.fragment_regist_success)
                }

                is UiState.Failure -> {
                    state.message?.run {
                        notify(this)
                    }
                }
            }

        }
    }

    private fun components() {
        binding.imgBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.btnDaftar.setOnClickListener {
            if (validation()) {
                this.viewModel.register(
                    RegistrasiRequest(
                        binding.etEmail.text.toString(),
                        binding.etUsername.text.toString(),
                        Roles.fromString(binding.etRole.text.toString()),
                        binding.etPassword.text.toString(),
                        binding.etPasswordConfirmation.text.toString()
                    )
                )
            }
        }
        binding.etRole.setOnClickListener {
            val items = Roles.values().map {
                DropdownModel<Roles>(
                    it,
                    { role -> role.optionalName },
                    { "" },
                    Roles.fromString(binding.etRole.text.toString()) == it
                )
            }
            DropdownBottomSheet(requireContext(), items.toMutableList()) { result ->
                binding.etRole.setText(result.optionalName)
            }
                .setTitle("Pilih Role")
                .showActionButton(false)
                .show()
        }
    }

    private fun validation(): Boolean {
        if (
            binding.etEmail.text.isNullOrEmpty()
            || binding.etUsername.text.isNullOrEmpty()
            //|| binding.etRole.text.isNullOrEmpty()
            || binding.etPassword.text.isNullOrEmpty()
            || binding.etPasswordConfirmation.text.isNullOrEmpty()
            || (binding.etPassword.text.toString() != binding.etPasswordConfirmation.text.toString())
            || !binding.cbAgree.isChecked
        ) {
            binding.tlEmail.harusIsi(binding.etEmail.text.isNullOrEmpty())
            binding.tlUsername.harusIsi(binding.etUsername.text.isNullOrEmpty())
            // binding.tlRole.harusIsi(binding.etRole.text.isNullOrEmpty())
            binding.tlPassword.harusIsi(binding.etPassword.text.isNullOrEmpty())
            binding.tlPasswordConfirmation.harusIsi(binding.etPasswordConfirmation.text.isNullOrEmpty())

            if (binding.etPassword.text.toString() != binding.etPasswordConfirmation.text.toString()) {
                binding.tlPasswordConfirmation.error = "Password konfirmasi tidak sama"
            }

            if (!binding.cbAgree.isChecked) {
                toast("Setujui syarat dan ketentuan")
                return false
            }

            toast("Lengkapi semua field")

            return false
        }

        return true
    }

    private fun debugMode() {
        if (BuildConfig.DEBUG) {
            binding.etEmail.setText("khesa.oto@gmail.com")
            binding.etUsername.setText("khesa")
            binding.etRole.setText("Project Manager")
            binding.etPassword.setText("123456789")
            binding.etPasswordConfirmation.setText("123456789")
            binding.cbAgree.isChecked = true
        }
    }
}