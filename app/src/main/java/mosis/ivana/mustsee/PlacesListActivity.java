package mosis.ivana.mustsee;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mosis.ivana.mustsee.DataModel.ListPlacesAdapter;
import mosis.ivana.mustsee.DataModel.Place;

public class PlacesListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListPlacesAdapter placesAdapter;
    DatabaseReference dbReference;
    TextView noPlacesLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbReference= FirebaseDatabase.getInstance().getReference();
        noPlacesLabel= findViewById(R.id.placesListNoPlacesLabel);
        noPlacesLabel.setVisibility(View.VISIBLE);

        //create adapter
        placesAdapter = new ListPlacesAdapter(new ArrayList<Place>(), this);

        //set adapter to the list
        ListView placesList= findViewById(R.id.placesListList);
        placesList.setAdapter(placesAdapter);
        placesList.setOnItemClickListener(this);

        //get intent and load images
        Intent i = getIntent();
        String listType= i.getStringExtra("ListType");
        if(listType.equals("ALL"))
            LoadAllPlaces();
        else
        {
            String userId= i.getStringExtra("UserId");
            LoadUserPlaces(userId, listType);
        }
    }

    private void LoadUserPlaces(String userId, String listType) {
        DatabaseReference placesReference=dbReference.child("userPlaceRelations")
                .child(listType).child(userId);

        placesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LoadOnePlace(dataSnapshot.getKey());
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

    }

    private void LoadOnePlace(String placeId){
        DatabaseReference placeReference= dbReference.child("places").child(placeId);
        placeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placesAdapter.add(dataSnapshot.getValue(Place.class));
                noPlacesLabel.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void LoadAllPlaces() {
        DatabaseReference allPlaces= dbReference.child("places");
        allPlaces.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                placesAdapter.add(dataSnapshot.getValue(Place.class));
                noPlacesLabel.setVisibility(View.GONE);
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Place selectedPlace=(Place) parent.getItemAtPosition(position);

            Intent i=new Intent(this, PlaceInfoActivity.class);
            i.putExtra("placeId",selectedPlace.getPlaceID());
            startActivity(i);
    }
}
