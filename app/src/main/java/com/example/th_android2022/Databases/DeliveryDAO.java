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

    /**
     * creates Database access object.
     * creates Notification channel
     *
     * @param context of application
     */
    public DeliveryDAO(Context context) {
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
            String description = "databaseNotifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("3", name, importance);
            channel.setDescription(description);
            // Register the channel with the system;
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * insert a new Delivery and send a notification to the user.
     * The delivery will get a new id.
     *
     * @param delivery to be inserted
     */
    public void insertOnlySingleDelivery(Delivery delivery) {
        Delivery mutableCopy = new Delivery(delivery);

        //set id o delivery
        Number maxId = realm.where(Delivery.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        mutableCopy.setId(nextId);

        System.out.println("inserting delivery " + mutableCopy.getId());
        realm.executeTransaction(transactionRealm ->
                transactionRealm.insert(mutableCopy)
        );
        notify("New delivery on the way", delivery.getTag());
    }

    /**
     * returns the delivery, which matches orderId and deliveryService, or an empty delivery
     *
     * @param orderId         made by deliveryService
     * @param deliveryService name
     * @return delivery
     */
    public Delivery findFirstByOrderIdAndDeliveryService(String orderId, String deliveryService) {
        Delivery result = realm.where(Delivery.class).
                equalTo("orderId", orderId).
                equalTo("deliveryService", deliveryService).
                findFirst();
        if (result != null)
            result = new Delivery(result);
        return result;
    }

    /**
     * returns delivery which matches the database id. Useful if deliveryService or deliveryId are unknown
     *
     * @param id id of the delivery Object
     * @return delivery with the id or new delivery
     */
    public Delivery findFirstById(long id) {
        Delivery result = realm.where(Delivery.class).
                equalTo("id", id).
                findFirst();
        if (result != null)
            result = new Delivery(result);
        return result;
    }

    /**
     * if a delivery with the same id already exists, the values of it get overwritten with the new delivery and the user gets a notification.
     * if no delivery exists, insertOnlySingleDelivery is called.
     *
     * @param delivery delivery to be updated
     */
    public void updateOnlySingleDelivery(Delivery delivery) {
        realm.executeTransaction(transactionRealm -> {
            Delivery storedDelivery = realm.where(Delivery.class).
                    equalTo("orderId", delivery.getOrderId()).
                    equalTo("deliveryService", delivery.getDeliveryService()).
                    findFirst();
            if (storedDelivery != null) {
                System.out.println("updating delivery " + delivery.getId());
                notify("Update on your delivery", delivery.getTag());
                storedDelivery.setStatus(delivery.getStatus());
                storedDelivery.setTag(delivery.getTag());
                storedDelivery.setEmailList(delivery.getEmailList());
            } else {
                insertOnlySingleDelivery(delivery);
            }
        });
    }

    public List<Delivery> findAllDelivery() {
        RealmResults<Delivery> results = realm.where(Delivery.class).findAll();
        return realm.copyFromRealm(results);
    }

    /**
     * deletes delivery from database. Useful if deliveryService or deliveryId are unknown
     *
     * @param id id of the delivery Object
     */
    public void deleteById(long id) {
        realm.executeTransaction(transactionRealm -> {
            RealmResults<Delivery> results = realm.where(Delivery.class).equalTo("id", id).findAll();
            if (results.size() > 0) {
                System.out.println("deleting delivery " + id);
                results.deleteAllFromRealm();
            }
        });
    }

    /**
     * deletes the delivery, which matches orderId and deliveryService
     *
     * @param orderId         orderId given by the delivery service
     * @param deliveryService name of the delivery service
     */
    public void deleteByOrderIdAndDeliveryService(String orderId, String deliveryService) {
        realm.executeTransaction(transactionRealm -> {
            RealmResults<Delivery> results = realm.where(Delivery.class).
                    equalTo("orderId", orderId).
                    equalTo("deliveryService", deliveryService).
                    findAll();

            if (results.size() > 0) {
                System.out.println("deleting delivery " + orderId + " " + deliveryService);
                results.deleteAllFromRealm();
            }
        });
    }


    public void deleteAll() {
        realm.executeTransaction(transactionRealm -> realm.deleteAll());
    }


    private void notify(String title, String msg) {          //TODO set oncklick action to open app

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "3")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is always the same so user doesnt get to many notifications
        notificationManager.notify(1, builder.build());

    }
}
