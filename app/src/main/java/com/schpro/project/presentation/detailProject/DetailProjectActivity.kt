package com.schpro.project.presentation.detailProject

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.schpro.project.R
import com.schpro.project.core.base.BaseActivity
import com.schpro.project.core.extension.changeStatusBarColor
import com.schpro.project.core.extension.serializable
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class DetailProjectActivity : BaseActivity() {

    companion object {
        fun callingIntent(context: Context, params: Args) {
            val intent = Intent(context, DetailProjectActivity::class.java)
            intent.putExtra(DetailProjectActivity::class.java.name, params)
            context.startActivity(intent)
        }

        data class Args(
            val projectId: String
        ) : Serializable
    }

    override fun navGraph(): Int = R.navigation.detail_project_nav

    override fun initViews() {
        binding.bottomNavBar.visibility = View.GONE
        changeStatusBarColor(getColor(R.color.black), true)
        navController.addOnDestinationChangedListener { controller, destination, args ->
            when (destination.id) {
                R.id.fragment_detail_project -> {
                    toolbar(
                        true,
                        "Detail Project",
                        centered = false,
                        showBackButton = true
                    )
                }

                R.id.fragment_update_project -> toolbar(
                    true,
                    "Add Project",
                    centered = false,
                    showBackButton = true
                )

                R.id.fragment_detail_sprint -> toolbar(
                    true,
                    "Detail Sprint",
                    centered = false,
                    showBackButton = true
                )
            }
        }
    }

    override fun initNavHost() {
        val projectId = intent.extras?.serializable<Args>(this::class.java.name)
        val args = DetailProjectFragmentArgs.Builder()
            .setProjectId(projectId?.projectId ?: "")
            .build()
            .toBundle()

        navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navHost.navController.graph = navHost.navController.navInflater.inflate(navGraph())
        navController = navHost.navController
        navController.setGraph(navGraph(), args)
    }
}