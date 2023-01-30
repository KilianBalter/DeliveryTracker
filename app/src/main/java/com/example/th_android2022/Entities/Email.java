package com.example.th_android2022.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Email {

    String subject;
    String sender;
    String content;
    String date;
}
