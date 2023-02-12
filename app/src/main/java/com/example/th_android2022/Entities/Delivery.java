package com.example.th_android2022.Entities;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Delivery extends RealmObject {


    public enum Status
    {
        ACTIVE, DELIVERED, FALSE
    }

    @PrimaryKey
    private long id;

    private RealmList<Email> emailList;

    private String tag;

    private Status status;

    private String orderId;

    private String deliveryService;

    public Delivery() {  //idk wtf
        emailList = new RealmList<>();
    }

    public Delivery(Delivery d) {
        this.id = d.id;
        this.emailList = new RealmList<>();
        for (Email e: d.emailList){
            this.emailList.add(new Email(e));
        }
        this.tag = d.tag;
        this.status = d.status;
        this.orderId = d.orderId;
        this.deliveryService = d.deliveryService;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmailList(List<Email> emailList) {
        this.emailList = new RealmList<>();
        for(Email e: emailList){
            this.emailList.add(e);
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStatus(Status status) {
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

    public Status getStatus() {
        return status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDeliveryService() {
        return deliveryService;
    }
}
