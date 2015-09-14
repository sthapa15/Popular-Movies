package com.example.sukriti.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.sukriti.popularmovies.fragment.MoviesFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieModel implements Parcelable {

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {

        //Calls the new constructor and passes along the 'Parcel'
        //Returns the new Object
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    //parcel data types
    private String movieOrigTitle =null;
    private int id=0;
    private String moviePosterUrl = null;
    private String movieBackdropUrl = null;
    private String movieOverview = null;
    private String movieUserRating = null;
    private String movieReleaseDate = null;

    public MovieModel() {

    }

    public MovieModel(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.movieOrigTitle = movie.getString("original_title");
        this.moviePosterUrl = movie.getString("poster_path");
        this.movieBackdropUrl = movie.getString("backdrop_path");
        this.movieOverview = movie.getString("overview");
        this.movieUserRating = movie.getString("vote_average");
        this.movieReleaseDate = movie.getString("release_date");
    }
    public MovieModel(Cursor cursor) {
        this.id = cursor.getInt(MoviesFragment.COLUMN_MOVIE_ID);
        this.movieOrigTitle = cursor.getString(MoviesFragment.COLUMN_TITLE);
        this.moviePosterUrl = cursor.getString(MoviesFragment.COLUMN_POSTER_IMAGE);
        this.movieBackdropUrl = cursor.getString(MoviesFragment.COLUMN_BACKDROP_IMAGE);
        this.movieOverview = cursor.getString(MoviesFragment.COLUMN_OVERVIEW);
        this.movieUserRating = cursor.getString(MoviesFragment.COLUMN_RATING);
        this.movieReleaseDate = cursor.getString(MoviesFragment.COLUMN_DATE);
    }

    //retrieve values originally written into the 'Parcel'
    //private so that only the CREATOR field can access it
    private MovieModel(Parcel in) {
        movieOrigTitle = in.readString();
        id = in.readInt();
        moviePosterUrl = in.readString();
        movieBackdropUrl = in.readString();
        movieOverview = in.readString();
        movieUserRating = in.readString();
        movieReleaseDate = in.readString();
    }

    //write the values to save to the Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieOrigTitle);
        dest.writeInt(id);
        dest.writeString(moviePosterUrl);
        dest.writeString(movieBackdropUrl);
        dest.writeString(movieOverview);
        dest.writeString(movieUserRating);
        dest.writeString(movieReleaseDate);
    }


    public String getOriginalTitle() {
        return movieOrigTitle;
    }

    public int getId() { return id; }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public String getMovieBackdropUrl() {
        return movieBackdropUrl;
    }

    public String getOverview() {
        return movieOverview;
    }

    public String getVoteAverage() {
        return movieUserRating;
    }

    public String getReleaseDate() {
        return movieReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

