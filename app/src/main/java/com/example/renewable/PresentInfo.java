package com.example.renewable;

public class PresentInfo {

    String ID="", ENTRY_USER="",ENTRY_DATE= "",MPID = "", MEMBER_ID = "", MEMBER_DESC = "", YES_FLAG = "", MINIMUM_VALUE = "", MEASURED_VALUE = "", EXACT_VALUE ="", INSERT_TYPE = "";

    public String getID() {
        return ID;
    }

    public String getENTRY_USER() {
        return ENTRY_USER;
    }

    public String getENTRY_DATE() {
        return ENTRY_DATE;
    }

    public String getMPID() {
        return MPID;
    }

    public String getMEMBER_ID() {
        return MEMBER_ID;
    }

    public String getMEMBER_DESC() {
        return MEMBER_DESC;
    }

    public String getYES_FLAG() {
        return YES_FLAG;
    }

    public String getMINIMUM_VALUE() {
        return MINIMUM_VALUE;
    }

    public String getMEASURED_VALUE() {
        return MEASURED_VALUE;
    }

    public String getEXACT_VALUE() {
        return EXACT_VALUE;
    }

    public String getINSERT_TYPE() {
        return INSERT_TYPE;
    }

    public PresentInfo(String ID, String ENTRY_USER, String ENTRY_DATE, String MPID, String MEMBER_ID, String MEMBER_DESC, String YES_FLAG, String MINIMUM_VALUE, String MEASURED_VALUE, String EXACT_VALUE, String INSERT_TYPE) {
        this.ID = ID;
        this.ENTRY_USER = ENTRY_USER;
        this.ENTRY_DATE = ENTRY_DATE;
        this.MPID = MPID;
        this.MEMBER_ID = MEMBER_ID;
        this.MEMBER_DESC = MEMBER_DESC;
        this.YES_FLAG = YES_FLAG;
        this.MINIMUM_VALUE = MINIMUM_VALUE;
        this.MEASURED_VALUE = MEASURED_VALUE;
        this.EXACT_VALUE = EXACT_VALUE;
        this.INSERT_TYPE = INSERT_TYPE;
    }
}
