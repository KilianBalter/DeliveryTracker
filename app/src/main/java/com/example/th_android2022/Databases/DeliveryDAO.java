package com.example.th_android2022.Databases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.MainActivity;
import com.example.th_android2022.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DeliveryDAO {

    private final Realm realm;

    private final Context context;

    /**
     * Creates Database access object.
     * Creates Notification channel
     *
     * @param context Context of application
     */
    public DeliveryDAO(Context context) {
        this.context = context;
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "database";
            String description = "databaseNotifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("3", name, importance);
            channel.setDescription(description);
            //Register the channel with the system;
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Insert a new Delivery and send a notification to the user.
     * The delivery will get a new id.
     *
     * @param delivery Delivery to be inserted
     */
    public void insertOnlySingleDelivery(Delivery delivery) {
        Log.i("DAO Insert", "Inserting:\n" + delivery.getTag() + "\n" + delivery.getOrderId() + "\n" + delivery.getDeliveryService());
        Delivery mutableCopy = new Delivery(delivery);

        //Set id of delivery
        Number maxId = realm.where(Delivery.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        mutableCopy.setId(nextId);

        Log.i("DeliveryDAO","Inserting delivery " + mutableCopy.getId());
        realm.executeTransaction(transactionRealm ->
                transactionRealm.insert(mutableCopy)
        );
        notify("New delivery on the way", delivery.getTag());
    }

    /**
     * Returns the delivery, which matches orderId and deliveryService, or an empty delivery
     *
     * @param orderId         Made by deliveryService
     * @param deliveryService Name
     * @return Delivery
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
     * Returns delivery which matches the database id. Useful if deliveryService or deliveryId are unknown
     *
     * @param id ID of the delivery Object
     * @return Delivery with the id or new delivery
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
     * If a delivery with the same id already exists, the values of it get overwritten with the new delivery and the user gets a notification.
     * If no delivery exists, insertOnlySingleDelivery is called.
     *
     * @param delivery Delivery to be updated
     */
    public void updateOnlySingleDelivery(Delivery delivery) {
        AtomicBoolean insertInstead = new AtomicBoolean(false);

        realm.executeTransaction(transactionRealm -> {
            Delivery storedDelivery = realm.where(Delivery.class).
                    equalTo("orderId", delivery.getOrderId()).
                    equalTo("deliveryService", delivery.getDeliveryService()).
                    findFirst();
            if (storedDelivery != null) {
                Log.i("DeliveryDAO", "Updating delivery...");
                notify("Update on your delivery", delivery.getTag());
                storedDelivery.setStatus(delivery.getStatus());
                storedDelivery.setTag(delivery.getTag());
                storedDelivery.setEmailList(delivery.getEmailList());
            } else {
                insertInstead.set(true);
            }
        });

        if(insertInstead.get())
            insertOnlySingleDelivery(delivery);
    }

    /**
     * The values of the delivery that matches the ID get overwritten with the new values and the user gets a notification.
     *
     * @param delivery Delivery to be updated
     */
    public void updateById(Delivery delivery) {
        realm.executeTransaction(transactionRealm -> {
            Delivery storedDelivery = realm.where(Delivery.class).
                    equalTo("id", delivery.getId()).
                    findFirst();
            if (storedDelivery != null) {
                Log.i("DeliveryDAO", "Updating delivery...");
                notify("Update on your delivery", delivery.getTag());
                storedDelivery.setStatus(delivery.getStatus());
                storedDelivery.setTag(delivery.getTag());
                storedDelivery.setEmailList(delivery.getEmailList());
            } else {
                Log.i("DeliveryDAO", "Id not found.");
            }
        });
    }

    public List<Delivery> findAllDelivery() {
        RealmResults<Delivery> results = realm.where(Delivery.class).findAll();
        return realm.copyFromRealm(results);
    }

    /**
     * Deletes delivery from database. Useful if deliveryService or deliveryId are unknown
     *
     * @param id ID of the delivery Object
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
     * Deletes the delivery, which matches orderId and deliveryService
     *
     * @param orderId         orderId given by the delivery service
     * @param deliveryService Name of the delivery service
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


    private void notify(String title, String msg) {
        //"onClick" action of notification
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "3")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        //notificationId is always the same so user doesn't get to many notifications
        notificationManager.notify(1, builder.build());
    }
}
