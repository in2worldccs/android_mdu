package com.in2world.ccs.helper;

public class Constants {


    private final static String SOCKET_PORT = "4007";
    private final static String API_PORT = "3007";
    private final static String DOMAIN_NAME = "ibrahemayyad.ga";
    public final static String DOMAIN_URL = "https://"+DOMAIN_NAME+":"+API_PORT+"/api/v1/";
    public final static String SOCKET_SERVER_URL = "https://"+DOMAIN_NAME+":"+SOCKET_PORT;
    public final static String USER_ID = "user_id";
    public final static String GROUP_ID = "group_id";


    public enum ACTION{
        STARTFOREGROUND("STARTFOREGROUND"),
        STOPFOREGROUND("STOPFOREGROUND_ACTION");

        private String value;
        ACTION(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
