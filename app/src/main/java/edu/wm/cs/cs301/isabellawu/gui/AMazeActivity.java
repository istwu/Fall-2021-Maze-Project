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

import java.util.Random;

import edu.wm.cs.cs301.isabellawu.R;

public class AMazeActivity extends AppCompatActivity {

    private int seed;
    private int skill;
    private boolean perfect;
    private int generation; // 0 = DFS, 1 = Prim, 2 = Boruvka
    private boolean revisit;

    private static final String TAG = "AMazeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView skillText = (TextView) findViewById(R.id.skillText);
        SeekBar skillSeekBar = (SeekBar) findViewById(R.id.skillSeekBar);
        skillSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                skill = progress;
                skillText.setText("" + skill);
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

        Switch roomSwitch = (Switch) findViewById(R.id.roomSwitch);
        roomSwitch.setOnClickListener(view -> {
            perfect = !roomSwitch.isChecked();
            Toast toast = Toast.makeText(getApplicationContext(), "Rooms set to " + !perfect, Toast.LENGTH_SHORT);
            toast.show();
            Log.v(TAG, "Rooms set to " + !perfect);
        });

        Spinner algoSpinner = (Spinner) findViewById(R.id.algoSpinner);
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
        Intent intent = new Intent(this, GeneratingActivity.class);
        startActivity(intent);
    }

    public void generateNewMaze(View view) {
        Random random = new Random();
        seed = random.nextInt();
        Intent intent = new Intent(this, GeneratingActivity.class);
        startActivity(intent);
    }
}