package com.example.th_android2022;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
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
import com.google.common.collect.Lists;

import java.util.List;

public class EmailDisplay {

    Activity activity;
    View.OnClickListener logout;

    /**
     * creates new EmailDisplay
     * logout will be the action if the user logs out
     *
     * @param activity application activity
     * @param logout   onClickListener to logout the user
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

        for (Delivery delivery : Lists.reverse(deliveries)) {
            //TODO filter delivery by status

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            //create new row
            ConstraintLayout row = new ConstraintLayout(activity);
            row.setId(View.generateViewId());
            layout.addView(row);
            //row.setBackgroundResource(R.drawable.layout_bg);

            //Add divider
            int oneDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, Resources.getSystem().getDisplayMetrics());
            View divider = new View(activity);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2*oneDP);
            dividerParams.setMargins(0,3*oneDP,0,3*oneDP);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(activity.getResources().getColor(R.color.blue_app));
            layout.addView(divider);

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
            textView.setTextSize(16);
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
            stopButton.setImageResource(R.mipmap.red_foreground);
            stopButton.setScaleType(ImageView.ScaleType.FIT_XY);
            stopButton.setAdjustViewBounds(true);
            android.view.ViewGroup.LayoutParams params = stopButton.getLayoutParams();
            params.height = height / 13;
            stopButton.setLayoutParams(params);
            stopButton.setBackgroundResource(R.drawable.layout_bg);


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
            deliveredButton.setImageResource(R.mipmap.green_foreground);
            deliveredButton.setScaleType(ImageView.ScaleType.FIT_XY);
            deliveredButton.setAdjustViewBounds(true);
            android.view.ViewGroup.LayoutParams paramsDelivered = deliveredButton.getLayoutParams();
            paramsDelivered.height = height / 13;
            deliveredButton.setLayoutParams(paramsDelivered);
            deliveredButton.setBackgroundResource(R.drawable.layout_bg);


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
                //If tracking link is present, add Button to TextView that opens tracking link in browser intent
                if(e.getTrackingLink() != null) {
                    Button openLink = new Button(activity);
                    openLink.setOnClickListener(l -> {
                        Uri webpage = Uri.parse(e.getTrackingLink());
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        activity.startActivity(webIntent);
                    });
                    openLink.setText("Open Tracking Link");
                    openLink.setBackgroundColor(0xFF15A4C8);
                    layoutEmails.addView(openLink);
                }
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
