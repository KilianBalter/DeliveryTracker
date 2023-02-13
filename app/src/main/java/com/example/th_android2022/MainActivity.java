package com.example.th_android2022;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th_android2022.Services.PopCreator;

public class MainActivity extends AppCompatActivity {

    private static Activity activity;

    public static void showToast(final String toast) {
        try {
            activity.runOnUiThread(() -> Toast.makeText(activity, toast, Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);

        new PopCreator(this);

        Log.d("onCreate", "Finished");
    }

}