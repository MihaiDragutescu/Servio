package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NewDishActivity extends AppCompatActivity {
    private EditText dishNameEditTxt;
    private EditText ingredientsEditTxt;
    private EditText weightEditTxt;
    private EditText priceEditTxt;

    private Spinner dishCategorySpinner;
    private Button addDishBtn;
    private Button backBtn;
    private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dish);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        initSpinner();
        initClickListeners();
    }

    private void initViews() {
        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, NewDishActivity.this);

        dishNameEditTxt = findViewById(R.id.fullNameEditTxtAddDish);
        ingredientsEditTxt = findViewById(R.id.ingredientsEditTxtAddDish);
        weightEditTxt = findViewById(R.id.weightEditTextAddDish);
        priceEditTxt = findViewById(R.id.priceEditTextAddDish);

        dishCategorySpinner = findViewById(R.id.dishCategorySpinner);
        addDishBtn = findViewById(R.id.addDishBtn);
        backBtn = findViewById(R.id.backBtnAddDish);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void initSpinner() {
        String[] categories = {"Aperitive", "Supe si ciorbe", "Fel principal", "Paste", "Peste si fructe de mare", "Salate", "Garnituri", "Pizza", "Topping si sosuri", "Bauturi", "Deserturi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                categories);

        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        dishCategorySpinner.setAdapter(adapter);
    }

    private void initClickListeners() {
        addDishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dishNameEditTxt.getText().toString().trim().isEmpty() || ingredientsEditTxt.getText().toString().trim().isEmpty() ||
                        weightEditTxt.getText().toString().trim().isEmpty() || priceEditTxt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewDishActivity.this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String name = dishNameEditTxt.getText().toString().trim();
                            String ingredients = ingredientsEditTxt.getText().toString().trim();
                            String weight = weightEditTxt.getText().toString().trim();
                            String price = priceEditTxt.getText().toString().trim();
                            int quantity = 1;
                            String id = "Id" + name.replaceAll("\\s+", "");
                            String category = dishCategorySpinner.getSelectedItem().toString().trim();
                            String[] keyWords = name.toLowerCase().split("\\s+");

                            final Map<String, Object> dish = new HashMap<>();

                            dish.put("dishName", name);
                            dish.put("dishIngredients", ingredients);
                            dish.put("dishWeight", Double.valueOf(weight));
                            dish.put("dishPrice", Double.valueOf(price));
                            dish.put("dishQuantity", quantity);
                            dish.put("dishId", id);
                            dish.put("dishCategory", category);
                            dish.put("restaurant", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                            ArrayList<String> keyWordsList = new ArrayList<>(Arrays.asList(keyWords));

                            for (int i = 0; i < keyWordsList.size(); i++) {
                                if (keyWordsList.get(i).length() <= 2) {
                                    keyWordsList.remove(i);
                                }
                            }

                            keyWords = new String[keyWordsList.size()];
                            keyWords = keyWordsList.toArray(keyWords);

                            dish.put("keyWords", Arrays.asList(keyWords));

                            firebaseDatabaseHelper.insertData("Dishes", id, dish);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewDishActivity.this, "Preparatul a fost adaugat cu succes", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(NewDishActivity.this, MenuActivity.class));
                        }
                    }, 500);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(NewDishActivity.this,
                        MenuActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(NewDishActivity.this, MenuActivity.class));
    }
}
