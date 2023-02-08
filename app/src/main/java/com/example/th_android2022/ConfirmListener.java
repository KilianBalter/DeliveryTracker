package com.example.th_android2022;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class ConfirmListener implements View.OnClickListener {

    Activity activity;
    String question;
    View.OnClickListener yes;
    View.OnClickListener no;

    public ConfirmListener(Activity activity, String question, View.OnClickListener yes, View.OnClickListener no) {
        this.activity = activity;
        this.question = question;
        this.yes = yes;
        this.no = no;
    }

    /**
     * shows the confirm screen on the ui and sets onClickListener of yes and no button
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        activity.setContentView(R.layout.confirm_layout);
        ((TextView) activity.findViewById(R.id.text_question)).setText(question);
        activity.findViewById(R.id.b_no).setOnClickListener(no);
        activity.findViewById(R.id.b_yes).setOnClickListener(yes);
    }
}
