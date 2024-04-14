package com.csc131.deltamedicalteam.helper;

import static com.google.android.material.color.MaterialColors.ALPHA_FULL;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.csc131.deltamedicalteam.adapter.CurrentAllergiesList;
import com.csc131.deltamedicalteam.adapter.CurrentMedicationList;


public class SwipeItemTouchHelper extends ItemTouchHelper.Callback {

    private SwipeListener swipeListener;

    public SwipeItemTouchHelper(CurrentAllergiesList mAllergiesAdapter) {
    }
    public SwipeItemTouchHelper(CurrentMedicationList m) {
    }
    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (swipeListener != null) {
        swipeListener.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            ColorDrawable background = new ColorDrawable();
            background.setColor(getBgColorCode());

            // Define text paint
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(50); // Adjust text size as needed
            textPaint.setTextAlign(Paint.Align.CENTER);

            // Calculate text position
            float textX;
            float textY = itemView.getTop() + (itemView.getHeight() / 2);
            if (dX > 0) { // swipe right
                background.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                textX = dX / 2;
            } else { // swipe left
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                textX = itemView.getRight() + dX / 2;
            }

            // Draw background
            background.draw(c);

            // Draw text
            c.drawText("REMOVE", textX, textY, textPaint);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public int getBgColorCode() {
        return Color.LTGRAY;
    }

    // Define the SwipeListener interface
    public interface SwipeListener {
        void onItemDismiss(int position);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof TouchViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                TouchViewHolder itemViewHolder = (TouchViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

        if (viewHolder instanceof TouchViewHolder) {
            // Tell the view holder it's time to restore the idle state
            TouchViewHolder itemViewHolder = (TouchViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

    public interface TouchViewHolder {
        void onItemSelected();

        void onItemClear();
    }
}

