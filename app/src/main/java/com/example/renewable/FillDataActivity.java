package com.example.renewable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
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
    TextView qst1, qst2, qst3, qst4, qst5, qst6, qst7, qst8, qst9;
    EditText downvalue_discon, actualvalue_discon, catchedvalue_discon, downvalue_re, actualvalue_re, catchedvalue_re;
    Button sendbtn;
    ListView list;
    JSONObject Q1;
    JSONArray jsonArray = new JSONArray();
    ArrayList<Questions> qlist;
    ArrayList<AnswersList> answersLists;
    ArrayList<AnswersList2> answersList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);

        MPID = getIntent().getStringExtra("MPID");
        qlist = new ArrayList<>();
        downvalue_discon = findViewById(R.id.downvalue_discon);
        actualvalue_discon = findViewById(R.id.actualvalue_discon);
        catchedvalue_discon = findViewById(R.id.catchedvalue_discon);
        downvalue_re = findViewById(R.id.downvalue_re);
        actualvalue_re = findViewById(R.id.actualvalue_re);
        catchedvalue_re = findViewById(R.id.catchedvalue_re);

//        qst1 = findViewById(R.id.qst1);
//        qst2 = findViewById(R.id.qst2);
//        qst3 = findViewById(R.id.qst3);
//        qst4 = findViewById(R.id.qst4);
//        qst5 = findViewById(R.id.qst5);
//        qst6 = findViewById(R.id.qst6);
//        qst7 = findViewById(R.id.qst7);
//        qst8 = findViewById(R.id.qst8);
//        qst9 = findViewById(R.id.qst9);


        dataByMember = new ArrayList<>();
        answersLists = new ArrayList<>();
        answersList2 = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtn);

        try {
            GenericAsyncCall ds = new GenericAsyncCall();
            ds.execute();

        }catch (Exception e){}

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answersList2.add(new AnswersList2("-1",downvalue_discon.getText().toString(), actualvalue_discon.getText().toString(), catchedvalue_discon.getText().toString()));
                answersList2.add(new AnswersList2("0",downvalue_re.getText().toString(), actualvalue_re.getText().toString(), catchedvalue_re.getText().toString()));

               SaveFilledDataAsyncCall saveFilledDataAsyncCall = new SaveFilledDataAsyncCall();
               saveFilledDataAsyncCall.execute();
            }
        });

    }


    private class GenericAsyncCall extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;

        SoapObject questions;

        public GenericAsyncCall() {
            dialog = new ProgressDialog(FillDataActivity.this);
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... params) {
            //Invoke webservice
            dialog.dismiss();
            try{
                KSoapClass service = new KSoapClass();

                String data = ":49,:963";
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(android.util.Base64.decode(service.privateKey, android.util.Base64.DEFAULT));
                PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(android.util.Base64.decode(service.publicKey, android.util.Base64.DEFAULT));
                RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
                RSA.setKey(pubKey, privKey);
                byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                String base64Encoded = android.util.Base64.encodeToString(encodeData, android.util.Base64.DEFAULT);

                questions=service.GetGenericsDataTable(base64Encoded);
            }
            catch (Exception exception)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {

                if(questions!=null && questions.getPropertyCount() > 0){
                    GetReadableqstData(questions);
                }
            }catch (Exception e){}

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

    private void GetReadableqstData(SoapObject questions) {
        SoapObject so1, so2, so3;

        if (questions != null && questions.getPropertyCount() > 0){
            so1 = (SoapObject) questions.getProperty(1);
            if (so1 != null && so1.getPropertyCount() > 0){
                so2 = (SoapObject) so1.getProperty(0);
                if (so2 != null && so2.getPropertyCount() > 0){
                    for(int i=0; i<so2.getPropertyCount(); i++){
                        so3 = (SoapObject) so2.getProperty(i);
                        try{
                            qlist.add(new Questions(
                                    so3.getPropertyAsString("SYS_MINOR"),
                                    so3.getPropertyAsString("SYS_DESC")));
                        }catch (Exception e){

                        }
                    }
                }
            }
        }
        if(qlist!=null){
            list=(ListView)findViewById(R.id.list);
            list.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
            CustomAdapter adapter=new CustomAdapter(FillDataActivity.this);
            list.setAdapter(adapter);
//            qst1.setText(qlist.get(0).getSYS_DESC());
//            qst2.setText(qlist.get(1).getSYS_DESC());
//            qst3.setText(qlist.get(2).getSYS_DESC());
//            qst4.setText(qlist.get(3).getSYS_DESC());
//            qst5.setText(qlist.get(4).getSYS_DESC());
//            qst6.setText(qlist.get(5).getSYS_DESC());
//            qst7.setText(qlist.get(6).getSYS_DESC());
//            qst8.setText(qlist.get(7).getSYS_DESC());
//            qst9.setText(qlist.get(8).getSYS_DESC());
        }

    }
    public  static class ViewHolder {
        public static TextView qName;

        public static RadioButton yes;
        public static RadioButton no;
    }
    public class CustomAdapter extends ArrayAdapter<Questions> {

        Context mContext;

        public CustomAdapter(Context context) {
            super(context, R.layout.idanswerlist);
            this.mContext = context;
        }
        @Override
        public int getCount() {
            return qlist.size();
        }

        @Override
        public Questions getItem(int i) {
            return qlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

//            if (convertView == null) {

            ViewHolder viewHolder = new ViewHolder();
            LayoutInflater inflater = (FillDataActivity.this).getLayoutInflater();
            convertView = inflater.inflate(R.layout.idanswerlist, parent, false);


            viewHolder.qName = (TextView) convertView.findViewById(R.id.qst1);
            viewHolder.yes = (RadioButton) convertView.findViewById(R.id.yes);
            viewHolder.no = (RadioButton) convertView.findViewById(R.id.no);

            convertView.setTag(viewHolder);

            viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.qName.setText(qlist.get(position).getSYS_DESC());

            viewHolder.yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    answersLists.add(new AnswersList(qlist.get(position).getSYS_MINOR(), String.valueOf(isChecked)));
                }
            });
            viewHolder.no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        answersLists.add(new AnswersList(qlist.get(position).getSYS_MINOR(), String.valueOf(isChecked)));

                }
            });
//            } else {
//            }
//
//        }
            return convertView;
        }
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

//            MainPID, MemberID, YesFlag, MinimumValue, MeasuredValue, ExactValue, ENtryUser, MemberType
            JSONArray jsArray1 = new JSONArray(answersLists);
            JSONArray jsArray2 = new JSONArray(answersList2);

            Q1 = new JSONObject();
            try {
                Q1.put("MPID",MPID);
                Q1.put("jsArray1",jsArray1);
                Q1.put("jsArray2",jsArray2);
                Q1.put("ENtryUser",getSharedPreferences("Info", MODE_PRIVATE).getString("ID", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
                KSoapClass soap = new KSoapClass();
//
                String data = Q1.toString();

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


//    public void onRadioButtonClicked1(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.simpleRadioButton:
//                if (checked){
//                    answer1 = "1";
//                }
//
//                break;
//            case R.id.simpleRadioButton1:
//                if (checked)
//                    answer1 = "0";
//                Q1 = new JSONObject();
//                try {
//                    Q1.put("id", "3");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
//
//    }
//
//    public void onRadioButtonClicked2(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB21:
//                if (checked)
//                    answer2 = "1";
//                break;
//            case R.id.rB22:
//                if (checked)
//                    answer2 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked3(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB31:
//                if (checked)
//                    answer3 = "1";
//                break;
//            case R.id.rB32:
//                if (checked)
//                    answer3 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked4(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB41:
//                if (checked)
//                    answer4 = "1";
//                break;
//            case R.id.rB42:
//                if (checked)
//                    answer4 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked5(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB51:
//                if (checked)
//                    answer5 = "1";
//                break;
//            case R.id.rB52:
//                if (checked)
//                    answer5 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked6(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB61:
//                if (checked)
//                    answer6 = "1";
//                break;
//            case R.id.rB62:
//                if (checked)
//                    answer6 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked7(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB71:
//                if (checked)
//                    answer7 = "1";
//                break;
//            case R.id.rB72:
//                if (checked)
//                    answer7 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked8(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB81:
//                if (checked)
//                    answer8 = "1";
//                break;
//            case R.id.rB82:
//                if (checked)
//                    answer8 = "0";
//                break;
//        }
//    }
//
//    public void onRadioButtonClicked9(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.rB91:
//                if (checked)
//                    answer9 = "1";
//                break;
//            case R.id.rB92:
//                if (checked)
//                    answer9 = "0";
//                break;
//        }
//    }

}