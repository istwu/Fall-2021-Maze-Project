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

    private int seed;
    private int skill;
    private boolean perfect;
    private int generation;

    private static final String TAG = "GeneratingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        Bundle extras = getIntent().getExtras();
        seed = extras.getInt("seed");
        skill = extras.getInt("skill");
        perfect = extras.getBoolean("perfect");
        generation = extras.getInt("generation");

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Handler handler = new Handler();
        // code taken from https://www.py4u.net/discuss/694340
        new Thread(() -> {
            while (progress < 100) {
                progress += 5;
                handler.post(() -> progressBar.setProgress(progress));
                if (progress == 100) {
                    startGame();
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        manual = false;
        Spinner driverSpinner = findViewById(R.id.driverSpinner);
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

        Spinner configSpinner = findViewById(R.id.configSpinner);
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
        Intent intent = new Intent(this, AMazeActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("generation", generation);
        startActivity(intent);
        finish();
    }

    public void startGame() {
        // check if progress bar is at 100%
        // check if driver + config have been selected
        new Thread(() -> {
            while (driver == 0 || (!manual && config == 0)) {
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

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intent intent;
            if(manual) {
                intent = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                intent.putExtra("seed", seed);
                intent.putExtra("skill", skill);
                intent.putExtra("perfect", perfect);
                intent.putExtra("generation", generation);
                intent.putExtra("driver", driver);  // 1 = manual
            }
            else {
                intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                intent.putExtra("seed", seed);
                intent.putExtra("skill", skill);
                intent.putExtra("perfect", perfect);
                intent.putExtra("generation", generation);
                intent.putExtra("driver", driver);  // 2 = wizard, 3 = wallfollower
                intent.putExtra("config", config);   // 1 = premium, 2 = mediocre, 3 = soso, 4 = shaky
            }
            startActivity(intent);
        }).start();



    }
}