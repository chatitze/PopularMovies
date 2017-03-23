package com.chatitze.android.sinema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.chatitze.android.sinema.data.SinemaPreferences;
import com.chatitze.android.sinema.utilities.MovieDatabaseJsonUtils;
import com.chatitze.android.sinema.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ImageAdapter.ImageAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]>, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private String mSortBy = NetworkUtils.sortByPopularity;

    private String [] mMovieDataFromAsyncTask;

    private static final int MOVIES_LOADER_ID = 0;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

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
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = MOVIES_LOADER_ID;
        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;

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
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);


        /*
         * We won't be using FetchMoviesTask anymore
         */
        //loadMoviesData();

        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * This method will get the user's preferred sort option, and then tell some
     * background method to get the movie data in the background.
     */
//    private void loadMoviesData() {
//        showMoviesDataView();
//        new FetchMoviesTask().execute(mSortBy);
//    }

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
        mImageAdapter.setImageData(null);
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
        final String[] movieDetails = mMovieDataFromAsyncTask[clickedMovieIndex].split("_");
        final Intent startMovieDetailsActivityIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        startMovieDetailsActivityIntent.putExtra(Intent.EXTRA_TEXT, movieDetails);
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
    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our weather data */
            String[] mMovieData = null;

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
             * from MovieDatabase in the background.
             *
             * @return Weather data from MovieDatabase as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground() {
                URL movieRequestUrl = NetworkUtils.buildGetMoviesUrl(mSortBy);

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

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {
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
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMoviesDataView();
            String [] imageUrls = new String[data.length];

            for (int i = 0; i < data.length; i++){
                String[] myMovieDetails = data[i].split("_");
                imageUrls[i] = myMovieDetails[0];
            }
            setImageDownloadOption();
            mImageAdapter.setImageData(imageUrls);
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
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    private void setImageDownloadOption(){
        if(SinemaPreferences.isPreferredImageDownload_WifiOnly(this))
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
        switch (item.getItemId()){
            case R.id.action_sortByPopularity:
                mSortBy = NetworkUtils.sortByPopularity;
                break;
            case R.id.action_sortByTopRated:
                mSortBy = NetworkUtils.sortByTopRated;
                break;
            case R.id.action_refresh:
                invalidateData();
                break;
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        //loadMoviesData();
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
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

//    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            /* If there's no sortBy, there's nothing to look up. */
//            if (params.length == 0) {
//                return null;
//            }
//            URL movieRequestUrl = NetworkUtils.buildUrl(params[0]);
//
//            try {
//                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
//
//                String[] simpleJsonMovieData = MovieDatabaseJsonUtils
//                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);
//
//                return simpleJsonMovieData;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(final String[] movieData) {
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (movieData != null) {
//                showMoviesDataView();
//                String [] imageUrls = new String[movieData.length];
//
//                for (int i = 0; i < movieData.length; i++){
//                    String[] myMovieDetails = movieData[i].split("_");
//                    imageUrls[i] = myMovieDetails[0];
//                }
//                mImageAdapter.setImageData(imageUrls);
//                mMovieDataFromAsyncTask = movieData;
//            } else {
//                showErrorMessage();
//            }
//        }
//    }

}
