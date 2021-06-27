package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.servio.R;

public class WaiterUiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private LinearLayout takeOrders;
    private LinearLayout profile;
    private LinearLayout logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_ui);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        setOnClickListeners();
    }

    private void initViews() {
        firebaseAuth = FirebaseAuth.getInstance();

        takeOrders = findViewById(R.id.takeOrders);
        profile = findViewById(R.id.profile);
        logOut = findViewById(R.id.logOut);
    }

    private void setOnClickListeners() {

        takeOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(WaiterUiActivity.this, TakeOrdersActivity.class));
            }
        });

        profile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }
        );

        logOut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(WaiterUiActivity.this, LogInActivity.class));
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
