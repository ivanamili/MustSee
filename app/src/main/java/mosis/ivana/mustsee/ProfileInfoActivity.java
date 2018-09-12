package mosis.ivana.mustsee;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mosis.ivana.mustsee.DataModel.User;

public class ProfileInfoActivity extends AppCompatActivity implements ValueEventListener, View.OnClickListener{

    CircleImageView profileView;
    TextView btnSeeFriends;
    TextView btnSeeAddedPlaces;
    TextView btnSeeVisitedPlaces;

    //User for which data is shown
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent= getIntent();
        String userId=intent.getStringExtra("UserId");

        //get important views
        profileView = findViewById(R.id.profieInfoProfilePhoto);

        btnSeeFriends= findViewById(R.id.profileInfoBtnSeeFriends);
        btnSeeFriends.setOnClickListener(this);

        btnSeeAddedPlaces = findViewById(R.id.profileInfoBtnSeeAddedPlaces);
        btnSeeAddedPlaces.setOnClickListener(this);

        btnSeeVisitedPlaces = findViewById(R.id.profileInfoBtnSeeVisitedPlaces);
        btnSeeVisitedPlaces.setOnClickListener(this);

        DatabaseReference user= FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        user.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        user=dataSnapshot.getValue(User.class);
        Picasso.get().load(user.getProfilePhotoUrl()).into(profileView);

        TextView username= findViewById(R.id.profileInfoUserName);
        username.setText(user.getUsername());

        TextView xpPoints= findViewById(R.id.profileInfoXpPoints);
        xpPoints.setText(String.valueOf(user.getXpPoints()));

        TextView fullName = findViewById(R.id.profileInfoFullName2);
        fullName.setText(user.getFullName());

        TextView email= findViewById(R.id.profileInfoEmail);
        email.setText(user.getEmail());

        TextView joined = findViewById(R.id.profileInfoJoinedDate);
        joined.setText(user.getJoined());

        TextView friendsCount= findViewById(R.id.profileInfoFriendsCount);
        friendsCount.setText(String.valueOf(user.getFriendsCount()));

        TextView placesAddedCount= findViewById(R.id.profileInfoPlacesAddedCount);
        placesAddedCount.setText(String.valueOf(user.getPlacesAddedCount()));

        TextView placesVisitedCount =findViewById(R.id.profileInfoPlacesVisitedCount);
        placesVisitedCount.setText(String.valueOf(user.getPlacesVisitedCount()));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onClick(View v) {
        //show adequate lists of friends or places
        switch (v.getId()){
            case R.id.profileInfoBtnSeeFriends:
            {
                Intent i= new Intent(this, FriendsUniversalActivity.class);
                i.putExtra("UserId",user.getUserId());
                startActivity(i);
                break;
            }
            case R.id.profileInfoBtnSeeAddedPlaces: {
                Intent i = new Intent(this, PlacesListActivity.class);
                i.putExtra("ListType", "addedPlaces");
                i.putExtra("UserId", user.getUserId());
                startActivity(i);
                break;
            }
            case R.id.profileInfoBtnSeeVisitedPlaces:{
                Intent i = new Intent(this, PlacesListActivity.class);
                i.putExtra("ListType", "visitedPlaces");
                i.putExtra("UserId", user.getUserId());
                startActivity(i);
                break;
            }
        }
    }
}
