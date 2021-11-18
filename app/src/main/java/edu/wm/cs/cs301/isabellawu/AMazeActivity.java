package edu.wm.cs.cs301.isabellawu;

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

public class AMazeActivity extends AppCompatActivity {

    private int skill;
    private boolean perfect;
    private int generation; // 0 = DFS, 1 = Prim, 2 = Boruvka

    private SeekBar skillSeekBar;
    private Switch roomSwitch;
    private TextView skillText;
    private Spinner algoSpinner;

    private static final String TAG = "AMazeActivity";
    private Object AdapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        skillText = (TextView) findViewById(R.id.skillText);
        skillSeekBar = (SeekBar) findViewById(R.id.skillSeekBar);
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

        roomSwitch = (Switch) findViewById(R.id.roomSwitch);
        roomSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomSwitch.isChecked()) {
                    perfect = false;
                }
                else {
                    perfect = true;
                }
                Toast toast = Toast.makeText(getApplicationContext(), "Rooms set to " + !perfect, Toast.LENGTH_SHORT);
                toast.show();
                Log.v(TAG, "Rooms set to " + !perfect);
            }
        });

        algoSpinner = (Spinner) findViewById(R.id.algoSpinner);
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

    public void startGenerating(View view){
        Intent intent = new Intent(this, GeneratingActivity.class);
        startActivity(intent);
    }
}