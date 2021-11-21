package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.wm.cs.cs301.isabellawu.R;

public class WinningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }
}