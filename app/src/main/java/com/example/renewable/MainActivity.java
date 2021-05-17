package com.example.renewable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Button inspectBtn, connectBtn;
    SoapObject response2;
    String respId="", EmpName = "";

    private class GetVersion extends AsyncTask<String, Void, Void> {

        public GetVersion() {}

        @Override
        protected Void doInBackground(String... params) {

            KSoapClass con = new KSoapClass();
            response2 = con.GetAppVersion();
            return null; }

        @Override
        protected void onPostExecute(Void result) {

            GetReadableData2(response2); }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    private void GetReadableData2(SoapObject Sobj){

        SoapObject so1, so2, so3;

        if (Sobj != null && Sobj.getPropertyCount() > 0) {
            so1 = (SoapObject) Sobj.getProperty(1);
            if (so1 != null && so1.getPropertyCount() > 0) {
                so2 = (SoapObject) so1.getProperty(0);
                if (so2 != null && so2.getPropertyCount() > 0) {
                    for (int i = 0; i < so2.getPropertyCount(); i++) {
                        so3 = (SoapObject) so2.getProperty(i);

                        try{
                            if(!so3.getPropertyAsString("APP_ANDROID_VER")
                                    .equals(MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName) &&
                                    !so3.getPropertyAsString("UPDATE_REQUIRED").equals("0")){
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                                builder.setView(inflater.inflate(R.layout.dialog_info15, null));
                                final AlertDialog dialog = builder.create();
                                ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                dialog.show();
                                dialog.getWindow().setAttributes(lp);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);

                                Button btn = dialog.findViewById(R.id.btn2);
                                CircleImageView im = dialog.findViewById(R.id.im);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));

                                    }
                                });
                                im.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MainActivity.this.finishAffinity();
                                        System.exit(0);
                                    }
                                });
                            }
                        }
                        catch (Exception ex){}
                    }
                }
            }
        }
    }


    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
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
        setContentView(R.layout.activity_main);

        connectBtn = findViewById(R.id.connectBtn);
        inspectBtn = findViewById(R.id.inspectBtn);

        GetVersion api = new GetVersion();
        api.execute();

        try {
            EmployeesPositionAsyncCall api1 = new EmployeesPositionAsyncCall();
            api1.execute();
        }catch (Exception e){}


        inspectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InspectionIncquireActivity.class));
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConnectionIncquireActivity.class));
            }
        });
    }

    public void settings(View view) {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }

    private class EmployeesPositionAsyncCall extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog;

        SoapObject empPositionres;
        SoapObject empPositionres2;
        SoapObject resp;
        boolean flag=false;
        boolean respFlag=false;
        boolean setResp=false;
        String TID;
        public EmployeesPositionAsyncCall() {

            dialog = new ProgressDialog(MainActivity.this);
        }
        @Override
        protected Void doInBackground(String... params) {
            //Invoke webservice
            try{
                KSoapClass service = new KSoapClass();
                try {
                    String data = "sWhereOracle: ,sSQLWHERE:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("EMP_NO", "")+",RESP: ,iDatatype:2";
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(android.util.Base64.decode(service.privateKey, android.util.Base64.DEFAULT));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(android.util.Base64.decode(service.publicKey, android.util.Base64.DEFAULT));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
                    RSA.setKey(pubKey, privKey);
                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                    String base64Encoded = android.util.Base64.encodeToString(encodeData, android.util.Base64.DEFAULT);
                    resp=service.GET_APPRAISAL_DATE_SQL(base64Encoded);
                }
                catch (Exception ex){
                    return null;
                }

            }catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(resp!=null && resp.getPropertyCount()>0){
                GetReadableResp(resp);
            }
            SharedPreferences.Editor editor =
                    getSharedPreferences("Info", Context.MODE_PRIVATE).edit();
            editor.putString("respId", respId);
            editor.putString("NAME", EmpName);
            editor.apply();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("الرجاء الانتظار...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    private void GetReadableResp(SoapObject resp) {
        try{
            respId="";
            SoapObject so1, so2, so3;
            if (resp != null && resp.getPropertyCount() > 0){
                so1 = (SoapObject) resp.getProperty(1);
                if (so1 != null && so1.getPropertyCount() > 0){
                    so2 = (SoapObject) so1.getProperty(0);
                    if (so2 != null && so2.getPropertyCount() > 0){
                        so3 = (SoapObject) so2.getProperty(0);
                        try{
                            respId=so3.getPropertyAsString("ID");
                        }catch (Exception e){}
                        try{
                            EmpName=so3.getPropertyAsString("NAME");
                        }catch (Exception e){}
                    }
                }
            }else {
//                Toast.makeText(RequestVacationActivity.this, "رئيس القسم المعني ليس له رقم على نظام سير المعاملات يرجى مراجعة قسم الدعم الفني - دائرة مركز الحاسوب", Toast.LENGTH_SHORT).show();
//                return;
            }

        }catch(Exception e){}
    }
}