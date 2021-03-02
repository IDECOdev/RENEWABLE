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
    EditText cusmNum_et, processNum_et, cusmName, cusm_No, city, address_name, lacation;
    SharedPreferences shared;
    ProgressDialog pd;
    String CusmNo = "";
    InquirInfo inquirInfo;
    Button inquir_btn2, inquir_btn1, sendbtn, finishbtn, fillbtn;
    String CustomermNum;
    EditText EngNoteDate;
    TextView inspDate, noteDate, processNoteDate;
    SoapObject response2;
    TextView instext;
    RelativeLayout insLay;

    private class GetVersion extends AsyncTask<String, Void, Boolean> {

        public GetVersion() {}

        @Override
        protected Boolean doInBackground(String... params) {

            boolean flag = GetData2();
            return flag; }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                try {
                    GetReadableData2(response2);
                } catch (Exception ex) {

                }
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }
    private boolean GetData2(){
        try {

            String NameSpace = "http://tempuri.org/";
            String MethodName = "GetAppVersion";
            String InterFace = "IBillingWcfsrv/";
            String SoapAction = NameSpace+InterFace+MethodName;
            String Url = new KSoapClass().Url;

            SoapObject request = new SoapObject(NameSpace, MethodName);

            request.addProperty("DataType", "2");
            request.addProperty("sWhere", " and ID = '9'");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);
            androidHttpTransport.debug = true;

            androidHttpTransport.call(SoapAction, envelope);
            response2=(SoapObject) envelope.getResponse();

            return true; }

        catch (Exception e) {
            return false; }

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
                            if(!so3.getPropertyAsString("APP_ANDROID_VER").equals(InquirActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName)){
                                final AlertDialog.Builder builder = new AlertDialog.Builder(InquirActivity.this);
                                LayoutInflater inflater = InquirActivity.this.getLayoutInflater();
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

                                CircleImageView im = dialog.findViewById(R.id.im);
                                im.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finishAffinity();
                                        System.exit(0);
                                    }
                                });
                                Button btn = dialog.findViewById(R.id.btn2);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        final String appPackageName = getPackageName();
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ideco.com.jo/IDECO_APPS/IDECO_TASKS.apk")));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ideco.com.jo/IDECO_APPS/IDECO_TASKS.apk")));
                                        }

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
    startActivity(new Intent(InquirActivity.this, MainActivity.class));
    finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquir);

        checkAndRequestPermissions();

        cusmNum_et = findViewById(R.id.cusmNum_et);
        processNum_et = findViewById(R.id.processNum_et);

        insLay = findViewById(R.id.inspdata_lay);
        instext = findViewById(R.id.instext);

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
                startActivity(new Intent(InquirActivity.this, FillDataActivity.class));
            }
        });

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
                if(noteDate.getText().toString().equals("")){
                    Toast.makeText(InquirActivity.this, "ادخل تاريخ تزويد الجهة بالملاحظات", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(processNoteDate.getText().toString().equals("")){
                    Toast.makeText(InquirActivity.this, "ادخل تاريخ معالجة الملاحظات", Toast.LENGTH_SHORT).show();
                    return;
                }
                InsertFollowUpAsyncCall insertFollowUpAsyncCall = new InsertFollowUpAsyncCall();
                insertFollowUpAsyncCall.execute();
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
            if(!cusmNum_et.getText().toString().equals("")){
                int cn = Integer.parseInt(cusmNum_et.getText().toString().substring(4));
                int cc =  Integer.parseInt(cusmNum_et.getText().toString().substring(0,4));
//            and a."ca_cusm_num"=2536 and a.city_id =188
                if (!String.valueOf(cn).equals("") )
                    strWhereOracle += " and a.\"ca_cusm_num\"=" + cn;
//            if (txtSupSecriberName.Text.Trim() != "")
//            strWhereSql += " and CA_CUSM_NAME like ''%" + "" + "%''";
                if (!String.valueOf(cc).equals("") && !String.valueOf(cc).equals("-1"))
                    strWhereOracle += " and a.city_id =" + cc;
            }
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
                } catch (Exception e) {
                }

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
        boolean flag = false;
        boolean updateRen1 = false,updateRen2 = false;
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

                    String data2 = "mPID:" + inquirInfo.getMAIN_PID() + ",:0,: ,:0,:0,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,dtpInspDate:" + inspDate.getText().toString()
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
                        flag = soap.WorkFlowAdvanceByAdmin(getEncodedString3(soap,120, 1005249, Integer.parseInt(inquirInfo.getMAIN_PID()), "Root/RenewableInsp",
                                "1", " ", " ", " ", " ", " ", " ", " ", " ", " ",
                                " ",  " ", " ", " ", " "));
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

                    String data2 = "mPID:" + inquirInfo.getMAIN_PID() + ",:0,: ,:0,:0,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,dtpInspDate:" + inspDate.getText().toString()
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
                                + ",mPID:" + inquirInfo.getMAIN_PID() + ",txtFollowUps:تم الكشف على نظام الطاقة المتجددة" + dtf.format(now) + " ووجدت هناك ملاحظات ولم يتم استكمال اجراءات التشغيل";

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
                                        so3.getPropertyAsString("ID"),
                                        so3.getPropertyAsString("MAIN_PID"),
                                        so3.getPropertyAsString("CA_CUSM_NAME"),
                                        so3.getPropertyAsString("CITY_ID"),
                                        so3.getPropertyAsString("CTYM_NAME"),
                                        so3.getPropertyAsString("ca_cusm_num"));
                            }catch (Exception e){

                            }
                        }
                    }
                }
            }
            if(inquirInfo!=null){
                CusmNo = cusmNum_et.getText().toString();
                CustomermNum="";
                CustomermNum+= String.format(Locale.ENGLISH, "%03d", Integer.parseInt(inquirInfo.getCITY_ID()));
                CustomermNum+= "0"+String.format(Locale.ENGLISH, "%06d", Integer.parseInt(inquirInfo.getCa_cusm_num()));
                cusmName.setText(inquirInfo.getCA_CUSM_NAME());
                cusm_No.setText(CustomermNum);
                city.setText(inquirInfo.getCITY_ID());
                address_name.setText(inquirInfo.getCTYM_NAME());
                lacation.setText("لم يتم جلبه مع البيانات");

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
