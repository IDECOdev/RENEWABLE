package com.example.renewable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class InquirActivity extends AppCompatActivity {
    EditText cusmName, cusm_No, city, address_name, lacation;
    SharedPreferences shared;
    ProgressDialog pd;
    String CusmNo = "";
    InquirInfo inquirInfo;
    Button sendbtn, finishbtn, fillbtn;
    String CustomermNum;
    EditText EngNoteDate;
    TextView inspDate, noteDate, processNoteDate;
    SoapObject response2;
    TextView instext;
    RelativeLayout insLay;
    InsPresInfo presInfo;
    String Mpid = "";
    String dates="";
    ArrayList<String> datesArr;

    @Override
    public void onBackPressed() {
    startActivity(new Intent(InquirActivity.this, InspectionIncquireActivity.class));
    finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquir);

        checkAndRequestPermissions();

        insLay = findViewById(R.id.inspdata_lay);
        instext = findViewById(R.id.instext);

        sendbtn = findViewById(R.id.sendbtn);
        sendbtn.setEnabled(false);
        sendbtn. setBackground(getDrawable(R.drawable.shape3));
        sendbtn.setTextColor(getResources().getColor(R.color.grey));

        finishbtn = findViewById(R.id.finishbtn);
        finishbtn.setEnabled(false);
        finishbtn. setBackground(getDrawable(R.drawable.shape3));
        finishbtn.setTextColor(getResources().getColor(R.color.grey));

        fillbtn = findViewById(R.id.fillbtn);
        fillbtn.setEnabled(false);
        fillbtn. setBackground(getDrawable(R.drawable.shape3));
        fillbtn.setTextColor(getResources().getColor(R.color.grey));

        cusmName = findViewById(R.id.cusmName);
        cusm_No = findViewById(R.id.cusm_No);
        city = findViewById(R.id.city);
        address_name = findViewById(R.id.address_name);
        lacation = findViewById(R.id.lacation);

        inspDate = findViewById(R.id.inspDate);
        noteDate = findViewById(R.id.noteDate);
        processNoteDate = findViewById(R.id.processNoteDate);
        EngNoteDate = findViewById(R.id.EngNoteDate);

        datesArr = new ArrayList<>();

        if(getIntent().getStringExtra("MPID").equals("")){
            Intent i = getIntent();
            inquirInfo = (InquirInfo) i.getSerializableExtra("inboxDetail");
            Mpid = inquirInfo.getMAIN_PID();
        }else{
            Intent i = getIntent();
            inquirInfo = (InquirInfo) i.getSerializableExtra("inboxDetail");
            Mpid = getIntent().getStringExtra("MPID");
        }

        PresntDataAsyncCall presntDataAsyncCall=new PresntDataAsyncCall();
        presntDataAsyncCall.execute();

        dates =  getIntent().getStringExtra("dates");

        sendbtn.setEnabled(true);
        sendbtn. setBackground(getDrawable(R.drawable.shape4));
        sendbtn.setTextColor(getResources().getColor(R.color.white));

        finishbtn = findViewById(R.id.finishbtn);
        finishbtn.setEnabled(true);
        finishbtn. setBackground(getDrawable(R.drawable.shape4));
        finishbtn.setTextColor(getResources().getColor(R.color.white));

        fillbtn = findViewById(R.id.fillbtn);
        fillbtn.setEnabled(true);
        fillbtn. setBackground(getDrawable(R.drawable.shape4));
        fillbtn.setTextColor(getResources().getColor(R.color.white));

        insLay.setVisibility(View.VISIBLE);
        instext.setVisibility(View.VISIBLE);


        inspDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDate(inspDate);
            }
        });
        noteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDate(noteDate);
            }
        });
        processNoteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDate(processNoteDate);
            }
        });

        fillbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                inspDate.setText(presInfo.getINSP_ESTABLISH_DATEX());
//                noteDate.setText(presInfo.getPROVIDE_NOTES_DATEX());
//                processNoteDate.setText(presInfo.getPROCESS_NOTES_DATEX());
                if(!inspDate.getText().toString().equals("")){
                    dates = "i"+inspDate.getText().toString()+",";
                }
                if(!noteDate.getText().toString().equals("")){
                    dates += "n"+noteDate.getText().toString()+",";
                }
                if(!processNoteDate.getText().toString().equals("")){
                    dates += "p"+processNoteDate.getText().toString()+",";
                }
                if(!EngNoteDate.getText().toString().equals("")){
                    dates += "e"+EngNoteDate.getText().toString();
                }
                startActivity(new Intent(InquirActivity.this, FillDataActivity.class).putExtra("MPID", Mpid).putExtra("inboxDetail", inquirInfo).putExtra("dates",dates));

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inspDate.getText().toString().equals("")){
                    Toast.makeText(InquirActivity.this, "ادخل تاريخ التفتيش", Toast.LENGTH_SHORT).show();
                    return;
                }

                WorkFlowByAdminAsyncCall workFlowByAdminAsyncCall = new WorkFlowByAdminAsyncCall();
                workFlowByAdminAsyncCall.execute();

            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertFollowUpAsyncCall insertFollowUpAsyncCall = new InsertFollowUpAsyncCall();
                insertFollowUpAsyncCall.execute();
            }
        });

        CircleImageView bar = findViewById(R.id.copy1);


        checkAndRequestPermissions();


    }

    private class PresntDataAsyncCall extends AsyncTask<String, Void, Void> {

        SoapObject present;

        public PresntDataAsyncCall() {
            pd = new ProgressDialog(InquirActivity.this);
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... params) {
            try{
                  String data1 = "iMPID:"+Mpid+",DataType:0";
                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    KSoapClass soap = new KSoapClass();

                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data1);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    present = soap.GetTransRenewable(base64Encoded);

                } catch (Exception e) {
                }

            }
            catch (Exception exception)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            if(present!=null && present.getPropertyCount()>0 && !present.equals("anyType")){
                GetPresentData(present);
            }
        }

        @Override
        protected void onPreExecute() {
            pd.setMessage("الرجاء الانتظار...");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void SetDate(final TextView Date) {
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(InquirActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                try {
                    Date PickedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateFormatter.format(newDate.getTime()));
                    String da = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(PickedDate);
                    Date.setText(da);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
    private  boolean checkAndRequestPermissions() {
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (write != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (read != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (camera != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.CAMERA);

        if (internet != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.INTERNET);

        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 1);
            return false; }
        return true;
    }


    public void settings(View view) {
        startActivity(new Intent(InquirActivity.this, Settings.class));
    }

    private void GetPresentData(SoapObject soapObject1) {
        SoapObject so1, so2, so3;
        String ID="",MAIN_PID="",CA_CUSM_NAME="",CITY_ID="",CTYM_NAME="",ca_cusm_num="", PROVIDE_NOTES_DATEX="", PROCESS_NOTES_DATEX="", INSP_ESTABLISH_DATEX="", Eng_Notes = "";

        if (soapObject1 != null && soapObject1.getPropertyCount() > 0){
            so1 = (SoapObject) soapObject1.getProperty(1);
            if (so1 != null && so1.getPropertyCount() > 0){
                so2 = (SoapObject) so1.getProperty(0);
                if (so2 != null && so2.getPropertyCount() > 0){
                    for(int i=0; i<so2.getPropertyCount(); i++){
                        so3 = (SoapObject) so2.getProperty(i);
                        try{
                            try{
                                ID = so3.getPropertyAsString("ID");
                            }catch (Exception e){}

                            try{
                                MAIN_PID = so3.getPropertyAsString("MAIN_PID");
                            }catch (Exception e){}
                            try{
                                CA_CUSM_NAME =  so3.getPropertyAsString("CA_CUSM_NAME");
                            }catch (Exception e){}
                            try{
                                CITY_ID=  so3.getPropertyAsString("CITY_ID");
                            }catch (Exception e){}
                            try{
                                CTYM_NAME = so3.getPropertyAsString("CTYM_NAME");
                            }catch (Exception e){}
                            try{
                                ca_cusm_num = so3.getPropertyAsString("CA_CUSM_NUM");
                            }catch (Exception e){}
                            try{
                                INSP_ESTABLISH_DATEX = so3.getPropertyAsString("INSP_ESTABLISH_DATEX");
                            }catch (Exception e){}
                            try{
                                PROVIDE_NOTES_DATEX = so3.getPropertyAsString("PROVIDE_NOTES_DATEX");
                            }catch (Exception e){}
                            try{
                                PROCESS_NOTES_DATEX = so3.getPropertyAsString("PROCESS_NOTES_DATEX");
                            }catch (Exception e){}
                            try{
                                Eng_Notes = so3.getPropertyAsString("ENG_NOTES");
                            }catch (Exception e){}

                            presInfo = new InsPresInfo(ID,MAIN_PID,CA_CUSM_NAME,CITY_ID,CTYM_NAME,ca_cusm_num, PROVIDE_NOTES_DATEX, PROCESS_NOTES_DATEX, INSP_ESTABLISH_DATEX, Eng_Notes);

                        }catch (Exception e){

                        }
                    }
                }
            }
        }
        CusmNo = inquirInfo.getCa_cusm_num();
        CustomermNum="";
        CustomermNum+= String.format(Locale.ENGLISH, "%03d", Integer.parseInt(inquirInfo.getCITY_ID()));
        CustomermNum+= "0"+String.format(Locale.ENGLISH, "%06d", Integer.parseInt(inquirInfo.getCa_cusm_num()));
        cusmName.setText(inquirInfo.getCA_CUSM_NAME());
        cusm_No.setText(CustomermNum);
        city.setText(presInfo.getCITY_ID());
        address_name.setText(presInfo.getCTYM_NAME());
        if(!inquirInfo.getCA_X_COORDINATE().equals("") && !inquirInfo.getCA_Y_COORDINATE().equals("")){
            lacation.setText(inquirInfo.getCA_X_COORDINATE() + "," + inquirInfo.getCA_Y_COORDINATE());
        }else{
            lacation.setText("لم يتم تحديد احداثيات الموقع");
        }
        if(presInfo!=null){
            inspDate.setText(presInfo.getINSP_ESTABLISH_DATEX());
            noteDate.setText(presInfo.getPROVIDE_NOTES_DATEX());
            processNoteDate.setText(presInfo.getPROCESS_NOTES_DATEX());
            EngNoteDate.setText(presInfo.getEng_Notes());
        }

        if(!dates.equals("")){
            String arr[] = dates.split(",");
            for (int i = 0; i<arr.length; i++){
                if(arr[i].contains("i")){
                    inspDate.setText(arr[i].substring(1));
                }
                if(arr[i].contains("n")){
                    noteDate.setText(arr[i].substring(1));
                }
                if(arr[i].contains("p")){
                    processNoteDate.setText(arr[i].substring(1));
                }
                if(arr[i].contains("e")){
                    EngNoteDate.setText(arr[i].substring(1));
                }
            }
        }

    }

    private class WorkFlowByAdminAsyncCall extends AsyncTask<String, Void, Void> {
        boolean flag = false;
        boolean updateRen1 = false,updateRen2 = false;
        SoapPrimitive closeApp;
        public WorkFlowByAdminAsyncCall() {
            pd = new ProgressDialog(InquirActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {
                KSoapClass soap = new KSoapClass();
                String data1 = "Id:" + inquirInfo.getID()+",: ,engNote:" + EngNoteDate.getText().toString() + ",:0,:0,:0,:0,:0,:0,:0,: ,: ,:0,uId:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", "")
                        + ",strUserName:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("UserName", "")
                        + ",:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,dtpPROVIDE_NOTES_DATE:" + noteDate.getText().toString() + ",dtpInspDate:" + inspDate.getText().toString() + ",: ,: ,: ,dtpPROCESS_NOTES_DATE:" + processNoteDate.getText().toString() +
                        ",:0,:12";

                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data1);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    updateRen1 = soap.UpdateTransRenewableNew(base64Encoded);

                } catch (Exception e) {
                }

                if (updateRen1) {

                    String data2 = "mPID:" + Mpid + ",:0,: ,:0,:0,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,dtpInspDate:" + inspDate.getText().toString()
                            + ",: ,: ,: ,: ,: ,: ,:9,: ";

                    try {
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                        PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                        RSA.setKey(pubKey, privKey);

                        byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data2);
                        String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                        updateRen2 = soap.UPDATE_RenewableData(base64Encoded);
                    } catch (Exception e) {}

                    if (updateRen2) {
                        flag = soap.WorkFlowAdvanceByAdmin(getEncodedString3(soap,120, 1005249, Integer.parseInt(Mpid), "Root/RenewableInsp",
                                "1", " ", " ", " ", " ", " ", " ", " ", " ", " ",
                                " ",  " ", " ", " ", " "));
                    }
                    if(flag){
                        String data3;
                        if(getSharedPreferences("Info", Context.MODE_PRIVATE).getString("respId", "").equals("")){
                            data3 = ":"+Mpid+",:120,:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("EMP_NO", "")+",:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("NAME", "");
                        }else {
                            data3 =":"+Mpid+",:120,:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("respId", "")+",:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("NAME", "");
                        }

                        try {
                            KeyFactory kf = KeyFactory.getInstance("RSA");
                            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                            RSA.setKey(pubKey, privKey);

                            byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data3);
                            String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                            closeApp = soap.INSERT_RENEWABLE_APP_CLOSE(base64Encoded);
                        } catch (Exception e) {
                        }

                    }
                }

            } catch (Exception e) {}

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{
                if(flag){
                    if(Integer.parseInt(String.valueOf(closeApp))<0){
                        pd.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
                        LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                        final AlertDialog dialog1 = builder.create();
                        ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog1.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog1.show();
                        dialog1.getWindow().setAttributes(lp);
                        final Button exit=dialog1.findViewById(R.id.btn2);
                        final CircleImageView im=dialog1.findViewById(R.id.im);
                        final TextView textView3=dialog1.findViewById(R.id.textView3);
                        textView3.setText("تمت عملية الحفظ و الارسال بنجاح مع خطأ في الادخال الى جدول الاغلاق من التطبيق");

                        exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
//
                                cusmName.setText("");
                                cusm_No.setText("");
                                city.setText("");
                                address_name.setText("");
                                lacation.setText("");

                                sendbtn.setEnabled(false);
                                sendbtn. setBackground(getDrawable(R.drawable.shape3));
                                sendbtn.setTextColor(getResources().getColor(R.color.grey));

                                finishbtn.setEnabled(false);
                                finishbtn. setBackground(getDrawable(R.drawable.shape3));
                                finishbtn.setTextColor(getResources().getColor(R.color.grey));

                                fillbtn.setEnabled(false);
                                fillbtn. setBackground(getDrawable(R.drawable.shape3));
                                fillbtn.setTextColor(getResources().getColor(R.color.grey));

                                startActivity(new Intent(InquirActivity.this, InspectionIncquireActivity.class));
                                finish();

                            }
                        });
                    }else{
                        pd.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
                        LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                        final AlertDialog dialog1 = builder.create();
                        ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog1.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog1.show();
                        dialog1.getWindow().setAttributes(lp);
                        final Button exit=dialog1.findViewById(R.id.btn2);
                        final CircleImageView im=dialog1.findViewById(R.id.im);
                        final TextView textView3=dialog1.findViewById(R.id.textView3);
                        textView3.setText("تمت عملية الحفظ و الارسال بنجاح");

                        exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                                cusmName.setText("");
                                cusm_No.setText("");
                                city.setText("");
                                address_name.setText("");
                                lacation.setText("");

                                sendbtn.setEnabled(false);
                                sendbtn. setBackground(getDrawable(R.drawable.shape3));
                                sendbtn.setTextColor(getResources().getColor(R.color.grey));

                                finishbtn.setEnabled(false);
                                finishbtn. setBackground(getDrawable(R.drawable.shape3));
                                finishbtn.setTextColor(getResources().getColor(R.color.grey));

                                fillbtn.setEnabled(false);
                                fillbtn. setBackground(getDrawable(R.drawable.shape3));
                                fillbtn.setTextColor(getResources().getColor(R.color.grey));

                                startActivity(new Intent(InquirActivity.this, InspectionIncquireActivity.class));
                                finish();

                            }
                        });
                    }

                }else {
                    pd.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
                    LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                    final AlertDialog dialog1 = builder.create();
                    ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog1.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog1.show();
                    dialog1.getWindow().setAttributes(lp);
                    final Button exit=dialog1.findViewById(R.id.btn2);
                    final CircleImageView im=dialog1.findViewById(R.id.im);
                    final TextView textView3=dialog1.findViewById(R.id.textView3);
                    textView3.setText(" لقد حدث خطأ في حفظ البيانات");

                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                }
            }catch(Exception e){}

        }

        @Override
        protected void onPreExecute() {

            pd.setMessage("يرجى الأنتظار...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }
    private String getEncodedString3(KSoapClass service, int TID, int ModelID, int PID,
                                     String XML1, String XMLData1, String XML2, String XMLData2,
                                     String XML3, String XMLData3, String XML4, String XMLData4,
                                     String XML5, String XMLData5, String XML6, String XMLData6,
                                     String XML7, String XMLData7, String XML8, String XMLData8){
        try {

            String data =
                    "TID:"+TID+
                            ",ModelID:"+ModelID+
                            ",PID:"+PID+
                            ",XML1:"+XML1+
                            ",XMLData1:"+XMLData1+
                            ",XML2:"+XML2+
                            ",XMLData2:"+XMLData2+
                            ",XML3:"+XML3+
                            ",XMLData3:"+XMLData3+
                            ",XML4:"+XML4+
                            ",XMLData4:"+XMLData4+
                            ",XML5:"+XML5+
                            ",XMLData5:"+XMLData5+
                            ",XML6:"+XML6+
                            ",XMLData6:"+XMLData6+
                            ",XML7:"+XML7+
                            ",XMLData7:"+XMLData7+
                            ",XML8:"+XML8+
                            ",XMLData8:"+XMLData8;

            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(android.util.Base64.decode(service.privateKey, android.util.Base64.DEFAULT));
            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(android.util.Base64.decode(service.publicKey, android.util.Base64.DEFAULT));
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
            RSA.setKey(pubKey, privKey);
            byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
            String base64Encoded = android.util.Base64.encodeToString(encodeData, android.util.Base64.DEFAULT);
            return base64Encoded;
        }
        catch (Exception ex){
            return null;
        }
    }

    private class InsertFollowUpAsyncCall extends AsyncTask<String, Void, Void> {
        SoapPrimitive flag;
        boolean updateRen1 = false,updateRen2 = false;
        SoapPrimitive closeApp;

        public InsertFollowUpAsyncCall() {
            pd = new ProgressDialog(InquirActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {
                KSoapClass soap = new KSoapClass();
                String data1 = "Id:"+inquirInfo.getID()+ ",: ,engNote:" + EngNoteDate.getText().toString() + ",:0,:0,:0,:0,:0,:0,:0,: ,: ,:0,uId:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", "")
                        + ",strUserName:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("UserName", "")
                        + ",:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,dtpPROVIDE_NOTES_DATE:" + noteDate.getText().toString() + ",dtpInspDate:" + inspDate.getText().toString() + ",: ,: ,: ,dtpPROCESS_NOTES_DATE:" + processNoteDate.getText().toString() +
                        ",:0,:12";

                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data1);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    updateRen1 = soap.UpdateTransRenewableNew(base64Encoded);

                } catch (Exception e) {}

                if (updateRen1) {

                    String data2 = "mPID:" + Mpid + ",:0,: ,:0,:0,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,dtpInspDate:" + inspDate.getText().toString()
                            + ",: ,: ,: ,: ,: ,: ,:9,: ";
                    try {
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                        PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                        RSA.setKey(pubKey, privKey);

                        byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data2);
                        String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                        updateRen2 = soap.UPDATE_RenewableData(base64Encoded);
                    } catch (Exception e) {}
                }

                    if(updateRen2) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDateTime now = LocalDateTime.now();

                        String data3 = "uId:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", "") + ",strUserName:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("UserName", "")
                                + ",mPID:" + Mpid + ",txtFollowUps:تم الكشف على نظام الطاقة المتجددة بتاريخ " + dtf.format(now) + " ووجدت هناك ملاحظات ولم يتم استكمال اجراءات التشغيل";

                        try {
                            KeyFactory kf = KeyFactory.getInstance("RSA");
                            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                            RSA.setKey(pubKey, privKey);

                            byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data3);
                            String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                            flag = soap.InsertFollowUp(base64Encoded);
//                            if(String.valueOf(flag).equals("true")){
//                                closeApp = soap.INSERT_RENEWABLE_APP_CLOSE(":"+Mpid+",:120,:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", ""));
//                            }
                        } catch (Exception e) {
                        }
                    }

            } catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{
                if(flag.toString().equals("true")){

                         pd.dismiss();
                         final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
                         LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
                         builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                         final AlertDialog dialog1 = builder.create();
                         ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                         WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                         lp.copyFrom(dialog1.getWindow().getAttributes());
                         lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                         lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                         dialog1.show();
                         dialog1.getWindow().setAttributes(lp);
                         final Button exit=dialog1.findViewById(R.id.btn2);
                         final CircleImageView im=dialog1.findViewById(R.id.im);
                         final TextView textView3=dialog1.findViewById(R.id.textView3);
                         textView3.setText("تمت اضافة المتابعة بنجاح");

                         exit.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 dialog1.dismiss();
                             }
                         });

                }else {
                    pd.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
                    LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                    final AlertDialog dialog1 = builder.create();
                    ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog1.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog1.show();
                    dialog1.getWindow().setAttributes(lp);
                    final Button exit=dialog1.findViewById(R.id.btn2);
                    final CircleImageView im=dialog1.findViewById(R.id.im);
                    final TextView textView3=dialog1.findViewById(R.id.textView3);
                    textView3.setText("لم تتم عملية الاضافة");

                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                }
            }catch(Exception e){}
        }

        @Override
        protected void onPreExecute() {

            pd.setMessage("يرجى الأنتظار...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }


    private void GetReadableData1(SoapObject res){
    try{
        inquirInfo=null;
        if(res == null){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
//            cusmNum_et.setEnabled(true);
//            processNum_et.setEnabled(true);
        }
        else if(res.equals("anyType{}")){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
//            cusmNum_et.setEnabled(true);
//            processNum_et.setEnabled(true);
        } else{
            SoapObject so1, so2, so3;
            String ID="",MAIN_PID="",CA_CUSM_NAME="",CITY_ID="",CTYM_NAME="",ca_cusm_num="",CA_X_COORDINATE="",CA_Y_COORDINATE="";

            if (res != null && res.getPropertyCount() > 0){
                so1 = (SoapObject) res.getProperty(1);
                if (so1 != null && so1.getPropertyCount() > 0){
                    so2 = (SoapObject) so1.getProperty(0);
                    if (so2 != null && so2.getPropertyCount() > 0){
                        for(int i=0; i<so2.getPropertyCount(); i++){
                            so3 = (SoapObject) so2.getProperty(i);
                            try{
                                try{
                                    ID = so3.getPropertyAsString("ID");
                                }catch (Exception e){}

                                try{
                                    MAIN_PID = so3.getPropertyAsString("MAIN_PID");
                                }catch (Exception e){}
                                try{
                                    CA_CUSM_NAME =  so3.getPropertyAsString("CA_CUSM_NAME");
                                }catch (Exception e){}
                                try{
                                    CITY_ID=  so3.getPropertyAsString("CITY_ID");
                                }catch (Exception e){}
                                try{
                                    CTYM_NAME = so3.getPropertyAsString("CTYM_NAME");
                                }catch (Exception e){}
                                try{
                                    ca_cusm_num = so3.getPropertyAsString("ca_cusm_num");
                                }catch (Exception e){}
                                try{
                                    CA_X_COORDINATE = so3.getPropertyAsString("CA_X_COORDINATE");
                                }catch (Exception e){}
                                try{
                                    CA_Y_COORDINATE = so3.getPropertyAsString("CA_Y_COORDINATE");
                                }catch (Exception e){}

                                inquirInfo = new InquirInfo(ID,MAIN_PID,CA_CUSM_NAME,CITY_ID,CTYM_NAME,ca_cusm_num,CA_X_COORDINATE,CA_Y_COORDINATE);

                            }catch (Exception e){

                            }
                        }
                    }
                }
            }
            if(inquirInfo!=null){
                CusmNo = inquirInfo.getCa_cusm_num();
                CustomermNum="";
                CustomermNum+= String.format(Locale.ENGLISH, "%03d", Integer.parseInt(inquirInfo.getCITY_ID()));
                CustomermNum+= "0"+String.format(Locale.ENGLISH, "%06d", Integer.parseInt(inquirInfo.getCa_cusm_num()));
                cusmName.setText(inquirInfo.getCA_CUSM_NAME());
                cusm_No.setText(CustomermNum);
                city.setText(inquirInfo.getCITY_ID());
                address_name.setText(inquirInfo.getCTYM_NAME());
                if(!CA_X_COORDINATE.equals("") && !CA_X_COORDINATE.equals("")){
                    lacation.setText(CA_X_COORDINATE + "," + CA_Y_COORDINATE);
                }else{
                    lacation.setText("لم يتم تحديد احداثيات الموقع");
                }

                sendbtn.setEnabled(true);
                sendbtn. setBackground(getDrawable(R.drawable.shape4));
                sendbtn.setTextColor(getResources().getColor(R.color.white));

                finishbtn = findViewById(R.id.finishbtn);
                finishbtn.setEnabled(true);
                finishbtn. setBackground(getDrawable(R.drawable.shape4));
                finishbtn.setTextColor(getResources().getColor(R.color.white));

                fillbtn = findViewById(R.id.fillbtn);
                fillbtn.setEnabled(true);
                fillbtn. setBackground(getDrawable(R.drawable.shape4));
                fillbtn.setTextColor(getResources().getColor(R.color.white));

                insLay.setVisibility(View.VISIBLE);
                instext.setVisibility(View.VISIBLE);
            }
            else{
                Toast.makeText(this, "يوجد خطأ في رقم الاشتراك او رقم المعاملة", Toast.LENGTH_LONG).show();
//                cusmNum_et.setEnabled(true);
//                cusmNum_et.setText("");
//                processNum_et.setEnabled(true);
//                processNum_et.setText("");
            }
        }
    }catch (Exception e){
        Toast.makeText(this, "لقد حدث خطأ", Toast.LENGTH_SHORT).show();

    }

    }

}
