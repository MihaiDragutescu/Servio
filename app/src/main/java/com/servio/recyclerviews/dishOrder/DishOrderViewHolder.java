package com.servio.recyclerviews.dishOrder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

class DishOrderViewHolder extends RecyclerView.ViewHolder {
    private final TextView dishName;
    private final TextView dishQuantity;
    private final ImageButton buttonRemove;

    DishOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        dishName = itemView.findViewById(R.id.dishNameTxtViewRecyclerView);
        dishQuantity = itemView.findViewById(R.id.quantityTextView);
        buttonRemove = itemView.findViewById(R.id.removeBtnRecyclerView);
    }

    TextView getDishName() {
        return dishName;
    }

    TextView getDishQuantity() {
        return dishQuantity;
    }

    ImageButton getButtonRemove() {
        return buttonRemove;
    }
}
