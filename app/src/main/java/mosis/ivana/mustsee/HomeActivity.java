package mosis.ivana.mustsee;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mosis.ivana.mustsee.DataModel.User;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    public static User loggedUser;
    public static GeoLocation userLocation;
    private DatabaseReference databaseRef;

    Intent locationServiceIntent;

    private CircleImageView profileImageView;
    TextView username;
    TextView points;
    DrawerLayout drawer;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        databaseRef= FirebaseDatabase.getInstance().getReference();

        locationServiceIntent= new Intent(this, LocationTrackingService.class);
        locationServiceIntent.putExtra("userId",mAuth.getCurrentUser().getUid());
        startService(locationServiceIntent);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        //setting on click listeners
        Button btnFriends= findViewById(R.id.bth_friends);
        btnFriends.setOnClickListener(this);

        final DatabaseReference logedUserDatabaseRef= databaseRef.child("users").child(mAuth.getCurrentUser().getUid());
        logedUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedUser= dataSnapshot.getValue(User.class);
                Picasso.get().load(loggedUser.getProfilePhotoUrl()).into(profileImageView);

                username.setText(loggedUser.getUsername());
                points.setText(String.valueOf(loggedUser.getXpPoints()));
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //starts activity for adding new place
                Intent i= new Intent(HomeActivity.this, AddPlaceActivity.class);
                startActivity(i);
            }
        });
        fab.setColorFilter(Color.WHITE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        username= headerView.findViewById(R.id.sideDrawerTxtUsername);
        points= headerView.findViewById(R.id.sideDrawerTxtExpPoints);

        profileImageView= headerView.findViewById(R.id.sideDrawerProfilePhoto);
        profileImageView.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            stopService(locationServiceIntent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        stopService(locationServiceIntent);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //implement selecting itmes from nav drawer
        if(id==R.id.nav_logout){
            mAuth.signOut();
            Intent i= new Intent(this,MainActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            this.finish();
        }
        else if (id==R.id.nav_friends){
            Intent i= new Intent(this,FriendListActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bth_friends)
        {
            Intent i= new Intent(this,FriendListActivity.class);
            startActivity(i);
        }
        else if (v.getId()==R.id.sideDrawerProfilePhoto)
        {
            Intent i= new Intent(this, ProfileInfoActivity.class);
            i.putExtra("UserId",HomeActivity.loggedUser.getUserId());
            startActivity(i);
        }
    }
}
