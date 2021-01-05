package com.servio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;

public class LoadingActivity extends AppCompatActivity {

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, LoadingActivity.this);

        startAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getSignedUser(user);
    }

    private void getSignedUser(FirebaseUser user) {
        if (user != null) {
            firebaseDatabaseHelper.getSingleDataFieldValueWithCondition("Employees", "type", "userID", user.getUid(), new SimpleCallback<String>() {
                @Override
                public void callback(String type) {
                    switch (type) {
                        case "Ospatar":
                            Intent intent = new Intent(LoadingActivity.this, WaiterUiActivity.class);
                            finish();
                            startActivity(intent);
                            break;
                        case "Bucatar":
                            intent = new Intent(LoadingActivity.this, ChefUIActivity.class);
                            finish();
                            startActivity(intent);
                            break;
                        default:
                            intent = new Intent(LoadingActivity.this, AdminUIActivity.class);
                            finish();
                            startActivity(intent);
                    }
                }
            });
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(LoadingActivity.this, LogInActivity.class));
                }
            }, 2000);
        }
    }

    private void startAnimation() {
        ConstraintLayout constraintLayout = findViewById(R.id.loadingLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
}
