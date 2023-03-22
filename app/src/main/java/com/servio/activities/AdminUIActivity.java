package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.servio.R;

/**
 * Clasa in care va fi afisat meniul principal.
 */
public class AdminUIActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private LinearLayout seeEmployees;
    private LinearLayout seeHall;
    private LinearLayout seeMenu;
    private LinearLayout logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ui);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        setOnClickListeners();
    }

    private void initViews() {
        firebaseAuth = FirebaseAuth.getInstance();

        seeEmployees = findViewById(R.id.seeEmployees);
        seeHall = findViewById(R.id.seeHall);
        seeMenu = findViewById(R.id.seeMenu);
        logOut = findViewById(R.id.logOut);
    }

    private void setOnClickListeners() {

        seeEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AdminUIActivity.this, EmployeesListActivity.class));
            }
        });

        seeHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AdminUIActivity.this, HallActivity.class));
            }
        });

        seeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AdminUIActivity.this, MenuActivity.class));
            }
        });

        logOut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(AdminUIActivity.this, LogInActivity.class));
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
