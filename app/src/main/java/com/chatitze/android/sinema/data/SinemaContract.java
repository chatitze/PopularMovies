package com.chatitze.android.sinema.data;

import android.provider.BaseColumns;

/**
 * Created by chatitze on 14/03/2017.
 *
 *  Defines table and column names for the movies database. This class is not necessary, but keeps
 * the code organized.
 */

public class SinemaContract {

    public static final class SinemaEntry implements BaseColumns {
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

    }
}
