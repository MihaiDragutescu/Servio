package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.servio.R;
import com.servio.models.Dish;

public class DishInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        Button backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DishInfoActivity.this,
                        MenuActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(DishInfoActivity.this, MenuActivity.class));
    }
}
