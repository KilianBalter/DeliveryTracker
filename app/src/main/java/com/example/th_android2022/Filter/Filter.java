package com.example.th_android2022.Filter;

import android.content.Context;

import com.example.th_android2022.Entities.DeliveryWrapper;
import com.example.th_android2022.Entities.Email;

public class Filter {

    /**
     * filters tracking email and sends them to DeliveryWrapper
     *
     * @param email   to be filtered
     * @param context of application
     */
    public static void filter(Email email, Context context) {
        LinkFilter linkFilter = new LinkFilter();
        double trackingEmail;

        //if(RegexFilter.filter(email))
            trackingEmail = linkFilter.filter(email);

        if (trackingEmail == 1.0)
            AiFilter.train(email, true);
        else {
            trackingEmail = AiFilter.filter(email);
        }


        if (trackingEmail > 0.7) {
            DeliveryWrapper.extractData(email, context);
        } else if (trackingEmail > 0.3) {
            //TODO ask user
        }
    }
}
