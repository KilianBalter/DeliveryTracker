package com.example.th_android2022.Services;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th_android2022.Databases.DeliveryDAO;
import com.example.th_android2022.Entities.Delivery;
import com.example.th_android2022.Entities.Email;
import com.example.th_android2022.R;

import android.view.View;
import android.widget.TextView;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

public class PopReceiver extends Receiver{


    public PopReceiver(AppCompatActivity activity){
        super(activity);

//        userDataDAO.delete("email");  //TODO testing, remove later
//        userDataDAO.delete("pwd");

        String email = userDataDAO.load("email");
        String pwd = userDataDAO.load("pwd");
        String host = userDataDAO.load("server");

        if(pwd == null || email == null || host == null){
            createAccount();
        }
        else {
            startReceiving();
        }
    }

    public void createAccount(){
        System.out.println("creating account");

        activity.setContentView(R.layout.pop_login);

        activity.findViewById(R.id.b_login).setOnClickListener((View v) -> {

            TextView emailView =  activity.findViewById(R.id.email);
            String user = String.valueOf(emailView.getText());
    String password = String.valueOf(((TextView)(activity.findViewById(R.id.password))).getText());
            String host = String.valueOf(((TextView)(activity.findViewById(R.id.server))).getText());

            userDataDAO.storeKeyValuePair("email", user);
            userDataDAO.storeKeyValuePair("pwd", password);
            userDataDAO.storeKeyValuePair("server", host);

            startReceiving();
        });
    }

    public void receiveEmails(){

        new Thread(new Runnable() {
            public void run() {
                try {

                    DeliveryDAO deliveryDAO = new DeliveryDAO(activity);

                    String user = userDataDAO.load("email");
                    String password = userDataDAO.load("pwd");

                    String host = userDataDAO.load("server");

                    // create properties
                    Properties properties = new Properties();

                    String storeType = "pop3s";

                    properties.put("mail.pop3.host", host);
                    properties.put("mail.pop3.port", "995");
                    properties.put("mail.pop3.starttls.enable", "true");
//                    properties.put("mail.store.protocol", "pop3");


                    Session emailSession = Session.getDefaultInstance(properties);

                    // create the imap store object and connect to the imap server
                    Store store = emailSession.getStore(storeType);

                    store.connect(host, user, password);

                    // create the inbox object and open it
                    Folder inbox = store.getFolder("Inbox");
                    inbox.open(Folder.READ_WRITE);

                    // retrieve the messages from the folder in an array and print it
                    Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                    System.out.println("messages.length---" + messages.length);

                    for (int i = 0, n = messages.length; i < n; i++) {   // TODO mark emails as already read
                        Message message = messages[i];
                        message.setFlag(Flags.Flag.SEEN, true);

                        String content;
                        if(message.getContent() instanceof javax.mail.internet.MimeMultipart){
                            content = ((MimeMultipart) message.getContent()).getBodyPart(0).getContent().toString();
                        }
                        else {
                            content = message.getContent().toString();
                        }
                        Email emailObject = new Email(message.getSubject(), message.getFrom()[0].toString(), content, message.getSentDate());
                        List<Email> emails = new LinkedList<>();
                        emails.add(emailObject);
                        Delivery delivery = new Delivery();
                        delivery.setEmailList(emails);
                        deliveryDAO.insertOnlySingleDelivery(delivery);

                    }

                    inbox.close(false);
                    store.close();

                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

