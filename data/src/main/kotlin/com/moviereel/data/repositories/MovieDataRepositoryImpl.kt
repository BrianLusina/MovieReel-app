package com.moviereel.data.repositories

import com.moviereel.data.mapper.movies.MovieNowPlayingDataMapper
import com.moviereel.data.models.movies.MovieNowPlayingDataEntity
import com.moviereel.data.source.movies.MovieDataStoreFactory
import com.moviereel.data.source.movies.stores.MovieRemoteDataStore
import com.moviereel.domain.models.movies.*
import com.moviereel.domain.repositories.MoviesDomainRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Provides an implementation of the [MoviesDomainRepository] interface for communicating to and from
 * data sources
 */
class MovieDataRepositoryImpl
@Inject
constructor(
        val factory: MovieDataStoreFactory,
        val movieNowPlayingDataMapper: MovieNowPlayingDataMapper)
    : MoviesDomainRepository {

    private fun saveMoviesNowPlayingEntities(moviesNowPlaying: List<MovieNowPlayingDataEntity>): Completable {
        return factory.retrieveCacheDataStore().saveMoviesNowPlaying(moviesNowPlaying)
    }

    private fun saveMovieNowPlayingEntity(movieNowPlaying : MovieNowPlayingDataEntity) : Completable {
        return factory.retrieveCacheDataStore().saveMovieNowPlaying(movieNowPlaying)
    }

    override fun clearAllMovies(): Completable {
        val cacheDataStore = factory.retrieveCacheDataStore()
        return cacheDataStore.clearAllMovies()
    }

    override fun saveMoviesNowPlaying(moviesNowPlayingCache: List<MovieNowPlayingDomainModel>): Completable {
        val movieNowPlayingEntities = moviesNowPlayingCache.map {
            movieNowPlayingDataMapper.mapToEntity(it)
        }
        return saveMoviesNowPlayingEntities(movieNowPlayingEntities)
    }

    override fun clearMoviesNowPlaying(): Completable {
        val cacheDataStore = factory.retrieveCacheDataStore()
        return cacheDataStore.clearMoviesNowPlaying()
    }

    override fun getMoviesNowPlayingList(page: Int, language: String): Flowable<List<MovieNowPlayingDomainModel>> {
        val dataStore = factory.retrieveDataStore()
        return dataStore.getMoviesNowPlaying(page, language)
                .toFlowable()
                .flatMap {
                    if (dataStore is MovieRemoteDataStore) {
                        saveMoviesNowPlayingEntities(it).toFlowable()
                    } else {
                        Flowable.just(it)
                    }
                }.map { list ->
                    list.map { listItem ->
                        movieNowPlayingDataMapper.mapFromEntity(listItem)
                    }
                }
    }

    override fun getMovieNowPlaying(id: Long): Flowable<MovieNowPlayingDomainModel> {
        val dataStore = factory.retrieveDataStore()
        return dataStore.getMovieNowPlaying(id)
                .toFlowable()
                .flatMap {
                    if (dataStore is MovieRemoteDataStore) {
                        saveMovieNowPlayingEntity(it).toFlowable()
                    } else {
                        Flowable.just(it)
                    }
                }
                .map {
                    movieNowPlayingDataMapper.mapFromEntity(it)
                }
    }

    // TODO: implement below

    override fun getMoviesLatest(language: String): Flowable<MovieLatestModel> {
        // val dataStore = factory.retrieveDataStore()
        return Flowable.just(MovieLatestModel(
                "", 0, arrayListOf(), "","", arrayListOf(), arrayListOf(),
                0, 0, arrayListOf(), "", ""
        ))
    }

    override fun getMoviesPopular(page: Int, language: String): Flowable<List<MoviePopularModel>> {
        return Flowable.just(listOf())
    }

    override fun getMoviesTopRated(page: Int, language: String, region: String): Flowable<List<MovieTopRatedModel>> {
        return Flowable.just(listOf())
    }

    override fun getMoviesUpcoming(page: Int, language: String, region: String): Flowable<List<MovieUpcomingModel>> {
        return Flowable.just(listOf())
    }
}