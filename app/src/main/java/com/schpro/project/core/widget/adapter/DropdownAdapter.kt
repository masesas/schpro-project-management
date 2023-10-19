package com.schpro.project.core.widget.adapter

private const val NON_DEPRECATED_VIEW_TYPE = 0
private const val DEPRECATED_VIEW_TYPE = 1

data class DropdownModel<T>(
    val data: T,
    val valueBuilder: (T) -> String,
    val subTitleBuilder: (T) -> String = { "" },
    val selected: Boolean = false,
)

/*
class DropdownAdapter<T>(
    recyclerView: () -> RecyclerView,
    itemClickListener: (DropdownModel<T>) -> Unit,
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit,
    inSelectionHotSpot: (viewType: Int, itemView: View, event: MotionEvent) -> Boolean = { viewType: Int, itemView: View, event: MotionEvent ->
        when (viewType) {
            NON_DEPRECATED_VIEW_TYPE -> true
            DEPRECATED_VIEW_TYPE -> false
            else -> throw NotImplementedError()
        }
    },
    private val enabledPredicate: (DropdownModel<T>) -> Boolean = { true },

    ) : BaseRecyclerViewSelection<DropdownModel<T>, DropdownAdapter.ViewHolder<T>>(
    recyclerView = recyclerView,
    itemLayout = R.layout.item_dropdown,
    viewHolderFactory = { view, viewType ->
        val inSelectionHotSpotCurried = inSelectionHotSpot.curry()(viewType)(view)
        when (viewType) {
            NON_DEPRECATED_VIEW_TYPE -> ViewHolder(
                view,
                itemClickListener,
                inSelectionHotSpotCurried,
                enabledPredicate,
                viewType
            )

            DEPRECATED_VIEW_TYPE -> ViewHolder(
                view,
                itemClickListener,
                inSelectionHotSpotCurried,
                enabledPredicate,
                viewType
            )

            else -> throw NotImplementedError()
        }
    },
    onSelectionChangedListener = onSelectionChangedListener
) {

    override val selectionPredicate: SelectionTracker.SelectionPredicate<String> =
        object : SelectionTracker.SelectionPredicate<String>() {
            override fun canSetStateForKey(key: String, nextState: Boolean) =
                if (nextState) currentList.find { it.key == key }?.let(enabledPredicate)
                    ?: false else true

            override fun canSetStateAtPosition(position: Int, nextState: Boolean) =
                if (nextState) currentList[position].let(enabledPredicate) else true

            override fun canSelectMultiple() = true
        }

    override fun getItemViewType(position: Int): Int = NON_DEPRECATED_VIEW_TYPE

    override val inSelectionHotSpot: (viewType: Int, itemView: View, event: MotionEvent) -> Boolean =
        { viewType: Int, itemView: View, event: MotionEvent ->
            when (viewType) {
                NON_DEPRECATED_VIEW_TYPE -> true
                DEPRECATED_VIEW_TYPE -> false
                else -> throw NotImplementedError()
            }
        }

    fun notifyItemsWithPredicate() {
        val indexes =
            currentList.withIndex().mapNotNull { if (enabledPredicate(it.value)) null else it }
                .map { it.index }
        indexes.forEach { notifyItemChanged(it) }
    }

    class ViewHolder<T>(
        itemView: View,
        private val itemClickListener: (DropdownModel<T>) -> Unit,
        inSelectionHotSpot: (event: MotionEvent) -> Boolean,
        private val enabledPredicate: (DropdownModel<T>) -> Boolean,
        private val viewType: Int,
    ) : SelectionViewHolder<DropdownModel<T>>(itemView, inSelectionHotSpot) {

        private val title = itemView.findViewById<TextView>(R.id.tv_title)
        private val subTitle = itemView.findViewById<TextView>(R.id.tv_sub_title)

        override val selectionContainer: View = itemView.findViewById(R.id.container)

        override fun bind(
            item: DropdownModel<T>,
            isActivated: Boolean,
            isActionModeEnabled: Boolean
        ) {
            super.bind(item, isActivated, isActionModeEnabled)

            */
/*name_details_container.alpha =
                if (isActionModeEnabled && !enabledPredicate(item)) 0.2f else 1.0f*//*

        }

        override fun bind(item: DropdownModel<T>) {
            selectionContainer.setOnClickListener { itemClickListener(item) }
            title.text = item.valueBuilder(item.data)
            if (item.subTitleBuilder(item.data).isNotEmpty()) {
                subTitle.visibility = View.VISIBLE
                subTitle.text = item.subTitleBuilder(item.data)
            } else {
                subTitle.visibility = View.GONE
            }
        }
    }

}*/
