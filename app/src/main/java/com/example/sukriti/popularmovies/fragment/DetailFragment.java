package com.example.sukriti.popularmovies.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.adapter.ReviewAdapter;
import com.example.sukriti.popularmovies.adapter.TrailerAdapter;
import com.example.sukriti.popularmovies.data.MovieContract;
import com.example.sukriti.popularmovies.model.MovieModel;
import com.example.sukriti.popularmovies.model.ReviewModel;
import com.example.sukriti.popularmovies.model.TrailerModel;
import com.example.sukriti.popularmovies.utility.Util;
import com.squareup.picasso.Picasso;
import com.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();

    public static final String EXTRA_TEXT = "EXTRA_TEXT";

    private MovieModel mdMovie;
    private ImageView mdImageView;

    private TextView mdTitleView;
    private TextView mdOverviewView;
    private TextView mdDateView;
    private TextView mdVoteAverageView;


    private LinearListView mTrailersView;
    private LinearListView mReviewsView;

    private CardView mReviewsCardview;
    private CardView mTrailersCardview;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;


    private ScrollView mDetailLayout;

    private Toast mToast;

    private ShareActionProvider mShareActionProvider;

    // the first trailer to share
    private TrailerModel mTrailer;

    public DetailFragment() {
        // setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mdMovie != null) {
            inflater.inflate(R.menu.detail, menu);

            final MenuItem action_favorite = menu.findItem(R.id.action_favorite);
            MenuItem action_share = menu.findItem(R.id.action_share);

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Util.isFavorited(getActivity(), mdMovie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorited) {
                    action_favorite.setIcon(isFavorited == 1 ?
                            R.drawable.abc_btn_rating_star_on_mtrl_alpha :
                            R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                }
            }.execute();

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

            if (mTrailer != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                if (mdMovie != null) {
                    // check if movie is in favorites or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Util.isFavorited(getActivity(), mdMovie.getId());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavorited) {
                            // if it is in favorites
                            if (isFavorited == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.MovieEntry.CONTENT_URI,
                                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(mdMovie.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                                        mToast.setGravity(Gravity.CENTER,0,0);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add movie to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mdMovie.getId());
                                        values.put(MovieContract.MovieEntry.COLUMN_TITLE, mdMovie.getOriginalTitle());
                                        values.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE, mdMovie.getMoviePosterUrl());
                                        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE, mdMovie.getMovieBackdropUrl());
                                        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mdMovie.getOverview());
                                        values.put(MovieContract.MovieEntry.COLUMN_RATING, mdMovie.getVoteAverage());
                                        values.put(MovieContract.MovieEntry.COLUMN_DATE, mdMovie.getReleaseDate());

                                        return getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.setGravity(Gravity.CENTER,0,0);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mdMovie = arguments.getParcelable(DetailFragment.EXTRA_TEXT);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDetailLayout = (ScrollView) rootView.findViewById(R.id.detail_layout);

        if (mdMovie != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        mdImageView = (ImageView) rootView.findViewById(R.id.detail_poster_image_view);

        mdTitleView = (TextView) rootView.findViewById(R.id.detail_movie_title);
        mdOverviewView = (TextView) rootView.findViewById(R.id.detail_movie_plot);
        mdDateView = (TextView) rootView.findViewById(R.id.detail_movie_release_date);
        mdVoteAverageView = (TextView) rootView.findViewById(R.id.detail_movie_user_rating);

        mTrailersView = (LinearListView) rootView.findViewById(R.id.detail_trailers);
        mReviewsView = (LinearListView) rootView.findViewById(R.id.detail_reviews);

        mReviewsCardview = (CardView) rootView.findViewById(R.id.detail_reviews_cardview);
        mTrailersCardview = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);

        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<TrailerModel>());
        mTrailersView.setAdapter(mTrailerAdapter);

        mTrailersView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view,
                                    int position, long id) {
                TrailerModel trailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
            }
        });

        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<ReviewModel>());
        mReviewsView.setAdapter(mReviewAdapter);

        //populate detail fragment with movie data
        if (mdMovie != null) {

            String image_url = Util.buildMovieImageUrl(342, mdMovie.getMovieBackdropUrl());

            Picasso.with(getActivity()).load(image_url).into(mdImageView);

            mdTitleView.setText(mdMovie.getOriginalTitle());
            mdOverviewView.setText(mdMovie.getOverview());

            String movie_date = mdMovie.getReleaseDate();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                String date = DateUtils.formatDateTime(getActivity(),
                        formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                mdDateView.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String voteAvg = mdMovie.getVoteAverage();
            mdVoteAverageView.setText(voteAvg + "/10");
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mdMovie != null) {
            new FetchTrailersTask().execute(Integer.toString(mdMovie.getId()));
            new FetchReviewsTask().execute(Integer.toString(mdMovie.getId()));
        }
    }

    //share movie trailer link via intent
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Click link to watch trailer: " + mdMovie.getOriginalTitle() + " " +
                "http://www.youtube.com/watch?v=" + mTrailer.getKey());
        return shareIntent;
    }

    //fetch trailers from themoviedb.org
    public class FetchTrailersTask extends AsyncTask<String, Void, List<TrailerModel>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private List<TrailerModel> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<TrailerModel> results = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                // Only show youtube trailers
                if (trailer.getString("site").contentEquals("YouTube")) {
                    TrailerModel trailerModel = new TrailerModel(trailer);
                    results.add(trailerModel);
                }
            }

            return results;
        }

        @Override
        protected List<TrailerModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.themoviedb_apikey))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging. source: sunshine practice project
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data.
            return null;
        }

        @Override
        protected void onPostExecute(List<TrailerModel> trailers) {
            if (trailers != null) {
                if (trailers.size() > 0) {
                    mTrailersCardview.setVisibility(View.VISIBLE);
                    if (mTrailerAdapter != null) {
                        mTrailerAdapter.clear();
                        for (TrailerModel trailer : trailers) {
                            mTrailerAdapter.add(trailer);
                        }
                    }

                    mTrailer = trailers.get(0);
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareMovieIntent());
                    }
                }
            }
        }
    }


    //fetch reviews from themoviedb
    public class FetchReviewsTask extends AsyncTask<String, Void, List<ReviewModel>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private List<ReviewModel> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<ReviewModel> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new ReviewModel(review));
            }

            return results;
        }

        @Override
        protected List<ReviewModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.themoviedb_apikey))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging. - source: sunshine practice project
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data.
            return null;
        }

        @Override
        protected void onPostExecute(List<ReviewModel> reviews) {
            if (reviews != null) {
                if (reviews.size() > 0) {
                    mReviewsCardview.setVisibility(View.VISIBLE);
                    if (mReviewAdapter != null) {
                        mReviewAdapter.clear();
                        for (ReviewModel review : reviews) {
                            mReviewAdapter.add(review);
                        }
                    }
                }
            }
        }
    }
}

