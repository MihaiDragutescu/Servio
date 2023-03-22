package com.servio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.servio.R;

public class TableInfoActivity extends Activity {

    private Button closeDialogButton;
    private int tableNumber;
    private int tableNumberOfSeats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.table_info_dialog);

        initActivity();
        initViews();

        closeDialogButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void initActivity() {

        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        int xLocation = (int) bundle.getSerializable("xLocation");
        int yLocation = (int) bundle.getSerializable("yLocation");

        tableNumber = (int) bundle.getSerializable("tableNumber");
        tableNumberOfSeats = (int) bundle.getSerializable("tableNumberOfSeats");

        params.x = xLocation - 50;
        params.y = yLocation - 50;

        this.getWindow().setAttributes(params);
    }

    private void initViews() {
        closeDialogButton = findViewById(R.id.closeDialog);
        TextView tableNumberText = findViewById(R.id.textViewTableNumberText);
        TextView tableNumberOfSeatsText = findViewById(R.id.textViewTableNumberOfSeatsText);

        tableNumberText.setText(String.valueOf(tableNumber));
        tableNumberOfSeatsText.setText(String.valueOf(tableNumberOfSeats));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finish();
        }
        return false;
    }
}
