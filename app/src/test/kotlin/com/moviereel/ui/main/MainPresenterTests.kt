package com.moviereel.ui.main

import com.moviereel.TestSchedulerProvider
import com.moviereel.data.DataManager
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author lusinabrian on 19/04/17.
 * * Tests for [MainPresenterImpl]. Checks if the methods set out are actually being called
 */

@RunWith(MockitoJUnitRunner::class)
class MainPresenterTests {
    @Mock lateinit var mMockMainView: MainView
    @Mock lateinit var mMockDataManager: DataManager
    lateinit var mTestScheduler: TestScheduler

    lateinit var mainPresenter: MainPresenter<MainView>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val compositeDisposable = CompositeDisposable()
        mTestScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(mTestScheduler)
        mainPresenter = MainPresenterImpl(mMockDataManager, testSchedulerProvider, compositeDisposable)
        mainPresenter.onAttach(mMockMainView)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        RxAndroidPlugins.reset()
        mainPresenter.onDetach()
    }

    @Test
    fun testShouldDisplayNowPlayingMoviesFragment() {
        mMockMainView.showMoviesFragment()

        verify<MainView>(mMockMainView).showMoviesFragment()
    }

    @Test
    fun testShouldShowLatestSeriesFragment() {
        mMockMainView.showLatestSeriesFragment()

        verify<MainView>(mMockMainView).showLatestSeriesFragment()
    }

    @Test
    fun testShouldShowOnTheAirSeriesFragment() {
        mMockMainView.showOnTheAirSeriesFragment()

        verify<MainView>(mMockMainView).showOnTheAirSeriesFragment()
    }

    @Test
    fun testShouldShowAiringTodaySeriesFragment() {
        mMockMainView.showAiringTodaySeriesFragment()

        verify<MainView>(mMockMainView).showAiringTodaySeriesFragment()
    }

    @Test
    fun testShouldShowTopRatedSeriesFragment() {
        mMockMainView.showTopRatedSeriesFragment()

        verify<MainView>(mMockMainView).showTopRatedSeriesFragment()
    }

    @Test
    fun testShouldShowPopularSeriesFragment() {
        mMockMainView.showPopularSeriesFragment()

        verify<MainView>(mMockMainView).showPopularSeriesFragment()
    }

    @Test
    fun testShouldShowHelpSection() {
        mMockMainView.showHelpSection()

        verify<MainView>(mMockMainView).showHelpSection()
    }

    @Test
    fun testShouldShowSettingsScreen() {
        mMockMainView.showSettingsScreen()

        verify<MainView>(mMockMainView).showSettingsScreen()
    }

    @Test
    fun testShouldShowAboutFragment() {
        mMockMainView.showAboutFragment()

        verify<MainView>(mMockMainView).showAboutFragment()
    }

    companion object {

        @BeforeClass
        @Throws(Exception::class)
        fun onlyOnce() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }
}
