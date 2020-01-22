package com.in2world.ccs.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ObjectUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    /**
     * This method is responsible for de-serializing the Java Object into Json String.
     *
     * @param object Object to be de-serialized.
     * @return String
     */
    public static <T> String deserializeObjectToString(T object) {
        return (new Gson()).toJson(object);
    }


}
