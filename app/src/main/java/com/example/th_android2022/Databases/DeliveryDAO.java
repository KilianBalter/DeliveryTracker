package com.example.th_android2022.Databases;

import android.content.Context;

import com.example.th_android2022.Entities.Delivery;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeliveryDAO {

    Realm realm;

    DeliveryDAO(Context context){
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void insertOnlySingleDelivery(Delivery delivery){
        //TODO check id of delivery
        realm.executeTransaction(transactionRealm ->
            transactionRealm.insert(delivery)
        );
    }

    public List<Delivery> findAllDelivery(){
        RealmResults<Delivery> results = realm.where(Delivery.class).findAll();
        return realm.copyFromRealm(results);
    }
}
