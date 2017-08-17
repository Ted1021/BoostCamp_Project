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
    //    public static LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    private static Location mLocation;

    public static Location getLocation(Context context) {

        mContext = context;
        final LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLocation = location;
                Log.d("CHECK_GET_LOCATION", ">>>>>>>>>>>>> " + mLocation.getLongitude() + " / " + mLocation.getLatitude());

                mLocationManager.removeUpdates(this);
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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            return mLocation;
        }

        return null;
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
