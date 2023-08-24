package com.example.scannerr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ManualAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add);

        Button btnManualAddition2 = findViewById(R.id.btnManualAddition2);
        btnManualAddition2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to MainActivity2
                Intent intent = new Intent(ManualAdd.this, MainActivity3.class);
                startActivity(intent);
                finish(); // Optional, use if you don't want to keep the current activity in the stack
            }
        });
    }
}
