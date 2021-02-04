package com.example.renewable;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class InquirActivity extends AppCompatActivity {
    EditText cusmNum_et, processNum_et, cusmName, cusm_No, city, address_name, model_name, meter_num, smartMeter;
    SharedPreferences shared;
    ProgressDialog pd;
    String CusmNo = "";
    InquirInfo inquirInfo;
    Button inquir_btn2, inquir_btn1, sendbtn, finishbtn;
    String CustomermNum;

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
        model_name = findViewById(R.id.model_name);
        meter_num = findViewById(R.id.meter_num);
        smartMeter = findViewById(R.id.smartMeter);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        if(!getIntent().getStringExtra("CusmNum").equals("")){
//            cusmNum_et.setText(getIntent().getStringExtra("CusmNum"));
//            CustomerCashAsyncCall api = new CustomerCashAsyncCall();
//            api.execute();
//        }

        CircleImageView bar = findViewById(R.id.copy1);

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(InquirActivity.this, BarCodeActivity.class));
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
//                strWhereOracle += " and CA_CUSM_NAME like ''%" + txtSupSecriberName.Text.Trim() + "%''";
            if (!cusmNum_et.getText().toString().substring(0,3).equals("") && !cusmNum_et.getText().toString().substring(0,3).equals("-1"))
                strWhereOracle += " and city_id =" + cusmNum_et.getText().toString().substring(0,3);
            if (!processNum_et.getText().toString().equals(""))
                strWhereOracle += " and a.MAIN_PID =" + processNum_et.getText().toString();


            String data = "strWhereOracle:"+strWhereOracle+",strWhereSql:,datatype:11,modelId:1005249,taskId:120";
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
                    soapObject = soap.GET_INSPECTION_PROCESS(base64Encoded);
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

    private void GetReadableData1(SoapObject res){
    try{
        inquirInfo=null;
        if(res == null){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
            cusmNum_et.setEnabled(true);
        }
        else if(res.equals("anyType{}")){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
            cusmNum_et.setEnabled(true);
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
                model_name.setText(inquirInfo.getMTR_M_KIND() +"-"+ inquirInfo.getMTR_M_MODEL());
                meter_num.setText(inquirInfo.getMTR_M_NUM());
                if(inquirInfo.getSMART().equals("1"))
                    smartMeter.setText("نعم");
                else if(inquirInfo.getSMART().equals("0"))
                    smartMeter.setText("لا");


            }
            else{
                Toast.makeText(this, "يوجد خطأ في رقم العداد", Toast.LENGTH_LONG).show();
                cusmNum_et.setEnabled(true);
                cusmNum_et.setText("");

            }
        }
    }catch (Exception e){
        Toast.makeText(this, "لقد حدث خطأ", Toast.LENGTH_SHORT).show();

    }


    }



}
