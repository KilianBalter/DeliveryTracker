package com.example.th_android2022.Entities;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.th_android2022.Databases.DeliveryDAO;

import java.util.Date;
import java.util.List;

public class DeliveryWrapper {

    private final Context context;
    private DeliveryDAO DAO;
    private final String[] OrderIDVersions= {"Sendungsnummer:", "Bestellnr.", "Bestellung Nr."};

    public DeliveryWrapper(Context context){
        this.context = context;
        this.DAO = new DeliveryDAO(context);
    }


    public void extractData(Email Email){
        //String content = Email.getContent().toLowerCase();
        String OrderID = findOrderID(Email.getTrackingLink());
        if (OrderID == null){                                                             //if no OrderID is found in the tracking Link, try to find it in the subject
            OrderID = findOrderID(Email.getSubject());
        }
        String DeliveryService = findDeliveryService(Email);
        String status = "ACTIVE";
        String Tag = findTag(Email);
        Delivery d;
        if (OrderID == null){
            d = new Delivery();
        }
        else{
            d = DAO.findFirstByOrderIdAndDeliveryService(OrderID, DeliveryService);                 //Get existing delivery from Database
            if (d == null){                                                                         //No delivery found in database
                d = new Delivery();

            }
        }
        List<Email> emailList = d.getEmailList();
        emailList.add(Email);
        d.setEmailList(emailList);
        d.setStatus(status);
        d.setOrderId(OrderID);
        d.setDeliveryService(DeliveryService);
        d.setTag(Tag);
        System.out.println(d);
        DAO.insertOnlySingleDelivery(d);                                                            //Insert new delivery into Database
    }

    public String findOrderID(String content) {
        String OrderID = null;
        int i = 0;
        while(OrderID == null && i < OrderIDVersions.length){
            int start = content.indexOf(OrderIDVersions[i])+1;               //TODO die besipiel emails verwenden um das fuer mehr faelle aufzubohren, auch sollten nur zahlenfolgen entdeckt werden
            if (start != 0){
                int end = content.indexOf(' ',start);
                OrderID = content.substring(start, end);
                return OrderID;
            }
            i++;
        }

        return OrderID;
    }

    public String findDeliveryService(Email Email) {                             //filters the delivery service
        String[] Services = {"dhl","hermes","ups","dpd","gls","fedex"};          //Only 6 biggest services          //TODO "dhl" oder "ups" kommen so gut wie in jeder email vor, das geht so nicht
        String sender = Email.getSender().toLowerCase();
        for (int i = 0; i< Services.length; i++){
            int t = -1;
            t = sender.indexOf(Services[i]);
            if( t >= 0){
                return Services[i];
            }
        }
        return null;
    }

    public String findTag(Email Email) {
        return Email.getSubject() + " " + Email.getSender();
    }

}

