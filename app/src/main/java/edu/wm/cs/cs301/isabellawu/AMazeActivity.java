package edu.wm.cs.cs301.isabellawu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AMazeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startGenerating(View view){
        Intent intent = new Intent(this, GeneratingActivity.class);
        startActivity(intent);
    }
}