package com.example.renewable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.ksoap2.serialization.SoapPrimitive;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Settings extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    TextView changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(Settings.this);
                LayoutInflater inflater2 = Settings.this.getLayoutInflater();
                builder2.setView(inflater2.inflate(R.layout.dialog_change, null));
                final AlertDialog dialog2 = builder2.create();
                ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                lp2.copyFrom(dialog2.getWindow().getAttributes());
                lp2.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog2.getWindow().setAttributes(lp2);
                dialog2.show();

                final Button btn = dialog2.findViewById(R.id.changePass2);
                final EditText old = dialog2.findViewById(R.id.old);
                final EditText new1 = dialog2.findViewById(R.id.new1);
                final EditText new2 = dialog2.findViewById(R.id.new2);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String pass = Settings.this.getSharedPreferences("Info", MODE_PRIVATE).getString("password", "");
                        if(pass.equals(old.getText().toString())){
                            if(new1.getText().toString().equals(new2.getText().toString())){
                                if(new1.getText().toString().length() > 3){
                                    String NEW_PASS = new1.getText().toString();
                                    UpdateUserPassAsyncCall api= new UpdateUserPassAsyncCall(Settings.this, NEW_PASS);
                                    api.execute(); }
                                else
                                    Toast.makeText(Settings.this, "يرجى إستخدام كلمة سر أطول من 3 حروف", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(Settings.this, "كلمة السر الجديدة غير متطابقة", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(Settings.this, "كلمة السر القديمة خاطئة", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences settingsE = this.getSharedPreferences("UserInfo", 0);

        CheckBox repeatChkBx = (CheckBox) findViewById( R.id.checkBox );

        try{

            if(Integer.parseInt(getSharedPreferences("Info", MODE_PRIVATE).getString("ID", "").toString().trim()) > 0 && Integer.parseInt(settingsE.getString("Finger", "").toString().trim()) == 1){

                repeatChkBx.setChecked(true);
            }
            else {
                Toast.makeText(getBaseContext(), "يجب التاكد من الاعدادات وتفعيل الدخول بستخدام البصمة", Toast.LENGTH_LONG).show();

            }
        }catch (Exception ex)
        {


        }

        repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    Intent intentg = new Intent(getBaseContext(), FingerPrintAuthintication.class);
                    startActivity(intentg);
                    finish();
                }
                if ( !isChecked ) {
                    SharedPreferences settings = getBaseContext().getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Finger", "");
                    editor.commit();
                }
            }
        });
    }

    public void btnLogOffApp(View view) {

        SharedPreferences settings = getBaseContext().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("EMP_NO", "");
        editor.putString("Finger", "");

        editor.commit();

        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        ActivityCompat.finishAffinity(Settings.this);
        System.exit(1);
    }

    public class UpdateUserPassAsyncCall extends AsyncTask<String, Void, Void> {

        SoapPrimitive sp;
        String NEW_PASS;
        Activity activity;

        public UpdateUserPassAsyncCall(Activity activity, String NEW_PASS) {

            this.activity = activity;
            this.NEW_PASS = NEW_PASS;

        }

        @Override
        protected Void doInBackground(String... params) {
            String data = "pw:"+NEW_PASS+",id:"+activity.getSharedPreferences("Info", Context.MODE_PRIVATE).getString("ID", "")+",datatype:2";

            try{
                KeyFactory kf = KeyFactory.getInstance("RSA");
                KSoapClass soap = new KSoapClass();

                PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(android.util.Base64.decode(soap.privateKey, android.util.Base64.DEFAULT));
                PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(android.util.Base64.decode(soap.publicKey, android.util.Base64.DEFAULT));
                RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                RSA.setKey(pubKey, privKey);

                byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                String base64Encoded = android.util.Base64.encodeToString(encodeData, android.util.Base64.DEFAULT);

                sp=soap.Update_User_Pass(base64Encoded);
            }catch(Exception e){e.getMessage();}

            return null;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onPostExecute(Void aVoid) {
            if(sp!=null){
                if(sp.toString().equals("true")){

                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_message, null));
                    final AlertDialog dialog = builder.create();
                    ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                    final Button ok=dialog.findViewById(R.id.btn_ok);
                    final TextView textMessage=dialog.findViewById(R.id.message);
                    textMessage.setText("يرجى إعادة تسجيل الدخول في البرنامج");

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            activity.startActivity(new Intent(activity, LoginActvity.class));

                        }
                    });

                }
            }
            else{
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_message, null));
                final AlertDialog dialog = builder.create();
                ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.setCancelable(false);
                dialog.show();
                dialog.getWindow().setAttributes(lp);
                final Button ok=dialog.findViewById(R.id.btn_ok);
                final TextView textMessage=dialog.findViewById(R.id.message);
                textMessage.setText("حدث خطأ");

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

}
