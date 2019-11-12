package com.kkwinter.compass;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this.getApplicationContext();

        ImageView logo = findViewById(R.id.iv_logo);
        doBounceAnimation(logo);
    }


    //跳动动画，震动插值器
    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, 100, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.setStartDelay(500);
        animator.setDuration(2500);
        animator.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                initialize();

            }
        }, SPLASH_DURATION);
    }


    private void initialize() {
        LocationGetter.obtain(context, new LocationGetter.LocationListener() {
            @Override
            public void onFetchCompleted(LocationGetter.Moment moment, Location location) {

                Intent intent = new Intent(context, MainActivity.class);
                if (moment != null && location != null) {
                    intent.putExtra(MainActivity.LOCATION, location);
                    intent.putExtra(MainActivity.MOMENT, moment);
                }
                startActivity(intent);

                SplashActivity.this.finish();
            }
        });

    }

}
