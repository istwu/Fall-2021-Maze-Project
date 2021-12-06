package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.wm.cs.cs301.isabellawu.R;

/**
 * @author Isabella Wu
 */
public class WinningActivity extends AppCompatActivity {

    private int path;
    private int shortest_path;
    private int energy_used;

    private static final String TAG = "WinningActivity";

    /**
     * Instantiates TextViews to show the user has won, their
     * path length, and the solution path length. If the user had
     * been using a robot to go through the maze, also displays
     * the amount of energy used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        Bundle extras = getIntent().getExtras();
        path = extras.getInt("path");
        shortest_path = extras.getInt("shortest path");
        if(extras.containsKey("energy used")) {
            energy_used = extras.getInt("energy used");
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
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        this.finish();
    }
}