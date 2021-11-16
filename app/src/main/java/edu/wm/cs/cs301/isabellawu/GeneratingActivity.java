package edu.wm.cs.cs301.isabellawu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class GeneratingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);
    }

    public void startGame(View view) {
        // check if progress bar is at 100%
        // check if driver + config have been selected
//        Log.v();

        boolean test = true;
        Intent intent;
        if(test) {
            intent = new Intent(this, PlayManuallyActivity.class);
        }
        else {
            intent = new Intent(this, PlayAnimationActivity.class);
        }
        startActivity(intent);
    }
}