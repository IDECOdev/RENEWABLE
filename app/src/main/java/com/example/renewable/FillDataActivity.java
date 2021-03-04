package com.example.renewable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.ksoap2.serialization.SoapPrimitive;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

public class FillDataActivity extends AppCompatActivity {
    String answer1 = "0", answer2 = "0", answer3 = "0", answer4 = "0", answer5 = "0",
            answer6 = "0", answer7 = "0", answer8 = "0", answer9 = "0";
    ArrayList<String> dataByMember;
    String MPID = "";
    int counter = 0;
    EditText downvalue_discon, actualvalue_discon, catchedvalue_discon, downvalue_re, actualvalue_re, catchedvalue_re;
    Button sendbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);

        MPID = getIntent().getStringExtra("MPID");

        downvalue_discon = findViewById(R.id.downvalue_discon);
        actualvalue_discon = findViewById(R.id.actualvalue_discon);
        catchedvalue_discon = findViewById(R.id.catchedvalue_discon);
        downvalue_re = findViewById(R.id.downvalue_re);
        actualvalue_re = findViewById(R.id.actualvalue_re);
        catchedvalue_re = findViewById(R.id.catchedvalue_re);

        dataByMember = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtn);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SaveFilledDataAsyncCall saveFilledDataAsyncCall = new SaveFilledDataAsyncCall();
               saveFilledDataAsyncCall.execute();
            }
        });
    }
    ProgressDialog pd;
    private class SaveFilledDataAsyncCall extends AsyncTask<String, Void, Void> {
        boolean flag = false;
        SoapPrimitive insert;

        public SaveFilledDataAsyncCall() {
            pd = new ProgressDialog(FillDataActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
//findme     لبيانات كيف سيتم بعثها في ال data
                KSoapClass soap = new KSoapClass();
                String data = ":"+MPID+
                        ",:"+answer1+
                        ",:"+answer2+
                        ",:"+answer3+
                        ",:"+answer4+
                        ",:"+answer5+
                        ",:"+answer6+
                        ",:"+answer7+
                        ",:"+answer8+
                        ",:"+answer9+
                        ",:"+downvalue_discon.getText().toString()+
                        ",:"+actualvalue_discon.getText().toString()+
                        ",:"+catchedvalue_discon.getText().toString()+
                        ",:"+downvalue_re.getText().toString()+
                        ",:"+actualvalue_re.getText().toString()+
                        ",:"+ catchedvalue_re.getText().toString();

                try {
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(soap.privateKey));
                    PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(soap.publicKey));
                    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                    RSA.setKey(pubKey, privKey);

                    byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                    String base64Encoded = Base64.getEncoder().encodeToString(encodeData);
                    insert = soap.InsertINSPTemplate(base64Encoded);

                } catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try{
                if(flag) {
                    pd.dismiss();

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


    public void onRadioButtonClicked1(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.simpleRadioButton:
                if (checked)
                    answer1 = "1";
                break;
            case R.id.simpleRadioButton1:
                if (checked)
                    answer1 = "0";
                break;
        }

    }

    public void onRadioButtonClicked2(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB21:
                if (checked)
                    answer2 = "1";
                break;
            case R.id.rB22:
                if (checked)
                    answer2 = "0";
                break;
        }
    }

    public void onRadioButtonClicked3(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB31:
                if (checked)
                    answer3 = "1";
                break;
            case R.id.rB32:
                if (checked)
                    answer3 = "0";
                break;
        }
    }

    public void onRadioButtonClicked4(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB41:
                if (checked)
                    answer4 = "1";
                break;
            case R.id.rB42:
                if (checked)
                    answer4 = "0";
                break;
        }
    }

    public void onRadioButtonClicked5(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB51:
                if (checked)
                    answer5 = "1";
                break;
            case R.id.rB52:
                if (checked)
                    answer5 = "0";
                break;
        }
    }

    public void onRadioButtonClicked6(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB61:
                if (checked)
                    answer6 = "1";
                break;
            case R.id.rB62:
                if (checked)
                    answer6 = "0";
                break;
        }
    }

    public void onRadioButtonClicked7(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB71:
                if (checked)
                    answer7 = "1";
                break;
            case R.id.rB72:
                if (checked)
                    answer7 = "0";
                break;
        }
    }

    public void onRadioButtonClicked8(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB81:
                if (checked)
                    answer8 = "1";
                break;
            case R.id.rB82:
                if (checked)
                    answer8 = "0";
                break;
        }
    }

    public void onRadioButtonClicked9(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rB91:
                if (checked)
                    answer9 = "1";
                break;
            case R.id.rB92:
                if (checked)
                    answer9 = "0";
                break;
        }
    }

}