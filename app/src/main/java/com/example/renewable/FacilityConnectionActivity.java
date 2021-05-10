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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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


public class FacilityConnectionActivity extends AppCompatActivity {
    EditText cusmName, cusm_No, city, address_name, lacation;
    SharedPreferences shared;
    ProgressDialog pd;
    String CusmNo = "";
    InquirInfo inquirInfo;
    Button inquir_btn2, inquir_btn1, sendbtn;
    String CustomermNum;
    EditText EngNoteDate;
    TextView  connectionDate;
    EditText issuedRead, continuedRead;
    TextView instext;
    RelativeLayout insLay;
    ImageView issuedReadimage, continuedReadimage;
    private static final int ImageBack = 1;
    String ImageUri="";
    Uri ImageData;
    private String pictureImagePath;
    private Bitmap imageBitmap, imageBitmap2;
    String imageFileName1, imageFileName2;
    ConPresInfo presInfo;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FacilityConnectionActivity.this, ConnectionIncquireActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilityconnection);

        checkAndRequestPermissions();

        Intent i = getIntent();
        inquirInfo = (InquirInfo) i.getSerializableExtra("inboxDetail");

        continuedReadimage = findViewById(R.id.continuedReadimage);
        issuedReadimage = findViewById(R.id.issuedReadimage);

        insLay = findViewById(R.id.inspdata_lay);
        instext = findViewById(R.id.instext);

        inquir_btn1 = findViewById(R.id.inquir_btn1);
        inquir_btn2 = findViewById(R.id.inquir_btn2);

        sendbtn = findViewById(R.id.sendbtn);
        sendbtn.setEnabled(true);
        sendbtn. setBackground(getDrawable(R.drawable.shape3));
//        sendbtn.setTextColor(getResources().getColor(R.color.grey));

        cusmName = findViewById(R.id.cusmName);
        cusm_No = findViewById(R.id.cusm_No);
        city = findViewById(R.id.city);
        address_name = findViewById(R.id.address_name);
        lacation = findViewById(R.id.lacation);

        connectionDate = findViewById(R.id.connectionDate);
        issuedRead = findViewById(R.id.issuedRead);
        continuedRead = findViewById(R.id.continuedRead);
        EngNoteDate = findViewById(R.id.EngNoteDate);

        PresntDataAsyncCall presntDataAsyncCall=new PresntDataAsyncCall();
        presntDataAsyncCall.execute();

        continuedReadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeImage();
            }
        });

        issuedReadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,ImageBack);
                TakeImage1();
            }
        });
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDate.getText().toString().equals("")){
//                    connectionDate.setText("0");
                    Toast.makeText(FacilityConnectionActivity.this, "ادخل تاريخ ربط المنشأة", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(issuedRead.getText().toString().equals("")){
                    issuedRead.setText("0");
                }
                if(continuedRead.getText().toString().equals("")){
                    continuedRead.setText("0");
                }

                WorkFlowByAdminAsyncCall workFlowByAdminAsyncCall = new WorkFlowByAdminAsyncCall();
                workFlowByAdminAsyncCall.execute();

//                SaveImageAsyncCall saveImageAsyncCall = new SaveImageAsyncCall();
//                saveImageAsyncCall.execute();

            }
        });

        connectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDate(connectionDate);
            }
        });

        checkAndRequestPermissions();

    }

    private class PresntDataAsyncCall extends AsyncTask<String, Void, Void> {

        SoapObject present;

        public PresntDataAsyncCall() {
            pd = new ProgressDialog(FacilityConnectionActivity.this);
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... params) {
            try{
                String data1 = "iMPID:"+inquirInfo.getMAIN_PID()+",DataType:0";
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

    private void GetPresentData(SoapObject soapObject1) {
        SoapObject so1, so2, so3;
        String ID="",MAIN_PID="",CA_CUSM_NAME="",CITY_ID="",CTYM_NAME="",ca_cusm_num="", SYSTEM_CONN_DATEX="", REN_M_LREAD_OP="", REN_M_PREAD_OP="", NOTES="";

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
                                SYSTEM_CONN_DATEX = so3.getPropertyAsString("SYSTEM_CONN_DATEX");
                            }catch (Exception e){}
                            try{
                                REN_M_PREAD_OP = so3.getPropertyAsString("REN_M_PREAD_OP");
                            }catch (Exception e){}
                            try{
                                REN_M_LREAD_OP = so3.getPropertyAsString("REN_M_LREAD_OP");
                            }catch (Exception e){}
                            try{
                                NOTES = so3.getPropertyAsString("NOTES");
                            }catch (Exception e){}

                            presInfo = new ConPresInfo(ID,MAIN_PID,CA_CUSM_NAME,CITY_ID,CTYM_NAME,ca_cusm_num, SYSTEM_CONN_DATEX, REN_M_LREAD_OP, REN_M_PREAD_OP, NOTES);

                        }catch (Exception e){

                        }
                    }
                }
            }
        }

        if(presInfo!=null){
            CusmNo = presInfo.getCa_cusm_num();
            CustomermNum="";
            CustomermNum+= String.format(Locale.ENGLISH, "%03d", Integer.parseInt(presInfo.getCITY_ID()));
            CustomermNum+= "0"+String.format(Locale.ENGLISH, "%06d", Integer.parseInt(presInfo.getCa_cusm_num()));
            cusmName.setText(presInfo.getCA_CUSM_NAME());
            cusm_No.setText(CustomermNum);
            city.setText(presInfo.getCITY_ID());
            address_name.setText(presInfo.getCTYM_NAME());
            if(!inquirInfo.getCA_X_COORDINATE().equals("") && !inquirInfo.getCA_Y_COORDINATE().equals("")){
                lacation.setText(inquirInfo.getCA_X_COORDINATE() + "," + inquirInfo.getCA_Y_COORDINATE());
            }else{
                lacation.setText("لم يتم تحديد احداثيات الموقع");
            }

            connectionDate.setText(presInfo.getSYSTEM_CONN_DATEX());
            issuedRead.setText(presInfo.getREN_M_PREAD_OP());
            continuedRead.setText(presInfo.getREN_M_LREAD_OP());
            EngNoteDate.setText(presInfo.getNOTES());
        }

    }

    private void TakeImage() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        pictureImagePath = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName1 = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName1;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 1991);
    }

    private void TakeImage1() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        pictureImagePath = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName2 = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName2;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 1989);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1991) {
                try {
                    File imgFile = new File(pictureImagePath);
                    imageBitmap = decodeFile(imgFile);
                    if (imageBitmap != null) {
                        Bitmap temp = Bitmap.createScaledBitmap(imageBitmap, 600, 800, false);
//                        ImageView state = findViewById(R.id.img);
                        continuedReadimage.setImageBitmap(temp);
                    }
                } catch (Exception ex) {
                }
            }
            if (requestCode == 1989) {
                try {
                    File imgFile = new File(pictureImagePath);
                    imageBitmap2 = decodeFile(imgFile);
                    if (imageBitmap2 != null) {
                        Bitmap temp = Bitmap.createScaledBitmap(imageBitmap2, 600, 800, false);
//                        ImageView state = findViewById(R.id.img);
                        issuedReadimage.setImageBitmap(temp);
                    }
                } catch (Exception ex) {
                }
            }
        }
            if(requestCode == ImageBack){
            if(resultCode == RESULT_OK){
                ImageData = data.getData();
                ImageUri = String.valueOf(ImageData);
                Picasso.get().load(ImageData).into(issuedReadimage);
            }
        }

    }

    private Bitmap decodeFile(File f){
        try {
//            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private void SetDate(final TextView Date) {
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(FacilityConnectionActivity.this, new DatePickerDialog.OnDateSetListener() {

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


    public void settings(View view) {
        startActivity(new Intent(FacilityConnectionActivity.this, Settings.class));
    }


    private class WorkFlowByAdminAsyncCall extends AsyncTask<String, Void, Void> {
        boolean flag = false;
        boolean updateRen1 = false,updateRen2 = false;

        public WorkFlowByAdminAsyncCall() {
            pd = new ProgressDialog(FacilityConnectionActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {
                KSoapClass soap = new KSoapClass();

                String data1 = "Id:" +inquirInfo.getID()+",: ,engNote:" + EngNoteDate.getText().toString() +",:0,txtREN_M_PREAD_OP:"+issuedRead.getText().toString()+",txtREN_M_LREAD_OP:"+continuedRead.getText().toString()+",:0,:0,:0,:0,: ,: ,:0,uId:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", "")
                        + ",strUserName:" + getSharedPreferences("Info", Context.MODE_PRIVATE).getString("UserName", "")
                        + ",:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,:0,: ,: ,: ,: ,: ,dtpSYSTEM_CONN_DATE:"+connectionDate.getText().toString()+",:0,:2";

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
                     String data2 = "mPID:" + inquirInfo.getMAIN_PID() + ",:0,: ,:0,:0,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,:0,:0,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,: ,dtpSYSTEM_CONN_DATE:"+connectionDate.getText().toString()
                             + ",: ,: ,: ,: ,: ,:7,: ";

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
                     } catch (Exception e) {
                     }
                 }

                if(updateRen2){
                    flag = soap.WorkFlowAdvanceByAdmin(getEncodedString3(soap,84, 1005249, Integer.parseInt(inquirInfo.getMAIN_PID()), "Root/ RenewableData",
                            "1", "","","","","","","","","",
                            "","","","",""));
                }



            } catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{
                if(flag) {
                    pd.dismiss();
                    SaveImageAsyncCall saveImageAsyncCall = new SaveImageAsyncCall();
                    saveImageAsyncCall.execute();
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

    private class SaveImageAsyncCall extends AsyncTask<String, Void, Void> {
        boolean flag1 = false;
        SoapPrimitive closeApp;
        public SaveImageAsyncCall() {
            pd = new ProgressDialog(FacilityConnectionActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {

                KSoapClass soap = new KSoapClass();
                flag1 = soap.Insert_Renewable_Images(imageFileName1, imageFileName2, imageBitmap, imageBitmap2);
                if(String.valueOf(flag1).equals("true")){
                    closeApp = soap.INSERT_RENEWABLE_APP_CLOSE(":"+inquirInfo.getMAIN_PID()+",:84,:"+getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", ""));
                }
                } catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{

                if(flag1){
                    if(Integer.parseInt(String.valueOf(closeApp))<0){
                        pd.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(FacilityConnectionActivity.this);
                        LayoutInflater inflater = FacilityConnectionActivity.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                        final AlertDialog dialog1 = builder.create();
                        ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
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

                                cusmName.setText("");
                                cusm_No.setText("");
                                city.setText("");
                                address_name.setText("");
                                lacation.setText("");

                                sendbtn.setEnabled(false);
                                sendbtn. setBackground(getDrawable(R.drawable.shape3));
                                sendbtn.setTextColor(getResources().getColor(R.color.grey));

                            }
                        });
                    }else{
                        pd.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(FacilityConnectionActivity.this);
                        LayoutInflater inflater = FacilityConnectionActivity.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                        final AlertDialog dialog1 = builder.create();
                        ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
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

                            }
                        });
                    }

                }else {
                    pd.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FacilityConnectionActivity.this);
                    LayoutInflater inflater = FacilityConnectionActivity.this.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                    final AlertDialog dialog1 = builder.create();
                    ((FrameLayout) dialog1.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(Color.TRANSPARENT));
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

}
