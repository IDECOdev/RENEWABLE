package com.example.renewable;

import java.io.Serializable;

public class InquirInfo implements Serializable {

   private String  CTYM_NAME="",  CUSM_NAME="", MTR_NUM="", SMART="", MTR_CITY, MTR_M_KIND, MTR_M_MODEL, CUSM_ADDRES, MTR_M_NUM;

    public String getMTR_M_NUM() {
        return MTR_M_NUM;
    }

    public String getMTR_M_KIND() {
        return MTR_M_KIND;
    }

    public String getMTR_M_MODEL() {
        return MTR_M_MODEL;
    }

    public String getCUSM_ADDRES() {
        return CUSM_ADDRES;
    }

    public String getMTR_CITY() {
        return MTR_CITY;
    }

    public String getMTR_NUM() {
        return MTR_NUM;
    }

    public String getSMART() {
        return SMART;
    }


    public String getCTYM_NAME() {
        return CTYM_NAME;
    }

    public String getCUSM_NAME() {
        return CUSM_NAME;
    }

    public InquirInfo(String CTYM_NAME, String CUSM_NAME, String MTR_NUM, String MTR_CITY, String SMART , String MTR_M_KIND, String MTR_M_MODEL, String CUSM_ADDRES, String MTR_M_NUM) {
        this.CTYM_NAME = CTYM_NAME;
        this.CUSM_NAME = CUSM_NAME;
        this.MTR_NUM = MTR_NUM;
        this.SMART = SMART;
        this.MTR_CITY = MTR_CITY;
        this.MTR_M_KIND = MTR_M_KIND;
        this.MTR_M_MODEL = MTR_M_MODEL;
        this.CUSM_ADDRES = CUSM_ADDRES;
        this.MTR_M_NUM = MTR_M_NUM;
    }

}
