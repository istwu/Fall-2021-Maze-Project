package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import edu.wm.cs.cs301.isabellawu.R;

public class WinningActivity extends AppCompatActivity {

    private int path;
    private int shortest_path;
    private int energy_used;

    private int seed;
    private int skill;
    private boolean perfect;
    private int generation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

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

        TextView pathLength = (TextView) findViewById(R.id.pathLength);
        pathLength.setText("Path length: " + path);
        TextView shortestPath = (TextView) findViewById(R.id.shortestPath);
        shortestPath.setText("Shortest path: " + shortest_path);
        TextView energyUsed = (TextView) findViewById(R.id.energyUsed);
        if(extras.containsKey("energy used")) {
            energyUsed.setText("Energy used: " + energy_used);
        }

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
}