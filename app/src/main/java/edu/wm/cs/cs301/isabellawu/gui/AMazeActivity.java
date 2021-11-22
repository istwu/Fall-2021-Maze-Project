package edu.wm.cs.cs301.isabellawu.gui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Random;

import edu.wm.cs.cs301.isabellawu.R;

public class AMazeActivity extends AppCompatActivity {

    private int seed;
    private int skill;
    private boolean perfect;
    private int generation; // 0 = DFS, 1 = Prim, 2 = Boruvka
    private HashMap prev_values;

    private static final String TAG = "AMazeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prev_values = new HashMap<String, Object>();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if (extras.containsKey("seed")) {
                prev_values.put("seed", extras.getInt("seed"));
            }
            if (extras.containsKey("skill")) {
                prev_values.put("skill", extras.getInt("skill"));
            }
            if (extras.containsKey("perfect")) {
                prev_values.put("perfect", extras.getBoolean("perfect"));
            }
            if (extras.containsKey("generation")) {
                prev_values.put("generation", extras.getInt("generation"));
            }
        }

        TextView skillText = findViewById(R.id.skillText);
        skillText.setText(getString(R.string.skill_level, 0));
        SeekBar skillSeekBar = findViewById(R.id.skillSeekBar);
        skillSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                skill = progress;
                skillText.setText(getString(R.string.skill_level, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Setting difficulty", Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Setting difficulty");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Difficulty set to " + skill, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Difficulty set to " + skill);
            }
        });

        Switch roomSwitch = findViewById(R.id.roomSwitch);
        roomSwitch.setOnClickListener(view -> {
            perfect = !roomSwitch.isChecked();
            Toast toast = Toast.makeText(getApplicationContext(), "Rooms set to " + !perfect, Toast.LENGTH_SHORT);
            toast.show();
            Log.v(TAG, "Rooms set to " + !perfect);
        });

        Spinner algoSpinner = findViewById(R.id.algoSpinner);
        algoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int i, long l) {
                // i = position
                // l = id
                generation = i;
                String algo = "";
                switch(generation) {
                    case 0: algo = "DFS";
                    break;
                    case 1: algo = "Prim";
                    break;
                    case 2: algo = "Boruvka";
                    break;
                }
                Toast toast = Toast.makeText(getApplicationContext(), "Generation algorithm set to " + algo, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Generation algorithm set to " + algo);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {
            }
        });
    }

    public void getOldMaze(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "No previous maze to explore", Toast.LENGTH_SHORT);
            toast.show();
            Log.v(TAG, "No previous maze to explore");
        }
        else {
            Intent intent = new Intent(this, GeneratingActivity.class);
            intent.putExtra("seed", (int) prev_values.get("seed"));
            intent.putExtra("skill", (int) prev_values.get("skill"));
            intent.putExtra("perfect", (boolean) prev_values.get("perfect"));
            intent.putExtra("generation", (int) prev_values.get("generation"));
            Log.v(TAG, "Seed: " + prev_values.get("seed"));
            Log.v(TAG, "Skill level: " + prev_values.get("skill"));
            Log.v(TAG, "Rooms included: " + prev_values.get("perfect"));
            switch((int) prev_values.get("generation")) {
                case 0:
                    Log.v(TAG, "Generation algorithm: DFS");
                    break;
                case 1:
                    Log.v(TAG, "Generation algorithm: Prim");
                    break;
                case 2:
                    Log.v(TAG, "Generation algorithm: Boruvka");
                    break;
            }
            startActivity(intent);
        }
    }

    public void generateNewMaze(View view) {
        Random random = new Random();
        seed = random.nextInt();
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("seed", seed);
        intent.putExtra("skill", skill);
        intent.putExtra("perfect", perfect);
        intent.putExtra("generation", generation);
        Log.v(TAG, "Seed: " + seed);
        Log.v(TAG, "Skill level: " + skill);
        Log.v(TAG, "Rooms included: " + perfect);
        switch(generation) {
            case 0:
                Log.v(TAG, "Generation algorithm: DFS");
                break;
            case 1:
                Log.v(TAG, "Generation algorithm: Prim");
                break;
            case 2:
                Log.v(TAG, "Generation algorithm: Boruvka");
                break;
        }
        startActivity(intent);
    }
}