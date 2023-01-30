package com.example.th_android2022.Entities;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Delivery extends RealmObject {

    @PrimaryKey
    @Required
    long id;

    List<Email> emailList;

    String tag;

    String status; //TODO maybe enum

    String orderId;

    String deliveryService;

}
