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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import mosis.ivana.mustsee.DataModel.BasicFriendInfo;
import mosis.ivana.mustsee.DataModel.FriendsData;
import mosis.ivana.mustsee.DataModel.ListFriendsAdapter;

public class FriendsUniversalActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListFriendsAdapter friendsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_universal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ListView friendList= findViewById(R.id.universalFriendsList);
        friendsAdapter= new ListFriendsAdapter(new ArrayList<BasicFriendInfo>(),this);
        friendList.setAdapter(friendsAdapter);
        friendList.setOnItemClickListener(this);

        //getting user for which friends should be shown
        Intent intent= getIntent();
        String userId=intent.getStringExtra("UserId");

        DatabaseReference friends= FirebaseDatabase.getInstance().getReference().child("friendships").child( userId);
        friends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                friendsAdapter.add(new BasicFriendInfo(dataSnapshot.getKey(),dataSnapshot.getValue(FriendsData.class)));
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
        BasicFriendInfo info= (BasicFriendInfo) parent.getItemAtPosition(position);

        Intent i= new Intent(this, ProfileInfoActivity.class);
        i.putExtra("UserId",info.getId());
        startActivity(i);
    }
}
