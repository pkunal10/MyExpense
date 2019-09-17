package com.jk.admin.myexpense;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    ImageView iv;
    TextView tv;
    boolean handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        iv=(ImageView) findViewById(R.id.Iv);
        tv=(TextView) findViewById(R.id.Tv);

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.splash_screen);
        Animation animationfade= AnimationUtils.loadAnimation(this,R.anim.fade_animation);
        iv.setAnimation(animation);
        tv.setAnimation(animationfade);

        handler=new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        },4000);
    }
}
