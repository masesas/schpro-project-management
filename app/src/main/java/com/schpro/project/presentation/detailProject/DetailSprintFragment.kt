package com.schpro.project.presentation.detailProject

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.showPopupMenuMoveTask
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.TaskDialog
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Status
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import com.schpro.project.databinding.FragmentDetailSprintBinding
import com.schpro.project.presentation.detailProject.viewModel.DetailSprintViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSprintFragment :
    BaseFragment<FragmentDetailSprintBinding, DetailSprintViewModel>(FragmentDetailSprintBinding::inflate) {

    private val args: DetailSprintFragmentArgs by navArgs()

    private lateinit var userSession: User
    private val taskTodoList = mutableListOf<Task>()
    private val taskOnGoingList = mutableListOf<Task>()
    private val taskDoneList = mutableListOf<Task>()

    private val taskDialog: TaskDialog by lazy {
        TaskDialog(requireContext(), parentFragmentManager, userSession).apply {
            if (userSession.role == Roles.ProjectManager) {
                setAnggotaList(args.members.toList())
            } else {
                hideChooseAnggota()
            }

            setButtonAction("Submit") { dialog, task ->
                task.apply {
                    sprintId = args.sprint.id
                    projectId = args.sprint.projectId
                    byUser = userSession
                    status = Status.Todo
                }

                viewModel.saveTask(task)
            }
        }
    }

    private val taskTodoAdapter: BaseRecyclerViewAdapter<Task> by lazy {
        object : BaseRecyclerViewAdapter<Task>(
            R.layout.item_sprint_task,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<TextView>(R.id.tv_due_date).text = item.tenggatTask
                    findViewById<View>(R.id.btn_move_task).setOnClickListener {
                        findViewById<View>(R.id.btn_move_task).showPopupMenuMoveTask(item.status) { selected ->
                            item.apply {
                                status = selected
                            }
                            viewModel.updateTask(item)
                        }
                    }
                }
            }
        ) {}
    }

    private val taskOnGoingAdapter: BaseRecyclerViewAdapter<Task> by lazy {
        object : BaseRecyclerViewAdapter<Task>(
            R.layout.item_sprint_task,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<TextView>(R.id.tv_due_date).text = item.tenggatTask
                    findViewById<View>(R.id.btn_move_task).setOnClickListener {
                        findViewById<View>(R.id.btn_move_task).showPopupMenuMoveTask(item.status) { selected ->
                            item.apply {
                                status = selected
                            }
                            viewModel.updateTask(item)
                        }
                    }
                }
            }
        ) {}
    }

    private val taskDoneAdapter: BaseRecyclerViewAdapter<Task> by lazy {
        object : BaseRecyclerViewAdapter<Task>(
            R.layout.item_sprint_task,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<TextView>(R.id.tv_due_date).text = item.tenggatTask
                    findViewById<View>(R.id.btn_move_task).setOnClickListener {
                        findViewById<View>(R.id.btn_move_task).showPopupMenuMoveTask(item.status) { selected ->
                            item.apply {
                                status = selected
                            }
                            viewModel.updateTask(item)
                        }
                    }
                }
            }
        ) {}
    }

    override fun initViews() {
        components()
        observe()
    }

    override fun getViewModelClass() = DetailSprintViewModel::class.java

    private fun components() {
        binding.rvTaskTodo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskTodoAdapter
        }
        binding.rvTaskOnGoing.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskOnGoingAdapter
        }
        binding.rvTaskDone.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskDoneAdapter
        }

        binding.btnAddTask.setOnClickListener {
            if (userSession.role == Roles.ProjectTeam) {
                taskDialog.addSelectedAnggota(arrayListOf(userSession))
            }

            taskDialog.clearFields(userSession.role == Roles.ProjectManager)
            taskDialog.show()
        }

        binding.tabsContainer.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    // as Status.Todo
                    0 -> {
                        binding.rvTaskTodo.visibility = View.VISIBLE
                        binding.rvTaskOnGoing.visibility = View.GONE
                        binding.rvTaskDone.visibility = View.GONE
                        binding.btnAddTask.visibility = View.VISIBLE

                        viewModel.getTaskByStatus(args.sprint.id, Status.Todo)
                    }

                    // as Status.OnGoing
                    1 -> {
                        binding.rvTaskTodo.visibility = View.GONE
                        binding.rvTaskOnGoing.visibility = View.VISIBLE
                        binding.rvTaskDone.visibility = View.GONE
                        binding.btnAddTask.visibility = View.GONE

                        viewModel.getTaskByStatus(args.sprint.id, Status.OnGoing)
                    }

                    // as Status.Done
                    2 -> {
                        binding.rvTaskTodo.visibility = View.GONE
                        binding.rvTaskOnGoing.visibility = View.GONE
                        binding.rvTaskDone.visibility = View.VISIBLE
                        binding.btnAddTask.visibility = View.GONE

                        viewModel.getTaskByStatus(args.sprint.id, Status.Done)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        args.sprint.run {
            viewModel.getSprintDetail(id)
            viewModel.getTaskByStatus(id, Status.Todo)
            viewModel.getCountTask(id)
        }

        viewModel.getSession { user ->
            userSession = user!!
        }

        viewModel.sprintDetailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showProgress()
                is UiState.Success -> {
                    hideProgress()

                    val data = state.data
                    binding.tvTitle.text = data.title
                    binding.tvDate.text = data.date
                }

                is UiState.Failure -> {
                    hideProgress()
                }
            }
        }

        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                taskTodoList.clear()
                taskOnGoingList.clear()
                taskDoneList.clear()

                if (state.first == Status.Todo) {
                    binding.pbTask.visibility = View.GONE
                    taskTodoList.addAll(state.second)

                    taskTodoAdapter.submitList(taskTodoList)
                    taskTodoAdapter.notifyDataSetChanged()
                } else if (state.first == Status.OnGoing) {
                    binding.pbTask.visibility = View.GONE
                    taskOnGoingList.addAll(state.second)

                    taskOnGoingAdapter.submitList(taskOnGoingList)
                    taskOnGoingAdapter.notifyDataSetChanged()
                } else if (state.first == Status.Done) {
                    binding.pbTask.visibility = View.GONE
                    taskDoneList.addAll(state.second)

                    taskDoneAdapter.submitList(taskDoneList)
                    taskDoneAdapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.saveTaskState.observe(viewLifecycleOwner) { state ->
            if (state != null) {
                when (state) {
                    is UiState.Loading -> showProgressDialog()
                    is UiState.Success -> {
                        hideProgressDialog()
                        taskDialog.dismiss()
                        toast(state.data)
                    }

                    is UiState.Failure -> {
                        hideProgressDialog()
                        toast(state.message)
                    }
                }
            }
        }

        viewModel.countTaskState.observe(viewLifecycleOwner) { state ->
            binding.containerCount.tvCount1.text = state.totalTaskTodo.toString()
            binding.containerCount.tvCount2.text = state.totalTaskOnGoing.toString()
            binding.containerCount.tvCount3.text = state.totalTaskDone.toString()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun emitTask(data: List<Task>) {
        binding.pbTask.visibility = View.GONE
        taskTodoList.clear()
        taskTodoList.addAll(data)

        taskTodoAdapter.submitList(taskTodoList)
        taskTodoAdapter.notifyDataSetChanged()
    }
}