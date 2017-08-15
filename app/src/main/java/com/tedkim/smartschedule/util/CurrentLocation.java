package com.tedkim.smartschedule.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * @author 김태원
 * @file CurrentLocation.java
 * @brief get Current GPS
 * @date 2017.08.14
 */

public class CurrentLocation {

    private static Context mContext;
    public static LocationManager mLocationManager;
    private static Location mLocation;

    public static Location getLocation(Context context) {

        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled=" + isGPSEnabled);
        Log.d("Main", "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                Log.d("CHECK_GET_LOCATION", ">>>>>>>>>>>>> "+mLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        Log.d("CHECK_PERMISSION", ">>>>>>>>> in current location "+ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            return mLocation;
        }
//        else{
//
//            mLocation = null;
//            return mLocation;
//        }
        return mLocation;
    }

//    @Nullable
//    public static Location getLastKnownLocation() {
//
//        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (lastKnownLocation != null) {
//
//            return lastKnownLocation;
//        }
//        return null;
//    }
}
