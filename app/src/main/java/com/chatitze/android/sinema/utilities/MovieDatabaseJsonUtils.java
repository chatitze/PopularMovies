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
        final String MD_MOVIE_ID       = "id";
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
            int movieId              = movie.getInt(MD_MOVIE_ID);
            double voteAverage       = movie.getDouble(MD_RATING);

            parsedMovieData[i] = posterPath + "_" + originalTitle + "_"
                    + voteAverage + "_" + releaseDateString + "_" + overview+ "_" + movieId;
        }
        return parsedMovieData;
    }

    public static String[] getSimpleTrailerStringsFromJson(Context context, String trailersJsonStr)
            throws JSONException {

        /* Trailer information. Each trailer's info is an element of the "result" array */
        final String MD_TRAILER_RESULTS  = "results";
        final String MD_TRAILER_ID       = "id";
        final String MD_TRAILER_KEY      = "key";
        final String MD_TRAILER_NAME     = "name";
        final String MD_TRAILER_SITE     = "site";
        final String MD_TRAILER_SIZE     = "size";
        final String MD_TRAILER_TYPE     = "type";

        /* String array to hold each trailer' detail */
        String[] parsedTrailerData = null;

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(MD_TRAILER_RESULTS);

        parsedTrailerData = new String[trailersArray.length()];

        for (int i = 0; i < trailersArray.length(); i++) {
            /* Get the JSON object representing the trailer */
            JSONObject trailerRecord = trailersArray.getJSONObject(i);

            String trailerId = trailerRecord.getString(MD_TRAILER_ID);
            String key       = trailerRecord.getString(MD_TRAILER_KEY);
            String name      = trailerRecord.getString(MD_TRAILER_NAME);
            String site      = trailerRecord.getString(MD_TRAILER_SITE);
            String type      = trailerRecord.getString(MD_TRAILER_TYPE);
            int size         = trailerRecord.getInt(MD_TRAILER_SIZE);

            parsedTrailerData[i] = trailerId + "_" + key + "_"
                    + name + "_" + site + "_" + type+ "_" + size;
        }
        return parsedTrailerData;
    }

}
