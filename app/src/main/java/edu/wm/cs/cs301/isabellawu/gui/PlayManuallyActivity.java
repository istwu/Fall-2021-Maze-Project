package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.isabellawu.R;

public class PlayManuallyActivity extends AppCompatActivity {

    private int steps;
    private int zoom;

    private static final String TAG = "PlayManuallyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        steps = 0;

        ToggleButton toggleMap = (ToggleButton) findViewById(R.id.toggleMapButton_manual);
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

        ToggleButton toggleSolution = (ToggleButton) findViewById(R.id.toggleSolutionButton);
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

        ToggleButton toggleWalls = (ToggleButton) findViewById(R.id.toggleWallsButton);
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

        SeekBar zoomBar = (SeekBar) findViewById(R.id.zoomSeekBar_manual);
        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                zoom = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Changing maze size", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Changing maze size");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Maze size set to " + zoom, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Maze size set to " + zoom);
            }
        });

    }

    public void forward(View view) {
        // move forward 1 step
        steps += 1;
        Toast toast = Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Forward, step count = " + steps);
    }

    public void left(View view) {
        // rotate left
        Toast toast = Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Left");
    }

    public void right(View view) {
        // rotate right
        Toast toast = Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT);
        toast.show();
        Log.v(TAG, "Right");
    }
}