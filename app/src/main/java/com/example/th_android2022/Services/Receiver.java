package com.example.th_android2022.Services;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Databases.UserDataDAO;
import com.example.th_android2022.EmailDisplay;

public abstract class Receiver {

    protected final AppCompatActivity activity;

    protected DeliveryDAO deliveryDAO;

    protected UserDataDAO userDataDAO;

    private final EmailDisplay display;

    public Receiver(AppCompatActivity activity){
        this.activity = activity;
        this.deliveryDAO = new DeliveryDAO(activity.getApplicationContext());
        this.userDataDAO = new UserDataDAO(activity.getApplicationContext(), this.getClass().getSimpleName());

        this.display = new EmailDisplay(activity);
    }

    abstract public void receiveEmails();

    protected void startReceiving(){
        receiveEmails();
        display.show();
        display.reload();
    }

}
