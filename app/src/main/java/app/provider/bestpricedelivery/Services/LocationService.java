package app.provider.bestpricedelivery.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import app.provider.bestpricedelivery.Constants.Constants;

public class LocationService extends Service implements android.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    Location previousLocation;
    final int timeFactor = 10 * 1000;
    boolean isDestroy = false;
    private LocationManager locationManager;
    final float displacementFactor = 15;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isDestroy = true;

       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (mGoogleApiClient != null) {
            requestLocationUpdates();
        } else {
            buildGoogleApiClient();
        }
//        initializing();

//        Toast.makeText(this, "Location services started", Toast.LENGTH_SHORT).show();
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(timeFactor); // two minute interval
        mLocationRequest.setFastestInterval(timeFactor);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(displacementFactor);
//        mLocationRequest.setMaxWaitTime(timeFactor*2);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
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

    void initializing() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

//        System.out.println("mGoogleApiClient"+mGoogleApiClient);
        if (locationManager != null) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeFactor, 20, this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
       /* mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(timeFactor);
        mLocationRequest.setFastestInterval(timeFactor);
        mLocationRequest.setSmallestDisplacement(5);*/

        requestLocationUpdates();

        System.out.println("LocationService+++++++++onConnected" + "onConnected");


//        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                Location currentLocation = new Location("");

                currentLocation.setLatitude(location.getLatitude());
                currentLocation.setLongitude(location.getLongitude());


                if (previousLocation == null) {
                    previousLocation = currentLocation;
                }


                double displacement = CommonUtill.distance(previousLocation.getLatitude(), previousLocation.getLongitude(), currentLocation.getLatitude(), currentLocation.getLongitude());

                double speed = calculateSpeed(displacement);

                location.setSpeed((float) speed);


                if (location.hasAccuracy()) {
                    Intent intent = new Intent("LOCATION_ACTION");

                    intent.putExtra("lat", location.getLatitude());
                    intent.putExtra("long", location.getLongitude());
                    try {
                        Constants.updateLocation(getApplicationContext(), location);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("latitude==Longi=dr", location.getLatitude() + " " + location.getLongitude());
//                    if(/*isBetterLocation(location,previousLocation) &&*/ /*location.getSpeed()>3 *//*&& previousLocation.distanceTo(currentLocation)>15*//* &&*/ location.getAccuracy()<=15)
                    if (location.getAccuracy() <= 20) {
                        try {
                            Constants.updateLocation(getApplicationContext(), location);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(intent);
                        previousLocation = currentLocation;
                    } else {
                        try {
                            Constants.updateLocation(getApplicationContext(), location);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("LOCATION_ACTION1");
                        intent1.putExtra("lat", location.getLatitude());
                        intent1.putExtra("long", location.getLongitude());
                        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(intent1);
                    }

                }

            }
        }

        ;

    };


    @Override
    public void onDestroy() {
        isDestroy = true;

//        Toast.makeText(this, "Location services stopped", Toast.LENGTH_LONG).show();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
//        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
/*        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mLocationRequest = null;*/

        System.out.println("onDestroy" + "checkSelfPermission");
        super.onDestroy();
    }


    @Override
    public void onConnectionSuspended(int i) {
// Do nothing.
    }

    @Override
    public void onLocationChanged(Location location) {

      /*  if (isDestroy) {
            mLocationRequest = null;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }*/

//        if (prelat <= 0 || prelong <= 0) {
//            prelat = location.getLatitude();
//            prelong = location.getLongitude();
//        }

        System.out.println("LocationService+++++++++onLocationChanged" + location + "--isDestroy " + isDestroy);


        float acc = location.getAccuracy();
        float spd = location.getSpeed();


        Location currentLocation = new Location("");

        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());


        if (previousLocation == null) {
            previousLocation = currentLocation;
        }


        double displacement = CommonUtill.distance(previousLocation.getLatitude(), previousLocation.getLongitude(), currentLocation.getLatitude(), currentLocation.getLongitude());

        double speed = calculateSpeed(displacement);
        location.setSpeed((float) speed);

//        CommonUtill.showTost(getApplicationContext(), "displacement ==" + displacement + "speed == " + speed);

//        System.out.println("displacement ==" + location.getLatitude()+","+location.getLongitude());

//        if (displacement >= 10) {

        if (location.hasAccuracy()) {

            Intent intent = new Intent("LOCATION_ACTION");
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("long", location.getLongitude());


            Log.e("latitude==Longi", location.getLatitude() + " " + location.getLongitude());
            if (isBetterLocation(location, previousLocation) && location.getSpeed() > 3 && previousLocation.distanceTo(currentLocation) > 15 && location.getAccuracy() <= 15) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                previousLocation = currentLocation;
            }
        }

//        }

    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    long prevtime = System.currentTimeMillis();

    private int calculateSpeed(double displacement) {
        int speed = 0;
        long currentTime = System.currentTimeMillis();

//        System.out.println("calculateSpeed == displacement ==" + displacement);

        long second = (currentTime - prevtime) / 1000;
//        System.out.println("calculateSpeed == second ==" + second);
        speed = (int) (displacement / second);

//        System.out.println("calculateSpeed == prevtime ==" + prevtime);
//        System.out.println("calculateSpeed == currentTime ==" + currentTime);
//        System.out.println("calculateSpeed == speed ==" + speed);

        prevtime = System.currentTimeMillis();

        return speed;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
// Do nothing.
    }
}