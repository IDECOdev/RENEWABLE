package com.example.renewable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Settings extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
}
