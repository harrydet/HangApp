package com.harrykristi.hangapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harrykristi.hangapp.R;

import fi.foyt.foursquare.api.entities.CompleteTip;


public class TipCardArrayAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    Context context;
    CompleteTip[] dataset;

    public TipCardArrayAdapter(Context context, CompleteTip[] dataset){
        this.context = context;
        this.dataset = dataset;
    }
    @Override
    public int getCount() {
        return dataset.length;
    }

    @Override
    public Object getItem(int position) {
        return dataset[position];
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
        return null;
    }
}
