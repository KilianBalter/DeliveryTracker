package com.example.th_android2022.Filter;

import android.content.Context;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.Email;

import java.util.LinkedList;
import java.util.List;

public class Filter {

    public static void filter(Email email, Context context){
        LinkFilter linkFilter = new LinkFilter();
        double trackingEmail;

        trackingEmail = linkFilter.filter(email);
//        if(trackingEmail == 1.0)
//            AiFilter.train(email, true);

//        if(trackingEmail != 1.0){                 //uncomment as soon as enough user data is collected
//            trackingEmail = AiFilter.filter(email);
//        }


        System.out.println(email.getSubject() + " is " + trackingEmail + "#####################################################");

        if(trackingEmail > 0.7){
            //TODO PACkagewrapper


            List<Email> emails = new LinkedList<>();
            emails.add(email);
            Delivery delivery = new Delivery();
            delivery.setEmailList(emails);
            new DeliveryDAO(context).insertOnlySingleDelivery(delivery);


        }
        else if(trackingEmail > 0.3){
            //TODO ask user
        }
    }
}
