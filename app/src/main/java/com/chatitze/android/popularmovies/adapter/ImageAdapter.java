package com.chatitze.android.popularmovies.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.chatitze.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;


/**
 * Created by chatitze on 02/02/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] imageUrls;

    public ImageAdapter(Context c, String [] imageUrls) {
        mContext = c;
        this.imageUrls = imageUrls;
    }

    public int getCount() {
        return imageUrls.length;
    }

    public Object getItem(int position) {
        return imageUrls[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = NetworkUtils.MOVIES_POSTER_ENDPOINT + getItem(position);
        Picasso.with(mContext).load(url).into(imageView);

        return imageView;
    }
}