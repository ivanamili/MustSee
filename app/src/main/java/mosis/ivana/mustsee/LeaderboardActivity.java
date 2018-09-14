package mosis.ivana.mustsee;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

import mosis.ivana.mustsee.DataModel.LeaderboardListAddapter;
import mosis.ivana.mustsee.DataModel.User;

public class LeaderboardActivity extends AppCompatActivity {

    LeaderboardListAddapter addapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addapter= new LeaderboardListAddapter(new ArrayList<User>(),this);
        ListView list=findViewById(R.id.leaderboardView);
        list.setAdapter(addapter);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                            addapter.remove(childSnapshot.getValue(User.class));
                            addapter.add(childSnapshot.getValue(User.class));
                            addapter.sort(new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) {
                                    return o2.getXpPoints()-o1.getXpPoints();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
