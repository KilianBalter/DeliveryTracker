package com.example.th_android2022;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.Email;
import com.example.th_android2022.Filter.AiFilter;

import java.util.List;

public class EmailDisplay {

    Activity activity;
    View.OnClickListener logout;

    public EmailDisplay(Activity activity, View.OnClickListener logout) {
        this.activity = activity;
        this.logout = logout;
    }


    public void show(){
        activity.setContentView(R.layout.delivery_list);
        activity.findViewById(R.id.back).setOnClickListener(logout);
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

                activity.setContentView(R.layout.email_list);
                LinearLayout layoutEmails = (LinearLayout) activity.findViewById(R.id.scrollLayout);

                TextView emails = new TextView(activity);
                String  text = "";
                for(Email e: delivery.getEmailList()){
                    text += e.getContent() + "\n#############################################\n\n";
                }
                emails.setText(text);
                emails.setTextIsSelectable(true);
                layoutEmails.addView(emails);

                activity.findViewById(R.id.back).setOnClickListener(v -> {
                    show();
                    reload();
                });
            });

            ConstraintLayout row = new ConstraintLayout(activity);
            row.setId(View.generateViewId());
            layout.addView(row);
//            View divider = new View(activity);
//            divider.setBackgroundResource(R.color.blue_app);
//            android.view.ViewGroup.LayoutParams dividerParams = new android.view.ViewGroup.LayoutParams(1, 100);
////            dividerParams.height = 1;
//            divider.setLayoutParams(dividerParams);
//            layout.addView(divider);

            textView.setId(View.generateViewId());
            row.addView(textView);
            ImageButton stopButton = new ImageButton(activity);
            stopButton.setOnClickListener((v)->{
                //train ai
                new Thread(() -> {
                    DeliveryDAO dao = new DeliveryDAO(activity);
                    for(Email email: dao.findFirstById(delivery.getId()).getEmailList())
                        AiFilter.train(email, false);
                    dao.deleteById(delivery.getId());
                }).start();

                layout.removeView(row);
            });
            stopButton.setId(View.generateViewId());
            row.addView(stopButton);
            stopButton.setImageResource(R.drawable.redstop);
            stopButton.setScaleType(ImageView.ScaleType.FIT_XY);
            stopButton.setAdjustViewBounds(true);
            android.view.ViewGroup.LayoutParams params = stopButton.getLayoutParams();
            params.height = 175;
            stopButton.setLayoutParams(params);


            ImageButton deliveredButton = new ImageButton(activity);
            deliveredButton.setOnClickListener((v)->{

                new Thread(() -> {
                    DeliveryDAO dao = new DeliveryDAO(activity);
                    for(Email email: dao.findFirstById(delivery.getId()).getEmailList())
                        AiFilter.train(email, true);
                    dao.deleteById(delivery.getId());
                }).start();
                layout.removeView(row);
            });
            deliveredButton.setId(View.generateViewId());
            row.addView(deliveredButton);
            deliveredButton.setImageResource(R.drawable.greendelivered);
            deliveredButton.setScaleType(ImageView.ScaleType.FIT_XY);
            deliveredButton.setAdjustViewBounds(true);
            android.view.ViewGroup.LayoutParams paramsDelivered = deliveredButton.getLayoutParams();
            paramsDelivered.height = 175;
            deliveredButton.setLayoutParams(paramsDelivered);

            ConstraintSet rowSet = new ConstraintSet();
            rowSet.clone(row);
            rowSet.connect(textView.getId(),ConstraintSet.LEFT,row.getId(),ConstraintSet.LEFT,10);
            rowSet.connect(stopButton.getId(),ConstraintSet.RIGHT,row.getId(),ConstraintSet.RIGHT,0);
            rowSet.connect(deliveredButton.getId(),ConstraintSet.RIGHT,stopButton.getId(),ConstraintSet.LEFT,0);
            rowSet.applyTo(row);
        }
        layout.invalidate();

    }

}
