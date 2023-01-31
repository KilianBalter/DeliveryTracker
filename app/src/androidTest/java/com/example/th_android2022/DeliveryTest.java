package com.example.th_android2022;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.Email;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.RealmList;

@RunWith(AndroidJUnit4.class)
public class DeliveryTest {

    @Test
    public void deliveryTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DeliveryDAO repo = new DeliveryDAO(appContext);
        Delivery d = new Delivery();
        d.setStatus("pending");
        d.setTag("test");
        repo.insertOnlySingleDelivery(d);
        List<Delivery> result = repo.findAllDelivery();
        Delivery stored = result.get(result.size() - 1);

        assertEquals(d.getDeliveryService(), stored.getDeliveryService());
        assertEquals(new RealmList<>(), stored.getEmailList());
        assertEquals(d.getStatus(), stored.getStatus());
        assertEquals(d.getTag(), stored.getTag());
    }

    @Test
    public void deliveryUpdateTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DeliveryDAO repo = new DeliveryDAO(appContext);
        Delivery d = new Delivery();
        d.setDeliveryService("dhl");
        d.setOrderId("1234");
        repo.insertOnlySingleDelivery(d);
        List<Delivery> allDeliveries = repo.findAllDelivery();
        long deliveriesSize = allDeliveries.size();
        Delivery result = repo.findFirstByOrderIdAndDeliveryService("1234", "dhl");

        Email e = new Email("delivery", "amazon", "balblub", new Date(2000, 1, 1));

        List<Email> emails = new LinkedList(result.getEmailList());
        emails.add(e);
        repo.updateOnlySingleDelivery(result);

        allDeliveries = repo.findAllDelivery();

        Delivery stored = allDeliveries.get(allDeliveries.size() - 1);

        assertEquals(deliveriesSize, allDeliveries.size());
        assertEquals(d.getDeliveryService(), stored.getDeliveryService());
        assertEquals(emails, stored.getEmailList());
        assertEquals(d.getStatus(), stored.getStatus());
        assertEquals(d.getTag(), stored.getTag());
    }
}
