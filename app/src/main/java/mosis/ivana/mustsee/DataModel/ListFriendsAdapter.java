package mosis.ivana.mustsee.DataModel;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mosis.ivana.mustsee.R;

public class ListFriendsAdapter extends ArrayAdapter<BasicFriendInfo> implements View.OnClickListener {

    private ArrayList<BasicFriendInfo> friends;
    Context mContext;

    public ListFriendsAdapter(ArrayList<BasicFriendInfo> data, Context context)
    {
        super(context, R.layout.friends_list_item,data);
        this.friends=data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BasicFriendInfo friendinfo= getItem(position);
        if(convertView==null)
            convertView= LayoutInflater.from(mContext).inflate(R.layout.friends_list_item,parent,false);

        TextView text= convertView.findViewById(R.id.friendListFriendUsername);
        text.setText(friendinfo.getBasicInfo().getFriendUserName());

        CircleImageView profileImageView=convertView.findViewById(R.id.friendListFriendPhoto);
        Picasso.get().load(friendinfo.getBasicInfo().getFirendPhotoURI()).into(profileImageView);
        return convertView;
    }


    @Override
    public void onClick(View v) {

    }
}
