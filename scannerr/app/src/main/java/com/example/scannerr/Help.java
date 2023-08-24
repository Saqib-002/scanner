package com.example.scannerr;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        Button backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main3);
                finish();
            }
        });
    }
}
