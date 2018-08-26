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

public class SearchForFriendsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    BluetoothAdapter mBluetoothAdapter;
    ListBluetoothAdapter pairedListAdapter;
    ListBluetoothAdapter discoveredListAdapter;
    ListView pairedList;
    ListView discoveredList;
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
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> pairedDevicesList= new ArrayList(pairedDevices);

        pairedListAdapter= new ListBluetoothAdapter(pairedDevicesList, getApplicationContext());
        pairedList= findViewById(R.id.PairedDevicesList);
        pairedList.setAdapter(pairedListAdapter);
        pairedList.setOnItemClickListener(this);

        discoveredListAdapter = new ListBluetoothAdapter(discoveredDevicesList,getApplicationContext());
        discoveredList= findViewById(R.id.DiscoveredDevicesList);
        discoveredList.setAdapter(discoveredListAdapter);
        discoveredList.setOnItemClickListener(this);

        //prepare for discovery
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        discoveryProgress = findViewById(R.id.SearchForDevicesProgressBar);
        discoveryProgress.setVisibility(View.GONE);

        //attach onClickListener
        Button search= findViewById(R.id.SearchFriendsActivityBtnSearch);
        search.setOnClickListener(this);


    }

    // BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredListAdapter.add(device);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.SearchFriendsActivityBtnSearch:
            {

                discoveryProgress.setVisibility(View.VISIBLE);
                mBluetoothAdapter.startDiscovery();
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mBluetoothAdapter.cancelDiscovery();
        Toast.makeText(this,"Clicked element in "+view.getId()+ " position "+position, Toast.LENGTH_SHORT).show();
        discoveryProgress.setVisibility(View.GONE);
    }
}
