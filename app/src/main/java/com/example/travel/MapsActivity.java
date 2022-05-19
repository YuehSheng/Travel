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
import android.content.DialogInterface;
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
    AlertDialog.Builder alertDialog;
    EditText title,date;
    Button add,cancel;

    public String commandStr = LocationManager.GPS_PROVIDER;

    public FloatingActionButton myLoc;
    public Circle curLoc;
    public FloatingActionButton changeMap;
    public FloatingActionButton addMarker;
    public FloatingActionButton cancelMarker;
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

        initAddMarkerDialog();
        initRemoveMarkerDialog();

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
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapType = GoogleMap.MAP_TYPE_NORMAL;
        mMap = googleMap;
        curLoc = googleMap.addCircle(new CircleOptions().center(new LatLng(24.178043381577726, 120.64712031103305)).radius(100).fillColor(0xff00dfff).strokeWidth(3).strokeColor(Color.CYAN));;
        curLoc.setVisible(false);

        LatLng feng = new LatLng(24.178043381577726, 120.64712031103305);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        cameraPosition = new CameraPosition.Builder().target(feng).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        initInfoWindowClick(googleMap);
//        initMapClick(googleMap);

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
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(loc).zoom(googleMap.getCameraPosition().zoom).build()), 1500, new GoogleMap.CancelableCallback() {
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
                changeMapType(googleMap);
            }
        });

        addMarker = (FloatingActionButton) findViewById(R.id.addMarker);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerButton(googleMap);
            }
        });

        cancelMarker = (FloatingActionButton) findViewById(R.id.cancelMarker);
        cancelMarker.hide();
        cancelMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.remove();
                addMarker.setImageResource(android.R.drawable.ic_menu_add);
                state = 1;
                cancelMarker.hide();
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

    //function about adding and setting marker
    public void addMarkerButton(GoogleMap googleMap){
        if(state == 1){//add
            BitmapDescriptor descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            marker = googleMap.addMarker(new MarkerOptions().position(googleMap.getCameraPosition().target).icon(descriptor));
            marker.setDraggable(true);
            state = 2;
            cancelMarker.show();
            addMarker.setImageResource(android.R.drawable.ic_menu_save);
        }
        else if(state == 2){//set

            //show dialog and set title,snippet here
            title.setText("");
            date.setText("");
            dialog.show();
            add.setOnClickListener(view -> {
                if(title.getText().toString().isEmpty() || date.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Title fields can't be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    marker.setDraggable(false);
                    marker.setTitle(title.getText().toString());
                    marker.setSnippet(date.getText().toString());
                    addMarker.setImageResource(android.R.drawable.ic_menu_add);
                    state = 1;
                    cancelMarker.hide();
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });



        }
    }

    public void changeMapType(GoogleMap googleMap){
        mMap.setMapType((mMap.getMapType()%4)+1);
    }

    public void initAddMarkerDialog(){
        dialog = new Dialog(this);
        dialog.setTitle("Add marker!");
        dialog.setContentView(R.layout.dialog_add);
        title = (EditText) dialog.findViewById(R.id.title);
        date = (EditText) dialog.findViewById(R.id.date);
        add = (Button) dialog.findViewById(R.id.add);
        cancel = (Button) dialog.findViewById(R.id.cancel);
    }

    public void initRemoveMarkerDialog(){
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete Marker !");
        alertDialog.setMessage("Do you want to delete the Marker!");
    }

    public void initInfoWindowClick(GoogleMap googleMap){
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        marker.remove();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }

        });
    }

}
