package com.movieappjc.data.remote

import com.movieappjc.app.common.utils.KeyUtils
import com.movieappjc.data.models.CastResultModel
import com.movieappjc.data.models.MovieCreditsResultModel
import com.movieappjc.data.models.MovieDetailModel
import com.movieappjc.data.models.MoviesResultModel
import com.movieappjc.data.models.PersonModel
import com.movieappjc.data.models.VideoResultModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieServices {

    @GET("trending/movie/day")
    suspend fun getTrending(
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MoviesResultModel

    @GET("movie/popular")
    suspend fun getPopular(
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MoviesResultModel

    @GET("movie/upcoming")
    suspend fun getComingSoon(
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MoviesResultModel

    @GET("movie/now_playing")
    suspend fun getPlayingNow(
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MoviesResultModel

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MovieDetailModel

    @GET("movie/{movie_id}/credits")
    suspend fun getCastCrew(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): CastResultModel
    
    @GET("movie/{movie_id}/videos")
    suspend fun getTrailerVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): VideoResultModel
    
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MoviesResultModel

    @GET("person/{person_id}")
    suspend fun getPersonDetail(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): PersonModel

    @GET("person/{person_id}/movie_credits")
    suspend fun getMovieCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = KeyUtils.apiKey(),
        @Query("language") language: String? = null
    ): MovieCreditsResultModel
}
