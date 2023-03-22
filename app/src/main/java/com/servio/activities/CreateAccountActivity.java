package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Clasa corespunzatoare activitatii in care utilizatorul isi poate crea cont.
 */
public class CreateAccountActivity extends AppCompatActivity {

    private EditText restaurantNameEditTxt;
    private EditText emailEditTxt;
    private EditText passwordEditTxt;
    private EditText passwordConfirmEditTxt;
    private Button createAccountButton;
    private Button backButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViews();
        setButtonClickEvents();
    }

    private void initViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, CreateAccountActivity.this);

        restaurantNameEditTxt = findViewById(R.id.restaurantNameEditTxt);
        emailEditTxt = findViewById(R.id.mailEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);
        passwordConfirmEditTxt = findViewById(R.id.passwordConfirmEditTxt);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }


    private void setButtonClickEvents() {
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String restaurantName = restaurantNameEditTxt.getText().toString().trim();
                final String email = emailEditTxt.getText().toString().trim();
                final String password = passwordEditTxt.getText().toString().trim();
                final String passwordConfirm = passwordConfirmEditTxt.getText().toString().trim();

                if (restaurantName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
                } else if (!password.equals((passwordConfirm))) {
                    Toast.makeText(CreateAccountActivity.this, "Parolele nu corespund", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(CreateAccountActivity.this, "Parola trebuie sa aiba cel putin 6 caractere", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(restaurantName)
                                        .build();

                                if (user != null) {
                                    user.updateProfile(profileUpdates);
                                }

                                final Map<String, Object> restaurant = new HashMap<>();
                                String id = "Id" + restaurantName.replaceAll("\\s+", "");
                                restaurant.put("restaurantId", id);
                                restaurant.put("restaurantName", restaurantName);

                                firebaseDatabaseHelper.insertData("Restaurants", id, restaurant);

                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    finish();
                                                    startActivity(new Intent(CreateAccountActivity.this, AdminUIActivity.class));
                                                }

                                            }
                                        });

                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CreateAccountActivity.this, "Eroare la crearea contului.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(CreateAccountActivity.this, LogInActivity.class));
    }
}
