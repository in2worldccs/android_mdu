package com.in2world.ccs.helper;

import java.util.List;


/**
 * Created by ibrahem ayyd on 8/12/2016.
 */
public class ValidationHelper {

    ValidationHelper validationHelper;

    /**
     * @param string cheke text
     * @return valid String or notValid String
     */
    public static boolean validString(String string) {
        if (string == null)
            return false;

        if (string.equals(""))
            return false;

        if (string.length() == 0)
            return false;

        return !string.equals("null");
    }

    /**
     * @param myObject
     * @return valid Objec or notValid Objec
     */
    public static boolean validObject(Object myObject) {
        return myObject != null;
    }
    /**
     * @param list
     * @return valid List or notValid List
     */
    public static boolean validList(List list) {
        if (list == null)
            return false;

        return list.size() > 0;
    }

    /**
     * @param position
     * @param list
     * @return valid Position in List or notValid Position in List
     */
    public static boolean validPosition(int position, List list) {
        return position >= 0 && position < list.size();
    }

    /**
     * @param position
     * @param objects
     * @return valid Position in Array or notValid Position in Array
     */
    public static boolean validPosition(int position, Object[] objects) {
        return position >= 0 && position < objects.length;
    }

    public static boolean validEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

}
