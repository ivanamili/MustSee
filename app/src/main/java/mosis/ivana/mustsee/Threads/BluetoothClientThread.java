package mosis.ivana.mustsee.Threads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import mosis.ivana.mustsee.AppConstants;
import mosis.ivana.mustsee.DataModel.FriendRequestMessage;
import mosis.ivana.mustsee.DataModel.MessageTag;
import mosis.ivana.mustsee.HomeActivity;

public class BluetoothClientThread extends Thread {

    private final BluetoothDevice targetDevice;
    public final BluetoothSocket mSocket;

    //handler used to dispatch code to main thread
    Handler mainHandler;
    private Context context;

    //streams used for sendind and receiving data
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public BluetoothClientThread(BluetoothDevice targetDevice, Context context) {
        this.targetDevice = targetDevice;
        this.context=context;
        BluetoothSocket tmp = null;

        try {
            UUID uuid = UUID.fromString(AppConstants.APP_UUID);
            tmp = targetDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(AppConstants.BC_TAG, "Socket's create() method failed", e);
        }
        mSocket = tmp;
    }

    @Override
    public void run() {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mSocket.connect();
        } catch (IOException connectException) {
            closeSocket();
            return;
        }
        //connection successfully created

        //acquire streams
        acqireStreams();
        //send request
        sendFriendRequest();
    }

    private void acqireStreams(){
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

    private void sendFriendRequest(){
        FriendRequestMessage friendRequest= new FriendRequestMessage(HomeActivity.loggedUser.getUsername(),
                                                                    HomeActivity.loggedUser.getUserId(),
                                                                    HomeActivity.loggedUser.getProfilePhotoUrl());
        friendRequest.setTAG(MessageTag.FRIEND_REQUEST);
        int numBytes;

        mainHandler = new Handler(context.getMainLooper());
        try{
            //send request
            mmOutStream.write(friendRequest.serialize());
            //wait for answer, bocking call
            numBytes = mmInStream.read(mmBuffer);
            //deserializeAnswer
            FriendRequestMessage answer= FriendRequestMessage.deserialize(mmBuffer);
            //inform user about result
            Runnable informUser;
            if(answer.getTAG()==MessageTag.ACCEPTED){
                informUser=new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Friend request has been Accepted!",Toast.LENGTH_SHORT).show();
                    }
                };
            }
            else if (answer.getTAG()==MessageTag.DECLINED){
                informUser=new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Friend request has been Rejected!",Toast.LENGTH_SHORT).show();
                    }
                };
            }
            else{
                informUser=new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Something went wrong while sending friend request!",Toast.LENGTH_SHORT).show();
                    }
                };
            }
            //execute in main thread
            mainHandler.post(informUser);
            //close socket and end the thread
            closeSocket();

        }catch (IOException e){
            Log.d(AppConstants.BC_TAG, "There was error while transmitting data", e);
        }
        catch (ClassNotFoundException e){
            Log.d(AppConstants.BC_TAG, "There was error while deserializing friend request answer", e);
        }
    }

    public void closeSocket(){
        try {
            mSocket.close();
        } catch (IOException closeException) {
            Log.e(AppConstants.BC_TAG, "Could not close the client socket", closeException);
        }
    }
}
