package com.chatitze.android.sinema;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.chatitze.android.sinema.data.Movie;
import com.chatitze.android.sinema.data.MovieContract;
import com.chatitze.android.sinema.data.MoviePreferences;
import com.chatitze.android.sinema.utilities.MovieDatabaseJsonUtils;
import com.chatitze.android.sinema.utilities.NetworkUtils;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by chatitze on 07/02/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private static final String SINEMA_SHARE_HASHTAG = " #SinemaApp";

    private Movie mMovie;
    private String [][] mTrailerDetails;
    private String [][] mReviewDetails;
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
        mFavorite      = (MaterialFavoriteButton) findViewById(R.id.iv_favourite);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String target = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            Gson gSon = new Gson();
            mMovie = gSon.fromJson(target, Movie.class); // Converts the JSON String to an Object

            if(MoviePreferences.isPreferredImageDownload_WifiOnly(this) && !NetworkUtils.isConnectedThroughWiFi(this))
                mMovieImage.setImageResource(R.drawable.place_holder_bitmap);
            else
                Picasso.with(MovieDetailsActivity.this).load(NetworkUtils.MOVIES_POSTER_ENDPOINT + mMovie.getPosterPath())
                        .placeholder(R.drawable.place_holder_bitmap) // optional
                        .error(R.drawable.place_holder_bitmap)
                        .into(mMovieImage);

            mOriginalTitle.setText(mMovie.getOriginalTitle());
            mRating.setText("Rating: " + mMovie.getVoteAverage());
            mReleaseDate.setText("Released: " + mMovie.getReleaseDate());
            mOverview.setText(mMovie.getOverview());
            // Fetch trailers for this movie, by providing the movie ID
            new FetchTrailerTask().execute(String.valueOf(mMovie.getMovieId()));
            // Fetch reviews for this movie, by providing the movie ID
            new FetchReviewTask().execute(String.valueOf(mMovie.getMovieId()));

        }

        // check in DB whether this movie is already marked as favorite, if so display as favorite
        checkIfMovieIsFavorite();

        mFavorite.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean isMarkedFavorite) {
                        if(isMarkedFavorite){ // add the movie as favorite : insert into movies table
                            mMovie.setFavorite(isMarkedFavorite);

                            // Create new empty ContentValues object
                            ContentValues contentValues = new ContentValues();
                            // Put the movie details into the ContentValues
                            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getMovieId());
                            contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
                            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getVoteAverage());
                            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
                            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
                            // Insert the content values via a ContentResolver
                            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                            // Display the URI that's returned with a Toast
                            if(uri != null) {
                                //Snackbar.make(buttonView, mMovie.getOriginalTitle() + " ADDED to your FAVORITE movies", Snackbar.LENGTH_SHORT).show();
                                Toast.makeText(getBaseContext(), mMovie.getOriginalTitle() + " ADDED to your FAVORITE movies", Toast.LENGTH_LONG).show();
                            }

                        } else { // remove the movies from favorite list : delete it from moves table
                            int deleteCount = getContentResolver().delete(createUriWithMovieID(), null, null);
                            if(deleteCount == 1){
                                //Snackbar.make(buttonView, mMovie.getOriginalTitle() + " removed from your FAVORITE movies", Snackbar.LENGTH_SHORT).show();
                                Toast.makeText(getBaseContext(), mMovie.getOriginalTitle() + " removed from your FAVORITE movies", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

    }

    private void checkIfMovieIsFavorite(){
        Cursor data = getContentResolver().query(createUriWithMovieID(), null, null, null, null);
        if(data != null && data.getCount() > 0){
            data.moveToFirst();
            int movieIdIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int movieIdFromCache = data.getInt(movieIdIndex);
            if(mMovie.getMovieId() == movieIdFromCache)
                mMovie.setFavorite(true);
        }
        mFavorite.setFavorite(mMovie.isFavorite());
    }

    private Uri createUriWithMovieID(){
        // Retrieve the id of the movie to query / delete
        int id = mMovie.getMovieId();

        // Build appropriate uri with String row id appended
        String stringId = Integer.toString(id);

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        // build a URI with id: URI:content://<authority>/movie/#
        uri = uri.buildUpon().appendPath(stringId).build();
        return  uri;
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
                .setText("Title: " + mMovie.getOriginalTitle()
                        + " Rating: " + mMovie.getVoteAverage()
                        + " Released: " + mMovie.getReleaseDate()
                        + " Overview: " + mMovie.getOverview()
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
                        .getSimpleTrailerStringsFromJson(jsonTrailerResponse);

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

    public class FetchReviewTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            /* If there's no movie id, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }
            URL reviewRequestUrl = NetworkUtils.buildGetReviewsUrl(params[0]);

            try {
                String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);

                String[] simpleJsonReviewData = MovieDatabaseJsonUtils
                        .getSimpleReviewStringsFromJson(jsonReviewResponse);

                return simpleJsonReviewData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String[] reviewData) {
            if (reviewData != null) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_review_layout);
                LayoutInflater inflater = LayoutInflater.from(MovieDetailsActivity.this);
                mReviewDetails = new String[reviewData.length][4];
                for (int i = 0; i < reviewData.length; i++) {
                    mReviewDetails[i] = reviewData[i].split("_");

                    View reviewView = inflater.inflate(R.layout.movie_review_content, linearLayout, false);

                    TextView reviewContent = (TextView) reviewView.findViewById(R.id.tv_review_content);
                    reviewContent.setText("\"" + mReviewDetails[i][3] + "\"");

                    TextView reviewAuthor = (TextView) reviewView.findViewById(R.id.tv_review_author);
                    reviewAuthor.setText(mReviewDetails[i][1] + " \n -----------------");

                    linearLayout.addView(reviewView);
                }
            } else{
                // do nothing - no reviews to display
            }
        }
    }
}
