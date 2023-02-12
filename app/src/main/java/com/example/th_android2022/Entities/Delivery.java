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

    private String status;

    private String orderId;

    private String deliveryService;

    public Delivery() {  //idk wtf
        emailList = new RealmList<>();
    }

    public Delivery(String tag, Status status, String orderId, String deliveryService) {
        this.emailList = new RealmList<>();
        this.tag = tag;
        this.status = status.toString();
        this.orderId = orderId;
        this.deliveryService = deliveryService;
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

    public void addEmail(Email e) {
        emailList.add(new Email(e));
    }

    public void setEmailList(List<Email> emailList) {
        this.emailList = new RealmList<>();
        this.emailList.addAll(emailList);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStatus(Status status) {
        this.status = status.toString();
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
        return status == null ? null : Status.valueOf(status);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDeliveryService() {
        return deliveryService;
    }
}
