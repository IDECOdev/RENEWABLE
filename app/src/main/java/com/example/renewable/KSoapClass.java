package com.example.renewable;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;

public class KSoapClass {

//    public String Url ="https://ideco.com.jo/IDECO_API_PROD/IdecoWcfsrv.svc";
//    String Url ="http://192.168.14.30/WebSite/PortalPublicWS/IdecoWcfsrv.svc";
    String Url ="http://192.168.14.30/WebSite/PortalPublicWS/IdecoWcfsrv.svc";

    public String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtysuzG+lFQ5oDPIDIigJ7l9/kiCsU3qJ0MaNsPzQLXPlrnGlx4x29gSeP5V8d5VVJt2X2vu9oO7+6cKa1rxPERGwGIZyqv8DU59nf7pIUjcQ0IwXJwY0h5wBEcgv8TooYO9VqAvsbyNnrHvY4rgspFB3qjDhkO4IewSKtS5/LT+pIyymBh3/BvmjsSo9TrJ8vaMRFcvg4QumKmpOZyAPnRwPpKdoarjB3UyPzLJaMJUgKxWZYdeQL/5Rqd3BRXnZCYcGZXnLuyGOKf9x5ZoqeqyByYAt0brSWToTk4yCJoyd/gzg+IF/IeniQKNxpYXs1zlnDNJk6kjaRFAWSgK+xHxbSuI34pQZPlTRmdxthjBEl/rsKa8puRVf2OT4OO9OJpUbPwaLAISzpM6s6gN+UtQ0ke03e6Hiu12gApGUGwr9F7mDDrc3weYiODaySgBM35w3jYLtGe3BfxvPegUjcyBrKJY7jjZGI6w319s2cOcyDDtOvLs+ebqxsunWyjdJOYLZjAHjSTrq36cv5bVX/u3B5THvdkHfm99RyQvqjQzY1VF2plQvYwJ7KDkZP4v2gPbDaKdmf757Qi/1SCX8oZASc+gOXQpPf6TpTwu/FFkhOt0nwqc9cpwYkIMlFW6oxO12lPVxB/x9LpXanE1G+CdhPei3HP2ZYHEsi26ht8kCAwEAAQ==";
    public String privateKey = "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";

    public SoapObject GetGenericsDataTable(String data){  //done zz
        try {
            String NameSpace = "http://tempuri.org/";
            String MethodName = "GetGenericsDataTable";
            String SoapAction = NameSpace + "IBillingWcfsrv/" +MethodName;
            SoapObject request = new SoapObject(NameSpace, MethodName);

            request.addProperty("data", data);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
            androidHttpTransport.debug = true;
            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            return response;
        } catch (Exception e) {
           return null;
        }

    }
    public SoapObject GetAppLoginUser(String data) {
        String NameSpace = "http://tempuri.org/";
        String MethodName = "GetAppLoginUser";
        String SoapAction = "http://tempuri.org/IBillingWcfsrv/GetAppLoginUser";

        SoapObject request = new SoapObject(NameSpace, MethodName);
        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            return response; }
        catch (Exception e) {
            return null; }
    }

    public SoapObject GetMeterInfo(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "GetCustomerBillingInfoEnc";
        String InterFace = "IBillingWcfsrv/";
        String SoapAction = NameSpace+InterFace+MethodName;
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            return response; }
        catch (Exception e) { return null; }
    }

    public SoapObject GetRenewable_Canceled(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "GetRenewable_Canceled";
        String InterFace = "IBillingWcfsrv/";
        String SoapAction = NameSpace+InterFace+MethodName;
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            return response; }
        catch (Exception e) {
            return null; }
    }
    public SoapObject GetTransRenewable(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "GetTransRenewable";
        String InterFace = "IBillingWcfsrv/";
        String SoapAction = NameSpace+InterFace+MethodName;
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            return response; }
        catch (Exception e) {
            return null; }
    }

    public boolean UpdateTransRenewableNew(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "UpdateTransRenewableNew";
        String InterFace = "IBillingWcfsrv/";
        String SoapAction = NameSpace+InterFace+MethodName;
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
           if(response.toString().equals("true")){
               return true;
           }else {
               return false;
           }
        }
        catch (Exception e) { return false; }
    }

    public boolean UPDATE_RenewableData(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "UPDATE_RenewableData";
        String InterFace = "IBillingWcfsrv/";
        String SoapAction = NameSpace+InterFace+MethodName;
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if(response.toString().equals("true")){
                return true;
            }else {
                return false;
            }
        }
        catch (Exception e) { return false; }
    }

    public boolean WorkFlowAdvanceByAdmin(String data) //done......x100000 zz
    {
        String NameSpace = "http://tempuri.org/";
        String MethodName = "WorkFlowAdvanceByAdmin";
        String SoapAction = "http://tempuri.org/IBillingWcfsrv/WorkFlowAdvanceByAdmin";

        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);

        androidHttpTransport.debug = true;

        try
        {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if(response.toString().equals("true")){
                return true;
            }else {
                return false;
            }
        }
        catch (Exception exception)
        {
            return false;
        }

    }

    public SoapPrimitive InsertFollowUp(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "InsertFollowUp";
        String InterFace = "IBillingWcfsrv/";
        String SoapAction = NameSpace+InterFace+MethodName;
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            return response; }
        catch (Exception e) { return null; }
    }

    public SoapPrimitive Update_User_Pass(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "UpdateUserPassword";
        String SoapAction = "http://tempuri.org/IBillingWcfsrv/UpdateUserPassword";
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            return response;
        } catch (Exception e) {
            return null;
        }

    }

    public boolean Insert_Renewable_Images(String imageFileName1, String imageFileName2, Bitmap bitmap1, Bitmap bitmap2){


        String NameSpace = "http://tempuri.org/";
        String MethodName = "Insert_Renewable_Images";
        String SoapAction = "http://tempuri.org/Insert_Renewable_Images";
        String Url = "http://192.168.14.72/WebSite/PortalPublicWS/Billing.asmx";
//        String Url= "http://ideco.com.jo/PortalPublicWS/Billing.asmx";

        SoapObject request = new SoapObject(NameSpace, MethodName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap1 = Bitmap.createScaledBitmap(bitmap1, 600, 800, false);
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        String encoded2;

        try{
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap2 = Bitmap.createScaledBitmap(bitmap2, 600, 800, false);
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            byteArray = byteArrayOutputStream .toByteArray();
            encoded2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        catch(Exception e){
            encoded2="";
        }

        request.addProperty("name1", imageFileName1);
        request.addProperty("name2", imageFileName2);
        request.addProperty("Imagebytes1", encoded);
        request.addProperty("Imagebytes2", encoded2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {

            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            String temp = response.toString();
            if(temp.contains("true"))
                return true;
            else
                return false;
        }
        catch (Exception e) {
            return false; }

    }

    public SoapObject GetINSPRenTemplate(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "GetINSPRenTemplate";
        String SoapAction = "http://tempuri.org/IBillingWcfsrv/GetINSPRenTemplate";
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            return response;
        } catch (Exception e) {
            return null;
        }

    }

    public SoapPrimitive InsertINSPTemplate(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "InsertINSPTemplate";
        String SoapAction = "http://tempuri.org/IBillingWcfsrv/InsertINSPTemplate";
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public SoapPrimitive UpdateINSPTemplate(String data){

        String NameSpace = "http://tempuri.org/";
        String MethodName = "UpdateINSPTemplate";
        String SoapAction = "http://tempuri.org/IBillingWcfsrv/UpdateINSPTemplate";
        SoapObject request = new SoapObject(NameSpace, MethodName);

        request.addProperty("data", data);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
        androidHttpTransport.debug = true;

        try {
            androidHttpTransport.call(SoapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public SoapObject GetAppVersion(){
        try {

            String NameSpace = "http://tempuri.org/";
            String MethodName = "GetAppVersion";
            String InterFace = "IBillingWcfsrv/";
            String SoapAction = NameSpace+InterFace+MethodName;
            SoapObject request = new SoapObject(NameSpace, MethodName);

            request.addProperty("DataType", "2");
            request.addProperty("sWhere", " and ID = '9'");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
            androidHttpTransport.debug = true;

            androidHttpTransport.call(SoapAction, envelope);
            SoapObject response2=(SoapObject) envelope.getResponse();

            return response2; }

        catch (Exception e) {
            return null; }

    }

}