package mosis.ivana.mustsee.DataModel;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

import mosis.ivana.mustsee.R;

public class ListBluetoothAdapter extends ArrayAdapter<BluetoothDevice> implements OnClickListener {

    private ArrayList<BluetoothDevice> devices;
    Context mContext;

    public ListBluetoothAdapter(ArrayList<BluetoothDevice> data, Context context)
    {
        super(context, R.layout.bluetooth_devices_list,data);
        this.devices=data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothDevice dev= getItem(position);

        if(convertView==null)
            convertView=LayoutInflater.from(mContext).inflate(R.layout.bluetooth_devices_list,parent,false);

        TextView text= convertView.findViewById(R.id.bluetoothDeviceName);
        text.setText(dev.getName());

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }
}
