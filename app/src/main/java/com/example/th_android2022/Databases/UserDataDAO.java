package com.example.th_android2022.Databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserDataDAO {

    private final String preferencesName;

    private final Context context;

    /**
     * Creates a new database with the name preferencesName
     *
     * @param context         Application context
     * @param preferencesName Name of database
     */
    public UserDataDAO(Context context, String preferencesName) {
        this.context = context;
        this.preferencesName = preferencesName;
    }

    /**
     * @param key The key to look up
     * @return Value stored under key
     */
    public String load(String key) {
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        String result = shared.getString(key, null);
        Log.i("UserDataDAO","Loading Userdata: " + key);
        return result;
    }


    /**
     * Deletes value stored under key
     *
     * @param key The key
     */
    public void delete(String key) {
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        shared.edit().remove(key).commit();
        Log.i("UserDataDAO","Deleting Userdata: " + key);
    }

    /**
     * @param key   Used for identifying
     * @param value Value to be stored
     */
    public void storeKeyValuePair(String key, String value) {
        Log.i("UserDataDAO","Storing Userdata: " + key);
        SharedPreferences shared = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
