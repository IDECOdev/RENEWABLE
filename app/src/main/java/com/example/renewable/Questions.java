package com.example.renewable;

public class Questions {
   String  MEMBER_ID="", MEMBER_DESC="";

    public Questions(String SYS_MINOR, String SYS_DESC) {
        this.MEMBER_ID = SYS_MINOR;
        this.MEMBER_DESC = SYS_DESC;
    }

    public String getSYS_MINOR() {
        return MEMBER_ID;
    }

    public String getSYS_DESC() {
        return MEMBER_DESC;
    }
}
