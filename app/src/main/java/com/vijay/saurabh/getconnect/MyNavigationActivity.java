package com.vijay.saurabh.getconnect;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vijay.saurabh.getconnect.Geofencing.SimpleGeofence;
import com.vijay.saurabh.getconnect.Geofencing.SimpleGeofenceStore;
import com.vijay.saurabh.getconnect.NearbyPlaces.Places;
import com.vijay.saurabh.getconnect.OfficialCircle.CircleTestActivity;
import com.vijay.saurabh.getconnect.directionhelpers.FetchURL;
import com.vijay.saurabh.getconnect.directionhelpers.PointsParser;
import com.vijay.saurabh.getconnect.directionhelpers.TaskLoadedCallback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,TaskLoadedCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,GoogleMap.OnMapClickListener {
    FirebaseAuth auth;
    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng,latLngplace;
    private Polyline currentPolyline;
    MarkerOptions options;
    LocationManager locationManager;
    FirebaseUser user;
    DatabaseReference firebaseDatabase, latlangReference;
    String current_username, current_useremail, current_imageurl;
    TextView tv_currentname, tv_currentcode;
    ImageView img;
    FloatingActionButton currentLocation,showPath;
    Double lat,lang;
    String place;
    private MarkerOptions place1, place2;
    ArrayList markerPoints = new ArrayList();
    int count = 0;
    private GeofencingClient mGeofencingClient;
    private Animation animShow;
    public static HashMap<Long, Marker> markerMap = new HashMap< Long, Marker>();
    public static HashMap<Marker, String> markerMapAddresses = new HashMap<Marker, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation);
        currentLocation=(FloatingActionButton)findViewById(R.id.current);
        showPath=(FloatingActionButton)findViewById(R.id.path);
        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        currentLocation.startAnimation(animShow);
        showPath.startAnimation(animShow);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //getSupportActionBar().hide();
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName());

                String placeDetailsStr = place.getName() + "\n"
                        + place.getId() + "\n"
                        + place.getLatLng().toString() + "\n"
                        + place.getAddress() + "\n"
                        + place.getAttributions();
                //txtPlaceDetails.setText(placeDetailsStr);

                LatLng placeThroughAC = place.getLatLng();
                String name = (String) place.getName();
                String addressPlace = (String) place.getAddress();
                showAddConfirmationDialog(placeThroughAC, name, addressPlace);
                //Toast.makeText(MapsActivity.this, placeDetailsStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        mGeofencingClient = LocationServices.getGeofencingClient(this);


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
        lat = getIntent().getDoubleExtra("lat",0);
        lang = getIntent().getDoubleExtra("lang",0);
        place = getIntent().getStringExtra("place");
        latLngplace = null;
        if(lat!=0 && lang!=0)
            latLngplace = new LatLng(lat,lang);

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
    private void showAddConfirmationDialog(final LatLng coordinates, final String name, final String addressOfLocation) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to?");
        builder.setNeutralButton("Zoom", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                //getLocationThroughAddress(address,0);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));

            }
        });
        builder.setPositiveButton("Add Marker", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                addPath(coordinates);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                //getLocation.setText("");

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();



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

        LatLng ald = new LatLng(25.492327, 81.866374);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ald));
        //mMap.setMaxZoomPreference(16.49f);
        mMap.setIndoorEnabled(true);
        mMap.setMinZoomPreference(15.0f);
        displayGeofences();

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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
                    mMap.addMarker(options);
                    addPath(latLng);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        addPath(latLng);
                    }
                });
                currentLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCurrentLocation();
                    }
                });
                showPath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (markerPoints.size() == 2) {
                            LatLng origin = (LatLng) markerPoints.get(0);
                            LatLng dest = (LatLng) markerPoints.get(1);
                            place1 = new MarkerOptions().position(origin).title("Location 1");
                            place2 = new MarkerOptions().position(dest).title("Location 2");
                            new FetchURL(MyNavigationActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "walking"), "walking");
                            // Getting URL to the Google Directions API
                            double valueResult = getdistance(origin,dest);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 17));
                            walking();
                        }
                    }
                });
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

        if(latLngplace!=null)
        {
            mMap.addMarker(new MarkerOptions().position(latLngplace).title(place).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngplace, 16));
            addPath(latLngplace);
        }

    }

    private void displayGeofences() {
        HashMap<String, SimpleGeofence> geofences = SimpleGeofenceStore
                .getInstance().getSimpleGeofences();

        for (Map.Entry<String, SimpleGeofence> item : geofences.entrySet()) {
            SimpleGeofence sg = item.getValue();

            CircleOptions circleOptions1 = new CircleOptions()
                    .center(new LatLng(sg.getLatitude(), sg.getLongitude()))
                    .radius(sg.getRadius()).strokeColor(Color.BLACK)
                    .strokeWidth(2).fillColor(0x500000ff);
            mMap.addCircle(circleOptions1);
        }

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

        }else if(id == R.id.places)
        {
            Intent i = new Intent(MyNavigationActivity.this,Places.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setFastestInterval(1000);
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
        if (location == null) {
            Toast.makeText(this, "Could Not Get Locations", Toast.LENGTH_SHORT).show();
        } else {
            //latLng = new LatLng(location.getLatitude(), location.getLongitude());
            double lat = location.getLatitude();
            double lang = location.getLongitude();
            user = auth.getCurrentUser();
            if (user != null) {
                //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
                latlangReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                latlangReference.child("lat").setValue(Double.toString(lat));
                latlangReference.child("lng").setValue(Double.toString(lang));
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    public void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, (LocationListener) this);
                markerPoints.clear();
                mMap.clear();
                displayGeofences();
            } else {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latLng = new LatLng(latitude, longitude);
                markerPoints.clear();
                mMap.clear();
                addPath(latLng);
                displayGeofences();
            }
        }
    }
    public void walking()
    {
        final MarkerOptions[] options = {new MarkerOptions().position(latLng).title("ME")};
        options[0].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        final Marker[] marker = {mMap.addMarker(options[0])};
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (markerPoints.size() >= 2) {
                    LatLng dest = (LatLng) markerPoints.get(1);
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    marker[0].remove();
                    options[0] = new MarkerOptions().position(latLng).title("ME");
                    options[0].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    marker[0] = mMap.addMarker(options[0]);
                    double valueResult = getdistance(latLng, dest);
                    int i;
                    for(i=0; i< PointsParser.points.size(); i++)
                    {
                        double dist = getdistance(latLng,PointsParser.points.get(i));
                        dist = round(dist,2);
                        if(latLng.equals(PointsParser.points.get(i)) || dist<=0.06)
                            break;
                    }
                    if(i==PointsParser.points.size() && PointsParser.points.size()>0)
                    {
                        addPath(latLng);
                        addPath(dest);
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                }
            }
        });
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        return url;
    }

    private void addPath(LatLng latLngcurr) {
        if(markerPoints.size()==2)
        {
            markerPoints.clear();
            mMap.clear();
            markerPoints.add(latLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngcurr,15));
            MarkerOptions options = new MarkerOptions();
            options.position(latLngcurr).title("ME");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }
        if (markerPoints.size() <= 1) {
            markerPoints.add(latLngcurr);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngcurr, 15));
            //txtTime.setText("0 sec");
            //txtDistance.setText("0 Km");
            MarkerOptions options = new MarkerOptions();
            // Setting the position of the marker
            options.position(latLngcurr);
            if (markerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (markerPoints.size() == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            if(latLngcurr==latLng)
                options.title("ME");

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);
        }
    }
    double getdistance(LatLng origin,LatLng dest)
    {
        int Radius = 6371;// radius of earth in Km
        double lat1 = origin.latitude;
        double lat2 = dest.latitude;
        double lon1 = origin.longitude;
        double lon2 = dest.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return valueResult;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

}
