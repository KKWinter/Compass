package com.kkwinter.compass;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kkwinter.compass.global.AppUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final static int REQUEST_CHECK_GOOGLE_SETTINGS = 0x99;
    public static final String LOCATION = "location";
    public static final String MOMENT = "moment";

    private View mMainView;
    private TextView mGreetingTextView;
    private TextView mPositionTextView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        if (!isGrantPermission()) {
            askForLocationDialog();
        }

        initializeView();
    }

    private void initializeView() {
        mMainView = findViewById(R.id.main_layout);
        mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

        mGreetingTextView = findViewById(R.id.tv_greeting);
        mGreetingTextView.setText(R.string.standard_greeting);

        findViewById(R.id.iv_location).setOnClickListener(this);

        mPositionTextView = findViewById(R.id.tv_position);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(MOMENT) && bundle.containsKey(LOCATION)) {
            Location location = bundle.getParcelable(LOCATION);
            LocationGetter.Moment moment = (LocationGetter.Moment) bundle.getSerializable(MOMENT);
            showResult(moment, location);
        }
    }

    private void initializeLocation() {
        LocationGetter.obtain(context, new LocationGetter.LocationListener() {
            @Override
            public void onFetchCompleted(LocationGetter.Moment moment, Location location) {
                showResult(moment, location);
            }
        });
    }


    private void showResult(LocationGetter.Moment moment, Location location) {
        if (location != null) {
            mPositionTextView.setVisibility(View.VISIBLE);
            mPositionTextView.setText(AppUtils.convert(location.getLatitude(), location.getLongitude()));
        } else {
            mPositionTextView.setVisibility(View.GONE);
        }

        if (moment != null) {
            switch (moment) {
                case MORNING:
                    mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_morning));
                    mGreetingTextView.setText(R.string.morning_greeting);
                    break;
                case AFTERNOON:
                    mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_evening));
                    mGreetingTextView.setText(R.string.afternoon_greeting);
                    break;
                case EVEN:
                    mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_night));
                    mGreetingTextView.setText(R.string.night_greeting);
                    break;
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_location:
                if (!isGrantPermission()) {
                    askForLocationDialog();
                } else {
                    locationProvidedDialog();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 检查权限是否获取
     *
     * @return true 获取, false没有获取
     */
    private boolean isGrantPermission() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 申请权限弹窗
     */
    private void askForLocationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(R.string.allow_location_permission);
        builder.setPositiveButton(
                R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CHECK_GOOGLE_SETTINGS);
                    }
                }
        );
        builder.setNegativeButton(
                R.string.dont_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }


    /**
     * 权限获取成功，关闭权限说明弹窗
     */
    private void locationProvidedDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_on_title);
        builder.setMessage(R.string.location_permission_allowed);
        builder.setPositiveButton(
                R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }


    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CHECK_GOOGLE_SETTINGS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeLocation();
                }
                break;
        }
    }
}
