package com.moviereel.ui.entertain.movie.toprated

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moviereel.presentation.view.entertain.movie.toprated.TopRatedPresenter
import com.moviereel.presentation.view.entertain.movie.toprated.TopRatedView
import com.moviereel.ui.entertain.base.EntertainPageBaseFragment
import com.moviereel.utils.listeners.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.fragment_entertainment_page.view.*
import org.koin.android.ext.android.inject
import javax.inject.Inject

/**
 * @author lusinabrian on 12/04/17
 */

class MovieTopRatedFrag : EntertainPageBaseFragment(), TopRatedView {

    val topRatedPresenter: TopRatedPresenter<TopRatedView> by inject()

//    @Inject
//    lateinit var movieTopRatedAdapter: MovieTopRatedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        topRatedPresenter.onAttach(this)

        setUp(rootView)

        return rootView
    }

    /**
     * Used to setup views in this fragment
     */
    override fun setUp(view: View) {
        super.setUp(view)
        with(view) {
            // fragRecyclerView.adapter = movieTopRatedAdapter

            endlessScrollListener = object : EndlessRecyclerViewScrollListener(gridLinearLayoutManager) {

                override fun onLoadMore(page: Int, totalItemsCount: Int, recyclerView: RecyclerView) {
                    topRatedPresenter.onLoadMoreFromApi(page)
                }
            }

            recycler_view_entertainment.addOnScrollListener(endlessScrollListener)
        }

        topRatedPresenter.onViewInitialized()
    }

    override fun onRefresh() {
        topRatedPresenter.onSwipeRefreshTriggered()
    }

//    override fun updateTopRatedMovies(movieTopRatedArrList: List<MovieTopRatedEntity>) {
//        val data = arrayListOf<MovieTopRatedEntity>()
//        data += movieTopRatedArrList
//        movieTopRatedAdapter.addItemsUsingDiff(data)
//    }
}
