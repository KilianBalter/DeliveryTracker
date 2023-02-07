package com.example.th_android2022.Entities;


import java.util.Date;

import io.realm.RealmObject;

public class Email extends RealmObject {
    String subject;
    String sender;
    String content;
    Date date;
    String trackingLink;


    public Email() {     //do not delete! Used for Realm Database
    }

    public Email(String subject, String sender, String content, Date date, String trackingLink) {
        this.subject = subject;
        this.sender = sender;
        this.content = content;
        this.date = date;
        this.trackingLink = trackingLink;
    }

    public Email(Email e) {
        this.subject = e.subject;
        this.sender = e.sender;
        this.content = e.content;
        this.date = e.date;
        this.trackingLink = e.trackingLink;
    }

    public String getSubject() {
        return subject;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public String getTrackingLink() {
        return trackingLink;
    }

    public void setTrackingLink(String trackingLink) {
        this.trackingLink = trackingLink;
    }
}
