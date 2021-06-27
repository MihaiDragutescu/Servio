package com.servio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.servio.R;

public class NewWallActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_wall_dialog);

        initViews();
    }

    private void initViews() {
        ImageView wallImageView = findViewById(R.id.perete);
        chooseWallType(wallImageView);

        ImageView wallImageView2 = findViewById(R.id.perete_2);
        chooseWallType(wallImageView2);

        ImageView wallImageView3 = findViewById(R.id.perete_3);
        chooseWallType(wallImageView3);

        ImageView wallImageView4 = findViewById(R.id.perete_4);
        chooseWallType(wallImageView4);

        ImageView wallImageView5 = findViewById(R.id.perete_5);
        chooseWallType(wallImageView5);

        ImageView wallImageView6 = findViewById(R.id.perete_6);
        chooseWallType(wallImageView6);
    }

    private void chooseWallType(final ImageView wallImage) {
        wallImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("wallType", String.valueOf(wallImage.getTag()));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
