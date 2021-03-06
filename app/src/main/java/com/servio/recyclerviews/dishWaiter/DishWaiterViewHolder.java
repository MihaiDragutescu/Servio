package com.servio.recyclerviews.dishWaiter;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

class DishWaiterViewHolder extends RecyclerView.ViewHolder {
    private final ConstraintLayout constraintLayout;

    private final TextView dishName;
    private final TextView dishCategory;

    private final EditText dishQuantity;
    private final ImageButton buttonAdd;

    DishWaiterViewHolder(@NonNull View itemView) {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.constraintLayout);

        dishName = itemView.findViewById(R.id.dishNameTxtViewRecyclerView);
        dishCategory = itemView.findViewById(R.id.categoryTxtViewMenu);

        dishQuantity = itemView.findViewById(R.id.quantityEditTextRecyclerView);
        dishQuantity.setImeOptions(EditorInfo.IME_ACTION_DONE);
        buttonAdd = itemView.findViewById(R.id.addBtnRecyclerView);
    }

    TextView getDishName() {
        return dishName;
    }

    EditText getDishQuantity() {
        return dishQuantity;
    }

    ImageButton getButtonAdd() {
        return buttonAdd;
    }

    TextView getDishCategory() {
        return dishCategory;
    }

    ConstraintLayout getCardView() {
        return constraintLayout;
    }
}
