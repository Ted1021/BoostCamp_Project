package com.tedkim.smartschedule.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * @author 김태원
 * @file CurrentLocation.java
 * @brief get Current GPS
 * @date 2017.08.14
 */

public class CurrentLocation {

    public static Location getLocation(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {continue;}

                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
        }

        if(bestLocation == null){
            // 판교 H스퀘어 좌표로 초기화
            bestLocation.setLongitude(127.1089531);
            bestLocation.setLatitude(37.4014619);
            Log.e("CHECK_ENTER", ">>>>>>>>> Location fail " + bestLocation.getLongitude()+"/"+bestLocation.getLatitude());
            return bestLocation;
        }
        Log.e("CHECK_ENTER", ">>>>>>>>> Location success " + bestLocation.getLongitude()+"/"+bestLocation.getLatitude());
        return bestLocation;
    }
}

