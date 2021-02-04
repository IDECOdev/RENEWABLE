package com.example.renewable;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    ProgressBar pg;
    int prog = 0;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Animation();
        StartHomeScreen();

    }

    public void Animation(){
        title =  findViewById(R.id.txt2);
        float x =400;
        title.setTranslationX(x);
        title.setAlpha(0);
        title.animate().translationX(0).alpha(1).
                setDuration(800).setStartDelay(500).start();
    }

    public void StartHomeScreen(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    for(int i=0; i<4; i++){
                        sleep(400);
                    }

                    Intent intent = new Intent(SplashActivity.this, LoginActvity.class);
                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
