package com.example.renewable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class FillDataActivity extends AppCompatActivity {
    ArrayList<String> dataByMember;
    String MPID = "";
    int counter = 0;
    EditText downvalue_discon, actualvalue_discon, catchedvalue_discon, downvalue_re, actualvalue_re, catchedvalue_re;
    Button sendbtn;
    ListView list;
    JSONObject Q1;
    JSONArray jsonArray = new JSONArray();
    ArrayList<Questions> qlist;
    ArrayList<PresentInfo> presentInfo;
    ArrayList<AnswersList> answersLists;
    ArrayList<AnswersListdesc> answersListdescs;
    ArrayList<AnswersList2> answersList2;
    ProgressDialog pd;
    boolean fill =false;
    InquirInfo inquirInfo;
    String dates = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);

        MPID = getIntent().getStringExtra("MPID");
        dates =  getIntent().getStringExtra("dates");

        Intent i = getIntent();
        inquirInfo = (InquirInfo) i.getSerializableExtra("inboxDetail");

        qlist = new ArrayList<>();
        downvalue_discon = findViewById(R.id.downvalue_discon);
        actualvalue_discon = findViewById(R.id.actualvalue_discon);
        catchedvalue_discon = findViewById(R.id.catchedvalue_discon);
        downvalue_re = findViewById(R.id.downvalue_re);
        actualvalue_re = findViewById(R.id.actualvalue_re);
        catchedvalue_re = findViewById(R.id.catchedvalue_re);

        dataByMember = new ArrayList<>();
        answersLists = new ArrayList<>();
        answersLists.clear();
        answersList2 = new ArrayList<>();
        answersList2.clear();
        answersListdescs = new ArrayList<>();
        answersListdescs.clear();

        sendbtn = findViewById(R.id.sendbtn);

        try {
            PresentAsyncCall ds = new PresentAsyncCall();
            ds.execute();

        }catch (Exception e){}

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean flag = true;

                for(int i=0; i<answersLists.size(); i++)
                    if(answersLists.get(i).answers.equals(""))
                        flag = false;

                if(flag){
                    answersList2.clear();
                    answersList2.add(new AnswersList2("-1",downvalue_discon.getText().toString(), actualvalue_discon.getText().toString(), catchedvalue_discon.getText().toString()));
                    answersList2.add(new AnswersList2("0",downvalue_re.getText().toString(), actualvalue_re.getText().toString(), catchedvalue_re.getText().toString()));

                    SaveFilledDataAsyncCall saveFilledDataAsyncCall = new SaveFilledDataAsyncCall();
                    saveFilledDataAsyncCall.execute();
                }
                else
                    Toast.makeText(FillDataActivity.this, "يرجى الإجابة على جميع الأسئلة", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class GenericAsyncCall2 extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;

        SoapObject questions;
        SoapObject present;

        public GenericAsyncCall2() {
            dialog = new ProgressDialog(FillDataActivity.this);
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... params) {
            //Invoke webservice
            try{
                KSoapClass service = new KSoapClass();

                String data = ":,:2";
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(android.util.Base64.decode(service.privateKey, android.util.Base64.DEFAULT));
                PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(android.util.Base64.decode(service.publicKey, android.util.Base64.DEFAULT));
                RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
                RSA.setKey(pubKey, privKey);
                byte[] encodeData = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data);
                String base64Encoded = android.util.Base64.encodeToString(encodeData, android.util.Base64.DEFAULT);

                questions=service.GetINSPRenTemplate(base64Encoded);

            }
            catch (Exception exception)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if(questions!=null && questions.getPropertyCount() > 0 && result==null){
                GetReadableqstData(questions);
            }

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

    private class PresentAsyncCall extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;

        SoapObject questions;
        SoapObject present;

        public PresentAsyncCall() {
            dialog = new ProgressDialog(FillDataActivity.this);
        }


        @Override
        protected Void doInBackground(String... params) {
            //Invoke webservice
            try{
                KSoapClass service1 = new KSoapClass();

                String data1 = ":and B.MPID ="+ MPID+",:1";
                KeyFactory kf1 = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpecPKCS81 = new PKCS8EncodedKeySpec(android.util.Base64.decode(service1.privateKey, android.util.Base64.DEFAULT));
                PrivateKey privKey1 = kf1.generatePrivate(keySpecPKCS81);
                X509EncodedKeySpec keySpecX5091 = new X509EncodedKeySpec(android.util.Base64.decode(service1.publicKey, android.util.Base64.DEFAULT));
                RSAPublicKey pubKey1 = (RSAPublicKey) kf1.generatePublic(keySpecX5091);
                RSA.setKey(pubKey1, privKey1);
                byte[] encodeData1 = RSA.encrypt(RSA.getPublicKey2(RSA.GetMap()), data1);
                String base64Encoded1 = android.util.Base64.encodeToString(encodeData1, android.util.Base64.DEFAULT);

                present=service1.GetINSPRenTemplate(base64Encoded1);

            }
            catch (Exception exception)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            if(result==null && present==null && present.getPropertyCount() < 0){
                GenericAsyncCall2 genericAsyncCall = new GenericAsyncCall2();
                genericAsyncCall.execute();
            }else{
                GetReadableqstData1(present);
            }

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

    private void GetReadableqstData1(SoapObject present) {
        SoapObject so1, so2, so3;

        presentInfo = new ArrayList<>();
        presentInfo.clear();
        if (present != null && present.getPropertyCount() > 0){
            so1 = (SoapObject) present.getProperty(1);
            if (so1 != null && so1.getPropertyCount() > 0){
                so2 = (SoapObject) so1.getProperty(0);
                if (so2 != null && so2.getPropertyCount() > 0){
                    for(int i=0; i<so2.getPropertyCount(); i++){
                        so3 = (SoapObject) so2.getProperty(i);
                        String ID="", ENTRY_USER="",ENTRY_DATE= "",MPID = "", MEMBER_ID = "", MEMBER_DESC = "", YES_FLAG = "", MINIMUM_VALUE = "", MEASURED_VALUE = "", EXACT_VALUE ="", INSERT_TYPE = "";
                        try{
                            ID =so3.getPropertyAsString("ID");
                        }catch (Exception e){
                        }  try{
                            ENTRY_USER =so3.getPropertyAsString("ENTRY_USER");
                        }catch (Exception e){
                        }  try{
                            ENTRY_DATE =so3.getPropertyAsString("ENTRY_DATE");
                        }catch (Exception e){
                        }  try{
                            MPID =so3.getPropertyAsString("MPID");
                        }catch (Exception e){
                        }  try{
                            MEMBER_ID =so3.getPropertyAsString("MEMBER_ID");
                        }catch (Exception e){
                        }  try{
                            MEMBER_DESC =so3.getPropertyAsString("MEMBER_DESC");
                        }catch (Exception e){
                        }  try{
                            YES_FLAG =so3.getPropertyAsString("YES_FLAG");
                        }catch (Exception e){
                        }  try{
                            MINIMUM_VALUE =so3.getPropertyAsString("MINIMUM_VALUE");
                        }catch (Exception e){
                        }  try{
                            EXACT_VALUE =so3.getPropertyAsString("EXACT_VALUE");
                        }catch (Exception e){
                        }  try{
                            MEASURED_VALUE =so3.getPropertyAsString("MEASURED_VALUE");
                        }catch (Exception e){
                        }  try{
                            INSERT_TYPE =so3.getPropertyAsString("INSERT_TYPE");
                        }catch (Exception e){
                        }
                        presentInfo.add(new PresentInfo(ID, ENTRY_USER,ENTRY_DATE,MPID , MEMBER_ID , MEMBER_DESC, YES_FLAG, MINIMUM_VALUE, MEASURED_VALUE, EXACT_VALUE, INSERT_TYPE));
                    }
                }
            }
        }


        if(!presentInfo.isEmpty()){
            answersLists.clear();
            for(int i =0; i<presentInfo.size();i++){
                if(presentInfo.get(i).getINSERT_TYPE().equals("1")){
                    answersLists.add(new AnswersList(presentInfo.get(i).getMEMBER_ID(), presentInfo.get(i).YES_FLAG));
                    answersListdescs.add(new AnswersListdesc(presentInfo.get(i).getMEMBER_ID(), presentInfo.get(i).YES_FLAG, presentInfo.get(i).getMEMBER_DESC()));
                }
            }
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
            CustomAdapter2 adapter=new CustomAdapter2(FillDataActivity.this);
            list.setAdapter(adapter);

            for(int i =0; i<presentInfo.size();i++){
                if(presentInfo.get(i).INSERT_TYPE.equals("2")){

                    if(presentInfo.get(i).getMEMBER_ID().equals("1")){
                        downvalue_discon.setText(presentInfo.get(i).MINIMUM_VALUE);
                        actualvalue_discon.setText(presentInfo.get(i).MEASURED_VALUE);
                        catchedvalue_discon.setText(presentInfo.get(i).EXACT_VALUE); }

                    if(presentInfo.get(i).getMEMBER_ID().equals("2")){
                        downvalue_re.setText(presentInfo.get(i).MINIMUM_VALUE);
                        actualvalue_re.setText(presentInfo.get(i).MEASURED_VALUE);
                        catchedvalue_re.setText(presentInfo.get(i).EXACT_VALUE); }

                }
            }

        }else{
            GenericAsyncCall2 genericAsyncCall = new GenericAsyncCall2();
            genericAsyncCall.execute();
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
                                    so3.getPropertyAsString("MEMBER_ID"),
                                    so3.getPropertyAsString("MEMBER_DESC")));
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

            for(int i=0; i<qlist.size(); i++)
                answersLists.add(new AnswersList(qlist.get(i).getSYS_MINOR(), ""));

            CustomAdapter adapter=new CustomAdapter(FillDataActivity.this);
            list.setAdapter(adapter);

        }

    }

    public class CustomAdapter extends BaseAdapter {

        Context mContext;

        public CustomAdapter(Context context) {
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

            LayoutInflater inflater = (FillDataActivity.this).getLayoutInflater();
            convertView = inflater.inflate(R.layout.idanswerlist, parent, false);

            TextView qName = (TextView) convertView.findViewById(R.id.qst1);
            RadioGroup rg = (RadioGroup) convertView.findViewById(R.id.g1);
            RadioButton rb1 = (RadioButton) convertView.findViewById(R.id.yes);
            RadioButton rb2 = (RadioButton) convertView.findViewById(R.id.no);

           qName.setText(qlist.get(position).getSYS_DESC());

           rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    switch (checkedId){

                        case R.id.yes :
                            answersLists.set(position, new AnswersList(qlist.get(position).getSYS_MINOR(), "1"));
                            break;
                        case R.id.no :
                            answersLists.set(position, new AnswersList(qlist.get(position).getSYS_MINOR(), "0"));
                            break;
                    }

                }
            });

//           Check(rb1, rb2, position);
//

           return convertView; }

        public void Check(RadioButton rb1, RadioButton rb2, int pos){

            if(answersLists.get(pos).answers.equals("1")){
                rb1.setChecked(true);
                rb2.setChecked(false); }
            else if(answersLists.get(pos).answers.equals("0")){
                rb2.setChecked(true);
                rb1.setChecked(false); }
            else{
                rb1.setChecked(false);
                rb2.setChecked(false); }

        }

    }

    public class CustomAdapter2 extends BaseAdapter {

        Context mContext;

        public CustomAdapter2(Context context) {
            this.mContext = context;
        }
        @Override
        public int getCount() {
            return answersLists.size();
        }

        @Override
        public AnswersList getItem(int i) {
            return answersLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (FillDataActivity.this).getLayoutInflater();
            convertView = inflater.inflate(R.layout.idanswerlist, parent, false);

            TextView qName = (TextView) convertView.findViewById(R.id.qst1);
            RadioGroup rg = (RadioGroup) convertView.findViewById(R.id.g1);
            RadioButton rb1 = (RadioButton) convertView.findViewById(R.id.yes);
            RadioButton rb2 = (RadioButton) convertView.findViewById(R.id.no);

            for(int i = 0 ; i<answersListdescs.size() ; i++){
                if(answersListdescs.get(i).id.equals(answersLists.get(position).getId())){
                    qName.setText(answersListdescs.get(i).getDesc());
                }
            }

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    switch (checkedId){

                        case R.id.yes :
                            answersLists.set(position, new AnswersList(answersLists.get(position).getId(), "1"));
                            break;
                        case R.id.no :
                            answersLists.set(position, new AnswersList(answersLists.get(position).getId(), "0"));
                            break;
                    }

                }
            });

            Check(rb1, rb2, position);

            return convertView;
        }

        public void Check(RadioButton rb1, RadioButton rb2, int pos){

            if(answersLists.get(pos).answers.equals("1")){
                rb1.setChecked(true);
                rb2.setChecked(false); }
            else if(answersLists.get(pos).answers.equals("0")){
                rb2.setChecked(true);
                rb1.setChecked(false); }
            else{
                rb1.setChecked(false);
                rb2.setChecked(false); }

        }

    }

    private class SaveFilledDataAsyncCall extends AsyncTask<String, Void, Void> {
        boolean flag = false;
        SoapPrimitive insert;

        public SaveFilledDataAsyncCall() {
            pd = new ProgressDialog(FillDataActivity.this);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {


                ArrayList<String> fin = new ArrayList<>();
                ArrayList<String> fin2 = new ArrayList<>();

                fin.clear();
                fin2.clear();

                for(int i=0; i<answersLists.size(); i++)
                    fin.add(answersLists.get(i).id+","+answersLists.get(i).answers);
                JSONArray jsArray1 = new JSONArray(fin);

                for(int i=0; i<answersList2.size(); i++)
                    fin2.add(answersList2.get(i).downvalue+","+answersList2.get(i).actualvalue+","+answersList2.get(i).catchedvalue);
                JSONArray jsArray2 = new JSONArray(fin2);

                Q1 = new JSONObject();

                try {
                    Q1.put("MPID",MPID);
                    Q1.put("jsArray1", jsArray1);
                    Q1.put("jsArray2", jsArray2);
                    Q1.put("ENtryUser",getSharedPreferences("Info", MODE_PRIVATE).getString("EMP_NO", ""));
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
                if(String.valueOf(insert).equals("true")) {
                    pd.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FillDataActivity.this);
                    LayoutInflater inflater = FillDataActivity.this.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                    final AlertDialog dialog = builder.create();
                    ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                    final Button exit=dialog.findViewById(R.id.btn2);
                    final CircleImageView im=dialog.findViewById(R.id.im);
                    final TextView textView3=dialog.findViewById(R.id.textView3);
                    textView3.setText("تمت عملية الحفظ للنموذج");

                    im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(FillDataActivity.this, InquirActivity.class).putExtra("MPID", MPID).putExtra("inboxDetail", inquirInfo).putExtra("dates",dates));
//                            finish();
                        }
                    });
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(FillDataActivity.this, InquirActivity.class).putExtra("MPID", MPID).putExtra("inboxDetail", inquirInfo).putExtra("dates",dates));
//                            finish();
                        }
                    });
                }else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FillDataActivity.this);
                    LayoutInflater inflater = FillDataActivity.this.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_vacstate, null));
                    final AlertDialog dialog = builder.create();
                    ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                    final Button exit=dialog.findViewById(R.id.btn2);
                    final CircleImageView im=dialog.findViewById(R.id.im);
                    final TextView textView3=dialog.findViewById(R.id.textView3);
                    textView3.setText("لقد حدث خطأ اثناء عملية الحفظ");

                    im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
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

}