package mosis.ivana.mustsee;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import mosis.ivana.mustsee.DataModel.Place;

public class PlaceInfoActivity extends AppCompatActivity implements View.OnClickListener {

    Place currentPlace;
    DatabaseReference placeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String placeId= i.getStringExtra("placeId");

        ImageView heart= findViewById(R.id.placeInfoHeart);
        heart.setOnClickListener(this);


        placeReference= FirebaseDatabase.getInstance().getReference().child("places").child(placeId);
        placeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentPlace=dataSnapshot.getValue(Place.class);

                ImageView picture= findViewById(R.id.placeInfoPicture);
                Picasso.get().load(currentPlace.getPhotoURL()).into(picture);

                TextView likecount= findViewById(R.id.placeInfoLikeCount);
                likecount.setText(String.valueOf(currentPlace.getLikeCount()));

                TextView commentCount= findViewById(R.id.placeInfoCommentCount);
                commentCount.setText(String.valueOf(currentPlace.getCommentCount()));

                TextView name= findViewById(R.id.placeInfoName);
                name.setText(currentPlace.getName());

                TextView country=findViewById(R.id.placeInfoCountry);
                country.setText(currentPlace.getCountry());

                TextView town= findViewById(R.id.placeInfoTown);
                town.setText(currentPlace.getCity());

                TextView category= findViewById(R.id.placeInfoCategory);
                category.setText(currentPlace.getCategoty().toString());

                TextView description= findViewById(R.id.placeInfoDescription);
                description.setText(currentPlace.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.placeInfoHeart:{
                final DatabaseReference checkRef=FirebaseDatabase.getInstance().getReference()
                        .child("likes").child(currentPlace.getPlaceID()).child(HomeActivity.loggedUser.getUserId());
                checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null)
                            return;

                        checkRef.setValue(true);

                        final DatabaseReference ref= placeReference.child("likeCount");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int oldCount= dataSnapshot.getValue(Integer.class);
                                ref.setValue(oldCount+1);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        return;


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }
    }
}
