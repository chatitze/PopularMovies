package com.chatitze.android.sinema.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by chatitze on 02/02/2017.
 */

/**
 * These utilities is used to communicate with the network.
 */
public class NetworkUtils {

    final static String MOVIEDATABASE_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    public final static String YOUTUBE_BASE_URL =
            "https://www.youtube.com/watch";

    public final static String MOVIES_POSTER_ENDPOINT =
            "http://image.tmdb.org/t/p/w500/";

    final static String PARAM_QUERY = "api_key";
    final static String API_KEY = "YOUR_API_KEY";

    final static String VIDEO = "videos";
    final static String REVIEW= "reviews";

    public final static String WATCH_QUERY = "v";

    /*
     * Default: results are sorted by popularity.
     */
    public final static String sortByTopRated = "top_rated";
    public final static String sortByPopularity = "popular";

    /**
     * Builds the URL used to query the Movie Database.
     *
     * @param sortBy The sort field. Either popular or top rated.
     * @return The URL to use to query the Movie Database.
     */
    public static URL buildGetMoviesUrl(String sortBy) {
        Uri builtUri = Uri.parse(MOVIEDATABASE_BASE_URL).buildUpon().appendPath(sortBy)
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();

        return buildURL(builtUri);
    }

    /**
     * Builds the URL used to query the Movie Trailers.
     *
     * @param id The id of movie.
     * @return The URL to use to query the Movie Trailers.
     */
    public static URL buildGetTrailersUrl(String id){
        Uri builtUri = Uri.parse(MOVIEDATABASE_BASE_URL).buildUpon().appendPath(id)
                .appendPath(VIDEO)
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();

        return buildURL(builtUri);
    }

    /**
     * Builds the URL used to query the Movie Reviews.
     *
     * @param id The id of movie.
     * @return The URL to use to query the Movie Reviews.
     */
    public static URL buildGetReviewsUrl(String id){
        Uri builtUri = Uri.parse(MOVIEDATABASE_BASE_URL).buildUpon().appendPath(id)
                .appendPath(REVIEW)
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();

        return buildURL(builtUri);
    }

    public static URL buildURL(Uri builtUri){
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isConnectedThroughWiFi(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
