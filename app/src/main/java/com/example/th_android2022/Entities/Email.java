package com.example.th_android2022.Entities;


import java.util.Date;

import io.realm.RealmObject;

public class Email extends RealmObject {
    String subject;
    String sender;
    String content;
    Date date;

    public Email(){     //idk

    }

    public Email(String subject, String sender, String content, Date date) {
        this.subject = subject;
        this.sender = sender;
        this.content = content;
        this.date = date;
    }

    public Email(Email e) {
        this.subject = e.subject;
        this.sender = e.sender;
        this.content = e.content;
        this.date = e.date;
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
}
