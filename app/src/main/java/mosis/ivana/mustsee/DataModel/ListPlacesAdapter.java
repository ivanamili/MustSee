package mosis.ivana.mustsee.DataModel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mosis.ivana.mustsee.R;

public class ListPlacesAdapter extends ArrayAdapter<Place> {

    private ArrayList<Place> places;
    Context mContext;

    public ListPlacesAdapter(ArrayList<Place> data, Context context)
    {
        super(context, R.layout.place_list_item,data);
        this.places=data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Place place= getItem(position);

        if(convertView==null)
            convertView= LayoutInflater.from(mContext).inflate(R.layout.place_list_item,parent,false);

        ImageView picture= convertView.findViewById(R.id.placeItemPicture);
        Picasso.get().load(place.getPhotoURL()).into(picture);

        TextView name= convertView.findViewById(R.id.placeItemName);
        name.setText(place.getName());
        TextView country= convertView.findViewById(R.id.placeItemCountry);
        country.setText(place.getCountry());
        TextView likeCount= convertView.findViewById(R.id.placeListItemLikeCount);
        likeCount.setText(String.valueOf(place.getLikeCount()));
        TextView commentCount= convertView.findViewById(R.id.placeListItemCommentCount);
        commentCount.setText(String.valueOf(place.getCommentCount()));

        return convertView;
    }
}
