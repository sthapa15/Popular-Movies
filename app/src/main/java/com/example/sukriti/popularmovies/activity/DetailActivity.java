package com.example.sukriti.popularmovies.activity;

import com.example.sukriti.popularmovies.R;
import com.example.sukriti.popularmovies.fragment.DetailFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.EXTRA_TEXT,
                    getIntent().getParcelableExtra(DetailFragment.EXTRA_TEXT));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}
