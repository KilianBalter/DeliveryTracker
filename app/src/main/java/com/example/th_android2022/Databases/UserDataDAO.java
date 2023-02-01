package com.example.th_android2022.Databases;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataDAO {

    private final String preferencesName;

    private final Context context;

    public UserDataDAO(Context context, String preferencesName){
        this.context = context;
        this.preferencesName = preferencesName;
    }

    public String load(String key){
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        String result = shared.getString(key, null);
        System.out.println("loading Userdata: " + key + ": " + result);
        return result;
    }

    public void delete(String key){
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        shared.edit().remove(key).commit();
        System.out.println("deleting Userdata: " + key);
    }

    public void storeKeyValuePair(String key, String value){
        System.out.println("storing userdata: " + key + ": " + value);
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
