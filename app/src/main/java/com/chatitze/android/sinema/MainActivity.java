package com.chatitze.android.sinema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chatitze.android.sinema.adapter.ImageAdapter;
import com.chatitze.android.sinema.data.Movie;
import com.chatitze.android.sinema.data.MovieContract;
import com.chatitze.android.sinema.data.MoviePreferences;
import com.chatitze.android.sinema.utilities.MovieDatabaseJsonUtils;
import com.chatitze.android.sinema.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageAdapter.ImageAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIES_LOADER_ID = 1;
    private static final int FAVORITE_LIST_LOADER_ID = 2;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private String mSortBy = NetworkUtils.sortByPopularity;

    private List<Movie> mMovieDataFromAsyncTask;

    private int mSelectedMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        Context context = MainActivity.this;
        ImageAdapter.ImageAdapterOnClickHandler clickHandler = MainActivity.this;
        mImageAdapter = new ImageAdapter(context, clickHandler);
        mRecyclerView.setAdapter(mImageAdapter);

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * List<Movie>. (implements LoaderCallbacks<SList<Movie>>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<List<Movie>> callback = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = null;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, bundleForLoader, callback);

        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * This is where we receive our callback from
     * {@link ImageAdapter.ImageAdapterOnClickHandler}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedMovieIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onClick(int clickedMovieIndex) {
        Movie movie = mMovieDataFromAsyncTask.get(clickedMovieIndex);
        Gson gSon = new Gson();
        String target = gSon.toJson(movie); // Converts the object to a JSON String

        final Intent startMovieDetailsActivityIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        startMovieDetailsActivityIntent.putExtra(Intent.EXTRA_TEXT, target);
        startActivity(startMovieDetailsActivityIntent);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(final int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Movie>>(this) {

            /* This list will hold and help cache our favorite movies data */
            List<Movie> mMovieData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            public void onStartLoading(){
                if(mMovieData != null){
                    deliverResult(mMovieData);
                }else{
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from MovieDatabase or queries from our internal Movies database for favorite movies
             * in the background.
             *
             * @return Movie data as a list.
             *         null if an error occurs
             */
            @Override
            public List<Movie> loadInBackground() {
                switch (id){
                    case MOVIES_LOADER_ID:
                        return getMoviesFromNetwork();
                    case FAVORITE_LIST_LOADER_ID:
                        return getFavoriteMoviesFromCache();
                }
                return null;
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Movie> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMoviesDataView();
            setImageDownloadOption();
            mImageAdapter.setMovieData(data);
            mMovieDataFromAsyncTask = data;
        } else {
            showErrorMessage();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    private void setImageDownloadOption(){
        if(MoviePreferences.isPreferredImageDownload_WifiOnly(this))
            mImageAdapter.setIsDownloadEnabled(NetworkUtils.isConnectedThroughWiFi(this));
        else
            mImageAdapter.setIsDownloadEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mSelectedMenuItem = item.getItemId();
        switch (mSelectedMenuItem){
            case R.id.action_sortByPopularity:
                mSortBy = NetworkUtils.sortByPopularity;
                break;
            case R.id.action_sortByTopRated:
                mSortBy = NetworkUtils.sortByTopRated;
                break;
            case R.id.action_favorite_movies:
                getSupportLoaderManager().restartLoader(FAVORITE_LIST_LOADER_ID, null, this);
                return true;
            case R.id.action_refresh:
                invalidateData();
                break;
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;

        }
        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*
         * Set this flag to true so that when control returns to MainActivity, it can refresh the
         * data.
         */
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    /**
     * OnStart is called when the Activity is coming into view. This happens when the Activity is
     * first created, but also happens when the Activity is returned to from another Activity. We
     * are going to use the fact that onStart is called when the user returns to this Activity to
     * check if the wifi only setting has changed. If it has changed,
     * we are going to perform a new query.
     */
    @Override
    protected void onStart() {
        super.onStart();
        /*
         * If the preferences for location or units have changed since the user was last in
         * MainActivity, perform another query and set the flag to false.
         *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
         * job done for now. Later in this course, we are going to show you more elegant ways to
         * handle converting the units from celsius to fahrenheit and back without hitting the
         * network again by keeping a copy of the data in a manageable format.
         */
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * If the favorite movies have changed since the user was last in
         * MainActivity, perform a query for the updated favorite movies' list.
         */
        if(mSelectedMenuItem == R.id.action_favorite_movies)
            getSupportLoaderManager().restartLoader(FAVORITE_LIST_LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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
        mRecyclerView.setVisibility(View.VISIBLE);
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
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        setImageDownloadOption();
        mImageAdapter.setMovieData(null);
    }


    private List<Movie> getFavoriteMoviesFromCache(){
        try {
            Cursor cursor =  getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_RATING);
            return readFromCursor(cursor);
        } catch (Exception e) {
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
        }
        return null;
    }

    private List<Movie> readFromCursor(Cursor cursor){

        List<Movie> favoriteListFromCache = new ArrayList<>();

        int movieIdIndex     = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int titleIndex       = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int overviewIndex    = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int ratingIndex      = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
        int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int posterPathIndex  = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String releaseDateString = cursor.getString(releaseDateIndex);
            int movieId = cursor.getInt(movieIdIndex);
            double rating = cursor.getDouble(ratingIndex);
            String title = cursor.getString(titleIndex);
            String overview = cursor.getString(overviewIndex);
            String posterPath = cursor.getString(posterPathIndex);

            favoriteListFromCache.add(new Movie.MovieBuilder().movieId(movieId)
                    .posterPath(posterPath).overview(overview)
                    .originalTitle(title).releaseDate(releaseDateString)
                    .voteAverage(rating).build());
        }
        return favoriteListFromCache;
    }

    private List<Movie> getMoviesFromNetwork(){
        URL movieRequestUrl = NetworkUtils.buildGetMoviesUrl(mSortBy);

        try {
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

            List<Movie> simpleMovieListData = MovieDatabaseJsonUtils.getSimpleMovieListFromJson(jsonMovieResponse);

            return simpleMovieListData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
