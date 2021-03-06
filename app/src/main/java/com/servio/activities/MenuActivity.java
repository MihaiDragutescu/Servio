package com.servio.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.servio.R;
import com.servio.models.Dish;
import com.servio.recyclerviews.dishAdmin.DishAdminAdapter;

import java.util.Arrays;

/* * Clasa activitatii in care va fi afisat meniul de preparate.*/

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addDishBtn;
    private ImageButton backImageButton;
    private ImageButton refreshImageButton;
    private EditText editTextSearchDish;
    private ImageButton imageButtonSearchDish;

    Context context;
    FirebaseFirestore firestoreReference = FirebaseFirestore.getInstance();
    private DishAdminAdapter dishAdminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        setLayoutManager();

        backImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(new Intent(MenuActivity.this,
                                AdminUIActivity.class));
                    }
                }
        );

        refreshImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTextSearchDish.setText("");

                        Query query = firestoreReference.collection("Dishes").orderBy("dishCategory", Query.Direction.DESCENDING);
                        dishAdminAdapter.stopListening();
                        FirestoreRecyclerOptions<Dish> options = new FirestoreRecyclerOptions.Builder<Dish>()
                                .setQuery(query, Dish.class)
                                .build();

                        dishAdminAdapter = new DishAdminAdapter(options, context);
                        recyclerView.setAdapter(dishAdminAdapter);
                        dishAdminAdapter.startListening();
                    }
                }
        );

        addDishBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(new Intent(MenuActivity.this, NewDishActivity.class));
                    }
                }
        );

        imageButtonSearchDish.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] keyWords = editTextSearchDish.getText().toString().toLowerCase().split("\\s+");

                        final Query query = firestoreReference.collection("Dishes").whereArrayContainsAny("keyWords", Arrays.asList(keyWords));

                        if (!editTextSearchDish.getText().toString().matches("")) {
                            query.get().addOnSuccessListener(
                                    new OnSuccessListener<QuerySnapshot>() {
                                        int valid = 0;

                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                if (document.exists()) {
                                                    valid = 1;
                                                }
                                            }
                                            if (valid == 1) {
                                                dishAdminAdapter.stopListening();
                                                FirestoreRecyclerOptions<Dish> options = new FirestoreRecyclerOptions.Builder<Dish>()
                                                        .setQuery(query, Dish.class)
                                                        .build();

                                                dishAdminAdapter = new DishAdminAdapter(options, context);
                                                recyclerView.setAdapter(dishAdminAdapter);
                                                dishAdminAdapter.startListening();
                                            } else {
                                                Toast.makeText(MenuActivity.this, "Nu s-a gasit produsul", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                            );

                        } else if (editTextSearchDish.getText().toString().matches("")) {
                            Query mainQuery = firestoreReference.collection("Dishes").orderBy("dishCategory", Query.Direction.DESCENDING);
                            dishAdminAdapter.stopListening();
                            FirestoreRecyclerOptions<Dish> options = new FirestoreRecyclerOptions.Builder<Dish>()
                                    .setQuery(mainQuery, Dish.class)
                                    .build();

                            dishAdminAdapter = new DishAdminAdapter(options, context);
                            recyclerView.setAdapter(dishAdminAdapter);
                            dishAdminAdapter.startListening();
                        }
                    }
                }
        );
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewMenu);
        addDishBtn = findViewById(R.id.addDishBtnMenu);
        backImageButton = findViewById(R.id.imageButtonBack);
        refreshImageButton = findViewById(R.id.imageButtonRefresh);
        editTextSearchDish = findViewById(R.id.searchDishEditText);
        editTextSearchDish.setImeOptions(EditorInfo.IME_ACTION_DONE);
        imageButtonSearchDish = findViewById(R.id.imageButtonSearchDish);
    }

    private void setLayoutManager() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = firestoreReference.collection("Dishes").orderBy("dishCategory", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Dish> options = new FirestoreRecyclerOptions.Builder<Dish>()
                .setQuery(query, Dish.class)
                .build();

        dishAdminAdapter = new DishAdminAdapter(options, this);
        context = this;

        recyclerView.setAdapter(dishAdminAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dishAdminAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dishAdminAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MenuActivity.this, AdminUIActivity.class));
    }
}
