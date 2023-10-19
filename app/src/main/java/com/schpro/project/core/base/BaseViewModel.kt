package com.schpro.project.core.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.User
import com.schpro.project.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val _usersProjectTeam = MutableLiveData<List<User>>()

    val usersProjectTeam: LiveData<List<User>>
        get() = _usersProjectTeam

    fun getSession(result: (User?) -> Unit) = userRepository.getSession(result)

    fun getUserProjectTeam() {
        viewModelScope.launch {
            userRepository.getUserByRole(Roles.ProjectTeam).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        _usersProjectTeam.value = response.data.toList()
                    }

                    is Resource.Failure -> _usersProjectTeam.value = arrayListOf()
                }
            }

        }
    }
}

class SingleLiveData<T> : MutableLiveData<T?>() {
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        super.observe(owner) { t ->
            if (t != null) {
                observer.onChanged(t)
                postValue(null)
            }
        }
    }
}