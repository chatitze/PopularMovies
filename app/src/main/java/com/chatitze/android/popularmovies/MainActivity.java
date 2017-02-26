package com.chatitze.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chatitze.android.popularmovies.adapter.ImageAdapter;
import com.chatitze.android.popularmovies.utilities.MovieDatabaseJsonUtils;
import com.chatitze.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity
                            implements ImageAdapter.ListItemClickListener{

    private RecyclerView mMoviesList;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private String mSortBy = NetworkUtils.sortByPopularity;

    private String [] movieDataFromAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 4);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);

        loadMoviesData();
    }

    /**
     * This method will get the user's preferred sort option, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMoviesData() {
        showMoviesDataView();
        new FetchMoviesTask(new AsyncResponse(){
            @Override
            public void processFinish(String[] output){
                movieDataFromAsyncTask = output;
            }
        }).execute(mSortBy);
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
        mMoviesList.setVisibility(View.VISIBLE);
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
        mMoviesList.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies, menu);
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

    /**
     * This is where we receive our callback from
     * {@link com.chatitze.android.popularmovies.adapter.ImageAdapter.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        final String[] mMovieDetails = movieDataFromAsyncTask[clickedItemIndex].split("_");
        final Intent i = new Intent(this, MovieDetailsActivity.class);
        i.putExtra("movieDetails", mMovieDetails);
        startActivity(i);
    }

    public interface AsyncResponse {
        void processFinish(String [] output);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        public AsyncResponse delegate = null;

        public FetchMoviesTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

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
                    String[] myMovieDetails = movieData[i].split("_");
                    imageUrls[i] = myMovieDetails[0];
                }
                ImageAdapter mImageAdapter = new ImageAdapter(MainActivity.this, imageUrls, MainActivity.this);
                mMoviesList.setAdapter(mImageAdapter);
                delegate.processFinish(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

}
