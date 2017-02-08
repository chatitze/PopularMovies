package com.chatitze.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chatitze.android.popularmovies.adapter.ImageAdapter;
import com.chatitze.android.popularmovies.utilities.MovieDatabaseJsonUtils;
import com.chatitze.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private GridView mGridView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private String mSortBy = NetworkUtils.sortByPopularity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gv_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        loadMoviesData();
    }

    /**
     * This method will get the user's preferred sort option, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMoviesData() {
        showMoviesDataView();
        new FetchMoviesTask().execute(mSortBy);
    }

    /**
     * This method will make the View for the movies data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mGridView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mGridView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }
            URL movieRequestUrl = NetworkUtils.buildUrl(params[0]);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                String[] simpleJsonMovieData = MovieDatabaseJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMoviesDataView();

                String [] imageUrls = new String[movieData.length];

                for (int i = 0; i < movieData.length; i++){
                    String movieString = movieData[i];
                    String[] myMovieDetails = movieString.split("_");

                    imageUrls[i] = myMovieDetails[0];
                }
                ImageAdapter mImageAdapter = new ImageAdapter(MainActivity.this, imageUrls);
                mGridView.setAdapter(mImageAdapter);

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Toast.makeText(getBaseContext(), "pic" + (position + 1) + " selected", Toast.LENGTH_SHORT).show();

                        final String[] mMovieDetails = movieData[position].split("_");
                        final Intent i = new Intent(v.getContext(), MovieDetailsActivity.class);
                        i.putExtra("movieDetails", mMovieDetails);
                        v.getContext().startActivity(i);
                    }
                });
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movies, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sortByPopularity:
                mSortBy = NetworkUtils.sortByPopularity;
                break;
            case R.id.action_sortByTopRated:
                mSortBy = NetworkUtils.sortByTopRated;
                break;
        }
        loadMoviesData();

        return super.onOptionsItemSelected(item);
    }

}
