package com.schpro.project.presentation.home

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.TaskDialog
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Status
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import com.schpro.project.databinding.FragmentDashboardBinding
import com.schpro.project.presentation.home.viewModel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment<FragmentDashboardBinding, DashboardViewModel>(FragmentDashboardBinding::inflate) {

    private lateinit var userSession: User
    private val projectList = mutableListOf<Project>()
    private val taskList = mutableListOf<Task>()

    private val taskDialog: TaskDialog by lazy {
        TaskDialog(requireContext(), parentFragmentManager, userSession).apply {
            hideChooseAnggota()
            disabledFields()
            setButtonAction("Mark as Done") { _, task ->
                task.apply {
                    status = Status.Done
                }

                viewModel.markAsDoneTask(task)
            }
        }
    }

    private val taskAdapter: BaseRecyclerViewAdapter<Task> by lazy {
        object : BaseRecyclerViewAdapter<Task>(
            R.layout.item_task,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<TextView>(R.id.tv_status).text = item.status.optionalName

                    setOnClickListener {
                        taskDialog.setTaskData(item)
                        taskDialog.show()
                    }
                }
            }
        ) {}
    }

    private val projectAdapter: BaseRecyclerViewAdapter<Project> by lazy {
        object : BaseRecyclerViewAdapter<Project>(
            R.layout.item_project_dashboard,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<LinearProgressIndicator>(R.id.progress).progress = 0
                    findViewById<TextView>(R.id.tv_percentage).text = "0%"
                }
            }
        ) {}
    }

    override fun initViews() {
        components()
        observer()
    }

    override fun getViewModelClass() = DashboardViewModel::class.java

    private fun components() {
        binding.rvTask.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
        binding.rvProject.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = projectAdapter
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun observer() {
        viewModel.getSession { user ->
            if (user != null) {
                userSession = user
                val role = user.role
                binding.tvHi.text = "Hi, ${user.username}"

                if (role == Roles.ProjectManager) {
                    binding.rvProject.visibility = View.VISIBLE
                    binding.rvTask.visibility = View.GONE
                    binding.tvTypeList.text = "Project Statistik"
                    binding.tvName.text = "My Project"
                    binding.containerCount.tvTitleCount1.text = "Total Project"
                    binding.containerCount.tvTitleCount3.text = "Complete"
                    binding.containerCount.imgCount2.setImageResource(R.drawable.ic_on_going_manager)
                } else {
                    binding.rvProject.visibility = View.GONE
                    binding.rvTask.visibility = View.VISIBLE
                    binding.tvName.text = "My Task"
                }

                viewModel.initial(user)
            } else {
                toast("data login tidak valid, silahkan login ulang")
            }
        }

        viewModel.countTaskState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                binding.containerCount.tvCount1.text = state.totalTaskTodo.toString()
                binding.containerCount.tvCount2.text = state.totalTaskOnGoing.toString()
                binding.containerCount.tvCount3.text = state.totalTaskDone.toString()
            }
        }

        viewModel.countProjectState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                binding.containerCount.tvCount1.text = state.totalProject.toString()
                binding.containerCount.tvCount2.text = state.totalProjectOnGoing.toString()
                binding.containerCount.tvCount3.text = state.totalProjectComplete.toString()
            }
        }

        viewModel.taskListState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                binding.tvEmptyTask.visibility = if (state.isEmpty()) View.VISIBLE else View.GONE

                taskList.clear()
                taskList.addAll(state)
                taskAdapter.submitList(taskList)
                taskAdapter.notifyDataSetChanged()
            }
        }

        viewModel.projectListState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                binding.tvEmptyTask.visibility = if (state.isEmpty()) View.VISIBLE else View.GONE
                projectList.clear()
                projectList.addAll(state)
                projectAdapter.submitList(projectList)
                projectAdapter.notifyDataSetChanged()
            }
        }

        viewModel.saveTaskState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                when (state) {
                    is UiState.Loading -> showProgressDialog()
                    is UiState.Success -> {
                        hideProgressDialog()
                        toast(state.data)
                        taskDialog.dismiss()
                    }

                    is UiState.Failure -> {
                        hideProgressDialog()
                        toast(state.message)
                    }
                }
            }
        }
    }
}