package mosis.ivana.mustsee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

import mosis.ivana.mustsee.DataModel.FriendsData;

public class ShowUsersOnMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GeoQueryEventListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    HashMap<String, FriendsData> friends;
    HashMap<String, Marker> markers;
    HashMap<String, Bitmap> bitmaps;
    DatabaseReference dbReference;
    GeoFire locations;
    boolean onlyFriends;
    GeoQuery query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.usersMap);
        mapFragment.getMapAsync(this);

        onlyFriends=true;

        friends=new HashMap<>();
        markers=new HashMap<>();
        bitmaps=new HashMap<>();


        dbReference= FirebaseDatabase.getInstance().getReference().child("friendships").child(HomeActivity.loggedUser.getUserId());
        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                friends.put(dataSnapshot.getKey(),dataSnapshot.getValue(FriendsData.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RadioGroup radioGroup= findViewById(R.id.radioGroupMap);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                markers.clear();
                mMap.clear();

                if(checkedId == R.id.radioButtonFriendsOnly)
                    onlyFriends=true;
                else
                    onlyFriends=false;

                //remove listener from geoFire and reattach it
                query.removeAllListeners();
                query.addGeoQueryEventListener(ShowUsersOnMapActivity.this);
            }
        });

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("geofire").child("locUsers");
        locations= new GeoFire(ref);
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
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera
        Location loc= LocationTrackingService.CurrentLocation;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
        //get center of the map
        LatLng newCenter= mMap.getCameraPosition().target;

        //get radius
        VisibleRegion visibleReg= mMap.getProjection().getVisibleRegion();
        float[] radius= new float[5];
        Location.distanceBetween(visibleReg.farLeft.latitude,visibleReg.farLeft.longitude,
                visibleReg.nearRight.latitude,visibleReg.nearRight.longitude,radius);
        query= locations.queryAtLocation(new GeoLocation(newCenter.latitude,newCenter.longitude),radius[0]/1000);
        query.removeAllListeners();
        query.addGeoQueryEventListener(this);

    }

    @Override
    public void onKeyEntered(final String key, GeoLocation location) {
        if(onlyFriends && !friends.containsKey(key))
            return;
        else if (friends.containsKey(key))
        {
            if(markers.containsKey(key))
            {
                markers.get(key).remove();
                markers.remove(key);
            }
            LatLng position= new LatLng(location.latitude,location.longitude);
            Marker newMarker=mMap.addMarker(new MarkerOptions().position(position));
            newMarker.setTag(key);
            markers.put(key,newMarker);

            //load image
            Picasso.get().load(friends.get(key).getFirendPhotoURI()).resize(70,70).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    bitmaps.put(key,bitmap);
                    markers.get(key).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        else
        {
            if(markers.containsKey(key))
                return;
            LatLng position= new LatLng(location.latitude,location.longitude);
            Marker newMarker= mMap.addMarker(new MarkerOptions().position(position));
            newMarker.setTag(key);
            if(key.equals(HomeActivity.loggedUser.getUserId()))
                newMarker.setTitle("My location");
            markers.put(key,newMarker);
        }
    }

    @Override
    public void onKeyExited(String key) {
        Marker m= markers.get(key);
        //that is non friend user in only friend mode
        if(m==null)
            return;
        markers.get(key).remove();
        markers.remove(key);
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Marker m= markers.get(key);
        //that is non friend user in only friend mode
        if(m==null)
            return;
        markers.get(key).setPosition(new LatLng(location.latitude,location.longitude));
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    @Override
    public void onCameraMove() {
        LatLng newCenter= mMap.getCameraPosition().target;
        VisibleRegion visibleReg= mMap.getProjection().getVisibleRegion();
        float[] radius= new float[5];
        Location.distanceBetween(visibleReg.farLeft.latitude,visibleReg.farLeft.longitude,
                visibleReg.nearRight.latitude,visibleReg.nearRight.longitude,radius);

        query.setCenter(new GeoLocation(newCenter.latitude,newCenter.longitude));
        query.setRadius(radius[0]/1000);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent i= new Intent(this, ProfileInfoActivity.class);
        i.putExtra("UserId",(String)marker.getTag());
        startActivity(i);
        return true;
    }
}
