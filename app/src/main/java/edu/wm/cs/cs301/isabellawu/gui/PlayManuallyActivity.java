package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.isabellawu.R;

/**
 * @author Isabella Wu
 */
public class PlayManuallyActivity extends AppCompatActivity {

    private int path;
    private int shortest_path;
    private int zoom;

    private int seed;
    private int skill;
    private boolean perfect;
    private int generation;

    private static final String TAG = "PlayManuallyActivity";

    /**
     * Displays the maze in the center of the screen.
     * Instantiates three ToggleButtons to turn on and off the map,
     * the maze solution, and the maze walls, creates a SeekBar to
     * zoom in and out of the maze, and three arrow buttons used to
     * navigate through the maze.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        Bundle extras = getIntent().getExtras();
        seed = extras.getInt("seed");
        skill = extras.getInt("skill");
        perfect = extras.getBoolean("perfect");
        generation = extras.getInt("generation");

        path = 0;
        shortest_path = 0; // set to path length of solution;

        ToggleButton toggleMap = findViewById(R.id.toggleMapButton_manual);
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

        ToggleButton toggleSolution = findViewById(R.id.toggleSolutionButton);
        toggleSolution.setOnClickListener(view -> {
            if(toggleSolution.isChecked()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Solution on", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Solution on");
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Solution off", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Solution off");
            }
        });

        ToggleButton toggleWalls = findViewById(R.id.toggleWallsButton);
        toggleWalls.setOnClickListener(view -> {
            if(toggleWalls.isChecked()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Walls on", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Walls on");
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Walls off", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Walls off");
            }
        });

        SeekBar zoomBar = findViewById(R.id.zoomSeekBar_manual);
        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Sets the zoom variable to the value from the SeekBar.
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                zoom = i;
            }

            /**
             * Displays a Toast message to inform user that the SeekBar is
             * receiving their input.
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Changing maze size", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Changing maze size");
            }

            /**
             * Displays a Toast message to inform user that the SeekBar stopped
             * receiving their input.
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Maze size set to " + zoom, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Maze size set to " + zoom);
            }
        });

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
        intent.putExtra("generation", generation);
        startActivity(intent);
        finish();
    }

    /**
     * Moves the player forward one step in the maze, and increments
     * the step counter by 1.
     */
    public void forward(View view) {
        // move forward 1 step
        path += 1;
        Toast toast = Toast.makeText(getApplicationContext(), "Forward (step count: " + path + ")", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Forward (step count: " + path + ")");
    }

    /**
     * Makes the player turn left in the maze.
     */
    public void left(View view) {
        // rotate left
        Toast toast = Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Left");
    }

    /**
     * Makes the player turn right in the maze.
     */
    public void right(View view) {
        // rotate right
        Toast toast = Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Right");
    }

    /**
     * Takes the user to the winning screen, passing in the user's
     * path length and the solution path length through an intent.
     */
    public void go2winning(View view) {
        // need to pass in steps
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("generation", generation);
        intent.putExtra("path", path);
        intent.putExtra("shortest path", shortest_path);
        startActivity(intent);
    }

    /**
     * Takes the user to the losing screen, passing in the user's
     * path length and the solution path length through an intent.
     */
    public void go2losing(View view) {
        // need to pass in steps, energy, reason for loss
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("generation", generation);
        intent.putExtra("path", path);
        intent.putExtra("shortest path", shortest_path);
        startActivity(intent);
    }
}