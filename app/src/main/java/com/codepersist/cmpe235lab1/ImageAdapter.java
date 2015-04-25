package com.codepersist.cmpe235lab1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jasroger on 4/17/15.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    // Initialize our array of icons
    private int [] mIcons = {
            R.drawable.login_icon, R.drawable.sensor_net_icon,
            R.drawable.sensor_map_icon, R.drawable.sensor_control_icon,
            R.drawable.sensor_monitor_icon, R.drawable.sensor_data_icon };


    private int [] mIconName = {
            R.string.login, R.string.sensor_profile, R.string.sensor_map,
            R.string.sensor_control, R.string.sensor_monitor, R.string.sensor_data
    };

    public ImageAdapter(Context c, LayoutInflater inflater) {
        mContext = c;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mIcons.length;
    }

    @Override
    public Object getItem(int position) {
        return (mIconName[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.icon_view, null);

            // if it's not recycled, initialize some attributes
            holder.mImageView = (ImageView) convertView.findViewById(R.id.imagename);
            holder.mTextView = (TextView) convertView.findViewById(R.id.iconname);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.mImageView.setImageResource(mIcons[position]);
        holder.mTextView.setText(mIconName[position]);
        return convertView;
    }

    private static class ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
    }
}
