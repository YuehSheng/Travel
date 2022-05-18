package com.example.travel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.travel.databinding.ActivityMapsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.security.Permission;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private int MapType;

    public LocationManager locationManager;
    public CameraPosition cameraPosition;
    public Location curPos;
    Dialog dialog;
    AlertDialog alertDialog;
    EditText title,snippet;
    Button add,cancel;

    public String commandStr = LocationManager.GPS_PROVIDER;

    public FloatingActionButton myLoc;
    public Circle curLoc;
    public FloatingActionButton changeMap;
    public FloatingActionButton addMarker;
    public Marker marker;

    /*1 => add marker
    * 2 => set marker
    * */
    public int state = 1;

//    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapType = GoogleMap.MAP_TYPE_NORMAL;

        curLoc = mMap.addCircle(new CircleOptions().center(new LatLng(24.178043381577726, 120.64712031103305)).radius(100).fillColor(0xff00dfff).strokeWidth(3).strokeColor(Color.CYAN));;
        curLoc.setVisible(false);

        LatLng feng = new LatLng(24.178043381577726, 120.64712031103305);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        cameraPosition = new CameraPosition.Builder().target(feng).zoom(13).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        //loc button
        myLoc = (FloatingActionButton) findViewById(R.id.myLoc);
        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    ActivityCompat.requestPermissions(MapsActivity.this,new String[]{ACCESS_FINE_LOCATION}, 12);

                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(commandStr, 1000, 0,locationListener);
                Location location = locationManager.getLastKnownLocation(commandStr);
                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
                System.out.println(loc);
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.pos);
//                mMap.addMarker(new MarkerOptions().position(loc).icon(descriptor));
                curLoc.setCenter(loc);
                curLoc.setVisible(true);


                //move camera to user's position
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(loc).zoom(mMap.getCameraPosition().zoom).build()), 1500, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
            }


        });


        changeMap = (FloatingActionButton) findViewById(R.id.changeMap);
        changeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMapType(mMap);
            }
        });

        addMarker = (FloatingActionButton) findViewById(R.id.addMarker);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == 1){//add
                    BitmapDescriptor descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    marker = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).icon(descriptor));
                    marker.setDraggable(true);
                    state = 2;
                    addMarker.setImageResource(android.R.drawable.ic_menu_save);
                }
                else if(state == 2){//set
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                    //show dialog and set title,snippet here

                    addMarker.setImageResource(android.R.drawable.ic_menu_add);
                    marker.setDraggable(false);
                    state = 1;
                }

            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//            Circle oldLoc = curLoc;
            LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
            float zoomSize = mMap.getCameraPosition().zoom;
            double PX = 5*156543.03392 * Math.cos(location.getLatitude() * Math.PI / 180) / Math.pow(2, zoomSize);
            curLoc.setRadius(PX);
//            System.out.println(cameraPosition);
        }
    };



    public void changeMapType(GoogleMap googleMap){
        mMap.setMapType((mMap.getMapType()%4)+1);
    }
}
