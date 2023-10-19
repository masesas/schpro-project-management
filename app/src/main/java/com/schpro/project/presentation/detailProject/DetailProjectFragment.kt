package com.schpro.project.presentation.detailProject

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
import com.schpro.project.core.widget.TaskDialog
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

    val args: DetailProjectFragmentArgs by navArgs()

    private val teams = mutableListOf<User>()
    private val sprints = mutableListOf<Sprint>()

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
        ) {}.apply {
            submitList(teams)
        }
    }

    private val sprintAdapter by lazy {
        object : BaseRecyclerViewAdapter<Sprint>(
            R.layout.item_sprint,
            bind = { item, holder, _, _ ->
                with(holder.itemView) {
                    findViewById<TextView>(R.id.tv_title).text = item.title
                }
            }
        ) {}.apply {
            submitList(sprints)
        }
    }

    override fun initViews() {
        components()
        observe()
        backPressed()
    }

    override fun getViewModelClass() = DetailProjectViewModel::class.java

    private fun observe() {
        viewModel.getDetailProject(args.projectId)
        viewModel.detailProject.observe(viewLifecycleOwner) { detailProject ->
            if (detailProject != null) {
                when (detailProject) {
                    is UiState.Loading -> showProgress()
                    is UiState.Success -> {
                        hideProgress()
                        val data = detailProject.data
                        teams.clear()
                        sprints.clear()

                        binding.tvTitle.text = data.title
                        binding.tvDesc.text = data.description
                        binding.tvDueDate.text = data.dueDate
                        binding.tvPercentage.text = "0%"
                        binding.progress.progress = 0

                        data.projectManager?.let {
                            teams.add(0, it)
                        }
                        teams.addAll(data.members)
                        sprints.addAll(data.sprints)

                        teamAdapter.submitList(teams.take(5).toMutableList())
                        teamAdapter.notifyDataSetChanged()

                        sprintAdapter.submitList(sprints)
                        sprintAdapter.notifyDataSetChanged()

                        binding.tvViewAllTeams.visibility =
                            if (teams.size > 5) View.VISIBLE else View.GONE
                        binding.tvEmptySprint.visibility =
                            if (sprints.isEmpty()) View.VISIBLE else View.GONE

                        viewModel.getSession { user ->
                            if (user != null) {
                                val menuHost: MenuHost = requireActivity()
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
    }

    private fun components() {
        val taskDialog = TaskDialog(requireContext())
        taskDialog.setButtonAction("Done") {
            toast("done")
        }

        rvTeams()
        rvSprints()

        binding.tvViewAllTeams.setOnClickListener {
            teamAdapter.submitList(teams)
            teamAdapter.notifyDataSetChanged()
        }

        binding.btnAddSprint.setOnClickListener {
            confirmationDialog(
                requireContext(),
                actionOk = { dialog ->
                    toast("ok")
                    dialog.dismiss()
                }
            )
        }
    }

    private fun rvTeams() = binding.rvTeam.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = teamAdapter
    }

    private fun rvSprints() = binding.rvSprint.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = sprintAdapter
    }

    private fun MenuHost.managerMenu(project: Project) {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
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
                menuInflater.inflate(R.menu.team_project_menu, menu)
                menu.setIconColor(requireContext(), R.id.exit_project)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.exit_project) {
                    toast("exit project")
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}