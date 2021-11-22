package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import edu.wm.cs.cs301.isabellawu.R;

/**
 * @author Isabella Wu
 */
public class LosingActivity extends AppCompatActivity {

    private int path;
    private int shortest_path;
    private int energy_used;

    private int seed;
    private int skill;
    private boolean perfect;
    private int generation;

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

        Bundle extras = getIntent().getExtras();
        seed = extras.getInt("seed");
        skill = extras.getInt("skill");
        perfect = extras.getBoolean("perfect");
        generation = extras.getInt("generation");
        path = extras.getInt("path");
        shortest_path = extras.getInt("shortest path");
        if(extras.containsKey("energy used")) {
            energy_used = extras.getInt("energy used");
        }

        TextView losing = findViewById(R.id.losing);
        // set text of losing based on reason for loss (placeholder)
        losing.setText(getString(R.string.no_energy));

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
        Intent intent = new Intent(this, AMazeActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("generation", generation);
        startActivity(intent);
        finish();
    }
}