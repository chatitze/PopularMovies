package com.chatitze.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatitze.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by chatitze on 07/02/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private String [] mMovieDetails;
    private ImageView mMovieImage;
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovieImage    = (ImageView) findViewById(R.id.iv_image);
        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mOverview      = (TextView) findViewById(R.id.tv_overview);
        mReleaseDate   = (TextView) findViewById(R.id.tv_release_date);
        mRating        = (TextView) findViewById(R.id.tv_rating);

        Bundle extras = getIntent().getExtras();
        mMovieDetails = extras.getStringArray("movieDetails");

        Picasso.with(MovieDetailsActivity.this).load(NetworkUtils.MOVIES_POSTER_ENDPOINT + mMovieDetails[0]).into(mMovieImage);
        mOriginalTitle.setText(mMovieDetails[1]);
        mRating.setText("Rating: " + mMovieDetails[2]);
        mReleaseDate.setText("Released: " + mMovieDetails[3]);
        mOverview.setText(mMovieDetails[4]);
    }

}
