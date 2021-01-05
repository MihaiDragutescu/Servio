package com.servio.recyclerviews.dishOrder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

class DishOrderViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout constraintLayout;

    private TextView dishName;
    private TextView dishQuantity;
    private ImageButton buttonRemove;

    DishOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.constraintLayout);

        dishName = itemView.findViewById(R.id.dishNameTxtViewRecyclerView);
        dishQuantity = itemView.findViewById(R.id.quantityTextView);
        buttonRemove = itemView.findViewById(R.id.removeBtnRecyclerView);
    }

    void setDishName(String name) {
        dishName.setText(name);
    }

    void setDishQuantity(String quantity) {
        dishQuantity.setText(quantity);
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

    ConstraintLayout getCardView() {
        return constraintLayout;
    }
}
