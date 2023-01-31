package com.example.th_android2022.Entities;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Delivery extends RealmObject {

    @PrimaryKey
    private long id;

    private RealmList<Email> emailList;

    private String tag;

    private String status; //TODO maybe enum

    private String orderId;

    private String deliveryService;

    public Delivery() {  //idk wtf
        emailList = new RealmList<>();
    }

    public Delivery(long id) {
        this();
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmailList(List<Email> emailList) {
        this.emailList = new RealmList(emailList);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setDeliveryService(String deliveryService) {
        this.deliveryService = deliveryService;
    }

    public long getId() {
        return id;
    }

    public List<Email> getEmailList() {
        return emailList;
    }

    public String getTag() {
        return tag;
    }

    public String getStatus() {
        return status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDeliveryService() {
        return deliveryService;
    }
}
