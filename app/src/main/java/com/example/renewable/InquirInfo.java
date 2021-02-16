package com.example.renewable;

import java.io.Serializable;

public class InquirInfo implements Serializable {

   private String  ID="",  MAIN_PID="", CA_CUSM_NAME="", CITY_ID="",CTYM_NAME, ca_cusm_num;

    public String getID() {
        return ID;
    }

    public String getMAIN_PID() {
        return MAIN_PID;
    }

    public String getCA_CUSM_NAME() {
        return CA_CUSM_NAME;
    }

    public String getCITY_ID() {
        return CITY_ID;
    }

    public String getCTYM_NAME() {
        return CTYM_NAME;
    }

    public String getCa_cusm_num() {
        return ca_cusm_num;
    }

    public InquirInfo(String ID, String MAIN_PID, String CA_CUSM_NAME, String CITY_ID, String CTYM_NAME, String ca_cusm_num) {
        this.ID = ID;
        this.MAIN_PID = MAIN_PID;
        this.CA_CUSM_NAME = CA_CUSM_NAME;
        this.CITY_ID = CITY_ID;
        this.CTYM_NAME = CTYM_NAME;
        this.ca_cusm_num = ca_cusm_num;
    }
}


