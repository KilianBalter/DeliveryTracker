package com.example.th_android2022.Databases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DeliveryDAO {

    private final Realm realm;

    private final Context context;

    public DeliveryDAO(Context context){
        this.context = context;
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "database";
            String description = "fml";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("3", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void insertOnlySingleDelivery(Delivery delivery){
        //TODO User notification

        Delivery mutableCopy = new Delivery(delivery);

        Number maxId = realm.where(Delivery.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        mutableCopy.setId(nextId);

        System.out.println("inserting delivery " + mutableCopy.getId());
        realm.executeTransaction(transactionRealm ->
                transactionRealm.insert(mutableCopy)
        );
        notify("new Delivery", delivery.getTag());
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

    public Delivery findFirstById(long id){
        Delivery result = realm.where(Delivery.class).
                equalTo("id", id).
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
                notify("New Delivery", delivery.getTag());
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


    public void deleteAll(){
        realm.executeTransaction(transactionRealm -> realm.deleteAll());
    }


    private void notify(String title, String msg){          //TODO set oncklick action to open app

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "3")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define

        notificationManager.notify(1, builder.build());

    }
}
