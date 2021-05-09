package com.example.renewable;

import java.io.Serializable;

public class InsPresInfo implements Serializable {

   private String  ID="",  MAIN_PID="", CA_CUSM_NAME="", CITY_ID="",CTYM_NAME="", ca_cusm_num="", PROVIDE_NOTES_DATEX="", PROCESS_NOTES_DATEX="", INSP_ESTABLISH_DATEX="", Eng_Notes;

    public String getID() {
        return ID;
    }

    public String getMAIN_PID() {
        return MAIN_PID;
    }

    public String getEng_Notes() {
        return Eng_Notes;
    }

    public String getPROVIDE_NOTES_DATEX() {
        return PROVIDE_NOTES_DATEX;
    }

    public String getPROCESS_NOTES_DATEX() {
        return PROCESS_NOTES_DATEX;
    }

    public String getINSP_ESTABLISH_DATEX() {
        return INSP_ESTABLISH_DATEX;
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

    public InsPresInfo(String ID, String MAIN_PID, String CA_CUSM_NAME, String CITY_ID, String CTYM_NAME, String ca_cusm_num, String PROVIDE_NOTES_DATEX, String PROCESS_NOTES_DATEX, String INSP_ESTABLISH_DATEX, String Eng_Notes) {
        this.ID = ID;
        this.MAIN_PID = MAIN_PID;
        this.CA_CUSM_NAME = CA_CUSM_NAME;
        this.CITY_ID = CITY_ID;
        this.CTYM_NAME = CTYM_NAME;
        this.ca_cusm_num = ca_cusm_num;
        this.PROVIDE_NOTES_DATEX = PROVIDE_NOTES_DATEX;
        this.PROCESS_NOTES_DATEX = PROCESS_NOTES_DATEX;
        this.INSP_ESTABLISH_DATEX = INSP_ESTABLISH_DATEX;
        this.Eng_Notes = Eng_Notes;
    }
}


