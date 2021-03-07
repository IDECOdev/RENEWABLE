package com.example.renewable;

public class Questions {
   String  SYS_MINOR="", SYS_DESC="";

    public Questions(String SYS_MINOR, String SYS_DESC) {
        this.SYS_MINOR = SYS_MINOR;
        this.SYS_DESC = SYS_DESC;
    }

    public String getSYS_MINOR() {
        return SYS_MINOR;
    }

    public String getSYS_DESC() {
        return SYS_DESC;
    }
}
