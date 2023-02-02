package com.example.th_android2022;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.th_android2022.Services.PopCreator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new PopCreator(this);

        Log.d("onCreate", "Finished");
    }


}