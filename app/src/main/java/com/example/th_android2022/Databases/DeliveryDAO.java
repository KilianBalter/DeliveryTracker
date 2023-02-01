package com.example.th_android2022.Databases;

import android.content.Context;

import com.example.th_android2022.Entities.Delivery;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DeliveryDAO {

    Realm realm;

    public DeliveryDAO(Context context){
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    public void insertOnlySingleDelivery(Delivery delivery){
        //TODO User notification

        Number maxId = realm.where(Delivery.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        delivery.setId(nextId);

        System.out.println("inserting delivery " + delivery.getId());
        realm.executeTransaction(transactionRealm ->
                transactionRealm.insert(delivery)
        );
    }

    public Delivery findFirstByOrderIdAndDeliveryService(String orderId, String deliveryService){
        Delivery result = realm.where(Delivery.class).
                equalTo("orderId", orderId).
                equalTo("deliveryService", deliveryService).
                findFirst();
        if (result != null)
            result = new Delivery(result);
        return result;
    }

    public void updateOnlySingleDelivery(Delivery delivery){
        realm.executeTransaction(transactionRealm -> {
            Delivery storedDelivery = realm.where(Delivery.class).
                    equalTo("orderId", delivery.getOrderId()).
                    equalTo("deliveryService", delivery.getDeliveryService()).
                    findFirst();
            if(storedDelivery != null) {
                System.out.println("updating delivery " + delivery.getId());
                storedDelivery.setStatus(delivery.getStatus());
                storedDelivery.setTag(delivery.getTag());
                storedDelivery.setEmailList(delivery.getEmailList());
            }
            else{
                insertOnlySingleDelivery(delivery);
            }
        });
    }

    public List<Delivery> findAllDelivery(){
        RealmResults<Delivery> results = realm.where(Delivery.class).findAll();
        return realm.copyFromRealm(results);
    }

    public void deleteById(long id){
        realm.executeTransaction(transactionRealm -> {
            RealmResults<Delivery> results = realm.where(Delivery.class).equalTo("id", id).findAll();
            if(results.size() > 0) {
                System.out.println("deleting delivery " + id);
                results.deleteAllFromRealm();
            }
        });
    }

    public void deleteByOrderIdAndDeliveryService(String orderId, String deliveryService){
        realm.executeTransaction(transactionRealm -> {
            RealmResults<Delivery> results = realm.where(Delivery.class).
                    equalTo("orderId", orderId).
                    equalTo("deliveryService", deliveryService).
                    findAll();

            if(results.size() > 0) {
                System.out.println("deleting delivery " + orderId + " " + deliveryService);
                results.deleteAllFromRealm();
            }
        });
    }

}
