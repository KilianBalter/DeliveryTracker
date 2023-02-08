package com.example.th_android2022;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    /**
     * creates new EmailDisplay
     * logout will be the action if the user logs out
     *
     * @param activity application activity
     * @param logout   onCklicklistener to logout the user
     */
    public EmailDisplay(Activity activity, View.OnClickListener logout) {
        this.activity = activity;
        this.logout = logout;
    }

    /**
     * shows email list and sets onClickListener
     */
    public void show() {
        activity.setContentView(R.layout.delivery_list);
        activity.findViewById(R.id.back).setOnClickListener(new ConfirmListener(activity, "Delete your account?", logout, (c) -> {
            show();
            reload();
        }));
        activity.findViewById(R.id.reload).setOnClickListener((View v) -> reload());
    }

    /**
     * fills email list with data from database
     */
    public void reload() {
        System.out.println("reloading...");
        DeliveryDAO repo = new DeliveryDAO(activity);

        List<Delivery> deliveries = repo.findAllDelivery();

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.scrollLayout);

        layout.removeAllViews();

        for (Delivery delivery : deliveries) {
            //TODO filter delivery by status

            //create new row
            ConstraintLayout row = new ConstraintLayout(activity);
            row.setId(View.generateViewId());
            layout.addView(row);

            //create textView
            TextView textView = new TextView(activity);
            String text = delivery.getTag() + "\n";
            if (delivery.getOrderId() != null)
                text += "Order: " + delivery.getOrderId() + "\n";
            if (delivery.getStatus() != null)
                text += "Status: " + delivery.getStatus();
            textView.setText(text);
            textView.setOnClickListener(new EmailListLoader(delivery));
            textView.setId(View.generateViewId());
            row.addView(textView);

            //create stopButton
            ImageButton stopButton = new ImageButton(activity);
            row.addView(stopButton);
            View.OnClickListener hideDelivery = new HideDeliveryListener(delivery, layout, row, false);
            View.OnClickListener deliveredListener = new ConfirmListener(activity, "Hide this because it was not a delivery?", hideDelivery, (c) -> {
                show();
                reload();
            });
            stopButton.setOnClickListener(deliveredListener);
            stopButton.setId(View.generateViewId());
            stopButton.setImageResource(R.drawable.redstop);
            stopButton.setScaleType(ImageView.ScaleType.FIT_XY);
            stopButton.setAdjustViewBounds(true);
            android.view.ViewGroup.LayoutParams params = stopButton.getLayoutParams();
            params.height = 175;
            stopButton.setLayoutParams(params);


            //create deliveredButton
            ImageButton deliveredButton = new ImageButton(activity);
            row.addView(deliveredButton);
            hideDelivery = new HideDeliveryListener(delivery, layout, row, true);
            deliveredListener = new ConfirmListener(activity, "Has the Package been delivered?", hideDelivery, (c) -> {
                show();
                reload();
            });
            deliveredButton.setOnClickListener(deliveredListener);
            deliveredButton.setId(View.generateViewId());
            deliveredButton.setImageResource(R.drawable.greendelivered);
            deliveredButton.setScaleType(ImageView.ScaleType.FIT_XY);
            deliveredButton.setAdjustViewBounds(true);
            android.view.ViewGroup.LayoutParams paramsDelivered = deliveredButton.getLayoutParams();
            paramsDelivered.height = 175;
            deliveredButton.setLayoutParams(paramsDelivered);


            //place all elements in row
            ConstraintSet rowSet = new ConstraintSet();
            rowSet.clone(row);
            rowSet.connect(textView.getId(), ConstraintSet.LEFT, row.getId(), ConstraintSet.LEFT, 10);
            rowSet.connect(deliveredButton.getId(), ConstraintSet.RIGHT, row.getId(), ConstraintSet.RIGHT, 0);
            rowSet.connect(stopButton.getId(), ConstraintSet.RIGHT, deliveredButton.getId(), ConstraintSet.LEFT, 0);
            rowSet.applyTo(row);
        }
        layout.invalidate();
    }


    class EmailListLoader implements View.OnClickListener {

        Delivery d;

        EmailListLoader(Delivery d) {
            this.d = d;
        }

        @Override
        public void onClick(View view) {
            activity.setContentView(R.layout.email_list);
            LinearLayout layoutEmails = (LinearLayout) activity.findViewById(R.id.scrollLayout);

            TextView emails = new TextView(activity);
            String email = "";
            for (Email e : d.getEmailList()) {
                //TODO button to open tracking link in browser intent, if tracking link present
                email += e.getContent() + "\n#############################################\n\n";
            }
            emails.setText(email);
            emails.setTextIsSelectable(true);
            layoutEmails.addView(emails);

            activity.findViewById(R.id.back).setOnClickListener(v -> {
                show();
                reload();
            });
        }
    }


    class HideDeliveryListener implements View.OnClickListener {
        Delivery delivery;
        LinearLayout layout;
        ConstraintLayout row;

        boolean isTrackingEmail;

        public HideDeliveryListener(Delivery delivery, LinearLayout layout, ConstraintLayout row, boolean isTrackingEmail) {
            this.delivery = delivery;
            this.layout = layout;
            this.row = row;
            this.isTrackingEmail = isTrackingEmail;
        }

        @Override
        public void onClick(View view) {


            DeliveryDAO dao = new DeliveryDAO(activity);
            dao.deleteById(delivery.getId());               //TODO: set status instead of deleting
            show();
            reload();

            new Thread(() -> {
                for (Email email : delivery.getEmailList())
                    AiFilter.train(email, isTrackingEmail);
            }).start();
        }
    }

}
