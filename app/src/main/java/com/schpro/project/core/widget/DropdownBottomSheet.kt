package com.schpro.project.core.widget

import android.content.Context
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.schpro.project.R
import com.schpro.project.core.base.BaseRecyclerViewAdapter
import com.schpro.project.core.widget.adapter.DropdownModel
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject


class DropdownBottomSheet<T : Any> constructor(
    private val context: Context,
    private val items: MutableList<DropdownModel<T>>,
    private val onItemClickListener: (T) -> Unit
) {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var view: View

    private lateinit var recyclerView: RecyclerView

    private var disposable = Disposable.empty()
    private val selectionSubject = BehaviorSubject.create<List<String>>()
    private var actionMode: ActionMode? = null
    /*private val dropdownAdapter: DropdownAdapter<T> by lazy {
        DropdownAdapter<T>(
            recyclerView = { recyclerView },
            itemClickListener = { item -> context.toast("$item clicked!") },
            onSelectionChangedListener = { selection ->
                when {
                    selection.isEmpty -> {
                        //finishActionMode()
                        dropdownAdapter.notifyItemsWithPredicate()
                    }

                    !selection.isEmpty -> {
                        if (actionMode == null) {
                            //actionMode = createAndStartActionMode(this)
                            dropdownAdapter.notifyItemsWithPredicate()
                        }
                        //actionMode?.title = selection.size().toString()
                    }
                }
                //button.isEnabled = !selection.isEmpty
                selectionSubject.onNext(selection.toList())
            }
        )
    }*/

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = BottomSheetDialog(context)
        view = LayoutInflater.from(context).inflate(R.layout.dropdown_bottomsheet, null)
        dialog.setContentView(view)

        dialog.setOnDismissListener {
            disposable.dispose()
        }

        view.findViewById<View>(R.id.tv_batal).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.tv_selesai).setOnClickListener {
            dialog.dismiss()
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                object : BaseRecyclerViewAdapter<DropdownModel<T>>(
                    R.layout.item_dropdown,
                    bind = { item, holder, _, _ ->
                        with(holder.itemView) {
                            findViewById<TextView>(R.id.tv_title).text =
                                item.valueBuilder(item.data)
                            if (item.subTitleBuilder(item.data).isNotEmpty()) {
                                findViewById<TextView>(R.id.tv_sub_title).text =
                                    item.subTitleBuilder(item.data)
                                findViewById<View>(R.id.tv_sub_title).visibility = View.VISIBLE
                            } else {
                                findViewById<View>(R.id.tv_sub_title).visibility = View.GONE
                            }

                            findViewById<LinearLayout>(R.id.container).isActivated = item.selected
                            setOnClickListener {
                                onItemClickListener(item.data)
                                dialog.dismiss()
                            }
                        }
                    }
                ) {}.apply {
                    submitList(items)
                }
        }

        //recyclerView.adapter = adapter
        /* recyclerView.adapter = dropdownAdapter

         val test = mutableListOf(
             DropdownModel(
                 Roles.ProjectManager.name,
                 Roles.ProjectManager,
                 valueBuilder = { it.optionalName }
             ),
             DropdownModel(
                 Roles.ProjectTeam.name,
                 Roles.ProjectTeam,
                 valueBuilder = { it.optionalName }
             ),
         )
         dropdownAdapter.submitList(test)*/
    }

    private fun finishActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    fun showActionButton(show: Boolean = false): DropdownBottomSheet<T> {
        view.findViewById<View>(R.id.ly_action).visibility =
            if (show) View.VISIBLE else View.GONE
        return this
    }

    fun setTitle(title: String): DropdownBottomSheet<T> {
        view.findViewById<TextView>(R.id.tv_title).text = title
        return this
    }

    fun show(): DropdownBottomSheet<T> {
        dialog.show()
        return this
    }
}
