package com.example.th_android2022;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Databases.UserDataDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Services.GmailReceiver;
import com.example.th_android2022.Services.Receiver;
import com.example.th_android2022.Services.TonlineReceiver;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TonlineReceiver receiver = new TonlineReceiver(this);

        Log.d("onCreate", "Finished");
    }


}