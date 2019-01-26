package com.vijay.saurabh.getconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vijay.saurabh.getconnect.OfficialCircle.CircleTestActivity;

import java.io.IOException;
import java.util.List;

public class MyNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    FirebaseAuth auth;
    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng;
    Marker marker;
    MarkerOptions options;
    LocationManager locationManager;
    FirebaseUser user;
    DatabaseReference firebaseDatabase, latlangReference;
    String current_username, current_useremail, current_imageurl;
    TextView tv_currentname, tv_currentcode;
    ImageView img;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //getSupportActionBar().hide();
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tv_currentname = header.findViewById(R.id.tv_name);
        tv_currentcode = header.findViewById(R.id.tv_code);
        img = header.findViewById(R.id.header_img);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = auth.getCurrentUser();

                current_username = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                current_useremail = dataSnapshot.child(user.getUid()).child("code").getValue(String.class);
                current_imageurl = dataSnapshot.child(user.getUid()).child("imageurl").getValue(String.class);

                tv_currentname.setText(current_username);
                tv_currentcode.setText(current_useremail);
                Picasso.get().load(current_imageurl).into(img);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        client = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        client.connect();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates("network", 1, 1, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (count >= 1)
            return;
        count++;
        double lat, lng;
        lat = location.getLatitude();
        lng = location.getLongitude();
        latLng = new LatLng(lat, lng);

        try {
            Geocoder geocoder = new Geocoder(MyNavigationActivity.this);
            List<Address> list = geocoder.getFromLocation(lat, lng, 1);
            String address = list.get(0).getAddressLine(0);
            //Toast.makeText(MyNavigationActivity.this, address, Toast.LENGTH_LONG).show();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            options = new MarkerOptions().position(latLng).title("Me");
            marker = mMap.addMarker(options);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mycircle) {

            Intent i = new Intent(MyNavigationActivity.this , CircleTestActivity.class);
            startActivity(i);

        }
        else if (id == R.id.nav_invite_members) {

        } else if (id == R.id.nav_shareloc) {

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT , "My Location is :"+"https://www.google.com/maps/search/?api=1&query="+latLng.latitude+","+latLng.longitude);
            startActivity(i.createChooser(i , "share using: "));


        } else if (id == R.id.nav_signout) {
            auth.signOut();
            Intent i = new Intent(MyNavigationActivity.this, MainActivity.class);
            startActivity(i);
            finish();


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(3000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(client , request , (LocationListener) this) ;


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location ==null)
        {
            Toast.makeText(this, "Could Not Get Locations", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(marker!=null)
            marker.remove();
            latLng = new LatLng(location.getLatitude() , location.getLongitude());
            options = new MarkerOptions().position(latLng);
            options.position(latLng);
            options.title("Current Location");
            marker = mMap.addMarker(options);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));

            double lat = location.getLatitude();
            double lang = location.getLongitude();
            user = auth.getCurrentUser();
            if(user != null) {
                //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
                latlangReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                latlangReference.child("lat").setValue(Double.toString(lat));
                latlangReference.child("lng").setValue(Double.toString(lang));
            }


        }

    }
}
