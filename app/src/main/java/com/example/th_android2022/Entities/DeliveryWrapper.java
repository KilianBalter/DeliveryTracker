package com.example.th_android2022.Entities;

import android.content.Context;

import com.example.th_android2022.Databases.DeliveryDAO;

import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;

public class DeliveryWrapper {

    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();       //TODO get context from constructor
    DeliveryDAO DAO = new DeliveryDAO(appContext);

    public void newEmail(Email Email){  //TODO was ist der sinn hiervon?
        extractData(Email);
    }

    public void extractData(Email Email){
        String content = Email.getContent().toLowerCase();
        String OrderID = findOrderID(content);
        if (OrderID.equals("NotFound")){                                                             //if no OrderID is found in the email content, try to find it in the subject
            OrderID = findOrderID(Email.getSubject());
        }
        String DeliveryService = findDeliveryService(Email);
        Delivery.Status state = Delivery.Status.ACTIVE;
        String Tag = findTag(Email);
        Delivery d;
        if (OrderID.equals("NotFound")){
            d = new Delivery();
            DAO.insertOnlySingleDelivery(d);                                                       //Insert new delivery into Database
        }
        else{
            d = DAO.findFirstByOrderIdAndDeliveryService(OrderID, DeliveryService);                 //Get existing delivery from Database
            if (d == null){                                                                         //No delivery found in database
                d = new Delivery();
                DAO.insertOnlySingleDelivery(d);
            }
        }

        List<Email> emailList = d.getEmailList();
        emailList.add(Email);
        d.setEmailList(emailList);
        d.setStatus(state);
        d.setOrderId(OrderID);
        d.setDeliveryService(DeliveryService);
        d.setTag(Tag);                                          //TODO store in database AFTER setting the fields, not before
    }

    public String findOrderID(String content) {
        String OrderID = "NotFound";                                 //TODO als default null verwenden
        int start = content.indexOf("Sendungsnummer:")+1;               //TODO die besipiel emails verwenden um das fuer mehr faelle aufzubohren, auch sollten nur zahlenfolgen entdeckt werden
        if (start != 0){
            int end = content.indexOf(' ',start);
            OrderID = content.substring(start, end);                //TODO return OrderID??
        }
        return "OrderID";
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
        return "unknown";                       //TODO als default bitte einfach null
    }

    public String findTag(Email Email) {
        return Email.getSubject();                      //TODO eventuell noch absender dazu?
    }


}

