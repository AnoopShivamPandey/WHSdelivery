package app.provider.bestpricedelivery.Tender;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Locale;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.GlobalAppApis;
import app.provider.bestpricedelivery.OtpScreen;
import app.provider.bestpricedelivery.R;
import app.provider.bestpricedelivery.Services.LocationService;
import app.provider.bestpricedelivery.Services.StallInventory;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class MainActivityForTender extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    ImageView ownersImage, settingsBtn;
    Activity mActivity;
    RelativeLayout bookingLayout;
    ImageView logoutBtn;
    TextView bookingsCount, ownerName;
    RelativeLayout stallListing;
    TextView employeeCount;

    void initialize() {
        employeeCount = findViewById(R.id.employeeCount);
        ownerName = findViewById(R.id.ownerName);
        stallListing = findViewById(R.id.stallListing);
        ownersImage = findViewById(R.id.ownersImage);
        bookingsCount = findViewById(R.id.bookingsCount);
        bookingLayout = findViewById(R.id.bookingLayout);
        bookingsCount = findViewById(R.id.bookingsCount);
        settingsBtn = findViewById(R.id.settingsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        Glide.with(mActivity)
                .load(Constants.getProfileImage(mActivity))
                .into(ownersImage);
        bookingLayout.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        stallListing.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    Bitmap stallBmp = null;
    Bitmap restaurantBmp = null;

    class ConvertImageAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            URL url1 = null;
            URL url2 = null;
            try {
                url1 = new URL("http://office.a2hosted.com/myTreat/public/assets/images/map_pointer.png");
                url2 = new URL("http://office.a2hosted.com/myTreat/public/assets/images/map_pointer.png");
                stallBmp = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                restaurantBmp = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        initialize();
        new ConvertImageAsync().execute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(MainActivityForTender.this, LocationService.class));
                }
            }, 1000);
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
//        getStallList();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
//            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(MainActivityForTender.this, LocationService.class));
                }
            }, 1000);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startService(new Intent(MainActivityForTender.this, LocationService.class));
                            }
                        }, 1000);
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMarkers = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("LOCATION_ACTION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("LOCATION_ACTION1"));
        updateWorkerStatus();
//        getStallList();

    }

    void updateWorkerStatus() {
        try {
            JSONObject jsonObject = new JSONObject(Constants.getTenderDetails(mActivity));
            if (jsonObject.getBoolean("status")) {
                if (jsonObject.has("data") && jsonObject.getJSONObject("data").has("total_bookings")) {
                    bookingsCount.setText(jsonObject.getJSONObject("data").getString("total_bookings"));
                }
                ownerName.setText(jsonObject.getJSONObject("data").getString("full_name"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    boolean showMarkers = true;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            mMap.clear();

//            Toast.makeText(context, "" + intent.getDoubleExtra("lat", 0), Toast.LENGTH_SHORT).show();
            if (!showMarkers) {
                return;
            }
            LatLng latLng = new LatLng(intent.getDoubleExtra("lat", 0),
                    intent.getDoubleExtra("long", 0));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(intent.getDoubleExtra("long", 0) + "");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.marker_image);
            if (stallBmp == null) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(stallBmp));
            }
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            showMarkers = false;
//            updateLocation();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookingLayout:
                startActivity(new Intent(mActivity, BookingListingForTender.class));
                break;
            case R.id.settingsBtn:
                startActivity(new Intent(mActivity, StallInventory.class));
                break;
            case R.id.stallListing:
                startActivity(new Intent(mActivity, StallList.class));
                break;
            case R.id.logoutBtn:
                showLogoutPopup();
                break;
        }
    }

    void getStallList() {
        try {
            String otp = new GlobalAppApis().getStallList(this);
            ApiService client = getApiClient_ByPost();
            Call<String> call = client.getStallList(Constants.getAPIToken(mActivity), otp);
            new ConnectToRetrofit(new RetrofitCallBackListenar() {
                @Override
                public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("status")) {
//                        Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        if (jsonObject.getJSONArray("data").length() == 0) {
                        } else {
                            setMarkers(jsonObject);

                        }
                    } else {
                        Toast.makeText(mActivity, "Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }

            }, mActivity, call, "", true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setWorkersCount(JSONObject markersJSON) {
        try {
            int totalOnline = 0;
            JSONArray resultArray = markersJSON.getJSONArray("data");
            for (int i = 0; i < resultArray.length(); i++) {
                if (resultArray.getJSONObject(i).getJSONObject("stall").getString("profile_status").equalsIgnoreCase("online")) {
                    totalOnline++;
                }
            }
            employeeCount.setText(totalOnline + "/" + resultArray.length());
        } catch (Exception e) {
            e.printStackTrace();
            employeeCount.setText(0 / 0);
        }
    }

    void setMarkers(JSONObject markersJSON) {
        try {
            setWorkersCount(markersJSON);
            try {
                int totalOnline = 0;
                JSONArray resultArray = markersJSON.getJSONArray("data");
                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject jsonObject = resultArray.getJSONObject(i).getJSONObject("stall");
                    createMarker(Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude")),
                            jsonObject.getString("full_name"),
                            jsonObject.getString("profile_status"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {
    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cart);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(icon));
    }

    void showLogoutPopup() {
        try {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mActivity);
            }
            builder.setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            logUserOut();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.drawable.app_logo)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void logUserOut() {
        try {
            String otp = new GlobalAppApis().getLogoutDetailsForTender(this);
            ApiService client = getApiClient_ByPost();
            Call<String> call = client.logoutUser(Constants.getAPIToken(mActivity), otp);
            new ConnectToRetrofit(new RetrofitCallBackListenar() {
                @Override
                public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        Constants.clearAllSavedPreferences(mActivity);
                        startActivity(new Intent(mActivity, OtpScreen.class));
                        finish();
                    } else {
                        Toast.makeText(mActivity, "Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }

            }, mActivity, call, "", true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}