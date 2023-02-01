package com.example.th_android2022;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.Email;

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
            TextView textView = new TextView(activity);
            textView.setText(delivery.getTag() + "\n" +
                    "Order: " + delivery.getOrderId() + "\n" +
                    "Status: " + delivery.getStatus());

            textView.setOnClickListener(View -> {
                layout.removeAllViews();
                TextView emails = new TextView(activity);
                String  text = "";
                for(Email e: delivery.getEmailList()){
                    text += e.getContent() + "\n#############################################\n\n";
                }
                emails.setText(text);
                layout.addView(emails);
            });

            layout.addView(textView);
        }
        layout.invalidate();
    }
}
