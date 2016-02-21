package com.harrykristi.hangapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harrykristi.hangapp.Models.TipVenue;
import com.harrykristi.hangapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import fi.foyt.foursquare.api.entities.CompleteTip;


public class TipCardArrayAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    Context context;
    List<TipVenue> dataset;

    public TipCardArrayAdapter(Context context, List<TipVenue> dataset){
        this.context = context;
        this.dataset = dataset;
    }
    @Override
    public int getCount() {
        return dataset.size();
    }

    @Override
    public Object getItem(int position) {
        return dataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.tip_list_item, null);
        TextView tipText = (TextView) convertView.findViewById(R.id.tip_text);
        ImageView tipImage = (ImageView) convertView.findViewById(R.id.tip_profile_picture);

        tipText.setText(dataset.get(position).getText());
        Picasso.with(context).load(dataset.get(position).getUser().getPhoto().getPrefix()+"50x50"+
                dataset.get(position).getUser().getPhoto().getSuffix()).placeholder(R.drawable.grey_placeholder_small).into(tipImage);
        return convertView;
    }
}
