package com.in2world.ccs.helper;

public class Constants {

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
