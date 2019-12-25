package com.example.asmstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sayHello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InjectTest injectTest=new InjectTest();
                injectTest.sayHello();
            }
        });
    }
}
