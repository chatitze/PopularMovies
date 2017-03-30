package com.chatitze.android.sinema.data;

/**
 * Created by chatitze on 29/03/2017.
 */

public class Movie {

    private int movieId;
    private String overview;
    private String releaseDate;
    private int [] genreIds;
    private String originalTitle;
    private String title;
    private String originalLanguage;

    private String posterPath;
    private String backdropPath;

    private double voteAverage;
    private double popularity;
    private int voteCount;

    private boolean isVideo;
    private boolean isAdult;
    private boolean isFavorite = false;


    public Movie(MovieBuilder movieBuilder){
        movieId = movieBuilder.movieId;
        overview = movieBuilder.overview;
        releaseDate = movieBuilder.releaseDate;
        genreIds = movieBuilder.genreIds;
        originalLanguage = movieBuilder.originalLanguage;
        originalTitle = movieBuilder.originalTitle;
        title = movieBuilder.title;
        posterPath = movieBuilder.posterPath;
        backdropPath = movieBuilder.backdropPath;
        voteAverage = movieBuilder.voteAverage;
        popularity = movieBuilder.popularity;
        voteCount = movieBuilder.voteCount;
        isVideo = movieBuilder.isVideo;
        isAdult = movieBuilder.isAdult;
    }


    public static class MovieBuilder{
        private int movieId;
        private String overview;
        private String releaseDate;
        private int [] genreIds;
        private String originalTitle;
        private String title;
        private String originalLanguage;

        private String posterPath;
        private String backdropPath;

        private double voteAverage;
        private double popularity;
        private int voteCount;

        private boolean isVideo;
        private boolean isAdult;
        private boolean isFavorite;

        public Movie build(){
            return new Movie(this);
        }

        public MovieBuilder movieId(int movieId){
            this.movieId = movieId;
            return this;
        }

        public MovieBuilder overview(String overview){
            this.overview = overview;
            return this;
        }

        public MovieBuilder releaseDate(String releaseDate){
            this.releaseDate = releaseDate;
            return this;
        }

        public MovieBuilder genreIds(int [] genreIds){
            this.genreIds = genreIds;
            return this;
        }

        public MovieBuilder originalTitle(String originalTitle){
            this.originalTitle = originalTitle;
            return this;
        }

        public MovieBuilder title(String title){
            this.title = title;
            return this;
        }

        public MovieBuilder originalLanguage(String originalLanguage){
            this.originalLanguage = originalLanguage;
            return this;
        }

        public MovieBuilder posterPath(String posterPath){
            this.posterPath = posterPath;
            return this;
        }

        public MovieBuilder backdropPath(String backdropPath){
            this.backdropPath = backdropPath;
            return this;
        }

        public MovieBuilder voteAverage(double voteAverage){
            this.voteAverage = voteAverage;
            return this;
        }

        public MovieBuilder popularity(double popularity){
            this.popularity = popularity;
            return this;
        }

        public MovieBuilder voteCount(int voteCount){
            this.voteCount = voteCount;
            return this;
        }

        public MovieBuilder isVideo(boolean isVideo){
            this.isVideo = isVideo;
            return this;
        }

        public MovieBuilder isAdult(boolean isAdult){
            this.isAdult = isAdult;
            return this;
        }

        public MovieBuilder isFavorite(boolean isFavorite){
            this.isFavorite = isFavorite;
            return this;
        }
    }


    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
