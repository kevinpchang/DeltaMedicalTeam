package com.csc131.deltamedicalteam.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.adapter.CurrentAllergiesList;

interface SwipeHelperAdapter {
    void onItemDismiss(int position);
}

public class SwipeItemTouchHelper extends ItemTouchHelper.Callback {

    private final CurrentAllergiesList mAdapter;
    private SwipeListener swipeListener;

    public SwipeItemTouchHelper(CurrentAllergiesList adapter) {
        mAdapter = adapter;
    }

    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (swipeListener != null) {
            swipeListener.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    // Define the SwipeListener interface
    public interface SwipeListener {
        void onItemDismiss(int position);
    }
}

