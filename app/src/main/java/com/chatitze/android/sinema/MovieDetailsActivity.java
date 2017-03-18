package com.chatitze.android.sinema;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatitze.android.sinema.data.SinemaPreferences;
import com.chatitze.android.sinema.utilities.NetworkUtils;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

/**
 * Created by chatitze on 07/02/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String SINEMA_SHARE_HASHTAG = " #SinemaApp";

    private String [] mMovieDetails;
    private ImageView mMovieImage;
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mRating;
    private MaterialFavoriteButton mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovieImage    = (ImageView) findViewById(R.id.iv_poster);
        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mOverview      = (TextView) findViewById(R.id.tv_overview);
        mReleaseDate   = (TextView) findViewById(R.id.tv_release_date);
        mRating        = (TextView) findViewById(R.id.tv_rating);
        mFavorite      = new MaterialFavoriteButton.Builder(this).favorite(true).create();

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            mMovieDetails = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);

            if(SinemaPreferences.isPreferredImageDownload_WifiOnly(this) && NetworkUtils.isConnectedThroughWiFi(this))
                Picasso.with(MovieDetailsActivity.this).load(NetworkUtils.MOVIES_POSTER_ENDPOINT + mMovieDetails[0])
                        .placeholder(R.drawable.place_holder_bitmap) // optional
                        .error(R.drawable.place_holder_bitmap)
                        .into(mMovieImage);
            else
                mMovieImage.setImageResource(R.drawable.place_holder_bitmap);

            mOriginalTitle.setText(mMovieDetails[1]);
            mRating.setText("Rating: " + mMovieDetails[2]);
            mReleaseDate.setText("Released: " + mMovieDetails[3]);
            mOverview.setText(mMovieDetails[4]);
        }

        mFavorite.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        //
                    }
                });
    }

    /**
     * Uses the ShareCompat Intent builder to create our Movie intent for sharing. We set the
     * type of content that we are sharing (just regular text), the text itself, and we return the
     * newly created Intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareMovieIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("Title: " + mMovieDetails[1]
                        + " Rating: " + mMovieDetails[2]
                        + " Released: " + mMovieDetails[3]
                        + " Overview: " + mMovieDetails[4]
                        + SINEMA_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareMovieIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
