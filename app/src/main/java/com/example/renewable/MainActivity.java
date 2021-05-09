package com.example.renewable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import org.ksoap2.serialization.SoapObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Button inspectBtn, connectBtn;
    SoapObject response2;

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


}