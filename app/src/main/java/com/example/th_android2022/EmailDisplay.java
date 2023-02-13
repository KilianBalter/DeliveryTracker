package com.example.th_android2022;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import androidx.core.content.ContextCompat;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.Email;
import com.example.th_android2022.Filter.AiFilter;

import java.util.Comparator;
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
        Log.i("EmailDisplay","reloading...");
        DeliveryDAO repo = new DeliveryDAO(activity);

        List<Delivery> deliveries = repo.findAllDelivery();

        LinearLayout deliveryList = (LinearLayout) activity.findViewById(R.id.listContent);

        deliveryList.removeAllViews();

        if (deliveries.size() == 0) {
            TextView textView = new TextView(activity);
            String text = "scanning emails ...";
            textView.setText(text);
            textView.setTextSize(16);
            deliveryList.addView(textView);
        } else {
            //Sort deliveries by status. To change order, change order of Status enum definitions
            deliveries.sort(Comparator.comparing(Delivery::getStatus));
            for (Delivery delivery : deliveries) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;

                //Create new row (delivery)
                ConstraintLayout row = new ConstraintLayout(activity);
                row.setId(View.generateViewId());
                deliveryList.addView(row);

                //Add divider
                int oneDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, Resources.getSystem().getDisplayMetrics());
                View divider = new View(activity);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2 * oneDP);
                dividerParams.setMargins(0, 3 * oneDP, 0, 3 * oneDP);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(ContextCompat.getColor(activity, R.color.blue_app));
                deliveryList.addView(divider);

                //Create textView to display Tag, Status and Service
                TextView infoText = new TextView(activity);
                String text = delivery.getTag() + "\n";
                text += "Order: " + (delivery.getOrderId() == null ? "?" : delivery.getOrderId()) + "\n";
                text += "Status: ";
                Delivery.Status status = delivery.getStatus();
                String service = "Service: " + (delivery.getDeliveryService() == null ? "?" : delivery.getDeliveryService());
                Spannable spannable = new SpannableString(text + status + "\n" + service);
                //Color status text depending on status
                int color;
                switch(status) {
                    case FALSE: color = Color.RED;          break;
                    case ACTIVE: color = Color.YELLOW;      break;
                    case DELIVERED: color = Color.GREEN;    break;
                    default: color = Color.WHITE;
                }
                spannable.setSpan(new ForegroundColorSpan(color), text.length(), (text + status).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                infoText.setText(spannable);
                infoText.setOnClickListener(new EmailListLoader(delivery));
                infoText.setId(View.generateViewId());
                infoText.setTextSize(16);
                infoText.setPadding(15,0,15,0);
                row.addView(infoText);

                //Create stopButton
                ImageButton stopButton = new ImageButton(activity);
                row.addView(stopButton);
                View.OnClickListener hideDelivery = new HideDeliveryListener(Delivery.Status.FALSE, delivery, deliveryList, row, false);
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


                //Create deliveredButton
                ImageButton deliveredButton = new ImageButton(activity);
                row.addView(deliveredButton);
                hideDelivery = new HideDeliveryListener(Delivery.Status.DELIVERED, delivery, deliveryList, row, true);
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


                //Place all elements in row
                ConstraintSet rowSet = new ConstraintSet();
                rowSet.clone(row);
                rowSet.connect(infoText.getId(), ConstraintSet.LEFT, row.getId(), ConstraintSet.LEFT);
                rowSet.connect(deliveredButton.getId(), ConstraintSet.RIGHT, row.getId(), ConstraintSet.RIGHT,15);
                rowSet.connect(stopButton.getId(), ConstraintSet.RIGHT, deliveredButton.getId(), ConstraintSet.LEFT, 5);
                rowSet.connect(stopButton.getId(), ConstraintSet.BOTTOM, row.getId(), ConstraintSet.BOTTOM);
                rowSet.connect(deliveredButton.getId(), ConstraintSet.BOTTOM, row.getId(), ConstraintSet.BOTTOM);
                rowSet.applyTo(row);
            }
            deliveryList.invalidate();
        }
    }


    class EmailListLoader implements View.OnClickListener {

        Delivery d;

        EmailListLoader(Delivery d) {
            this.d = d;
        }

        @Override
        public void onClick(View view) {
            activity.setContentView(R.layout.email_list);
            LinearLayout layoutEmails = (LinearLayout) activity.findViewById(R.id.listContent);

            TextView emails = new TextView(activity);
            String email = "";
            for (Email e : d.getEmailList()) {
                //If tracking link is present, add Button to TextView that opens tracking link in browser
                if(e.getTrackingLink() != null) {
                    Button openLink = new Button(activity);
                    openLink.setOnClickListener(l -> {
                        Uri webpage = Uri.parse(e.getTrackingLink());
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        activity.startActivity(webIntent);
                    });
                    openLink.setText("Open Tracking Link");
                    openLink.setBackgroundColor(ContextCompat.getColor(activity, R.color.teal_700));
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
        Delivery.Status type;
        Delivery delivery;
        LinearLayout layout;
        ConstraintLayout row;

        boolean isTrackingEmail;

        public HideDeliveryListener(Delivery.Status type, Delivery delivery, LinearLayout layout, ConstraintLayout row, boolean isTrackingEmail) {
            this.type = type;
            this.delivery = delivery;
            this.layout = layout;
            this.row = row;
            this.isTrackingEmail = isTrackingEmail;
        }

        @Override
        public void onClick(View view) {
            //Update status depending on which button was clicked
            DeliveryDAO dao = new DeliveryDAO(activity);
            Delivery d = dao.findFirstById(delivery.getId());
            d.setStatus(type);
            dao.updateById(d);

            show();
            reload();

            new Thread(() -> {
                for (Email email : delivery.getEmailList())
                    AiFilter.train(email, isTrackingEmail);
            }).start();
        }
    }

}
