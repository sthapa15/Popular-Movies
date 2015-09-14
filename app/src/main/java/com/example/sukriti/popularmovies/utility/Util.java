package com.example.sukriti.popularmovies.utility;

import android.content.Context;
import android.database.Cursor;

import com.example.sukriti.popularmovies.data.MovieContract;

/**
 * Created by Sukriti Thapa
 */
public class Util {

    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   //arguments selection
                null
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildMovieImageUrl(int width, String movieName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + movieName;
    }

}
