package com.example.th_android2022.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.th_android2022.Databases.UserDataDAO;
import com.example.th_android2022.Entities.Email;
import com.example.th_android2022.Filter.Filter;
import com.example.th_android2022.MainActivity;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

public class PopReceiver extends BroadcastReceiver {


    /**
     * Connects to pop server and stores all unread emails in database
     *
     * @param context Context of the application
     */
    public static void receiveEmails(Context context) {
        new Thread(() -> {
            try {
                UserDataDAO userDataDAO = new UserDataDAO(context, PopCreator.class.getSimpleName());

                String user = userDataDAO.load("email");
                String password = userDataDAO.load("pwd");
                String host = userDataDAO.load("server");
                int emailIndex = Integer.parseInt(userDataDAO.load("emailIndex"));

                // create properties
                Properties properties = new Properties();

                String storeType = "pop3s";

                properties.put("mail.pop3.host", host);
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");

                Session emailSession = Session.getDefaultInstance(properties);

                // create the imap store object and connect to the imap server
                Store store = emailSession.getStore(storeType);

                store.connect(host, user, password);

                // create the inbox object and open it
                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_WRITE);

                // retrieve the messages from the folder in an array
                Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                Log.i("PopReceiver", "messages.length: " + messages.length);
                if (emailIndex == 0) {   //don't load all messages on first connect. Old emails are unimportant
                    emailIndex = messages.length - 30;
                }
                for (int i = emailIndex, n = messages.length; i < n; i++) {
                    Message message = messages[i];
                    message.setFlag(Flags.Flag.SEEN, true);

                    String content;   //content will be the extracted text from the email body
                    if (message.getContent() instanceof MimeMultipart) {
                        if (((MimeMultipart) message.getContent()).getBodyPart(0).getContent() instanceof MimeMultipart) {
                            content = ((MimeMultipart) ((MimeMultipart) message.getContent()).getBodyPart(0).getContent()).getBodyPart(0).getContent().toString();
                        } else {
                            content = ((MimeMultipart) message.getContent()).getBodyPart(0).getContent().toString();
                        }
                    } else {
                        content = message.getContent().toString();
                    }

                    Email emailObject = new Email(message.getSubject(), ((InternetAddress) message.getFrom()[0]).getPersonal(), content, message.getSentDate(), null);
                    if(userDataDAO.load("accountReady").equals("false"))
                        break;
                    Filter.filter(emailObject, context);
                    userDataDAO.storeKeyValuePair("emailIndex", String.valueOf(message.getMessageNumber()));
                }

                inbox.close(false);
                store.close();

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.showToast("DeliveryTracker failed to fetch emails");
            }

        }).start();
    }

    /**
     * Calls receiveEmails
     * This method is inherited from BroadcastReceiver. It will be called by alarmClock.
     *
     * @param context {@link Context} of the application
     * @param intent  {@link Intent} placeholder, not used
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("PopReceiver", "Received alarm");
        receiveEmails(context);
    }
}

