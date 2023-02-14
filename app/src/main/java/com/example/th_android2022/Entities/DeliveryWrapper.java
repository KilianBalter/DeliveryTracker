package com.example.th_android2022.Entities;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.th_android2022.Databases.DeliveryDAO;

import java.util.List;
import java.util.regex.Pattern;

import io.realm.RealmList;

public class DeliveryWrapper {
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("(\\d|-)+");
    private static final String[] ORDER_ID_PREFIXES = {"Sendungsnummer ", "Sendungsnummer: ", "Sendung ", "Bestellung ", "Bestellnr. ", "Bestellung Nr. "};


  public static void extractData(@NonNull Email email, Context appContext){
        DeliveryDAO DAO = new DeliveryDAO(appContext);
        Delivery d;
        String content = email.getContent().toLowerCase();
        String orderID = findOrderID(email.getTrackingLink());
        if (orderID == null) {                                                          //if no orderID is found in the tracking link, try to find it in the subject
            orderID = findOrderID(email.getSubject());
        }
        if (orderID == null) {
            orderID = findOrderID(content);
        }
        String deliveryService = findDeliveryService(email);
        String tag = findTag(email);
        if (orderID == null || deliveryService == null) {
            d = new Delivery(tag, Delivery.Status.ACTIVE, orderID, deliveryService);
            d.addEmail(email);
            DAO.insertOnlySingleDelivery(d);
        }
        else{
            d = DAO.findFirstByOrderIdAndDeliveryService(orderID, deliveryService);
            if (d == null){
                d = new Delivery(tag, Delivery.Status.ACTIVE, orderID, deliveryService);
                d.addEmail(email);
                DAO.insertOnlySingleDelivery(d);
            }
            else {
                List<Email> EmailList = d.getEmailList();
                EmailList.add(email);
                d.setEmailList(EmailList);
                d.setTag(tag);
                DAO.updateOnlySingleDelivery(d);
            }
        }
    }

    private static String findOrderID(String content) {
        String orderID = null;
        int start = -1;
        int i = 0;

        //Search through email for common words that preface an orderID. If found check if follow up was actually an ID
        while(i < ORDER_ID_PREFIXES.length && start == -1) {
            if(content.contains(ORDER_ID_PREFIXES[i])) {
                start = content.indexOf(ORDER_ID_PREFIXES[i]) + ORDER_ID_PREFIXES[i].length();

                int end = content.indexOf(' ',start);
                orderID = content.substring(start, end);

                if(!ORDER_ID_PATTERN.matcher(orderID).matches()) {
                    start = -1;
                    orderID = null;
                }
            }

            i++;
        }
        return orderID;
    }

    private static String findDeliveryService(@NonNull Email Email) {
        //Search for 6 biggest services
        String[] services = {"dhl","hermes","ups","dpd","fedex","gls"};
        String full = Email.getSender().toLowerCase() + Email.getContent().toLowerCase();
        for (String service : services) {
            if (full.contains(service))
                return service;
        }
        return null;
    }

    private static String findTag(@NonNull Email Email) {
        return Email.getSubject();
    }


}

