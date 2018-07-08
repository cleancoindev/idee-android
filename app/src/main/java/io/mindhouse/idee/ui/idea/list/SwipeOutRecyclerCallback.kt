package io.mindhouse.idee.ui.idea.list

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class SwipeOutRecyclerCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    var onSwipedOut: ((Int) -> Unit)? = null

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        (viewHolder as? IdeaRecyclerAdapter.ViewHolder)?.let {
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(it.foreground)
        }
    }

    override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        (viewHolder as? IdeaRecyclerAdapter.ViewHolder)?.let {
            ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView,
                    it.foreground, dX, dY, actionState, isCurrentlyActive)

        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        (viewHolder as? IdeaRecyclerAdapter.ViewHolder)?.let {
            ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView,
                    it.foreground, dX, dY, actionState, isCurrentlyActive)

        }
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        (viewHolder as? IdeaRecyclerAdapter.ViewHolder)?.let {
            ItemTouchHelper.Callback.getDefaultUIUtil().clearView(it.foreground)
        }
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipedOut?.invoke(viewHolder.adapterPosition)
    }
}