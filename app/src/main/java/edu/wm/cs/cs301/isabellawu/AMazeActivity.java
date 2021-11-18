package edu.wm.cs.cs301.isabellawu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AMazeActivity extends AppCompatActivity {

    private int skill;
    private boolean perfect;
    private int generation; // 0 = DFS, 1 = Prim, 2 = Boruvka

    SeekBar skillSeekBar;
    Switch roomSwitch;
    TextView skillText;

    private static final String TAG = "AMazeActivity";

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

            }
        });

    }

    public void startGenerating(View view){
        Intent intent = new Intent(this, GeneratingActivity.class);
        startActivity(intent);
    }
}