package com.example.th_android2022.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Databases.UserDataDAO;
import com.example.th_android2022.EmailDisplay;
import com.example.th_android2022.R;

import java.util.Calendar;

public class PopCreator {


    protected final AppCompatActivity activity;

    protected DeliveryDAO deliveryDAO;

    protected UserDataDAO userDataDAO;

    private final EmailDisplay display;

    public PopCreator(AppCompatActivity activity){

        this.activity = activity;
        this.deliveryDAO = new DeliveryDAO(activity.getApplicationContext());
        this.userDataDAO = new UserDataDAO(activity.getApplicationContext(), this.getClass().getSimpleName());

        View.OnClickListener logoutListener = view -> {
            System.out.println("logout");
//                stopReceiving();

            userDataDAO.delete("email");
            userDataDAO.delete("pwd");
            userDataDAO.delete("server");

            Thread t1 = new Thread(() -> {
                DeliveryDAO dao = new DeliveryDAO(activity);
                dao.deleteAll();
            });
            t1.start();

            createAccount();
        };

        this.display = new EmailDisplay(activity, logoutListener);

        String email = userDataDAO.load("email");
        String pwd = userDataDAO.load("pwd");
        String host = userDataDAO.load("server");

        if(pwd == null || email == null || host == null){
            createAccount();
        }
        else {
            restartReceiving();
        }
    }

    public void createAccount(){
        System.out.println("creating account");

        activity.setContentView(R.layout.pop_login);

        activity.findViewById(R.id.b_login).setOnClickListener((View v) -> {

            TextView emailView =  activity.findViewById(R.id.email);
            String user = String.valueOf(emailView.getText());
            String password = String.valueOf(((TextView)(activity.findViewById(R.id.password))).getText());
            String host = String.valueOf(((TextView)(activity.findViewById(R.id.server))).getText());

            userDataDAO.storeKeyValuePair("email", user);
            userDataDAO.storeKeyValuePair("pwd", password);
            userDataDAO.storeKeyValuePair("server", host);
            userDataDAO.storeKeyValuePair("emailIndex", "0");

            restartReceiving();
        });
    }


    protected void restartReceiving(){

        PopReceiver.receiveEmails(activity);


        Intent intent = new Intent(activity, PopReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmMgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                1000*3, alarmIntent);

        System.out.println("background started");
        display.show();
        display.reload();
    }
}



