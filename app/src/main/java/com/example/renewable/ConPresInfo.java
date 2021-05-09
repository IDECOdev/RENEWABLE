package com.example.renewable;

import java.io.Serializable;

public class ConPresInfo implements Serializable {

   private String  ID="",  MAIN_PID="", CA_CUSM_NAME="", CITY_ID="",CTYM_NAME="", ca_cusm_num="", SYSTEM_CONN_DATEX="", REN_M_LREAD_OP="", REN_M_PREAD_OP="", NOTES="";

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

    public String getSYSTEM_CONN_DATEX() {
        return SYSTEM_CONN_DATEX;
    }

    public String getREN_M_LREAD_OP() {
        return REN_M_LREAD_OP;
    }

    public String getREN_M_PREAD_OP() {
        return REN_M_PREAD_OP;
    }

    public String getNOTES() {
        return NOTES;
    }

    public String getCa_cusm_num() {
        return ca_cusm_num;
    }

    public ConPresInfo(String ID, String MAIN_PID, String CA_CUSM_NAME, String CITY_ID, String CTYM_NAME, String ca_cusm_num, String SYSTEM_CONN_DATEX, String REN_M_LREAD_OP, String REN_M_PREAD_OP, String NOTES) {
        this.ID = ID;
        this.MAIN_PID = MAIN_PID;
        this.CA_CUSM_NAME = CA_CUSM_NAME;
        this.CITY_ID = CITY_ID;
        this.CTYM_NAME = CTYM_NAME;
        this.ca_cusm_num = ca_cusm_num;
        this.SYSTEM_CONN_DATEX = SYSTEM_CONN_DATEX;
        this.REN_M_LREAD_OP = REN_M_LREAD_OP;
        this.REN_M_PREAD_OP = REN_M_PREAD_OP;
        this.NOTES = NOTES;
    }
}


