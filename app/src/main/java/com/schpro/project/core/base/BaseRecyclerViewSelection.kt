package com.schpro.project.core.base

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.SelectionTracker.Builder
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

private const val SELECTION_ID = "selection_id"

interface Identifiable : Serializable {
    val key: String
}

interface ISelectionContainer {
    val selectionContainer: View
}

interface ISelectionItemDetails {
    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String>
}

abstract class BaseRecyclerViewSelection<T : Identifiable, VH : SelectionViewHolder<T>>(
    recyclerView: () -> RecyclerView,
    @LayoutRes private val itemLayout: Int,
    private val viewHolderFactory: (view: View, viewType: Int) -> VH,
    diffCallback: DiffUtil.ItemCallback<T> = BaseItemCallback(),
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit
) : ListAdapter<T, VH>(diffCallback) {

    private val tracker: SelectionTracker<String> by lazy {
        createMultipleItemSelectionTracker(
            recyclerView = recyclerView(),
            keyProvider = { pos -> getItemKey(pos) },
            positionProvider = { key -> getItemPosition(key) },
            onSelectionChangedListener = onSelectionChangedListener,
            selectionPredicate = selectionPredicate
        )
    }

    abstract val selectionPredicate: SelectionTracker.SelectionPredicate<String>

    abstract val inSelectionHotSpot: (viewType: Int, itemView: View, event: MotionEvent) -> Boolean

    private fun getItemKey(pos: Int) = getItemId(currentList[pos])

    private fun getItemId(item: T) = item.key

    private fun getItemPosition(key: String) = currentList.indexOfFirst { getItemId(it) == key }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list)
        tracker.deselectIfItemsNotFound(currentList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        viewHolderFactory(
            LayoutInflater.from(parent.context).inflate(itemLayout, parent, false),
            viewType
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val isActivated = tracker.isSelected(getItemKey(position))
        val item = getItem(position)
        holder.bind(item, isActivated, tracker.hasSelection())
    }

    override fun onViewRecycled(holder: VH) {
        holder.unbind()
        super.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        holder.unbind()
        return super.onFailedToRecycleView(holder)
    }
}

abstract class SelectionViewHolder<T : Identifiable>(
    itemView: View,
    private val inSelectionHotSpot: (event: MotionEvent) -> Boolean
) : RecyclerView.ViewHolder(itemView), ISelectionItemDetails, ISelectionContainer {

    private var key: String? = null

    override fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
        createItemDefaultItemDetails(
            adapterPosition = adapterPosition,
            selectionKey = key,
            inSelectionHotSpot = inSelectionHotSpot
        )

    abstract fun bind(item: T)

    open fun bind(item: T, isActivated: Boolean, isActionModeEnabled: Boolean) {
        selectionContainer.isActivated = isActivated
        key = item.key
        bind(item)
    }

    @CallSuper
    open fun unbind() {
        key = null
    }
}

private class RecyclerViewKeyProvider(
    private val keyProvider: (Int) -> String?,
    private val positionProvider: (String) -> Int
) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int) = keyProvider(position)
    override fun getPosition(key: String) = positionProvider(key)
}

private class AdapterItemDetailsLookupSelectMultipleItems(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? =
        recyclerView.findChildViewUnder(e.x, e.y)?.let { view ->
            (recyclerView.getChildViewHolder(view) as ISelectionItemDetails).getItemDetails()
        }
}

fun <T : Identifiable> SelectionTracker<String>.deselectIfItemsNotFound(currentItems: List<T>) {
    selection.forEach { key -> if (currentItems.none { it.key == key }) deselect(key) }
}

fun createItemDefaultItemDetails(
    adapterPosition: Int,
    selectionKey: String?,
    inSelectionHotSpot: (event: MotionEvent) -> Boolean
) = object : ItemDetailsLookup.ItemDetails<String>() {

    override fun getPosition(): Int = adapterPosition

    override fun getSelectionKey(): String? = selectionKey

    override fun inSelectionHotspot(e: MotionEvent): Boolean = inSelectionHotspot(e)
}

fun createMultipleItemSelectionTracker(
    recyclerView: RecyclerView,
    keyProvider: (Int) -> String?,
    positionProvider: (String) -> Int,
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit,
    selectionPredicate: SelectionTracker.SelectionPredicate<String>
): SelectionTracker<String> = Builder(
    SELECTION_ID,
    recyclerView,
    RecyclerViewKeyProvider(keyProvider, positionProvider),
    AdapterItemDetailsLookupSelectMultipleItems(recyclerView),
    StorageStrategy.createStringStorage(),
).withSelectionPredicate(selectionPredicate).build().apply {
    addObserver(object : SelectionTracker.SelectionObserver<String>() {
        override fun onSelectionChanged() {
            onSelectionChangedListener(selection)
        }
    })
}