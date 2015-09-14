package com.example.sukriti.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.fragment.DetailFragment;
import com.example.sukriti.popularmovies.fragment.MoviesFragment;
import com.example.sukriti.popularmovies.model.MovieModel;


public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    private boolean mdTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container) != null) {
            mdTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new DetailFragment(),
                                DetailFragment.TAG)
                        .commit();
            }
        } else {
            mdTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(MovieModel movie) {
        if (mdTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.EXTRA_TEXT, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, DetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.EXTRA_TEXT, movie);
            startActivity(intent);
        }
    }
}


