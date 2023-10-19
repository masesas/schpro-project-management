package com.schpro.project.core.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.NavigationRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.schpro.project.R
import com.schpro.project.core.extension.changeStatusBarColor
import com.schpro.project.databinding.ActivityLayoutBinding

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var _navController: NavController
    private lateinit var _binding: ActivityLayoutBinding
    private lateinit var _navHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLayoutBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        toolbar()
        initNavHost()
        bottomNav()
        initViews()

        /*supportFragmentManager.addOnBackStackChangedListener {
            shouldDisplayHomeUp()
        }*/
    }

    protected open fun initNavHost() {
        _navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        _navHost.navController.graph = _navHost.navController.navInflater.inflate(navGraph())
        _navController = _navHost.navController
        _navController.setGraph(navGraph())
    }

    protected var navController
        get() = _navController
        set(value) {
            _navController = value
        }

    protected var navHost
        get() = _navHost
        set(value) {
            _navHost = value
        }

    protected val binding get() = _binding

    @NavigationRes
    abstract fun navGraph(): Int

    abstract fun initViews()

    fun progressBar() = _binding.toolbarContainer.progress

    fun navContainer() = _binding.navHost

    fun bottomNavContainer() = binding.bottomNavBar

    fun toolbar() = _binding.toolbarContainer.toolbar

    open fun toolbar(
        show: Boolean = false,
        title: String = "Schpro Apps",
        centered: Boolean = false,
        showBackButton: Boolean = false,
    ) {
        if (show) {
            _binding.toolbarContainer.toolbar.isTitleCentered = centered
            setSupportActionBar(_binding.toolbarContainer.toolbar)
            supportActionBar?.show()
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            changeStatusBarColor(getColor(R.color.black), true)

            supportActionBar?.title = title
            supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
            supportActionBar?.setDisplayShowHomeEnabled(showBackButton)

            binding.toolbarContainer.toolbar.setNavigationIconTint(getColor(R.color.white))
            binding.toolbarContainer.toolbar.setNavigationOnClickListener {
                super.onBackPressed()
            }
        } else {
            if (supportActionBar != null) {
                supportActionBar!!.hide()
            }

            _binding.toolbarContainer.toolbar.visibility = View.GONE
        }
    }

    open fun bottomNav(@MenuRes menu: Int? = null) {
        _binding.bottomNavBar.menu.clear()
        if (menu != null) {
            _binding.bottomNavBar.visibility = View.VISIBLE
            _binding.bottomNavBar.inflateMenu(menu)
        } else {
            _binding.bottomNavBar.visibility = View.GONE
        }
    }

    open fun shouldDisplayHomeUp(): Boolean {
        //Enable Up button only  if there are entries in the back stack
        var canBack = false
        try {
            canBack = supportFragmentManager.backStackEntryCount > 0
        } catch (ex: Exception) {
//            Log.e(getClass().getCanonicalName(), ex.getMessage());getMessage
        }
        if (canBack) {
            //drawerDisable()
        } else {
            //mainActivity.drawerEnable()
        }

        return canBack
    }
}