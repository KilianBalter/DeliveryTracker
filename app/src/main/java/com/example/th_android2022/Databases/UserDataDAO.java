package com.example.th_android2022.Databases;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataDAO {

    private final String preferencesName;

    private final Context context;               //init in main?

    UserDataDAO(Context context, String preferencesName){
        this.context = context;
        this.preferencesName = preferencesName;
    }

    public String load(String key){
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return shared.getString(key, "");
    }

    public void storeEmail(String key, String value){
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
