package com.servio.recyclerviews.dishAdmin;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

/**
 * Clasa de tip ViewHolder care primeste elementele vizuale corespunzatoare fisierului XML:
 * "dish_admin_view.xmlew.xml"
 */
class DishAdminViewHolder extends RecyclerView.ViewHolder {

    private TextView dishName;
    private TextView dishCategory;

    private ImageButton buttonEdit;
    private ImageButton buttonRemove;
    private ImageButton buttonInfo;

    DishAdminViewHolder(@NonNull View itemView) {
        super(itemView);

        dishName = itemView.findViewById(R.id.dishNameTxtViewRecyclerView);
        dishCategory = itemView.findViewById(R.id.categoryTxtViewMenu);

        buttonEdit = itemView.findViewById(R.id.editBtnRecyclerView);
        buttonRemove = itemView.findViewById(R.id.removeBtnRecyclerView);
        buttonInfo = itemView.findViewById(R.id.infoBtnRecyclerView);
    }

    void setDishName(String name) {
        dishName.setText(name);
    }

    void setDishCategory(String category) {
        dishCategory.setText(category);
    }

    ImageButton getButtonEdit() {
        return buttonEdit;
    }

    ImageButton getButtonRemove() {
        return buttonRemove;
    }

    ImageButton getButtonInfo() {
        return buttonInfo;
    }
}
