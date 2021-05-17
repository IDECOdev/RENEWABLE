package com.example.renewable;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class ConnectionIncquireActivity extends AppCompatActivity {
    EditText cusmNum_et, processNum_et;
    ProgressDialog pd;
    InquirInfo inquirInfo;
    ArrayList<InquirInfo> info;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    TextView data;

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
        startActivity(new Intent(ConnectionIncquireActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_incquire);

        cusmNum_et = findViewById(R.id.cusmNum_et);
        processNum_et = findViewById(R.id.processNum_et);

        data=findViewById(R.id.data);
        info = new ArrayList<>();
        expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setGroupIndicator(null);

        expandableListView.setAdapter((BaseExpandableListAdapter)null);
        CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
        customerCashAsyncCall.execute();

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
                if(cusmNum_et.getText().length()==0){
                    CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
                    customerCashAsyncCall.execute();
                }
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
                if(processNum_et.getText().length()==0){
                    CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
                    customerCashAsyncCall.execute();
                }
            }
        });


        CircleImageView bar = findViewById(R.id.copy1);

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanDialog dialog = new ScanDialog(ConnectionIncquireActivity.this);
                dialog.show();
            }
        });

    }
    public void InquirProcessNum(View view) {
        if(processNum_et.getText().toString().equals(""))
        {
            Toast.makeText(ConnectionIncquireActivity.this, "يرجى إدخال رقم العملية", Toast.LENGTH_LONG).show();
        }else{
            CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
            customerCashAsyncCall.execute();
        }
    }

    public void InquirCusmNum(View view) {
        if(cusmNum_et.getText().toString().equals(""))
        {
            Toast.makeText(ConnectionIncquireActivity.this, "يرجى إدخال رقم الاشتراك", Toast.LENGTH_LONG).show();
        }else{
            CustomerCashAsyncCall customerCashAsyncCall = new CustomerCashAsyncCall();
            customerCashAsyncCall.execute();
        }
    }
    public void settings(View view) {
        startActivity(new Intent(ConnectionIncquireActivity.this, Settings.class));
    }

    private class CustomerCashAsyncCall extends AsyncTask<String, Void, Void> {
        String temp;
        SoapObject soapObject, soapObject1;
        String strWhereOracle = "";

        public CustomerCashAsyncCall() {
            pd = new ProgressDialog(ConnectionIncquireActivity.this);
            temp = cusmNum_et.getText().toString();
        }
        @Override
        protected Void doInBackground(String... params) {
            if(!cusmNum_et.getText().toString().equals("")){
                int cn = Integer.parseInt(cusmNum_et.getText().toString().substring(4));
                int cc =  Integer.parseInt(cusmNum_et.getText().toString().substring(0,3));

                if (!String.valueOf(cn).equals("") )
                    strWhereOracle += " and a.\"ca_cusm_num\"=" + cn;
                if (!String.valueOf(cc).equals("") && !String.valueOf(cc).equals("-1"))
                    strWhereOracle += " and a.city_id =" + cc;
            }
            if (!processNum_et.getText().toString().equals(""))
                strWhereOracle += " and a.MAIN_PID =" + processNum_et.getText().toString();

            String data = "strWhereOracle:"+strWhereOracle+",strWhereSql: ,taskId:84,datatype:5";
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
                    info.clear();
                    expandableListView.setAdapter((BaseExpandableListAdapter)null);
                    GetReadableData(soapObject);
                }else{
                    expandableListView.setAdapter((BaseExpandableListAdapter)null);
                    data.setVisibility(View.VISIBLE);
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

    private void GetReadableData(SoapObject soapObject1) {
        SoapObject so1, so2, so3;
        String ID="",MAIN_PID="",CA_CUSM_NAME="",CITY_ID="",CTYM_NAME="",ca_cusm_num="",CA_X_COORDINATE="",CA_Y_COORDINATE="";
        if(soapObject1 == null){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
            cusmNum_et.setEnabled(true);
            processNum_et.setEnabled(true);
        }
        else if(soapObject1.equals("anyType{}")){
            Toast.makeText(this, "لا يوجد معلومات لهذا العداد", Toast.LENGTH_LONG).show();
            cusmNum_et.setEnabled(true);
            processNum_et.setEnabled(true);
        } else{
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
                                    ca_cusm_num = so3.getPropertyAsString("ca_cusm_num");
                                }catch (Exception e){}
                                try{
                                    CA_X_COORDINATE = so3.getPropertyAsString("CA_X_COORDINATE");
                                }catch (Exception e){}
                                try{
                                    CA_Y_COORDINATE = so3.getPropertyAsString("CA_Y_COORDINATE");
                                }catch (Exception e){}

                                info.add(new InquirInfo(ID,MAIN_PID,CA_CUSM_NAME,CITY_ID,CTYM_NAME,ca_cusm_num,CA_X_COORDINATE,CA_Y_COORDINATE));

                            }catch (Exception e){

                            }
                        }
                    }
                }
            }
        }

        if(info.isEmpty()){
            data.setVisibility(View.VISIBLE);
        }else {
            data.setVisibility(View.INVISIBLE);
            expandableListAdapter = new CustomExpandableListAdapter(ConnectionIncquireActivity.this);
            expandableListView.setAdapter(expandableListAdapter);
        }
    }

    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;


        public CustomExpandableListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return info.get(listPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
//                convertView = layoutInflater.inflate(R.layout.vocationd_list_item, null);
            }


            return null;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int listPosition) {
            return info.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return info.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(final int listPosition, boolean isExpanded,
                                 View convertView, final ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.inbox, null);

            }
            TextView cusmname = (TextView) convertView.findViewById(R.id.name);
            TextView status = (TextView) convertView.findViewById(R.id.status);

            cusmname.setTypeface(null, Typeface.BOLD);
            cusmname.setText("اسم المشترك : "+info.get(listPosition).getCA_CUSM_NAME());
            status.setText("رقم المعاملة : "+info.get(listPosition).getMAIN_PID());

            RelativeLayout card_lay=(RelativeLayout) convertView
                    .findViewById(R.id.card_lay);
            card_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(ConnectionIncquireActivity.this, FacilityConnectionActivity.class);
                        intent.putExtra("inboxDetail", info.get(listPosition));
                        startActivity(intent);

                }
            });

            ImageView img=convertView.findViewById(R.id.im);
            img.setImageResource(R.drawable.ic_baseline_arrow);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ConnectionIncquireActivity.this, InquirActivity.class);
                    intent.putExtra("inboxDetail", info.get(listPosition));
                    startActivity(intent);
                }
            });


            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }
}