package com.schpro.project.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.schpro.project.R.color
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(private val bindingInflater: (inflater: LayoutInflater) -> VB) :
    Fragment() {

    private lateinit var _binding: VB

    protected lateinit var viewModel: VM

    private val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVM = type.actualTypeArguments[1] as Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater)
        viewModel = createViewModelLazy(classVM.kotlin, { viewModelStore }).value
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        hideProgress()
    }

    private fun init() {
        this.viewModel = if (useSharedViewModel) {
            ViewModelProvider(requireActivity())[getViewModelClass()]
        } else {
            ViewModelProvider(this)[getViewModelClass()]
        }
    }

    internal fun notify(message: String) =
        Snackbar.make(activityNavContainer(), message, Snackbar.LENGTH_SHORT).show()

    internal fun notifyWithAction(
        message: Int,
        actionText: Int,
        action: () -> Any
    ) {
        val snackBar = Snackbar.make(activityNavContainer(), message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction(actionText) { _ -> action.invoke() }
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                activity?.applicationContext!!,
                color.black
            )
        )
        snackBar.show()
    }

    private fun activityNavContainer() = (activity as BaseActivity).navContainer()

    internal fun showProgress() = progressStatus(View.VISIBLE)

    internal fun hideProgress() = progressStatus(View.GONE)

    internal fun toolbar() = (activity as BaseActivity).toolbar()

    internal fun showBottomNav() {
        (activity as BaseActivity).bottomNavContainer().visibility = View.VISIBLE
    }

    internal fun hideBottomNav() {
        (activity as BaseActivity).bottomNavContainer().visibility = View.GONE
    }

    internal fun showProgressDialog() = (activity as BaseActivity).showProgressDialog()

    internal fun hideProgressDialog() = (activity as BaseActivity).hideProgressDialog()

    open fun backPressed(enabled: Boolean = false, action: (() -> Unit)? = null) {
        if (activity != null) {
            requireActivity()
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(enabled) {
                    override fun handleOnBackPressed() {
                        if (action != null) {
                            action.invoke()
                        } else {
                            requireActivity().finish()
                        }
                    }
                })
        }
    }

    private fun progressStatus(viewStatus: Int) =
        with(activity) { if (this is BaseActivity) this.progressBar().visibility = viewStatus }

    protected val binding get() = _binding

    open var useSharedViewModel: Boolean = false

    protected abstract fun initViews()

    protected abstract fun getViewModelClass(): Class<VM>

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.clear()
        println("on destory fragment")
    }
}