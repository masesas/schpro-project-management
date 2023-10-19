package com.schpro.project.presentation.auth

import android.content.Context
import android.content.Intent
import com.schpro.project.R
import com.schpro.project.core.base.BaseActivity
import com.schpro.project.core.extension.serializable
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    companion object {
        fun callingIntent(context: Context, params: Args) {
            val intent = Intent(context, AuthActivity::class.java)
            intent.putExtra(AuthActivity::class.java.name, params)
            context.startActivity(intent)
        }

        enum class Nav {
            Login, Registrasi,
        }

        data class Args(
            val initial: Nav
        ) : Serializable
    }

    override fun navGraph(): Int = R.navigation.auth_navigation

    override fun initViews() {
        incomingIntent()
    }

    private fun incomingIntent() {
        intent.serializable<Args>(this::class.java.name)?.run {
            when (initial) {
                Nav.Login -> navController.navigate(R.id.fragment_login)
                Nav.Registrasi -> navController.navigate(R.id.fragment_registrasi)

            }
        }
    }
}