package com.schpro.project.core.injection

import android.app.Activity
import androidx.fragment.app.FragmentManager
import com.schpro.project.core.base.BaseActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object BaseActivityModule {

    @Provides
    fun provideBaseActivity(activity: Activity): BaseActivity {
        check(activity is BaseActivity) {
            "Every Activity is expected to extend BaseActivity"
        }

        return activity
    }

    @Provides
    fun provideFragmentManager(baseActivity: BaseActivity): FragmentManager {
        return baseActivity.supportFragmentManager
    }
}