package com.servio.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;

/**
 * Clasa corespunzatoare activitatii cu care se deschide aplicatia.
 * Acesta este meniul de autentificare.
 */
public class LogInActivity extends AppCompatActivity {

    private EditText emailEditTxt;
    private EditText passwordEditTxt;
    private Button logInButton;
    private Button exitButton;
    private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startAnimation();
        initViews();
        setButtonClickEvents();
    }

    private void initViews() {
        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, LogInActivity.this);

        logInButton = findViewById(R.id.loginButton);
        exitButton = findViewById(R.id.exitButton);
        emailEditTxt = findViewById(R.id.mailEditTxtLogIn);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Daca obiectul user este diferit de null, atunci porneste un nou intent catre activitatea
     * AdminUIActivity.class. In acel intent este transmis obiectul user prin
     * 'intent.putExtra("user", user)'. In final, activitatea este schimbata si LogInActivity este
     * inchisa.
     *
     * @param user utilizatorul curent conectat
     */
    private void getSignedUser(FirebaseUser user) {
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseDatabaseHelper.getSingleDataFieldValueWithCondition("Employees", "type", "userID", user.getUid(), new SimpleCallback<String>() {
                @Override
                public void callback(String type) {
                    switch (type) {
                        case "Ospatar":
                            Intent intent = new Intent(LogInActivity.this, WaiterUiActivity.class);
                            finish();
                            startActivity(intent);
                            break;
                        case "Bucatar":
                            intent = new Intent(LogInActivity.this, ChefUIActivity.class);
                            finish();
                            startActivity(intent);
                            break;
                        default:
                            intent = new Intent(LogInActivity.this, AdminUIActivity.class);
                            finish();
                            startActivity(intent);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setButtonClickEvents() {
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditTxt.getText().toString().trim();
                final String password = passwordEditTxt.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Completati datele de autentificare.", Toast.LENGTH_SHORT).show();
                } else {
                    /* Autentificam utilizatorul utilizand API-ul Firebase */
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        getSignedUser(FirebaseAuth.getInstance().getCurrentUser());
                                    } else {
                                        Toast.makeText(LogInActivity.this, "Email-ul sau parola sunt incorecte", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        exitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishAffinity();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void startAnimation() {
        ConstraintLayout constraintLayout = findViewById(R.id.loginActivityLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
}
