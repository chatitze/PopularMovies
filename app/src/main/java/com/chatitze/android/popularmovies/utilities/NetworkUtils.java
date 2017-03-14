package com.chatitze.android.popularmovies.utilities;

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

    public final static String MOVIES_POSTER_ENDPOINT =
            "http://image.tmdb.org/t/p/w500/";

    final static String PARAM_QUERY = "api_key";
    final static String API_KEY = "YOUR_API_KEY";

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
    public static URL buildUrl(String sortBy) {
        Uri builtUri = Uri.parse(MOVIEDATABASE_BASE_URL).buildUpon().appendPath(sortBy)
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();

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
