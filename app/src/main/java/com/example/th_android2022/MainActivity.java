package com.example.th_android2022;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.th_android2022.Services.PopReceiver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PopReceiver receiver = new PopReceiver(this);

        Log.d("onCreate", "Finished");
    }


}