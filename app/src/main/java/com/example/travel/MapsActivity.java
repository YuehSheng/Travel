package com.example.travel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.travel.databinding.ActivityMapsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private int MapType;

    public LocationManager locationManager;

    public String commadStr = LocationManager.GPS_PROVIDER;

    public FloatingActionButton myLoc;
    public FloatingActionButton changeMap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        /*boolean locHasGone = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean externalHasGone = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            if (!locHasGone && !externalHasGone) {//如果兩個權限都未取得
                permissions = new String[2];
                permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
                permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            } else if (!locHasGone) {//如果只有相機權限未取得
                permissions = new String[1];
                permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            } else if (!externalHasGone) {//如果只有存取權限未取得
                permissions = new String[1];
                permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            } else {

                return;
            }
            requestPermissions(permissions, 100);
        }*/


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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        myLoc = (FloatingActionButton) findViewById(R.id.myLoc);
        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);

                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(commadStr, 1000, 0,locationListener);
                Location location = locationManager.getLastKnownLocation(commadStr);
                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.pos);
                mMap.addMarker(new MarkerOptions().position(loc).icon(descriptor));

                //move camera to user's position
                final CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(13).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1500, new GoogleMap.CancelableCallback() {
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


    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

        }
    };

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        StringBuffer word = new StringBuffer();
//        switch (permissions.length) {
//            case 1:
//                if (permissions[0].equals(Manifest.permission.CAMERA)) word.append("位置權限");
//                else word.append("儲存權限");
//                if (grantResults[0] == 0) word.append("已取得");
//                else word.append("未取得");
//                word.append("\n");
//                if (permissions[0].equals(Manifest.permission.CAMERA)) word.append("儲存權限");
//                else word.append("位置權限");
//                word.append("已取得");
//
//                break;
//            case 2:
//                for (int i = 0; i < permissions.length; i++) {
//                    if (permissions[i].equals(Manifest.permission.CAMERA)) word.append("位置權限");
//                    else word.append("儲存權限");
//                    if (grantResults[i] == 0) word.append("已取得");
//                    else word.append("未取得");
//                    if (i < permissions.length - 1) word.append("\n");
//                }
//                break;
//        }
//        Toast.makeText(this, word.toString(), Toast.LENGTH_LONG).show();
//    }



    public void changeMapType(GoogleMap googleMap){
        mMap.setMapType((mMap.getMapType()%4)+1);
    }
}