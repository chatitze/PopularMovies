package com.chatitze.android.sinema.utilities;

import com.chatitze.android.sinema.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chatitze on 03/02/2017.
 */

public class MovieDatabaseJsonUtils {

    public static List<Movie> getSimpleMovieListFromJson(String moviesJsonStr)
            throws JSONException {

        /* Movie information. Each movie's info is an element of the "result" array */
        final String MD_RESULTS        = "results";
        final String MD_MOVIE_ID       = "id";
        final String MD_POSTER_PATH    = "poster_path";
        final String MD_OVERVIEW       = "overview";
        final String MD_ORIGINAL_TITLE = "original_title";
        final String MD_RATING         = "vote_average";
        final String MD_RELEASE_DATE   = "release_date";

        /* ArrayList to hold each movies' detail */
        List<Movie> parsedMovieList = new ArrayList<>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MD_RESULTS);

        for (int i = 0; i < moviesArray.length(); i++) {
            /* Get the JSON object representing the movie */
            JSONObject movie = moviesArray.getJSONObject(i);

            String posterPath        = movie.getString(MD_POSTER_PATH);
            String overview          = movie.getString(MD_OVERVIEW);
            String originalTitle     = movie.getString(MD_ORIGINAL_TITLE);
            String releaseDateString = movie.getString(MD_RELEASE_DATE);
            int movieId              = movie.getInt(MD_MOVIE_ID);
            double voteAverage       = movie.getDouble(MD_RATING);

            parsedMovieList.add(new Movie.MovieBuilder().movieId(movieId).posterPath(posterPath).overview(overview)
                    .originalTitle(originalTitle).releaseDate(releaseDateString)
                    .voteAverage(voteAverage).isFavorite(false).build());

        }

        return parsedMovieList;
    }

    public static String[] getSimpleTrailerStringsFromJson(String trailersJsonStr)
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

    public static String[] getSimpleReviewStringsFromJson(String trailersJsonStr)
            throws JSONException {

       /* Review information. Each review's info is an element of the "result" array */
        final String MD_REVIEW_RESULTS  = "results";
        final String MD_REVIEW_ID       = "id";
        final String MD_REVIEW_AUTHOR   = "author";
        final String MD_REVIEW_CONTENT  = "content";
        final String MD_REVIEW_TMDB_URL = "url";

        /* String array to hold each trailer' detail */
        String[] parsedReviewData = null;

        JSONObject reviewsJson = new JSONObject(trailersJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(MD_REVIEW_RESULTS);

        parsedReviewData = new String[reviewsArray.length()];

        for (int i = 0; i < reviewsArray.length(); i++) {
            /* Get the JSON object representing the trailer */
            JSONObject reviewRecord = reviewsArray.getJSONObject(i);

            String reviewId  = reviewRecord.getString(MD_REVIEW_ID);
            String author    = reviewRecord.getString(MD_REVIEW_AUTHOR);
            String content   = reviewRecord.getString(MD_REVIEW_CONTENT);
            String tmdbUrl   = reviewRecord.getString(MD_REVIEW_TMDB_URL);

            parsedReviewData[i] = reviewId + "_" + author + "_"
                    + tmdbUrl + "_" + content;
        }
        return parsedReviewData;
    }
}
