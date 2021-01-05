package com.servio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.servio.R;
import com.servio.models.Dish;

public class DishInfoDialogActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dish_info_dialog);

        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Dish dish = (Dish) bundle.getSerializable("dish");

        TextView dishNameTextView = findViewById(R.id.textViewDishNameText);
        dishNameTextView.setText(dish.getDishName());

        TextView dishIngredientsTextView = findViewById(R.id.textViewDishIngredientsText);
        dishIngredientsTextView.setText(dish.getDishIngredients());

        TextView dishWeightTextView = findViewById(R.id.textViewDishWeightText);
        String dishWeight = dish.getDishWeight() + " grame";
        dishWeightTextView.setText(dishWeight);

        TextView dishPriceTextView = findViewById(R.id.textViewDishPriceText);
        String dishPrice = dish.getDishPrice() + " lei";
        dishPriceTextView.setText(dishPrice);

        TextView dishCategoryTextView = findViewById(R.id.textViewDishCategoryText);
        dishCategoryTextView.setText(dish.getDishCategory());
    }
}
