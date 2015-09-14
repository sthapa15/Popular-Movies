package com.example.sukriti.popularmovies.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.adapter.GridViewAdapter;
import com.example.sukriti.popularmovies.data.MovieContract;
import com.example.sukriti.popularmovies.model.MovieModel;


public class MoviesFragment extends Fragment {

    private GridViewAdapter mdGridViewAdapter;
    private GridView mGridView;

    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String RATING = "vote_average.desc";
    private static final String POPULARITY = "popularity.desc";
    private static final String FAVORITE = "favorite";
    private static final String MOVIES_KEY = "movies";

    private String mdSortBy = POPULARITY;

    private ArrayList<MovieModel> mdMovies = null;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_DATE
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_POSTER_IMAGE = 3;
    public static final int COLUMN_BACKDROP_IMAGE = 4;
    public static final int COLUMN_OVERVIEW = 5;
    public static final int COLUMN_RATING = 6;
    public static final int COLUMN_DATE = 7;



    public MoviesFragment() {
    }

    /**
     *Callback interface that all activities containing this fragment must
     * implement. This allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        void onItemSelected(MovieModel movie);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu, menu);

        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_fav = menu.findItem(R.id.action_sort_by_fav);

        if (mdSortBy.contentEquals(RATING)){
            if(!action_sort_by_rating.isChecked()){
                action_sort_by_rating.setChecked(true);
            }
        }else if (mdSortBy.contentEquals(POPULARITY)){
            if(!action_sort_by_popularity.isChecked()){
                action_sort_by_popularity.setChecked(true);
            }
        }else if (mdSortBy.contentEquals(FAVORITE)){
            if(!action_sort_by_fav.isChecked()){
                action_sort_by_fav.setChecked(true);
            }
        }

    }

    //updating gridview based on sorting method selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.action_sort_by_rating:
                if(item.isChecked()) {
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                }
                mdSortBy = RATING;
                updateGridViewMovies(mdSortBy);
                return true;
            case R.id.action_sort_by_popularity:
                if(item.isChecked()) {
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                }
                mdSortBy=POPULARITY;
                updateGridViewMovies(mdSortBy);
                return true;
            case R.id.action_sort_by_fav:
                if(item.isChecked()) {
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                }
                mdSortBy=FAVORITE;
                updateGridViewMovies(mdSortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridview_movies);

        mdGridViewAdapter = new GridViewAdapter(getActivity(), new ArrayList<MovieModel>());

        mGridView.setAdapter(mdGridViewAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel movie = mdGridViewAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                mdSortBy = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                mdMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mdGridViewAdapter.setData(mdMovies);
            } else {
                updateGridViewMovies(mdSortBy);
            }
        } else {
            updateGridViewMovies(mdSortBy);
        }
        return view;
    }

    private void updateGridViewMovies(String sort_by) {
        if(!sort_by.contentEquals(FAVORITE)){
            new FetchMovieTask().execute(sort_by);
        }else{
            new FetchFavoriteMoviesTask(getActivity()).execute();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mdSortBy.contentEquals(POPULARITY)) {
            outState.putString(SORT_SETTING_KEY, mdSortBy);
        }
        if (mdMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mdMovies);
        }
        super.onSaveInstanceState(outState);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, List<MovieModel>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private List<MovieModel> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<MovieModel> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                MovieModel movieModel = new MovieModel(movie);
                results.add(movieModel);
            }

            return results;
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, params[0])
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
                    // buffer for debugging. Source: sunshine practice
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
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data.
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            if (movies != null) {
                if (mdGridViewAdapter != null) {
                    mdGridViewAdapter.setData(movies);
                }
                mdMovies = new ArrayList<>();
                mdMovies.addAll(movies);
            }
        }
    }

    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<MovieModel>> {

        private Context mContext;

        public FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        private List<MovieModel> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<MovieModel> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MovieModel movie = new MovieModel(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<MovieModel> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            if (movies != null) {
                if (mdGridViewAdapter != null) {
                    mdGridViewAdapter.setData(movies);
                }
                mdMovies = new ArrayList<>();
                mdMovies.addAll(movies);
            }
        }
    }
}