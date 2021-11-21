package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import edu.wm.cs.cs301.isabellawu.R;

public class GeneratingActivity extends AppCompatActivity {

    private int progress;
    private int driver;
    private boolean manual;
    private int config;

    private static final String TAG = "GeneratingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(100);
        progress = progressBar.getProgress();

        manual = false;
        Spinner driverSpinner = (Spinner) findViewById(R.id.driverSpinner);
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 0 = none
            // 1 = manual
            // 2 = wizard
            // 3 = wallfollower
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                driver = i;
                String d = "";
                switch(driver){
                    case 0: {
                        d = "None";
                        manual = false;
                        break;
                    }
                    case 1: {
                        d = "Manual";
                        manual = true;
                        break;
                    }
                    case 2: {
                        d = "Wizard";
                        manual = false;
                        break;
                    }
                    case 3: {
                        d = "WallFollower";
                        manual = false;
                        break;
                    }
                }
                Toast toast = Toast.makeText(getApplicationContext(), "Driver set to " + d, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Driver set to " + d);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner configSpinner = (Spinner) findViewById(R.id.configSpinner);
        configSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 0 = none
            // 1 = premium
            // 2 = mediocre
            // 3 = soso
            // 4 = shaky
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                config = i;
                String c = "";
                switch(config){
                    case 0: c = "None";
                        break;
                    case 1: c = "Premium";
                        break;
                    case 2: c = "Mediocre";
                        break;
                    case 3: c = "So-so";
                        break;
                    case 4: c = "Shaky";
                }
                Toast toast = Toast.makeText(getApplicationContext(), "Robot configuration set to " + c, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Robot configuration set to " + c);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }

    public void startGame(View view) {
        // check if progress bar is at 100%
        // check if driver + config have been selected
        if(progress == 100 && driver != 0 && (manual || (!manual && config != 0))) {
            Intent intent;
            if(manual) {
                intent = new Intent(this, PlayManuallyActivity.class);
            }
            else {
                intent = new Intent(this, PlayAnimationActivity.class);
            }
            startActivity(intent);
        }
        else {
            if(progress != 100) {
                Log.v(TAG, "" + progress);
                Toast toast = Toast.makeText(getApplicationContext(), "Maze not finished loading.", Toast.LENGTH_SHORT);
                toast.show();
            }
            if(driver == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Need to choose a driver.", Toast.LENGTH_SHORT);
                toast.show();
            }
            if(!manual && config == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Need to set robot configuration.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}