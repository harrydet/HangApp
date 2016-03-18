package com.harrykristi.hangapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.harrykristi.hangapp.model.VenueHangApp;
import com.harrykristi.hangapp.R;
import com.harrykristi.hangapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileListAdapter extends BaseAdapter{

    Context mContext;
    List<User> mDatasetUser;
    List<VenueHangApp>  mDatasetVenue;

    public ProfileListAdapter(Context context, List<User> datasetUser, List<VenueHangApp> datasetVenue){
        mDatasetUser = datasetUser;
        mDatasetVenue = datasetVenue;
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mDatasetUser != null){
            return mDatasetUser.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_previous_checkin, parent, false);

        TextView previousVenueWith = (TextView) rowView.findViewById(R.id.previous_venue_with);
        previousVenueWith.setText(String.format("with %s %s", mDatasetUser.get(position).getFirst_name(), mDatasetUser.get(position).getLast_name()));

        TextView previousVenueName = (TextView) rowView.findViewById(R.id.previous_venue_name);
        previousVenueName.setText(String.format("%s", mDatasetVenue.get(position).getName()));

        CircleImageView previousVenuePhoto = (CircleImageView) rowView.findViewById(R.id.previous_venue_photo);
        Picasso.with(mContext).load(Uri.parse(mDatasetVenue.get(position).getPhotoUrl("700x350"))).placeholder(R.drawable.grey_placeholder).noFade().into(previousVenuePhoto);
        //previousVenuePhoto.setImageURI((Uri.parse(mDatasetVenue.get(position).getPhotoUrl("700x350"))));
        return rowView;
    }

    public void updateDataset(List<User> datasetUser, List<VenueHangApp> datasetVenue){
        mDatasetUser = datasetUser;
        mDatasetVenue = datasetVenue;
        notifyDataSetChanged();
    }
}
