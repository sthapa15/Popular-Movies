package com.example.sukriti.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.model.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TrailerAdapter extends BaseAdapter {

    private final Context mdContext;
    private final LayoutInflater mdInflater;
    private final TrailerModel mdLock = new TrailerModel();

    private List<TrailerModel> mdTrailers;

    public TrailerAdapter(Context context, List<TrailerModel> objects) {
        mdContext = context;
        mdInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mdTrailers = objects;
    }

    public Context getContext() {
        return mdContext;
    }

    public void add(TrailerModel object) {
        synchronized (mdLock) {
            mdTrailers.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mdLock) {
            mdTrailers.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mdTrailers.size();
    }

    @Override
    public TrailerModel getItem(int position) {
        return mdTrailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mdInflater.inflate(R.layout.item_movie_trailer, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final TrailerModel trailer = getItem(position);

        viewHolder = (ViewHolder) convertView.getTag();

        String trailer_image_url = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        Picasso.with(getContext()).load(trailer_image_url).into(viewHolder.imageView);

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.trailer_image);
        }
    }

}
