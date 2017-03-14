package com.chatitze.android.sinema.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chatitze on 03/02/2017.
 */

public class MovieDatabaseJsonUtils {

    public static String[] getSimpleMovieStringsFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        /* Movie information. Each movie's info is an element of the "result" array */
        final String MD_RESULTS        = "results";
        final String MD_POSTER_PATH    = "poster_path";
        final String MD_OVERVIEW       = "overview";
        final String MD_ORIGINAL_TITLE = "original_title";
        final String MD_RATING         = "vote_average";
        final String MD_RELEASE_DATE   = "release_date";

        /* String array to hold each movies' detail */
        String[] parsedMovieData = null;

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MD_RESULTS);

        parsedMovieData = new String[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            /* Get the JSON object representing the movie */
            JSONObject movie = moviesArray.getJSONObject(i);

            String posterPath        = movie.getString(MD_POSTER_PATH);
            String overview          = movie.getString(MD_OVERVIEW);
            String originalTitle     = movie.getString(MD_ORIGINAL_TITLE);
            String releaseDateString = movie.getString(MD_RELEASE_DATE);
            double voteAverage       = movie.getDouble(MD_RATING);

            parsedMovieData[i] = posterPath + "_" + originalTitle + "_"
                    + voteAverage + "_" + releaseDateString + "_" + overview;
        }
        return parsedMovieData;
    }

}
