package com.example.mlr3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button task1, task2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task1 = (Button) findViewById(R.id.task_1);
        task2 = (Button) findViewById(R.id.task_2);
        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void onClickTask1(View view)
    {
        Intent intent = new Intent(MainActivity.this, task1.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fall_above,R.anim.fall_down);
    }

    public void onClickTask2(View view)
    {
        Intent intent = new Intent(MainActivity.this, task2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up,R.anim.slide_in);
    }
}
