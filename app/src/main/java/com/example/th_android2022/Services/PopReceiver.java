package com.example.th_android2022.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.th_android2022.Databases.UserDataDAO;
import com.example.th_android2022.Entities.Email;
import com.example.th_android2022.Filter.Filter;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

public class PopReceiver extends BroadcastReceiver {


    /**
     * connects to pop server and stores all unread emails in database
     *
     * @param context context of the application
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
                System.out.println("messages.length---" + messages.length);
                if (emailIndex == 0) {   //dont load all messages on first connect. Old emails are unimportant
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

                    Email emailObject = new Email(message.getSubject(), message.getFrom()[0].toString(), content, message.getSentDate(), null);
                    Filter.filter(emailObject, context);
                    userDataDAO.storeKeyValuePair("emailIndex", String.valueOf(message.getMessageNumber()));
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
        }).start();
    }

    /**
     * calls receiveEmails
     * This method is inherited from BroadcastReceiver. It will be called by alarmClock.
     *
     * @param context {@link Context} of the application
     * @param intent  {@link Intent} placeholder, not used
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("recieved alarm");
        receiveEmails(context);
    }
}

