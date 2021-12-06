package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;

import edu.wm.cs.cs301.isabellawu.R;
import edu.wm.cs.cs301.isabellawu.generation.Order;

/**
 * @author Isabella Wu
 */
public class AMazeActivity extends AppCompatActivity {

    private int seed;
    private int skill;
    private boolean perfect;
    private Order.Builder builder;

    private static final String TAG = "AMazeActivity";

    /**
     * Instantiates a SeekBar for skill level, a switch to toggle rooms in
     * the maze, a Spinner to choose between generation algorithms
     * (DFS, Prim, Boruvka), and buttons to either re-generate a maze
     * using previous parameters (seed, skill level, rooms, generation algorithm)
     * or to generate a new maze based on user input.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView skillText = findViewById(R.id.skillText);
        skillText.setText(getString(R.string.skill_level, 0));
        SeekBar skillSeekBar = findViewById(R.id.skillSeekBar);
        skillSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Changes the text on the screen to display the skill level.
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                skill = progress;
                skillText.setText(getString(R.string.skill_level, progress));
            }

            /**
             * Sends a Logcat message to inform user that the SeekBar is
             * receiving their input.
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "Setting difficulty");
            }

            /**
             * Sends a Logcat message to inform user that the SeekBar stopped
             * receiving their input.
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "Difficulty set to " + skill);
            }
        });

        Switch roomSwitch = findViewById(R.id.roomSwitch);
        roomSwitch.setOnClickListener(view -> {
            perfect = !roomSwitch.isChecked();
            Log.v(TAG, "Rooms set to " + !perfect);
        });

        Spinner algoSpinner = findViewById(R.id.algoSpinner);
        algoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Sets the generation instance variable to an int depending
             * on the generation algorithm chosen by the user.
             */
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int i, long l) {
                // i = position
                // l = id
                switch(i) {
                    case 0: {
                        builder = Order.Builder.DFS;
                        break;
                    }
                    case 1: {
                        builder = Order.Builder.Prim;
                        break;
                    }
                    case 2: {
                        builder = Order.Builder.Boruvka;
                        break;
                    }
                }
                Log.v(TAG, "Builder set to " + builder);
            }

            /**
             * Empty method.
             */
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Starts GeneratingActivity using the values stored for the
     * previously generated maze.
     */
    public void getOldMaze(View view) {                 // FIX THIS
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if(!sharedPref.contains("seed")) {
            Log.v(TAG, "No previous maze to explore, generating new maze");
            generateNewMaze(view);
        }
        else {
            Log.v(TAG, "Revisiting old maze with the following values:");
            Log.v(TAG, "Seed: " + sharedPref.getInt("seed", seed));
            Log.v(TAG, "Skill level: " + sharedPref.getInt("skill", skill));
            Log.v(TAG, "Rooms included: " + sharedPref.getBoolean("perfect", perfect));
            switch(sharedPref.getInt("builderInt", 0)) {
                case 0:
                    Log.v(TAG, "Builder: DFS");
                    break;
                case 1:
                    Log.v(TAG, "Builder: Prim");
                    break;
                case 2:
                    Log.v(TAG, "Builder: Boruvka");
                    break;
            }

            Intent intent = new Intent(this, GeneratingActivity.class);
            intent.putExtra("seed", sharedPref.getInt("seed", seed));
            intent.putExtra("skill", sharedPref.getInt("skill", skill));
            intent.putExtra("perfect", sharedPref.getBoolean("perfect", perfect));
            switch(sharedPref.getInt("builderInt", 0)) {
                case 0: intent.putExtra("builder", Order.Builder.DFS);
                break;
                case 1: intent.putExtra("builder", Order.Builder.Prim);
                break;
                case 2: intent.putExtra("builder", Order.Builder.Boruvka);
                break;
            }
            startActivity(intent);
        }
    }

    /**
     * Randomly generates a seed and starts GeneratingActivity
     * using the values set by the user.
     */
    public void generateNewMaze(View view) {
        Random random = new Random();
        seed = random.nextInt();
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("builder", builder);

        // saving to shared preferences:
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("seed", seed);
        editor.putInt("skill", skill);
        editor.putBoolean("perfect", perfect);
        int builderInt = 0;
        switch(builder) {
            case DFS: builderInt = 0;
            break;
            case Prim: builderInt = 1;
            break;
            case Boruvka: builderInt = 2;
            break;
        }
        editor.putInt("builderInt", builderInt);
        editor.apply();

        Log.v(TAG, "Generating new maze with the following values:");
        Log.v(TAG, "Seed: " + seed);
        Log.v(TAG, "Skill level: " + skill);
        Log.v(TAG, "Rooms included: " + perfect);
        switch(builder) {
            case DFS:
                Log.v(TAG, "Builder: DFS");
                break;
            case Prim:
                Log.v(TAG, "Builder: Prim");
                break;
            case Boruvka:
                Log.v(TAG, "Builder: Boruvka");
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}