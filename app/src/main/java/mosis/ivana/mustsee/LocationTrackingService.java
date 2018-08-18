package mosis.ivana.mustsee;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationTrackingService extends Service implements LocationListener {

    protected LocationManager locationManager;
    private long minTime = 10000; // frequency update: 10 seconds
    private float minDistance = 50; // frequency update: 50 meter
    private String locationProvider = LocationManager.NETWORK_PROVIDER;
    private String userId;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("geofire").child("locUsers");
    GeoFire geoFire = new GeoFire(ref);

    public LocationTrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        minTime=intent.getLongExtra("minTime",5000);
        minDistance=intent.getFloatExtra("minDistance",50);
        userId=intent.getStringExtra("userId");
        String pom= intent.getStringExtra("locationProvider");
        if(pom!=null && !pom.equals(""))
            locationProvider=pom;

        sendLocationUpdates();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void buildNotification() {
        String stop = "stop";/*
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);*/

        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                //.setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_tracking);
        startForeground(1, builder.build());
    }

    private void sendLocationUpdates() {
        //Run service in different thread
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(LocationTrackingService.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(LocationTrackingService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, LocationTrackingService.this);
                    Looper.loop();
                }
                catch (Exception e){
                    Log.e("TRACKING_SERVICE",
                            "Tracking service has failed to start listening for location updates."+e.getMessage());
                }
            }
        });
        serviceThread.start();
    }

    //**********************************
    //FROM LOCATION lISTENER INTERFACE
    //**********************************

    @Override
    public void onLocationChanged(Location location) {
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error!=null)
                {
                    Log.e("TRACKING_SERVICE","Writing location failed! Error: "+error);
                }
                else
                {
                    Log.e("TRACKING_SERVICE", "Location written in database sucessfully.");
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
}
