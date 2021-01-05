package com.servio.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
    private LinearLayout settings;
    private LinearLayout logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_ui);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startAnimation();
        initViews();
        setOnClickListeners();
    }

    private void initViews() {
        firebaseAuth = FirebaseAuth.getInstance();

        takeOrders = findViewById(R.id.takeOrders);
        settings = findViewById(R.id.settings);
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

        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //finish();
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

    private void startAnimation() {
        LinearLayout linearLayout = findViewById(R.id.waiterUILayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
