package com.moviereel.domain.repositories

import com.moviereel.domain.models.movies.*
import io.reactivex.Flowable

/**
 * Interface defining methods for how the data layer can pass data to and from the Domain layer.
 * This is to be implemented by the data layer, setting the requirements for the
 * operations that need to be implemented
 */
interface MoviesRepository {

    /**
     * Get a list of now playing movies
     * @param page [Int] Page request
     * @param language [String] Language, defaults to english
     * @return [Flowable]
     */
    fun getMoviesNowPlayingList(page : Int = 1, language: String = "en-US") : Flowable<List<MovieNowPlayingModel>>

    /**
     * get a now playing movie detail
     * @param id [Int] Id of the Now playing movie
     * @return [Flowable]
     */
    fun getMovieNowPlaying(id : Int) : Flowable<MovieNowPlayingModel>

    fun getMoviesLatest(language: String) : Flowable<MovieLatestModel>

    fun getMoviesPopular(page : Int, language: String) : Flowable<List<MoviePopularModel>>

    fun getMoviesTopRated(page : Int, language: String, region: String) : Flowable<List<MovieTopRatedModel>>

    fun getMoviesUpcoming(page : Int, language: String, region: String) : Flowable<List<MovieUpcomingModel>>
}
