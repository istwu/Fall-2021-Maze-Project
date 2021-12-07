package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.wm.cs.cs301.isabellawu.R;

/**
 * @author Isabella Wu
 */
public class LosingActivity extends AppCompatActivity {

    private int path;
    private int shortest_path;
    private int energy_used;
    private int losing;

    private static final String TAG = "LosingActivity";

    /**
     * Instantiates TextViews to show the user has lost, the reason
     * why they lost (eg. crashed into a wall, ran out of energy), their path length,
     * and the solution path length. If the user had been using a robot to go through
     * the maze, also displays the amount of energy used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);

        if(AMazeActivity.musicPlayer == null) {
            AMazeActivity.musicPlayer = MediaPlayer.create(LosingActivity.this, R.raw.game_over);
            AMazeActivity.musicPlayer.start();
            AMazeActivity.musicPlayer.setLooping(true);
        }

        Bundle extras = getIntent().getExtras();
        path = extras.getInt("path");
        shortest_path = extras.getInt("shortest path");
        if(extras.containsKey("energy used")) {
            energy_used = extras.getInt("energy used");
        }
        // 0 = crashed into wall
        // 1 = ran out of energy
        // 2 = jumped over border wall
        // 3 = stuck in loop
        losing = extras.getInt("losing");

        TextView losingReason = findViewById(R.id.losingReason);
        // set text of losing based on reason for loss (placeholder)
        switch(losing) {
            case 0: {
                losingReason.setText(getString(R.string.crashed));
                break;
            }
            case 1: {
                losingReason.setText(getText(R.string.no_energy));
                break;
            }
            case 2: {
                losingReason.setText(getText(R.string.jump_border));
                break;
            }
            case 3: {
                losingReason.setText(getText(R.string.looping));
                break;
            }
        }


        TextView pathLength = findViewById(R.id.pathLength);
        pathLength.setText(getString(R.string.path_length, path));
        TextView shortestPath = findViewById(R.id.shortestPath);
        shortestPath.setText(getString(R.string.shortest_path, shortest_path));
        TextView energyUsed = findViewById(R.id.energyUsed);
        if(extras.containsKey("energy used")) {
            energyUsed.setText(getString(R.string.energy_used, energy_used));
        }
    }

    /**
     * Passes the seed, skill, perfect, and generation variables back
     * to AMazeActivity to be saved, then closes the activity, returning
     * the user back to the title screen.
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "Returning to title screen");
        if(AMazeActivity.musicPlayer != null) {
            AMazeActivity.musicPlayer.release();
            AMazeActivity.musicPlayer = null;
        }
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        this.finish();
    }
}