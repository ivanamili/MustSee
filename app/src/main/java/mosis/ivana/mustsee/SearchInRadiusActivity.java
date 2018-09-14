package mosis.ivana.mustsee;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import mosis.ivana.mustsee.DataModel.User;

public class SearchInRadiusActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    HashMap<String, Marker> markers;
    GeoFire locations;
    GeoQuery query;
    User loggedUser;
    double currentLatitude;
    double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_in_radius);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.inRadiusMap);
        mapFragment.getMapAsync(this);

        loggedUser= HomeActivity.loggedUser;
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("geofire").child("locPlaces");
        locations= new GeoFire(ref);

        markers=new HashMap<>();

        Button search= findViewById(R.id.searcByRadius);
        search.setOnClickListener(this);
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
        mMap.setOnMarkerClickListener(this);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("geofire").child("locUsers")
                .child(loggedUser.getUserId()).child("l");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Object> coordinates=(ArrayList<Object>) dataSnapshot.getValue();
                currentLatitude=(double)coordinates.get(0);
                currentLongitude=(double)coordinates.get(1);
                if(query!=null)
                    query.setCenter(new GeoLocation(currentLatitude,currentLongitude));
                if(markers.containsKey("MYLOCATION"))
                {
                    markers.get("MYLOCATION").setPosition(new LatLng(currentLatitude,currentLongitude));
                }
                else
                {
                    Marker newMarker= mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(currentLatitude,currentLongitude)).title("My Location"));
                    newMarker.setTag("MYLOCATION");
                    markers.put("MYLOCATION",newMarker );
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.searcByRadius)
        {
            EditText radius= findViewById(R.id.radiusEdit);
            String  rad=radius.getText().toString();
            double radiusDouble= Double.parseDouble(rad);
            if(query==null)
            {
                query=locations.queryAtLocation(new GeoLocation(currentLatitude,currentLongitude),radiusDouble);
                query.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Marker newMarker= mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.latitude,location.longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mouseum)));
                        newMarker.setTag(key);
                        markers.put(key, newMarker);
                    }

                    @Override
                    public void onKeyExited(String key) {
                        markers.get(key).remove();
                        markers.remove(key);
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                        markers.get(key).setPosition(new LatLng(location.latitude,location.longitude));
                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });
            }
            else
            {
                query.setRadius(radiusDouble);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(((String)marker.getTag()).equals("MYLOCATION"))
            return true;
        Intent i= new Intent(this, PlaceInfoActivity.class);
        i.putExtra("placeId",(String)marker.getTag());
        startActivity(i);
        return true;    }
}
