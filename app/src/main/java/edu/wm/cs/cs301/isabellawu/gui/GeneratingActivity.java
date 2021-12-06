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

import edu.wm.cs.cs301.isabellawu.R;
import edu.wm.cs.cs301.isabellawu.generation.Maze;
import edu.wm.cs.cs301.isabellawu.generation.MazeFactory;
import edu.wm.cs.cs301.isabellawu.generation.Order;

/**
 * @author Isabella Wu
 */
public class GeneratingActivity extends AppCompatActivity implements Order {

    private int seed;
    private int skill;
    private boolean perfect;
    private Order.Builder builder;

    private int driver;
    private boolean manual;
    private int config;

    public static Maze maze;
    private MazeFactory factory;
    private ProgressBar progressBar;
    private Handler handler;
    private int percentdone;

    private static final String TAG = "GeneratingActivity";

    /**
     * Instantiates a ProgressBar to display maze generation loading
     * progress, and two Spinners to select between drivers (Manual, Wizard,
     * WallFollower) and robot configurations (Premium, Mediocre, So-so,
     * Shaky). When the progress reaches 100% and the driver/config are
     * properly selected, starts either PlayManuallyActivity or
     * PlayAnimationActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        Bundle extras = getIntent().getExtras();
        seed = extras.getInt("seed");
        skill = extras.getInt("skill");
        perfect = extras.getBoolean("perfect");
        builder = (Builder) extras.get("builder");

        manual = false;
        Spinner driverSpinner = findViewById(R.id.driverSpinner);
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 0 = none
            // 1 = manual
            // 2 = wizard
            // 3 = wallfollower
            /**
             * Sets the driver variable to an int corresponding to the selected driver,
             * and the manual boolean variable to either true or false depending on
             * whether or not the Manual option in the spinner is selected.
             * Then, displays a Toast informing the user of their choice.
             */
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
                Log.v(TAG, "Driver set to " + d);
            }

            /**
             * Empty method.
             */
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
            /**
             * Sets the config variable to an int corresponding to the selected
             * configuration. Then, displays a Toast informing the user of their choice.
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                config = i;
                switch(config){
                    case 0: Log.v(TAG, "Robot configuration set to None");
                        break;
                    case 1: Log.v(TAG, "Robot configuration set to Premium");
                        break;
                    case 2: Log.v(TAG, "Robot configuration set to Mediocre");
                        break;
                    case 3: Log.v(TAG, "Robot configuration set to So-so");
                        break;
                    case 4: Log.v(TAG, "Robot configuration set to Shaky");
                }
            }

            /**
             * Empty method.
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        factory = new MazeFactory();
        progressBar = findViewById(R.id.progressBar);
        percentdone = 0;
        handler = new Handler();
        generateMaze();
    }

    private void generateMaze() {
        new Thread(() -> {
            factory.order(this);
            factory.waitTillDelivered();
        }).start();
    }

    /**
     * Passes the seed, skill, perfect, and builder variables back
     * to AMazeActivity to be saved, then closes the activity, returning
     * the user back to the title screen.
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "Returning to title screen");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public int getSkillLevel() {
        return skill;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return perfect;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        maze = mazeConfig;
    }

    @Override
    public void updateProgress(int percentage) {
        if (percentdone < percentage && percentage <= 100) {
            percentdone = percentage;
            handler.post(() -> progressBar.setProgress(percentdone));
            if(percentdone == 100) {
                startGame();
            }
        }
    }

    /**
     * Called once the progress bar reaches 100%. If a driver is not
     * selected, displays a warning message in a TextView asking for the user
     * to select a driver. If a robot is selected as a driver and no robot
     * configuration is selected, displays another warning message asking
     * for the user to select a configuration. Once both driver and configuration
     * have been chosen, starts either PlayManuallyActivity or PlayAnimationActivity
     * depending on the driver.
     */
    public void startGame() {
        // check if progress bar is at 100%
        // check if driver + config have been selected
        new Thread(() -> {
            while (driver == 0 || (!manual && config == 0) || maze == null) {
                TextView driverWarning = (TextView) findViewById(R.id.driverWarning);
                TextView configWarning = (TextView) findViewById(R.id.configWarning);
                if(driver == 0) {
                    driverWarning.setText(getString(R.string.driver_warning));
                }
                else {
                    driverWarning.setText("");
                }
                if(!manual && config == 0) {
                    configWarning.setText(getString(R.string.config_warning));
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
                intent.putExtra("driver", driver);  // 1 = manual
            }
            else {
                intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                intent.putExtra("driver", driver);  // 2 = wizard, 3 = wallfollower
                intent.putExtra("config", config);   // 1 = premium, 2 = mediocre, 3 = soso, 4 = shaky
            }
            startActivity(intent);
            return;
        }).start();

    }
}