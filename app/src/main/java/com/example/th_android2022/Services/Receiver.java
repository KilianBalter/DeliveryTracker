//package com.example.th_android2022.Services;
//
//import android.content.Intent;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.th_android2022.Databases.DeliveryDAO;
//import com.example.th_android2022.Databases.UserDataDAO;
//import com.example.th_android2022.EmailDisplay;
//
//import java.io.Serializable;
//
//public abstract class Receiver implements Serializable {
//
//    protected final AppCompatActivity activity;
//
//    protected DeliveryDAO deliveryDAO;
//
//    protected UserDataDAO userDataDAO;
//
//    private final EmailDisplay display;
//
//    public Receiver(AppCompatActivity activity){
//        this.activity = activity;
//        this.deliveryDAO = new DeliveryDAO(activity.getApplicationContext());
//        this.userDataDAO = new UserDataDAO(activity.getApplicationContext(), this.getClass().getSimpleName());
//
//        this.display = new EmailDisplay(activity);
//    }
//
//    abstract public void receiveEmails();
//
//    protected void startReceiving(){
////        receiveEmails();
//        Intent i = new Intent(activity, BackgroundService.class);
//        i.putExtra("receiver", this);
//        activity.startService(i);
//
//        display.show();
//        display.reload();
//    }
//
//}
