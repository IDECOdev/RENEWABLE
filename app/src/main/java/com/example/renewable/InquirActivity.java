package com.example.renewable;

import android.Manifest;
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

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class InquirActivity extends AppCompatActivity {
    EditText cusmNum_et, processNum_et, cusmName, cusm_No, city, address_name, lacation;
    SharedPreferences shared;
    ProgressDialog pd;
    String CusmNo = "";
    InquirInfo inquirInfo;
    Button inquir_btn2, inquir_btn1, sendbtn, finishbtn;
    String CustomermNum;
    EditText inspDate, noteDate, processNoteDate, EngNoteDate;

    public class ScanDialog extends Dialog {

        public Activity activity;
        public Dialog dialog;
        public ScanDialog(Activity act) {
            super(act);
            // TODO Auto-generated constructor stub
            this.activity = act;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.scan);

                startScanning();


        }
        public void startScanning() {

            CodeScanner mCodeScanner;
            CodeScannerView mCodeScannerView;

            mCodeScannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(activity, mCodeScannerView);
            mCodeScanner.startPreview();   // this line is very important, as you will not be able to scan your code without this, you will only get blank screen
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!result.toString().equals("")){
                                cusmNum_et.setText(result.toString().replaceAll(" ", ""));
                                CustomerCashAsyncCall api = new CustomerCashAsyncCall();
                                api.execute();
                                ScanDialog.this.dismiss(); }
                            else
                                cusmNum_et.setText("");
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
        LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_info, null));
        final AlertDialog dialog = builder.create();
        ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        final Button exit = dialog.findViewById(R.id.btn2);
        final CircleImageView im = dialog.findViewById(R.id.im);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquir);

        cusmNum_et = findViewById(R.id.cusmNum_et);
        processNum_et = findViewById(R.id.processNum_et);

        inquir_btn1 = findViewById(R.id.inquir_btn1);
        inquir_btn2 = findViewById(R.id.inquir_btn2);

        sendbtn = findViewById(R.id.sendbtn);
        sendbtn.setEnabled(false);
        sendbtn. setBackground(getDrawable(R.drawable.shape3));
        sendbtn.setTextColor(getResources().getColor(R.color.grey));

        finishbtn = findViewById(R.id.finishbtn);
        finishbtn.setEnabled(false);
        finishbtn. setBackground(getDrawable(R.drawable.shape3));
        finishbtn.setTextColor(getResources().getColor(R.color.grey));

        cusmName = findViewById(R.id.cusmName);
        cusm_No = findViewById(R.id.cusm_No);
        city = findViewById(R.id.city);
        address_name = findViewById(R.id.address_name);
        lacation = findViewById(R.id.lacation);

        inspDate = findViewById(R.id.inspDate);
        noteDate = findViewById(R.id.noteDate);
        processNoteDate = findViewById(R.id.processNoteDate);
        EngNoteDate = findViewById(R.id.EngNoteDate);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inspDate.getText().toString().equals("")){
                    Toast.makeText(InquirActivity.this, "ادخل تاريخ التفتيش", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(noteDate.getText().toString().equals("")){
                    Toast.makeText(InquirActivity.this, "ادخل تاريخ تزويد الجهة بالملاحظات", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(processNoteDate.getText().toString().equals("")){
                    Toast.makeText(InquirActivity.this, "ادخل تاريخ معالجة الملاحظات", Toast.LENGTH_SHORT).show();
                    return;
                }

                WorkFlowByAdminAsyncCall workFlowByAdminAsyncCall = new WorkFlowByAdminAsyncCall();
                workFlowByAdminAsyncCall.execute();

            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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


        CircleImageView bar = findViewById(R.id.copy1);

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanDialog dialog = new ScanDialog(InquirActivity.this);
                dialog.show();
            }
        });
        checkAndRequestPermissions();

        cusmNum_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cusmNum_et.setTextColor(Color.parseColor("#009900"));
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        processNum_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                processNum_et.setTextColor(Color.parseColor("#009900"));
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void SetDate(final EditText Date) {
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(InquirActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                try {
                    Date PickedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateFormatter.format(newDate.getTime()));
                    String da = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(PickedDate);
//                    Fyear = String.valueOf(year);
//                    Fmonth = String.valueOf(monthOfYear);
//                    Fday = String.valueOf(dayOfMonth);
                    Date.setText(da);
//                    insDate.setTextColor(Color.parseColor("#009900"));
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

    public void InquirProcessNum(View view) {
        if(processNum_et.getText().toString().equals(""))
        {
            Toast.makeText(InquirActivity.this, "يرجى إدخال رقم العملية", Toast.LENGTH_LONG).show();
        }else{
            CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
            customerCashAsyncCall.execute();
        }
    }

    public void InquirCusmNum(View view) {
        if(cusmNum_et.getText().toString().equals(""))
        {
            Toast.makeText(InquirActivity.this, "يرجى إدخال رقم الاشتراك", Toast.LENGTH_LONG).show();
        }else{
            CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
            customerCashAsyncCall.execute();
        }
    }

    public void settings(View view) {
        startActivity(new Intent(InquirActivity.this, Settings.class));
    }

    private class CustomerCashAsyncCall extends AsyncTask<String, Void, Void> {
        String temp;
        SoapObject soapObject;
        String strWhereOracle = "";

        public CustomerCashAsyncCall() {
            pd = new ProgressDialog(InquirActivity.this);
            temp = cusmNum_et.getText().toString();
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {

            if (!cusmNum_et.getText().toString().substring(6).equals("") )
                strWhereOracle += " and \"ca_cusm_num\"=" + cusmNum_et.getText().toString().substring(6);
//            if (txtSupSecriberName.Text.Trim() != "")
//            strWhereSql += " and CA_CUSM_NAME like ''%" + "" + "%''";
            if (!cusmNum_et.getText().toString().substring(0,3).equals("") && !cusmNum_et.getText().toString().substring(0,3).equals("-1"))
                strWhereOracle += " and city_id =" + cusmNum_et.getText().toString().substring(0,3);
            if (!processNum_et.getText().toString().equals(""))
                strWhereOracle += " and a.MAIN_PID =" + processNum_et.getText().toString();


            String data = "strWhereOracle:"+strWhereOracle+",strWhereSql: ,taskId:120,datatype:5";
                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    KSoapClass soap = new KSoapClass();

                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    soapObject = soap.GetRenewable_Canceled(base64Encoded);
                } catch (Exception e) {}

                return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{
                if(soapObject!=null && soapObject.getPropertyCount() > 0 && !soapObject.equals("anyType")){
                    GetReadableData1(soapObject);
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

    private class WorkFlowByAdminAsyncCall extends AsyncTask<String, Void, Void> {
        boolean flag;
        boolean updateRen;
        public WorkFlowByAdminAsyncCall() {
            pd = new ProgressDialog(InquirActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {
                KSoapClass soap = new KSoapClass();
                String data = "dtpInspDate:"+inspDate.getText().toString()+",dtpPROVIDE_NOTES_DATE:"+noteDate.getText().toString()+",dtpPROCESS_NOTES_DATE:"+processNoteDate.getText().toString()
                        +",uId:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID","")+",strUserName:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID","")
                        +",mPID:"+processNum_et.getText().toString()+",engNote:"+EngNoteDate.getText().toString()+",Id:";
                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    updateRen = soap.UpdateRenewable(base64Encoded);
                } catch (Exception e) {}

                if(updateRen){
                    flag = soap.WorkFlowAdvanceByAdmin(120, 1005249, Integer.parseInt(processNum_et.getText().toString()), "Root/ RenewableInsp",
                            "1", "","","","","","","","","",
                            "","","","","");
                }

            } catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{
                if(flag){
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
                    textView3.setText("خطأ في حفظ البيانات");

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


    private class InsertFollowUpAsyncCall extends AsyncTask<String, Void, Void> {
        SoapPrimitive flag;
        boolean updateRen;
        public InsertFollowUpAsyncCall() {
            pd = new ProgressDialog(InquirActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {
                KSoapClass soap = new KSoapClass();
                String data1 = "dtpInspDate:"+inspDate.getText().toString()+",dtpPROVIDE_NOTES_DATE:"+noteDate.getText().toString()+",dtpPROCESS_NOTES_DATE:"+processNoteDate.getText().toString()
                        +",uId:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID","")+",strUserName:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID","")
                        +",mPID:"+processNum_et.getText().toString()+",engNote:"+EngNoteDate.getText().toString()+",Id:";
                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data1);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    updateRen = soap.UpdateRenewable(base64Encoded);
                } catch (Exception e) {}

                if(updateRen){
                    String data2 = "dtpInspDate:"+inspDate.getText().toString()+",dtpPROVIDE_NOTES_DATE:"+noteDate.getText().toString()+",dtpPROCESS_NOTES_DATE:"+processNoteDate.getText().toString()
                            +",uId:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID","")+",strUserName:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID","")
                            +",mPID:"+processNum_et.getText().toString()+",engNote:"+EngNoteDate.getText().toString()+",Id:";
                    try {
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                        PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                        RSA.setKey(pubKey, privKey);

                        byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data2);
                        String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                        flag = soap.InsertFollowUp(base64Encoded);
                    } catch (Exception e) {}

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
            cusmNum_et.setEnabled(true);
            processNum_et.setEnabled(true);
        }
        else if(res.equals("anyType{}")){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
            cusmNum_et.setEnabled(true);
            processNum_et.setEnabled(true);
        } else{
            SoapObject so1, so2, so3;

            if (res != null && res.getPropertyCount() > 0){
                so1 = (SoapObject) res.getProperty(1);
                if (so1 != null && so1.getPropertyCount() > 0){
                    so2 = (SoapObject) so1.getProperty(0);
                    if (so2 != null && so2.getPropertyCount() > 0){
                        for(int i=0; i<so2.getPropertyCount(); i++){
                            so3 = (SoapObject) so2.getProperty(i);
                            try{
                                inquirInfo = new InquirInfo(
                                        so3.getPropertyAsString("CTYM_NAME"),
                                        so3.getPropertyAsString("CUSM_NAME"),
                                        so3.getPropertyAsString("MTR_NUM"),
                                        so3.getPropertyAsString("MTR_CITY"),
                                        so3.getPropertyAsString("SMART"),
                                        so3.getPropertyAsString("KIND"),
                                        so3.getPropertyAsString("MODEL"),
                                        so3.getPropertyAsString("ADDRESS"),
                                        so3.getPropertyAsString("MTR_M_NUM"));
                            }catch (Exception e){

                            }
                        }
                    }
                }
            }
            if(inquirInfo!=null){
                CusmNo = cusmNum_et.getText().toString();
                CustomermNum="";
                CustomermNum+= String.format(Locale.ENGLISH, "%03d", Integer.parseInt(inquirInfo.getMTR_CITY()));
                CustomermNum+= "0"+String.format(Locale.ENGLISH, "%06d", Integer.parseInt(inquirInfo.getMTR_NUM()));
                cusmName.setText(inquirInfo.getCUSM_NAME());
                cusm_No.setText(CustomermNum);
                city.setText(inquirInfo.getCTYM_NAME());
                address_name.setText(inquirInfo.getCUSM_ADDRES());
                lacation.setText(inquirInfo.getMTR_M_KIND() +"-"+ inquirInfo.getMTR_M_MODEL());
            }
            else{
                Toast.makeText(this, "يوجد خطأ في رقم الاشتراك او رقم المعاملة", Toast.LENGTH_LONG).show();
                cusmNum_et.setEnabled(true);
                cusmNum_et.setText("");
                processNum_et.setEnabled(true);
                processNum_et.setText("");

            }
        }
    }catch (Exception e){
        Toast.makeText(this, "لقد حدث خطأ", Toast.LENGTH_SHORT).show();

    }


    }



}
