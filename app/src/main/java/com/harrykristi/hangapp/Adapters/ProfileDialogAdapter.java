package com.harrykristi.hangapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harrykristi.hangapp.ProfileFragment;
import com.harrykristi.hangapp.R;

public class ProfileDialogAdapter extends BaseAdapter {

    private Context mContext;

    public ProfileDialogAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_profile_picture, parent, false);
        TextView optionText = (TextView) rowView.findViewById(R.id.source_text);
        ImageView optionImage = (ImageView) rowView.findViewById(R.id.source_picture);
        switch (position){
            case 0:
                optionText.setText("Camera");
                optionImage.setImageResource(R.drawable.ic_camera);
                break;
            case 1:
                optionText.setText("Gallery");
                optionImage.setImageResource(R.drawable.ic_galery);
                break;
            default:
                break;
        }
        return rowView;
    }
}
