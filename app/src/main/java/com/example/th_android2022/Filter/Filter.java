package com.example.th_android2022.Filter;

import android.content.Context;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.DeliveryWrapper;
import com.example.th_android2022.Entities.Email;

import java.util.LinkedList;
import java.util.List;

public class Filter {

    /**
     * filters tracking email and sends them to DeliveryWrapper
     *
     * @param email   to be filtered
     * @param context of application
     */
    public static void filter(Email email, Context context) {
        LinkFilter linkFilter = new LinkFilter();
        double trackingEmail = 0;

        if(RegexFilter.filter(email))
            trackingEmail = linkFilter.filter(email);

        if (trackingEmail == 1.0)
            AiFilter.train(email, true);
        else {
            trackingEmail = AiFilter.filter(email);
        }


        if (trackingEmail > 0.7) {
            //TODO packageWrapper
            DeliveryWrapper DW = new DeliveryWrapper(context);
            DW.extractData(email);
            //List<Email> emails = new LinkedList<>();
            //emails.add(email);
            //Delivery delivery = new Delivery();
            //delivery.setEmailList(emails);
            //new DeliveryDAO(context).insertOnlySingleDelivery(delivery);


        } else if (trackingEmail > 0.3) {
            //TODO ask user
        }
    }
}
