package mosis.ivana.mustsee.Threads;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MulticastSocket;
import java.util.UUID;

import mosis.ivana.mustsee.AppConstants;
import mosis.ivana.mustsee.DataModel.FriendRequestMessage;
import mosis.ivana.mustsee.DataModel.MessageTag;
import mosis.ivana.mustsee.DataModel.UserResponse;
import mosis.ivana.mustsee.FriendListActivity;

public class BluetoothServerConnectionThread extends Thread {

    private BluetoothAdapter mBluetoothAdapter;
    //socket to listen on
    public BluetoothServerSocket mmServerSocket;
    public BluetoothSocket mSocket;

    //handler used to communicate with main thread
    Handler mainHandler;
    Context context;

    //streams used for sending and receiving data
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public BluetoothServerConnectionThread(Context context) {
        this.context=context;
        //get adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSocket = null;
        BluetoothServerSocket tmp = null;
        try {
            UUID uuid = UUID.fromString(AppConstants.APP_UUID);
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(AppConstants.APP_NAME, uuid);
        } catch (IOException e) {
            Log.e(AppConstants.BS_TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //previous connection hasn't finished with streams, wait until mSocket is null
                if(mSocket!=null)
                    continue;
                //listen for connection, this is blocking call
                mSocket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(AppConstants.BS_TAG, "Socket's accept() method failed", e);
                //probably someone closed server socket from the outside, so this thread should finish
                break;
            }

            //connection successfully created, begin friendship protocol
            //more blocking calls inside
            if (mSocket != null) {
                //acquire streams
                acquireStreams();
                //acceptFriendRequest
                acceptFriendRequest(mmOutStream);
            }
        }
    }

    private void acquireStreams(){
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = mSocket.getInputStream();
        } catch (IOException e) {
            Log.e(AppConstants.BS_TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(AppConstants.BS_TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    private void acceptFriendRequest(OutputStream outStream){
        //wait for request
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        try{
            numBytes = mmInStream.read(mmBuffer);
            final FriendRequestMessage message= FriendRequestMessage.deserialize(mmBuffer);
            if(message.getTAG()== MessageTag.FRIEND_REQUEST){
                //alert the user of incoming friend request
                Runnable promtUser = new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("User "+message.getUserName()+" sent you friend request.")
                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FriendListActivity.bluetoothServerThread.handleUserResponseToRequest(UserResponse.ACCEPTED);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FriendListActivity.bluetoothServerThread.handleUserResponseToRequest(UserResponse.DECLINED);
                                    }
                                })
                                .create();
                    }
                };
                mainHandler=new Handler(context.getMainLooper());
                mainHandler.post(promtUser);
            }
        }
        catch (IOException e){
            Log.d(AppConstants.BS_TAG, "There was error while transmitting data", e);
        }
        catch (ClassNotFoundException e){
            Log.d(AppConstants.BS_TAG, "There was error while deserializing friend request", e);
        }

    }

    public void closeServerSocket() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(AppConstants.BS_TAG, "Could not close the server socket", e);
        }
    }

    public void handleUserResponseToRequest(UserResponse responseCode){
        //user has accepted friend request
        //write to database
        if (responseCode==UserResponse.ACCEPTED){

        }
        //user has declined friend request
        else if (responseCode==UserResponse.DECLINED){

        }
    }


}
