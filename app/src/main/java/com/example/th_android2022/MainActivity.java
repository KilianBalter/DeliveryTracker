package com.example.th_android2022;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Databases.UserDataDAO;
import com.example.th_android2022.Services.GmailReceiver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GmailReceiver receiver = new GmailReceiver(this);

        DeliveryDAO deliveryDAO = new DeliveryDAO(this.getApplicationContext());

        UserDataDAO userDataDAO = new UserDataDAO(this.getApplicationContext(), "Gmail");  //TODO an gmailReceiver uebergeben

        Log.d("onCreate", "Finished");
    }

}