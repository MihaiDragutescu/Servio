package com.servio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.servio.R;

public class NewWallActivity2 extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_wall_dialog_2);

        initViews();
    }

    private void initViews() {
        ImageView wallImageView7 = findViewById(R.id.perete_7);
        chooseWallType(wallImageView7);

        ImageView wallImageView8 = findViewById(R.id.perete_8);
        chooseWallType(wallImageView8);

        ImageView wallImageView9 = findViewById(R.id.perete_9);
        chooseWallType(wallImageView9);

        ImageView wallImageView10 = findViewById(R.id.perete_10);
        chooseWallType(wallImageView10);

        ImageView wallImageView11 = findViewById(R.id.perete_11);
        chooseWallType(wallImageView11);
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

 /*   @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return false;
    }*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
