package mosis.ivana.mustsee;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mosis.ivana.mustsee.DataModel.ListBluetoothAdapter;
import mosis.ivana.mustsee.Threads.BluetoothClientThread;

public class SearchForFriendsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    BluetoothAdapter mBluetoothAdapter;

    //ListBluetoothAdapter pairedListAdapter;
    ListBluetoothAdapter discoveredListAdapter;
    //ListView pairedList;
    ListView discoveredList;

    BluetoothClientThread sendRequestThread;

    ArrayList<BluetoothDevice> discoveredDevicesList;
    ProgressBar discoveryProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        discoveredDevicesList = new ArrayList<>();


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        //reading already paired devices
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        ArrayList<BluetoothDevice> pairedDevicesList= new ArrayList(pairedDevices);
//
//        //setting adapter and onItemClick listener for paired devices list
//        pairedListAdapter= new ListBluetoothAdapter(pairedDevicesList, getApplicationContext());
//        pairedList= findViewById(R.id.PairedDevicesList);
//        pairedList.setAdapter(pairedListAdapter);
//        pairedList.setOnItemClickListener(this);

        //setting adapter and onItemClick listener for discovered devices list
        discoveredListAdapter = new ListBluetoothAdapter(discoveredDevicesList,getApplicationContext());
        discoveredList= findViewById(R.id.DiscoveredDevicesList);
        discoveredList.setAdapter(discoveredListAdapter);
        discoveredList.setOnItemClickListener(this);

        //prepare for discovery
        IntentFilter bluetoothActionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, bluetoothActionFoundFilter);

        //register receiver for listening to discovery events
        IntentFilter filterStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoverStateChangedReceiver, filterStarted);
        registerReceiver(discoverStateChangedReceiver,filterFinished);

        //hide progressBar by default
        discoveryProgress = findViewById(R.id.SearchForDevicesProgressBar);
        discoveryProgress.setVisibility(View.INVISIBLE);

        //attach onClickListener to button
        Button search= findViewById(R.id.SearchFriendsActivityBtnSearch);
        search.setOnClickListener(this);
    }

    // BroadcastReceiver for ACTION_FOUND.
    //adds discovered device to adequate adapter (NOTE! add to adapter, not to list!)
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //not already in the list
                if(discoveredListAdapter.getPosition(device) == -1)
                    discoveredListAdapter.add(device);
            }
        }
    };
    // BroadcastReceiver for ACTION_DISCOVERY_STARTED and ACTION_DISCOVERY_FINISHED
    //controls visibility of progress bar that indicates whether discovery is in progress
    private final BroadcastReceiver discoverStateChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                discoveryProgress.setVisibility(View.VISIBLE);
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                discoveryProgress.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //detach receivers
        unregisterReceiver(mReceiver);
        unregisterReceiver(discoverStateChangedReceiver);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.SearchFriendsActivityBtnSearch:
            {
                mBluetoothAdapter.startDiscovery();
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //stop discover before attempting to create connection
        mBluetoothAdapter.cancelDiscovery();
        Toast.makeText(this,"Clicked element in "+parent.getId()+ " position "+position, Toast.LENGTH_SHORT).show();
        BluetoothDevice deviceToConnect;

//        if(parent.getId()== R.id.PairedDevicesList)
//            deviceToConnect= pairedListAdapter.getItem(position);
//        else

        deviceToConnect=discoveredListAdapter.getItem(position);

        //create client side connection
        //start thread
        sendRequestThread= new BluetoothClientThread(deviceToConnect, getApplicationContext());
        sendRequestThread.start();
    }
}
