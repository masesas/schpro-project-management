package com.schpro.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.schpro.project.presentation.home.HomeActivity
import com.schpro.project.presentation.overview.OverviewActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) {
                HomeActivity.callingIntent(this);
            } else {
                toOverview()
            }
        }
    }

    private fun toOverview() {
        startActivity(Intent(this, OverviewActivity::class.java))
    }
}