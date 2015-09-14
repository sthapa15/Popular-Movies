package com.example.sukriti.popularmovies.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.model.ReviewModel;

import java.util.List;


public class ReviewAdapter extends BaseAdapter {

    private final Context mdContext;
    private final LayoutInflater mdInflater;
    private final ReviewModel mdLock = new ReviewModel();

    private List<ReviewModel> mdReviews;

    public ReviewAdapter(Context context, List<ReviewModel> objects) {
        mdContext = context;
        mdInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mdReviews = objects;
    }

    public Context getContext() {
        return mdContext;
    }

    public void add(ReviewModel object) {
        synchronized (mdLock) {
           mdReviews.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mdLock) {
            mdReviews.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mdReviews.size();
    }

    @Override
    public ReviewModel getItem(int position) {
        return mdReviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // View view = convertView;
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mdInflater.inflate(R.layout.item_movie_review, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        final ReviewModel review = getItem(position);

        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.authorView.setText(review.getAuthor());
        viewHolder.contentView.setText(Html.fromHtml(review.getContent()));

        return convertView;
    }

    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }

}