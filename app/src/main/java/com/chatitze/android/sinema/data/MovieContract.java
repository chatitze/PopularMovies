package com.chatitze.android.sinema.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chatitze on 14/03/2017.
 *
 *  Defines table and column names for the movies database. This class is not necessary, but keeps
 * the code organized.
 */

public class MovieContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.chatitze.android.sinema";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "movies" directory
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        // MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        /* Used internally as the name of our movies table. */
        public static final String TABLE_NAME = "movies";

        /* Movie ID as returned by API, used to identify the icon to be used */
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* Release date of a movie */
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* Original title */
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        /* Overview */
        public static final String COLUMN_OVERVIEW = "overview";

        /* Rating */
        public static final String COLUMN_RATING = "rating";

        /* Poster path */
        public static final String COLUMN_POSTER_PATH = "poster_path";


        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        movies
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        | _id  |  original_title  | release_date | movie_id | rating |         overview            |   poster_path   |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |  1   |       Life       |  2016-06-18  | 328111   |  5.8   |   The quiet life of a ...   |WLQN5aiQG8R8K.jpg|
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |  2   |        Sing      |  2017-01-05  | 335797   |  6.7   |   Explore the mysterious... |eeKwixW7pAR8K.jpg|
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        | 43   |      Arrival     |  2016-09-23  | 329865   | 6.9    | Interstellar chronicles...  |wixW7pARQG8R8K.jpg|
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

         */
    }
}
