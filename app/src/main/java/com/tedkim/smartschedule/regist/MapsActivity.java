package com.tedkim.smartschedule.regist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tedkim.smartschedule.R;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener ,View.OnClickListener {

    private GoogleMap mGoogleMap;

    Toolbar mToolbar;
    ImageButton mBack, mSave, mSearch;
    EditText mSearchLocation;

    CameraPosition mCameraPosition;
    Geocoder mGeocoder;
    LatLng mCurrentPosition;

    Double mLatitude, mLongitude;
    String mCurrentAddress;

    static final int MAX_LEN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initView();

        initData();
    }

    private void initView(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_maps);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0, 0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mSearch = (ImageButton) findViewById(R.id.imageButton_search);
        mSearch.setOnClickListener(this);

        mSearchLocation = (EditText) findViewById(R.id.editText_searchLocation);
    }

    private void initData(){

        mCurrentAddress = getIntent().getStringExtra("ADDRESS");
        mSearchLocation.setText(mCurrentAddress);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGeocoder = new Geocoder(MapsActivity.this);

        addMarker();

        moveCamera(mCurrentPosition);

        setUiComponents();
    }

    private void getPosition(){
        try {
            List<Address> addressList = mGeocoder.getFromLocationName(mCurrentAddress, MAX_LEN);
            mCurrentPosition = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

        } catch (IOException e) {
            Log.e("ERROR_GeoCoder", ">>>>>>>>>>>>>>> "+mCurrentPosition);
            e.printStackTrace();
        }
    }

    private void addMarker(){

        getPosition();

        mGoogleMap.addMarker(new MarkerOptions().position(mCurrentPosition).draggable(true).title(mCurrentAddress));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentPosition));
    }

    private void setUiComponents(){

        UiSettings uiSettings = mGoogleMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(this);
            uiSettings.setMyLocationButtonEnabled(true);
        }

        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
    }

    private void moveCamera(LatLng currentPosition){

        mCameraPosition = CameraPosition.builder().target(currentPosition).zoom(14).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    }

    private void searchLocation(){

        // TODO - MapActivity 를 이용해 새로운 주소지를 검색하게 되면 아예 맵을 초기화 시키고 새로 띄우는 로직으로 구상해보자
        mCurrentAddress = mSearchLocation.getText().toString();
        getPosition();
        moveCamera(mCurrentPosition);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.imageButton_back:
                finish();
                break;

            case R.id.imageButton_save:

                Intent intent = new Intent();

                intent.putExtra("ADDRESS", mCurrentAddress);
                intent.putExtra("LATITUDE", mLatitude);
                intent.putExtra("LONGITUDE", mLongitude);
                setResult(RESULT_OK, intent);

                finish();
                break;

            case R.id.imageButton_search:
                searchLocation();
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        mCurrentPosition = marker.getPosition();
        moveCamera(mCurrentPosition);

        try {
            List<Address> address = mGeocoder.getFromLocation(mCurrentPosition.latitude, mCurrentPosition.longitude, MAX_LEN);
            mCurrentAddress = address.get(0).getAddressLine(0);
            mSearchLocation.setText(mCurrentAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }
}
