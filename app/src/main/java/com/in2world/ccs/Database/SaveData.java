package com.in2world.ccs.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.in2world.ccs.helper.ValidationHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ibrahem ayyd on 5/18/2017.
 */

public class SaveData {

    private static final String TAG = "SaveData";

    private static SaveData instance;
    private static SharedPreferences mSharedPreferences;

    private SaveData() {
    }

    public static void init(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SaveData getInstance() {
        if (instance == null) {
            validateInitialization();
            Class var0 = SaveData.class;
            synchronized (SaveData.class) {
                if (instance == null) {
                    instance = new SaveData();
                }
            }
        }

        return instance;
    }

    public void saveInt(String key, int value) {
        Log.d(TAG, "saveInt: key " + key + " value " + value);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        Log.d(TAG, "getInt: key " + key);
        return this.isKeyExists(key) ? mSharedPreferences.getInt(key, 0) : 0;
    }

    public void saveBoolean(String key, boolean value) {
        Log.d(TAG, "saveBoolean: key " + key + " value " + value);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        Log.d(TAG, "getBoolean: key " + key);
        return this.isKeyExists(key) && mSharedPreferences.getBoolean(key, false);
    }

    public void saveFloat(String key, float value) {
        Log.d(TAG, "saveFloat: key " + key + " value " + value);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        Log.d(TAG, "getFloat: key " + key);
        return this.isKeyExists(key) ? mSharedPreferences.getFloat(key, 0.0F) : 0.0F;
    }

    public void saveLong(String key, long value) {
        Log.d(TAG, "saveLong: key " + key + " value " + value);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        Log.d(TAG, "getLong: key " + key);
        return this.isKeyExists(key) ? mSharedPreferences.getLong(key, 0L) : 0L;
    }

    public void saveString(String key, String value) {
        Log.d(TAG, "saveString: key " + key + " value " + value);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        Log.d(TAG, "getString: key " + key);
        return this.isKeyExists(key) ? mSharedPreferences.getString(key, (String) null) : null;
    }

    public <T> void saveObject(String key, T object) {
        String objectString = (new Gson()).toJson(object);
        Log.d(TAG, "saveObject: key " + key + " value " + objectString);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, objectString);
        editor.apply();
    }

    public <T> T getObject(String key, Class<T> classType) {
        if (this.isKeyExists(key)) {
            String objectString = mSharedPreferences.getString(key, (String) null);
            if (objectString != null) {
                return (new Gson()).fromJson(objectString, classType);
            }
        }

        return null;
    }

    public <T> void saveObjectsList(String key, List<T> objectList) {
        String objectString = (new Gson()).toJson(objectList);
        Log.d(TAG, "saveObjectsList: key " + key + " value " + objectString);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, objectString);
        editor.apply();
    }

    public <T> List getObjectsList(String key, Class<T> classType) {
        if (this.isKeyExists(key)) {
            String objectString = mSharedPreferences.getString(key, (String) null);
            Log.d(TAG, "getObjectsList : key  " + key + "  json " + objectString);
            if (ValidationHelper.validString(objectString)) {
                return fromJson(objectString, classType);
            }
        }

        return null;
    }


    public void clearSession() {
        Log.d(TAG, "clearSession: ");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean deleteValue(String key) {
        if (this.isKeyExists(key)) {
            Log.d(TAG, "deleteValue: key "+key);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(key);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    private static void validateInitialization() {
        if (mSharedPreferences == null) {
            throw new SaveDataException("DataSave Class must be initialized inside your application class by calling DataSave.init(getApplicationContext)");
        }
    }

    public boolean isKeyExists(String key) {
        Map<String, ?> map = mSharedPreferences.getAll();
        if (map.containsKey(key)) {
            Log.w(TAG, "isKeyExists: key "+key);
            return true;
        } else {
            Log.e(TAG, "isKeyExists: No element founded in sharedPrefs with the key " + key);
            return false;
        }
    }

    public <T> List<T> fromJson(String json, Class<T> clazz) {
        // Consuming remote method
        List<T> list = new ArrayList<T>();
        try {
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(json).getAsJsonArray();
            Gson gson = new Gson();
            for (JsonElement itemJson : array) {
                T entity = gson.fromJson(itemJson, clazz);
                list.add(entity);
            }
            Log.w(TAG, "fromJson: list " + list.size());
        } catch (IllegalStateException e) {
            Log.e(TAG, "fromJson: IllegalStateException" + e.getMessage());
            Type type = new ListParameterizedType(clazz);
            list = new Gson().fromJson(json, type);
        }


        return list;
    }

    public static class SaveDataException extends RuntimeException {
        public SaveDataException(String message) {
            super(message);
        }
    }


    private static class ListParameterizedType implements ParameterizedType {

        private Type type;

        public ListParameterizedType(Type type) {
            this.type = type;
        }

        @NonNull
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @NonNull
        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        // implement equals method too! (as per javadoc)
    }
}
