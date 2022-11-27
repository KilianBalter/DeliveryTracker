package com.example.th_android2022;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GoogleAccountCredential mCredential;
    private Gmail service;

    private final int RC_REQUEST_ACCOUNT_PICKER = 1;

    private static final String[] SCOPES = {GmailScopes.GMAIL_READONLY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Log.d("onCreate", "Finished");
    }

    private void init() {
        mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public void chooseAccount(View view) {
        Runnable runnable = () -> {
            try {
                startActivityForResult(mCredential.newChooseAccountIntent(), RC_REQUEST_ACCOUNT_PICKER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        AsyncTask.execute(runnable);
    }

    public void listMails(View view) {
        Runnable runnable = () -> {
            try {
                ListMessagesResponse response = service.users().messages().list("me").execute();

                List<Message> messages = new ArrayList<Message>();

                messages.addAll(response.getMessages());

                int i = 0;
                for(Message message: messages) {
                    if(i >= 10)
                        break;
                    Log.d("listMails", "Id: " + message.getId());
                    Message actualMessage = service.users().messages().get("me", message.getId()).execute();
                    Log.d("listMails", "Snippet: " + actualMessage.getSnippet());
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        AsyncTask.execute(runnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "Entered");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_REQUEST_ACCOUNT_PICKER) {
            try {
                Log.d("onActivityResult", "RC = REQUEST_ACCOUNT_PICKER");
                mCredential.setSelectedAccountName(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                service = new Gmail.Builder(HTTP_TRANSPORT, jsonFactory, mCredential)
                        .setApplicationName("Th-Android2022")
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("onActivityResult", "Exited");
    }

}