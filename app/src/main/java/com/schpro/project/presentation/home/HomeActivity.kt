package com.schpro.project.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.schpro.project.R
import com.schpro.project.core.base.BaseActivity
import com.schpro.project.core.extension.toast
import com.schpro.project.data.models.Roles
import com.schpro.project.presentation.home.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    val viewModel: HomeViewModel by viewModels()
    var doubleBackPressedClick = false

    companion object {
        fun callingIntent(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun navGraph(): Int = R.navigation.home_nav

    override fun initViews() {
        toolbar(true)

        // only emit this when in root nav
        onBackPressedDispatcher
            .addCallback(
                this,
                object : OnBackPressedCallback(binding.bottomNavBar.visibility == View.VISIBLE) {
                    override fun handleOnBackPressed() {
                        if (binding.bottomNavBar.visibility == View.VISIBLE) {
                            if (doubleBackPressedClick) {
                                exitProcess(0)
                            }

                            doubleBackPressedClick = true
                            toast("Tekan 2x Untuk Keluar Aplikasi")
                            Handler(Looper.getMainLooper()).postDelayed({
                                doubleBackPressedClick = false
                            }, 2000)
                        }
                    }
                })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_dashboard -> toolbar(true)
                R.id.fragment_add_project -> toolbar(true, "Add Project", centered = true)
                R.id.fragment_project -> toolbar(true, "My Project", centered = true)
                R.id.fragment_profile -> toolbar(true, "Profile", centered = true)
            }
        }
    }

    override fun bottomNav(menu: Int?) {
        viewModel.getSession { user ->
            if (user != null) {
                when (user.role) {
                    Roles.ProjectManager -> super.bottomNav(R.menu.manager_home_menu)
                    Roles.ProjectTeam -> super.bottomNav(R.menu.team_home_menu)
                }
            } else {
                super.bottomNav(R.menu.team_home_menu)
            }
        }

        binding.bottomNavBar.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) or super.onOptionsItemSelected(item)
    }
}