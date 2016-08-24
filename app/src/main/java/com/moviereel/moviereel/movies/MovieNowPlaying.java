package com.moviereel.moviereel.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.moviereel.moviereel.APIUrlEndpoints;
import com.moviereel.moviereel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Project: Movie Reel
 * Package: com.moviereel.moviereel.movies
 * Created by lusinabrian on 22/08/16 at 20:32
 * <p/>
 * Description: displays the latest movies
 */
public class MovieNowPlaying extends Fragment{
    private static final String MOVIENOW_PLAYING_TAG = MovieNowPlaying.class.getSimpleName();
    private MovieAdapter movieAdapter;
    private List<MovieModel> foodModelList;
    private CoordinatorLayout coordinatorLayout;
    private OkHttpClient client = new OkHttpClient();

    public MovieNowPlaying(){}

    public static Fragment newInstance(){
        MovieNowPlaying movieNowPlaying = new MovieNowPlaying();
        movieNowPlaying.setRetainInstance(true);
        return movieNowPlaying;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadMoviesTask loadMovies = new LoadMoviesTask();
        if(isNetworkAvailable()) {
            loadMovies.execute();
        }else{
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getString(R.string.snackbar_warning_no_internet_conn), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.snackbar_no_internet_conn_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, getString(R.string.snackbar_no_internet_conn_retry), Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });
            snackbar.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movierecy_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.movie_recy_recyclerview_id);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.movie_recy_coordinator_layout);

        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.movie_recy_swiperefresh_layout_id);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.dark_slate_blue,
                R.color.dark_slate_gray,
                R.color.dark_cyan,
                R.color.dark_yellow,
                R.color.dark_turquoise,
                R.color.dark_sea_green);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(movieAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(scaleAdapter);
        return rootView;
    }

    /**
     Method to check network availability
     Using ConnectivityManager to check for Network Connection
     * */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * Method to load movies task, works on a separate thread*/
    private class LoadMoviesTask extends AsyncTask<String, Void, String> {
        APIUrlEndpoints APIURLs = new APIUrlEndpoints();
        SweetAlertDialog progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.cadet_blue));
            progressDialog.setTitleText("Hold on");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Request request = new Request.Builder()
                            .url(APIURLs.getNowPlaying())
                            .build();
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                //TODO: Creating a JSON object, parsing through it, adding the data to a model and the adapter
                try {
                    /*PASS the response to the JSONObject*/
                    JSONObject jsonObject = new JSONObject(res);
                    /*get the results array from the JSON object*/
                    JSONArray result = jsonObject.getJSONArray("results");

                    /*iterate the result array and add the loop through the objects
                    * obtain the JSONObjects storing the relevant data to variables*/
                    for(int x = 0; x < result.length(); x++){
                        JSONObject jObject = result.getJSONObject(x);
                        String poster_path = jObject.getString("poster_path");
                        String backdrop_path = jObject.getString("backdrop_path");
                        String overview = jObject.getString("overview");
                        String release_date = jObject.getString("release_date");
                        JSONArray genre_ids = jObject.getJSONArray("genre_ids");
                        int id = jObject.getInt("id");
                        String title =jObject.getString("original_title");
                        double popularity = jObject.getDouble("popularity");
                        int vote_count = jObject.getInt("vote_count");

                        String data = poster_path + " " + backdrop_path + " " + overview + " " + release_date + " " + genre_ids + " " + String.valueOf(id) + " " + title + " " +  String.valueOf(popularity)+ " " + String.valueOf(vote_count);

                        Log.d(MOVIENOW_PLAYING_TAG, data);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(MOVIENOW_PLAYING_TAG, e.toString());
                }
                Log.d(MOVIENOW_PLAYING_TAG, res);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TODO: post results and kill of spinner
            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
        }
    }

/*END*/
}