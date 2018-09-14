package mosis.ivana.mustsee.DataModel;

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

public class LeaderboardListAddapter extends ArrayAdapter<User>{
    private ArrayList<User> users;
    Context mContext;

    public LeaderboardListAddapter(ArrayList<User> data, Context context)
    {
        super(context, R.layout.leaderboard_list_item,data);
        this.users=data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User userInfo= getItem(position);
        if(convertView==null)
            convertView= LayoutInflater.from(mContext).inflate(R.layout.leaderboard_list_item,parent,false);

        TextView text= convertView.findViewById(R.id.leaderboardFriendUsername);
        text.setText(userInfo.getUsername());
        TextView text2= convertView.findViewById(R.id.leaderboardPoints);
        text2.setText(String.valueOf(userInfo.getXpPoints())+"XP");

        CircleImageView profileImageView=convertView.findViewById(R.id.leaderboardFriendPhoto);
        Picasso.get().load(userInfo.getProfilePhotoUrl()).into(profileImageView);
        return convertView;
    }
}
