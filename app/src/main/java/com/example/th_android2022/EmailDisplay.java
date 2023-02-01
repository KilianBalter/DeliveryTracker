package com.example.th_android2022;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;

import java.util.List;

public class EmailDisplay {

    Activity activity;

    public EmailDisplay(Activity activity) {
        this.activity = activity;
    }


    public void show(){
        activity.setContentView(R.layout.delivery_list);

        activity.findViewById(R.id.reload).setOnClickListener((View v) -> reload());
    }

    public void reload(){
        System.out.println("reloading...");
        DeliveryDAO repo = new DeliveryDAO(activity);

        List<Delivery> deliveries = repo.findAllDelivery();

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.scrollLayout);

        layout.removeAllViews();

        for(Delivery delivery: deliveries){
            TextView text = new TextView(activity);
            text.setText(delivery.getTag() + "\n" +
                    "Order: " + delivery.getOrderId() + "\n" +
                    "Status: " + delivery.getStatus());
            layout.addView(text);

        }
        layout.invalidate();
    }
}
