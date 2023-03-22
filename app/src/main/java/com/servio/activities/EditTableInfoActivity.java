package com.servio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.servio.R;

public class EditTableInfoActivity extends Activity {

    private Button saveDataButton;
    private EditText tableNumberOfSeats;
    private int tableNumberOfSeatsValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_table_info_dialog);

        initActivity();
        initViews();

        saveDataButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tableNumberOfSeats.getText().toString().trim().isEmpty()) {
                            Toast.makeText(EditTableInfoActivity.this, "Completati campurile cu informatii", Toast.LENGTH_SHORT).show();
                        } else {
                            tableNumberOfSeatsValue = Integer.parseInt(tableNumberOfSeats.getText().toString().trim());

                            Intent intent = new Intent();
                            intent.putExtra("tableNumberOfSeats", tableNumberOfSeatsValue);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
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
        tableNumberOfSeatsValue = (int) bundle.getSerializable("tableNumberOfSeats");

        params.x = xLocation - 50;
        params.y = yLocation - 50;

        this.getWindow().setAttributes(params);
    }

    private void initViews() {
        saveDataButton = findViewById(R.id.saveData);
        tableNumberOfSeats = findViewById(R.id.editTextTableNumberOfSeats);
        tableNumberOfSeats.setText(String.valueOf(tableNumberOfSeatsValue));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
