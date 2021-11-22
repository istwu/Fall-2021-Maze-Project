package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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

        Bundle extras = getIntent().getExtras();
        int seed = extras.getInt("seed");
        int skill = extras.getInt("skill");
        Boolean perfect = extras.getBoolean("perfect");
        int generation = extras.getInt("generation");
        Log.v(TAG, "Seed: " + seed);
        Log.v(TAG, "Skill level: " + skill);
        Log.v(TAG, "Rooms included: " + perfect);
        switch(generation) {
            case 0:
                Log.v(TAG, "Generation algorithm: DFS");
                break;
            case 1:
                Log.v(TAG, "Generation algorithm: Prim");
                break;
            case 2:
                Log.v(TAG, "Generation algorithm: Boruvka");
                break;
        }

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Handler handler = new Handler();
        // code taken from https://www.py4u.net/discuss/694340
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress < 100) {
                    progress += 5;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                    if (progress == 100) {
                        startGame();
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

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

    public void startGame() {
        // check if progress bar is at 100%
        // check if driver + config have been selected
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (driver == 0 || (!manual && config == 0)) {
                    if(progress == 100 && driver != 0 && (manual || (!manual && config != 0))) {

                    }
                    else {
                        TextView driverWarning = (TextView) findViewById(R.id.driverWarning);
                        TextView configWarning = (TextView) findViewById(R.id.configWarning);
                        if(driver == 0) {
                            driverWarning.setText("Please choose a driver.");
                        }
                        else {
                            driverWarning.setText("");
                        }
                        if(!manual && config == 0) {
                            configWarning.setText("Please choose a robot configuration.");
                        }
                        else {
                            configWarning.setText("");
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent;
                if(manual) {
                    intent = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                }
                else {
                    intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                }
                startActivity(intent);
            }
        }).start();



    }
}