package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
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

public class PlayAnimationActivity extends AppCompatActivity {

    private int zoom;
    private boolean paused;
    private int speed;
    private int energy;

    private static final String TAG = "PlayManuallyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        ToggleButton toggleMap = (ToggleButton) findViewById(R.id.toggleMapButton_auto);
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

        ImageView sensor_forward = (ImageView) findViewById(R.id.sensor_forward);
        ImageView sensor_left = (ImageView) findViewById(R.id.sensor_left);
        ImageView sensor_right = (ImageView) findViewById(R.id.sensor_right);
        ImageView sensor_backward = (ImageView) findViewById(R.id.sensor_backward);

        ProgressBar energy = (ProgressBar) findViewById(R.id.energyBar);

        SeekBar zoomBar = (SeekBar) findViewById(R.id.zoomSeekBar_auto);
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

        ImageButton pauseplay = (ImageButton) findViewById(R.id.pause_play);
        pauseplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        SeekBar animationSpeed = (SeekBar) findViewById(R.id.speedSeekBar);
        animationSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speed = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Changing speed", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Changing speed");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Speed set to " + speed, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Speed set to " + speed);
            }
        });
    }
}