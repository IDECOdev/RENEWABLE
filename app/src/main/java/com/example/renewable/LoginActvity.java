package com.example.renewable;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;


public class LoginActvity extends AppCompatActivity {

    EditText username_edt, pass_edt;
    private KeyStore keyStore;
    private static final String KEY_NAME = "EDMTDev";
    private Cipher cipher;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        init();
    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(username_edt.getText().toString().equals("")||pass_edt.getText().toString().equals(""))
                Toast.makeText(LoginActvity.this, "يرجى عدم ترك حقول فارغة", Toast.LENGTH_SHORT).show();
            else{
                LoginInfoAsyncCall api = new LoginInfoAsyncCall(
                        username_edt.getText().toString(),
                        pass_edt.getText().toString());
                api.execute(); }
        }
    });
    }

    private void init() {

        username_edt = findViewById(R.id.id);
        pass_edt = findViewById(R.id.pass);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void fingerprint(View view) {
        String netAddress = null;
        try
        {
            netAddress = new NetTask().execute("www.google.com").get();
        }
        catch (Exception e1)
        {
            netAddress = null;
            //e1.printStackTrace();
        }
        if(netAddress == null){
            Toast.makeText(getBaseContext(), "الجهاز غير متصل بالانترنت", Toast.LENGTH_LONG).show();
            return;
        }
        try{
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);

            if(Integer.parseInt(getSharedPreferences("Info", MODE_PRIVATE).getString("ID", "").toString().trim()) > 0 && Integer.parseInt(settings.getString("Finger", "").toString().trim()) == 1){

                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActvity.this);
                LayoutInflater inflater = LoginActvity.this.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_fingerinfo, null));
                final AlertDialog dialog = builder.create();
                ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
                final CircleImageView exit=dialog.findViewById(R.id.ex);
                final ImageView finger_image=dialog.findViewById(R.id.finger_image);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                if (ActivityCompat.checkSelfPermission(LoginActvity.this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (!fingerprintManager.isHardwareDetected())
                    Toast.makeText(LoginActvity.this, "خاصية البصمة غير مفعلة على الهاتف", Toast.LENGTH_SHORT).show();

                else {
                    if (!fingerprintManager.hasEnrolledFingerprints())
                        Toast.makeText(LoginActvity.this, "يجب تفعيل الدخول باستخدام البصمة على الهاتف", Toast.LENGTH_SHORT).show();
                    else {
                        if (!keyguardManager.isKeyguardSecure())
                            Toast.makeText(LoginActvity.this, "يجب تفعيل الدخول باستخدام البصمة على الهاتف", Toast.LENGTH_SHORT).show();
                        else
                            genKey();

                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerprintHandler helper = new FingerprintHandler(LoginActvity.this);
                            helper.startAuthentication(fingerprintManager, cryptoObject);
                        }
                    }
                }

            }
            else {
                Toast.makeText(getBaseContext(), "يجب التاكد من الاعدادات وتفعيل الدخول بستخدام البصمة", Toast.LENGTH_LONG).show();

            }
        }catch (Exception ex)
        {
            Toast.makeText(getBaseContext(), "يجب التاكد من الاعدادات وتفعيل الدخول بستخدام البصمة", Toast.LENGTH_LONG).show();

        }
    }

    private boolean cipherInit() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey)keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        } catch (IOException e1) {

            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {

            e1.printStackTrace();
            return false;
        } catch (CertificateException e1) {

            e1.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e1) {

            e1.printStackTrace();
            return false;
        } catch (KeyStoreException e1) {

            e1.printStackTrace();
            return false;
        } catch (InvalidKeyException e1) {

            e1.printStackTrace();
            return false;
        }

    }

    private void genKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
                );
            }
            keyGenerator.generateKey();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
        }

    }

    public class LoginInfoAsyncCall extends AsyncTask<String, Void, Void> {

        SoapObject soapObject;
//        ProgressDialog progressDialog;
        String username;
        String password;

        public LoginInfoAsyncCall(String username, String password) {

            this.username = username;
            this.password = password;
//            progressDialog = new ProgressDialog();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {

            String data = "DataType:1,Where: and A.USER_NAME='"+username+
                    "' and A.USR_DFN_PASSWORD='"+password+"' and b.app_id = 8";

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

                soapObject=soap.GetAppLoginUser(base64Encoded); }

            catch (Exception e){
                e.getMessage();
                Toast.makeText(LoginActvity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            GetReadable(soapObject);
//            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        private void GetReadable(SoapObject sp) {
            SoapObject so1, so2, so3;
            String ID = "";
            String GM_FLAG = "";

            if (sp != null && sp.getPropertyCount() > 0){
                so1 = (SoapObject) sp.getProperty(1);
                if (so1 != null && so1.getPropertyCount() > 0) {
                    so2 = (SoapObject) so1.getProperty(0);
                    if (so2 != null && so2.getPropertyCount() > 0) {
                        so3 = (SoapObject) so2.getProperty(0);
                        try{
                            ID = so3.getPropertyAsString("ID");
                            GM_FLAG = so3.getPropertyAsString("GM_FLAG"); }
                        catch (Exception ex){ GM_FLAG = "0"; }
                    }
                }
            }

            if(ID != null){
                if(!ID.equals("")){

                    SharedPreferences.Editor editor =
                            getSharedPreferences("Info", Context.MODE_PRIVATE).edit();
                    editor.putString("ID", ID);
                    editor.putString("GM_FLAG", GM_FLAG);
                    editor.putString("UserName", username);
                    editor.apply();

//                    startActivity(new Intent(LoginActvity.this, InquirActivity.class).putExtra("CusmNum",""));
//                    startActivity(new Intent(LoginActvity.this, FacilityConnectionActivity.class).putExtra("CusmNum",""));
                    startActivity(new Intent(LoginActvity.this, MainActivity.class).putExtra("CusmNum",""));
                    finish();
                }
                else
                    new AlertDialog.Builder(LoginActvity.this)
                            .setMessage("كلمة سر خاطئة او حساب غير موجود")
                            .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            }).show();}
            else
                new AlertDialog.Builder(LoginActvity.this)
                        .setMessage("كلمة سر خاطئة او حساب غير موجود")
                        .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();


        }
}
}