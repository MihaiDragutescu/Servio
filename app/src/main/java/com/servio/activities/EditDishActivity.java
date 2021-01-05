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

import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.models.Dish;

import java.util.ArrayList;
import java.util.Arrays;

public class EditDishActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText ingredientsEditText;
    private EditText weightEditText;
    private EditText priceEditText;
    private Spinner dishCategorySpinner;
    private Button saveInfoButton;
    private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private Dish dish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dish);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        dish = (Dish) bundle.getSerializable("dish");

        initViews();
        initSpinner();
        saveData();
    }

    private void initViews() {
        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, EditDishActivity.this);

        nameEditText = findViewById(R.id.fullNameEditTxtEditDish);
        nameEditText.setText(dish.getDishName());

        ingredientsEditText = findViewById(R.id.ingredientsEditTxtEditDish);
        ingredientsEditText.setText(dish.getDishIngredients());

        weightEditText = findViewById(R.id.weightEditTextEditDish);
        weightEditText.setText(String.valueOf(dish.getDishWeight()));

        priceEditText = findViewById(R.id.priceEditTextEditDish);
        priceEditText.setText(String.valueOf(dish.getDishPrice()));

        dishCategorySpinner = findViewById(R.id.editDishCategorySpinner);

        Button backButton = findViewById(R.id.backBtnEditDish);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(new Intent(EditDishActivity.this,
                                MenuActivity.class));
                    }
                }
        );

        saveInfoButton = findViewById(R.id.editDishBtn);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void initSpinner() {
        String[] jobs = {"Aperitive", "Supe si ciorbe", "Fel principal", "Paste", "Peste si fructe de mare", "Salate", "Garnituri", "Pizza", "Topping si sosuri", "Bauturi", "Deserturi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                jobs);

        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        dishCategorySpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(dish.getDishCategory());
        dishCategorySpinner.setSelection(spinnerPosition);
    }

    private void saveData() {
        saveInfoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nameEditText.getText().toString().trim().isEmpty() || ingredientsEditText.getText().toString().trim().isEmpty() ||
                                weightEditText.getText().toString().trim().isEmpty() || priceEditText.getText().toString().trim().isEmpty()) {
                            Toast.makeText(EditDishActivity.this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.VISIBLE);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    firebaseDatabaseHelper.simpleUpdateField("Dishes", dish.getDishId(), "dishName", nameEditText.getText().toString());

                                    String name = nameEditText.getText().toString().toLowerCase().trim();

                                    String[] keyWords = name.split("\\s+");
                                    ArrayList<String> keyWordsList = new ArrayList<>(Arrays.asList(keyWords));

                                    for (int i = 0; i < keyWordsList.size(); i++) {
                                        if (keyWordsList.get(i).length() <= 2) {
                                            keyWordsList.remove(i);
                                        }
                                    }

                                    keyWords = new String[keyWordsList.size()];
                                    keyWords = keyWordsList.toArray(keyWords);

                                    firebaseDatabaseHelper.simpleUpdateField("Dishes", dish.getDishId(), "keyWords", Arrays.asList(keyWords));

                                    firebaseDatabaseHelper.simpleUpdateField("Dishes", dish.getDishId(), "dishIngredients", ingredientsEditText.getText().toString());

                                    firebaseDatabaseHelper.simpleUpdateField("Dishes", dish.getDishId(), "dishWeight", Double.valueOf(weightEditText.getText().toString()));

                                    firebaseDatabaseHelper.simpleUpdateField("Dishes", dish.getDishId(), "dishPrice", Double.valueOf(priceEditText.getText().toString()));

                                    firebaseDatabaseHelper.simpleUpdateField("Dishes", dish.getDishId(), "dishCategory", dishCategorySpinner.getSelectedItem().toString());

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(EditDishActivity.this, "Datele au fost actualizate cu succes", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(EditDishActivity.this, MenuActivity.class));
                                }
                            }, 500);
                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EditDishActivity.this, MenuActivity.class));
    }
}
