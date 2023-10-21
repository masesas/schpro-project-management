package com.schpro.project.presentation.detailProject

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.schpro.project.R
import com.schpro.project.core.base.BaseFragment
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.setIconColor
import com.schpro.project.core.extension.toast
import com.schpro.project.core.widget.SprintDialog
import com.schpro.project.core.widget.confirmationDialog
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Sprint
import com.schpro.project.data.models.User
import com.schpro.project.databinding.FragmentDetailProjectBinding
import com.schpro.project.presentation.detailProject.viewModel.DetailProjectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProjectFragment :
    BaseFragment<FragmentDetailProjectBinding, DetailProjectViewModel>(FragmentDetailProjectBinding::inflate) {

    private val args: DetailProjectFragmentArgs by navArgs()

    private val teams = mutableListOf<User>()
    private var sprints = mutableListOf<Sprint>()

    private val menuHost: MenuHost by lazy {
        requireActivity()
    }

    private val teamAdapter by lazy {
        object : BaseRecyclerViewAdapter<User>(
            R.layout.item_anggota,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<View>(R.id.tv_role).visibility = View.VISIBLE
                    findViewById<View>(R.id.img_delete).visibility = View.GONE

                    findViewById<TextView>(R.id.tv_username).text = item.username
                    findViewById<TextView>(R.id.tv_role).text = item.role.optionalName
                }
            }
        ) {}
    }

    private val sprintAdapter by lazy {
        object : BaseRecyclerViewAdapter<Sprint>(
            R.layout.item_sprint,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                    findViewById<View>(R.id.img_delete).setOnClickListener {
                        viewModel.deleteSprint(item)
                    }

                    setOnClickListener {
                        findNavController().navigate(
                            R.id.fragment_detail_sprint,
                            DetailSprintFragmentArgs.Builder(item)
                                .build()
                                .toBundle()
                        )
                    }
                }
            }
        ) {}
    }

    private val sprintDialog: SprintDialog by lazy {
        SprintDialog(requireContext(), parentFragmentManager).apply {
            setTitle("Add Sprint")
            setButtonSave { dialog, name, rangeDate ->
                if (name.isEmpty() || rangeDate.isEmpty()) {
                    toast("judul dan tanggal harus di isi")
                    return@setButtonSave
                }

                val sprint = Sprint().apply {
                    title = name
                    date = rangeDate
                }

                viewModel.saveSprint(sprint)
            }
        }
    }

    override fun initViews() {
        components()
        observe()
        backPressed()
    }

    override fun getViewModelClass() = DetailProjectViewModel::class.java

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        viewModel.getSession { user ->
            viewModel.getSprintList(args.projectId)
            viewModel.getDetailProject(args.projectId)
        }

        viewModel.detailProject.observe(viewLifecycleOwner) { detailProject ->
            if (detailProject != null) {
                when (detailProject) {
                    is UiState.Loading -> showProgress()
                    is UiState.Success -> {
                        hideProgress()
                        teams.clear()
                        val data = detailProject.data

                        binding.tvTitle.text = data.title
                        binding.tvDesc.text = data.description
                        binding.tvDueDate.text = data.dueDate
                        binding.tvPercentage.text = "0%"
                        binding.progress.progress = 0

                        data.byUser?.let {
                            teams.add(0, it)
                        }
                        teams.addAll(data.members)

                        teamAdapter.submitList(teams.take(5).toMutableList())
                        teamAdapter.notifyDataSetChanged()

                        binding.tvViewAllTeams.visibility =
                            if (teams.size > 5) View.VISIBLE else View.GONE

                        viewModel.getSession { user ->
                            if (user != null) {
                                if (user.role == Roles.ProjectManager) {
                                    binding.btnAddSprint.visibility = View.VISIBLE
                                    menuHost.managerMenu(data)
                                } else {
                                    binding.btnAddSprint.visibility = View.GONE
                                    menuHost.teamMenu(data)
                                }
                            }
                        }
                    }

                    is UiState.Failure -> {
                        hideProgress()
                        toast("Fail to load project")
                    }
                }
            } else {
                toast("Fail to load project")
            }
        }

        viewModel.sprintList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbSprint.visibility = View.VISIBLE
                    binding.tvEmptySprint.visibility = View.GONE
                }

                is UiState.Failure -> {
                    binding.pbSprint.visibility = View.GONE
                    binding.tvEmptySprint.visibility = View.VISIBLE
                    //toast(state.message)
                }

                is UiState.Success -> {
                    binding.pbSprint.visibility = View.GONE
                    binding.tvEmptySprint.visibility = View.GONE

                    sprints = state.data.toMutableList()
                    sprintAdapter.submitList(sprints)
                    sprintAdapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.saveSprint.observe(viewLifecycleOwner) { state ->
            state?.run {
                when (this) {
                    is UiState.Loading -> showProgress()
                    is UiState.Failure -> {
                        hideProgress()
                        toast(message)
                    }

                    is UiState.Success -> {
                        hideProgress()
                        toast(data)
                        sprintDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun components() {
        binding.rvTeam.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = teamAdapter
        }

        binding.rvSprint.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sprintAdapter
        }

        binding.tvViewAllTeams.setOnClickListener {
            teamAdapter.submitList(teams)
            teamAdapter.notifyDataSetChanged()
        }

        binding.btnAddSprint.setOnClickListener {
            sprintDialog.show()
        }
    }

    private fun MenuHost.managerMenu(project: Project) {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menu.clear()
                menuInflater.inflate(R.menu.manager_project_menu, menu)
                menu.setIconColor(requireContext(), R.id.update_project)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.update_project) {
                    val args = UpdateProjectFragmentArgs
                        .Builder(project)
                        .setProject(project)
                        .build()
                        .toBundle()

                    findNavController().navigate(R.id.fragment_update_project, args)
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun MenuHost.teamMenu(project: Project) {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menu.clear()
                menuInflater.inflate(R.menu.team_project_menu, menu)
                menu.setIconColor(requireContext(), R.id.exit_project)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.exit_project) {
                    confirmationDialog(
                        requireContext(),
                        actionOk = { dialog ->
                            dialog.dismiss()
                        }
                    )
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}