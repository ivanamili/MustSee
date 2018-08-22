package mosis.ivana.mustsee;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FriendListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BLUETOOTH_FOR_DISCOVERY = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH_FOR_SEARCH = 2;

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set on click listeners
        findViewById(R.id.friendsActivityBtnDiscoverable).setOnClickListener(this);
        findViewById(R.id.friendsActivityBtnSearchFriends).setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        //buttons that work with bluetooth
        //put all the code for other buttons before this section
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Your device doesn't support Bluetooth!",Toast.LENGTH_SHORT).show();
            return;
        }


        switch (v.getId())
        {
            case R.id.friendsActivityBtnDiscoverable:
            {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH_FOR_DISCOVERY);
                    return;
                }
                makeDeviceDiscoverable();
                break;
            }

            case R.id.friendsActivityBtnSearchFriends:
            {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH_FOR_SEARCH);
                    return;
                }
                searchForFriends();
                break;
            }
        }
    }

    private void searchForFriends()
    {
        Intent i = new Intent(this, SearchForFriendsActivity.class);
        startActivity(i);
    }

    private void makeDeviceDiscoverable()
    {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
        startActivity(discoverableIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_ENABLE_BLUETOOTH_FOR_DISCOVERY)
            {
                makeDeviceDiscoverable();
                return;
            }
            if(requestCode==REQUEST_ENABLE_BLUETOOTH_FOR_SEARCH)
            {
                searchForFriends();
            }
        }
        else
        {
            Log.e("FRIENDS_ACTIVITY","Bluetooth not enabled!");
        }

    }
}
