package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.isabellawu.R;
import edu.wm.cs.cs301.isabellawu.generation.Maze;
import edu.wm.cs.cs301.isabellawu.generation.Order;

/**
 * @author Isabella Wu
 */
public class PlayAnimationActivity extends AppCompatActivity {

    private int config;
    private int path;
    private int shortest_path;
    private int zoom;
    private boolean paused;
    private int speed;
    private int energy_used;

    private int seed;
    private int skill;
    private boolean perfect;
    private Order.Builder builder;

    private static final String TAG = "PlayAnimationActivity";

    /**
     * Displays the maze in the center of the screen.
     * Instantiates one ToggleButton to turn on and off the map,
     * the maze solution, and the maze walls, creates a SeekBar to
     * zoom in and out of the maze, and displays a ProgressBar to
     * show the robot's remaining energy. Also instantiates a
     * button to pause/play the animation, and a SeekBar to
     * adjust the animation speed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        Bundle extras = getIntent().getExtras();
        seed = extras.getInt("seed");
        skill = extras.getInt("skill");
        perfect = extras.getBoolean("perfect");
        builder = (Order.Builder) extras.get("builder");
        config = extras.getInt("config");  // 1 = premium, 2 = mediocre, 3 = soso, 4 = shaky
        // set sensors on robot based on config int value

        path = 0;
        shortest_path = 0; // set to path length of solution;

        ToggleButton toggleMap = findViewById(R.id.toggleMapButton_auto);
        toggleMap.setOnClickListener(view -> {
            if(toggleMap.isChecked()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Map on", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Map on");
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Map off", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Map off");
            }
        });

        // change the color of these depending on sensor status
        ImageView sensor_forward = findViewById(R.id.sensor_forward);
        ImageView sensor_left = findViewById(R.id.sensor_left);
        ImageView sensor_right = findViewById(R.id.sensor_right);
        ImageView sensor_backward = findViewById(R.id.sensor_backward);

        // change this based on remaining energy
        ProgressBar energy = findViewById(R.id.energyBar);
        energy.setProgress(3500);
        energy_used = 0;

//        SeekBar zoomBar = findViewById(R.id.zoomSeekBar_auto);
//        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            /**
//             * Sets the zoom variable to the value from the SeekBar.
//             */
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                zoom = i;
//            }
//
//            /**
//             * Displays a Toast message to inform user that the SeekBar is
//             * receiving their input.
//             */
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast toast = Toast.makeText(getApplicationContext(), "Changing maze size", Toast.LENGTH_SHORT);
//                toast.show();
//                Log.v(TAG, "Changing maze size");
//            }
//
//            /**
//             * Displays a Toast message to inform user that the SeekBar stopped
//             * receiving their input.
//             */
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast toast = Toast.makeText(getApplicationContext(), "Maze size set to " + zoom, Toast.LENGTH_SHORT);
//                toast.show();
//                Log.v(TAG, "Maze size set to " + zoom);
//            }
//        });

        ImageButton pauseplay = findViewById(R.id.pause_play);
        pauseplay.setOnClickListener(view -> {
            if(!paused) {
                paused = true;
                pauseplay.setImageResource(R.drawable.mr_media_play_light);
                Toast toast = Toast.makeText(getApplicationContext(), "Pausing animation", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Pausing animation");
            }
            else {
                paused = false;
                pauseplay.setImageResource(R.drawable.mr_media_pause_light);
                Toast toast = Toast.makeText(getApplicationContext(), "Playing animation", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Playing animation");
            }
        });

        SeekBar animationSpeed = findViewById(R.id.speedSeekBar);
        animationSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Sets the speed variable to the value from the SeekBar.
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = i;
            }

            /**
             * Displays a Toast message to inform user that the SeekBar is
             * receiving their input.
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Changing speed", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Changing speed");
            }

            /**
             * Displays a Toast message to inform user that the SeekBar stopped
             * receiving their input.
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Speed set to " + speed, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Speed set to " + speed);
            }
        });

        Maze maze = GeneratingActivity.maze;
        MazePanel mazePanel = findViewById(R.id.mazePanel_auto);
    }

    /**
     * Passes the seed, skill, perfect, and generation variables back
     * to AMazeActivity to be saved, then closes the activity, returning
     * the user back to the title screen.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AMazeActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("builder", builder);
        startActivity(intent);
        finish();
    }

    /**
     * Takes the user to the winning screen, passing in the user's
     * path length and the solution path length through an intent.
     */
    public void go2winning(View view) {
        // need to pass in steps, energy
        Toast toast = Toast.makeText(getApplicationContext(), "Moving to winning screen", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Moving to winning screen");
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("builder", builder);
        intent.putExtra("path", path);
        intent.putExtra("shortest path", shortest_path);
        intent.putExtra("energy used", energy_used);
        startActivity(intent);
    }

    /**
     * Takes the user to the losing screen, passing in the user's
     * path length and the solution path length through an intent.
     */
    public void go2losing(View view) {
        Toast toast = Toast.makeText(getApplicationContext(), "Moving to losing screen", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Moving to losing screen");
        // need to pass in steps, energy, reason for loss
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("builder", builder);
        intent.putExtra("path", path);
        intent.putExtra("shortest path", shortest_path);
        intent.putExtra("energy used", energy_used);
        startActivity(intent);
    }
}