package com.example.android.personalkasappv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        //Thread
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000); //1000 = 1 dtk
                }catch (InterruptedException i){
                    i.printStackTrace();
                }finally {
                    Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(intent);
                    //class activity
                    finish();
                }
            }
        };
        timerThread.start();
    }
}
