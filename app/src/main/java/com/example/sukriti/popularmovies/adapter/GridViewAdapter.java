package com.example.sukriti.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.model.MovieModel;

public class GridViewAdapter extends BaseAdapter {

    private Context mdContext;
    private List<MovieModel> mdMovies;
    private final LayoutInflater mdInflater;

    private final MovieModel mdLock = new MovieModel();

    public GridViewAdapter(Context context, List<MovieModel> movies) {
        mdContext = context;
        mdInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mdMovies = movies;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mdMovies != null) {
            count = mdMovies.size();
        }
        return count;
    }

    @Override
    public MovieModel getItem(int position) {
        MovieModel item = null;
        if (mdMovies != null) {
            item = mdMovies.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return mdContext;
    }

    public void add(MovieModel movie) {
        synchronized (mdLock) {
            mdMovies.add(movie);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mdLock) {
            mdMovies.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<MovieModel> data) {
        clear();
        for (MovieModel movie : data) {
            add(movie);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mdInflater.inflate(R.layout.grid_item_movies, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final MovieModel movie = getItem(position);

        String image_url = "http://image.tmdb.org/t/p/w185" + movie.getMoviePosterUrl();

        viewHolder = (ViewHolder) convertView.getTag();

        Picasso.with(getContext()).load(image_url).into(viewHolder.imageView);

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_item_movies_imageview);
        }
    }
}


