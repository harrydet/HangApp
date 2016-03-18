package com.harrykristi.hangapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.harrykristi.hangapp.Interfaces.RecyclerViewClickListener;
import com.harrykristi.hangapp.R;
import com.harrykristi.hangapp.model.Response;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VenueCardAdapter extends RecyclerView.Adapter<VenueCardAdapter.ViewHolder> {

    private final Context mContext;
    Response mResponse;
    private static RecyclerViewClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public View mCardView;
        public ViewHolder(View v) {
            super(v);
            mCardView = v;
            mCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onRecyclerViewClicked(v, this.getLayoutPosition());
        }
    }

    public VenueCardAdapter(Response response, Context context, RecyclerViewClickListener clickListener){
        mResponse = response;
        mContext = context;
        mClickListener = clickListener;
    }

    @Override
    public VenueCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.venue_card_view, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(VenueCardAdapter.ViewHolder holder, int position) {
        TextView tv = (TextView) holder.mCardView.findViewById(R.id.card_venue_name);
        TextView attendance = (TextView) holder.mCardView.findViewById(R.id.attendance_venue_card);
        CircleImageView circleImageView = (CircleImageView) holder.mCardView.findViewById(R.id.card_round_photo);
        RatingBar ratingBar = (RatingBar) holder.mCardView.findViewById(R.id.venue_card_rating);

        tv.setText(mResponse.getVenueNameAt(position));
        attendance.setText(String.format("%d people attending", mResponse.totalCheckinsAt(position)));
        float rating = mResponse.getVenueRatingAt(position);
        ratingBar.setRating(rating);
        try{
            Picasso.with(mContext).load(Uri.parse(mResponse.getPhotoAtVenue(position, "700x350"))).placeholder(R.drawable.grey_placeholder).noFade().into(circleImageView);
        } catch (Exception e){

        }

    }

    public boolean appendResponse(Response response){
        int count = 0;
        if(mResponse != null){
            count = mResponse.getTotalVenues();
        }
        int totalVenuesAdded = response.getTotalVenues() - count;
        mResponse = response;
        notifyItemRangeInserted(count, totalVenuesAdded);
        return count%10 == 0;
    }

    @Override
    public int getItemCount() {

        return (mResponse == null)?0:mResponse.getTotalVenues();
    }
}
