package com.moviereel.ui.entertain.movie.toprated

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moviereel.data.db.entities.movie.MovieTopRatedEntity
import com.moviereel.ui.entertain.base.EntertainPageBaseFragment
import com.moviereel.utils.listeners.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_entertainment_page.view.*
import javax.inject.Inject

/**
 * @author lusinabrian on 12/04/17
 */

class MovieTopRatedFrag : EntertainPageBaseFragment(), MovieTopRatedView {

    @Inject
    lateinit var movieTopRatedPresenter: MovieTopRatedPresenter<MovieTopRatedView>

    @Inject
    lateinit var movieTopRatedAdapter: MovieTopRatedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activityComponent.inject(this)

        super.onCreateView(inflater, container, savedInstanceState)

        movieTopRatedPresenter.onAttach(this)

        setUp(rootView)

        return rootView
    }

    /**
     * Used to setup views in this fragment
     */
    override fun setUp(view: View) {
        super.setUp(view)
        with(view) {
            fragRecyclerView.adapter = movieTopRatedAdapter

            mEndlessScrollListener = object : EndlessRecyclerViewScrollListener(mGridLinearLayoutManager) {

                override fun onLoadMore(page: Int, totalItemsCount: Int, recyclerView: RecyclerView) {
                    movieTopRatedPresenter.onLoadMoreFromApi(page)
                }
            }

            fragRecyclerView.addOnScrollListener(mEndlessScrollListener)
        }

        movieTopRatedPresenter.onViewInitialized()
    }

    override fun onRefresh() {
        movieTopRatedPresenter.onSwipeRefreshTriggered()
    }

    override fun updateTopRatedMovies(movieTopRatedArrList: List<MovieTopRatedEntity>) {
        val data = arrayListOf<MovieTopRatedEntity>()
        data += movieTopRatedArrList
        movieTopRatedAdapter.addItemsUsingDiff(data)
    }
}
