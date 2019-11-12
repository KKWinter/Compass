package com.kkwinter.compass;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kkwinter.compass.global.solar.SunriseSunsetCalculator;

import java.util.Calendar;
import java.util.Date;

public class LocationGetter {

    public enum Moment {
        MORNING,
        AFTERNOON,
        EVEN
    }


    /**
     * 获取经纬度以及当前时刻
     *
     * @param context          context
     * @param locationListener listener
     */
    public static void obtain(Context context, final LocationListener locationListener) {
        if (context == null || locationListener == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            locationListener.onFetchCompleted(null, null);

            return;
        }


        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    locationListener.onFetchCompleted(updateDayState(location), location);
                } else {
                    locationListener.onFetchCompleted(null, null);
                }

            }
        });
    }

    private static Moment updateDayState(Location location) {

        com.kkwinter.compass.global.solar.Location location1 = new com.kkwinter.compass.global.solar.Location(location.getLatitude(), location.getLongitude());
        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(location1, Calendar.getInstance().getTimeZone());

        Date sunrise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(Calendar.getInstance()).getTime();
        Date sunset = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(Calendar.getInstance()).getTime();
        Date current = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Date noon = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        Date midNight = calendar1.getTime();


        if (current.after(midNight) && current.before(sunrise)) {
            return Moment.EVEN;
        }

        if (current.after(sunrise) && current.before(noon)) {
            return Moment.MORNING;
        }

        if (current.after(noon) && current.before(sunset)) {
            return Moment.AFTERNOON;
        }

        if (current.after(sunset)) {
            return Moment.EVEN;
        }

        return null;
    }


    interface LocationListener {
        void onFetchCompleted(Moment moment, Location location);
    }
}
