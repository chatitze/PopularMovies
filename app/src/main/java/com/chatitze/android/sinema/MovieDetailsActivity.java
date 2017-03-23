package com.chatitze.android.sinema;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatitze.android.sinema.data.SinemaPreferences;
import com.chatitze.android.sinema.utilities.MovieDatabaseJsonUtils;
import com.chatitze.android.sinema.utilities.NetworkUtils;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by chatitze on 07/02/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String SINEMA_SHARE_HASHTAG = " #SinemaApp";

    private String [] mMovieDetails;
    private String [][] mTrailerDetails;
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

            if(SinemaPreferences.isPreferredImageDownload_WifiOnly(this) && !NetworkUtils.isConnectedThroughWiFi(this))
                mMovieImage.setImageResource(R.drawable.place_holder_bitmap);
            else
                Picasso.with(MovieDetailsActivity.this).load(NetworkUtils.MOVIES_POSTER_ENDPOINT + mMovieDetails[0])
                        .placeholder(R.drawable.place_holder_bitmap) // optional
                        .error(R.drawable.place_holder_bitmap)
                        .into(mMovieImage);

            mOriginalTitle.setText(mMovieDetails[1]);
            mRating.setText("Rating: " + mMovieDetails[2]);
            mReleaseDate.setText("Released: " + mMovieDetails[3]);
            mOverview.setText(mMovieDetails[4]);
            // Fetch trailers for this movie, by providing the movie ID
            new FetchTrailerTask().execute(mMovieDetails[5]);

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

    /**
     * This method is called when play a trailer button is clicked. It will open the specified trailer
     * represented by the variable trailerUri using implicit Intents.
     *
     * @param clickedTrailerIndex Trailer index that was clicked.
     */
    private void onClickPlayTrailer(int clickedTrailerIndex) {
        Uri trailerUri = Uri.parse(NetworkUtils.YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(NetworkUtils.WATCH_QUERY, mTrailerDetails[clickedTrailerIndex][1])
                .build();
        /*
         * Here, we create the Intent with the action of ACTION_VIEW. This action allows the user
         * to view particular content. In this case, our trailer URL.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);

        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public class FetchTrailerTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            /* If there's no movie id, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }
            URL trailerRequestUrl = NetworkUtils.buildGetTrailersUrl(params[0]);

            try {
                String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);

                String[] simpleJsonTrailerData = MovieDatabaseJsonUtils
                        .getSimpleTrailerStringsFromJson(MovieDetailsActivity.this, jsonTrailerResponse);

                return simpleJsonTrailerData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String[] trailerData) {
            if (trailerData != null) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_trailer_layout);
                LayoutInflater inflater = LayoutInflater.from(MovieDetailsActivity.this);
                mTrailerDetails = new String[trailerData.length][6];
                for (int i = 0; i < trailerData.length; i++) {
                    final int position = i;
                    mTrailerDetails[i] = trailerData[i].split("_");

                    View trailerView = inflater.inflate(R.layout.movie_trailer_content, linearLayout, false);

                    // fill in any details dynamically here
                    TextView trailerName = (TextView) trailerView.findViewById(R.id.tv_trailer_name);
                    trailerName.setText(mTrailerDetails[i][2]);

                    ImageView trailerPlayButton = (ImageView) trailerView.findViewById(R.id.iv_play_button);
                    trailerPlayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickPlayTrailer(position);
                        }
                    });
                    linearLayout.addView(trailerView);
                }
            } else{
                // do nothing - no trailers to display
            }
        }
    }
}
