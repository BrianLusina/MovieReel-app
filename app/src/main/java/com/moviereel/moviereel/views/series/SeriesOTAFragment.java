package com.moviereel.moviereel.views.series;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import com.moviereel.moviereel.R;
import com.moviereel.moviereel.models.SeriesBeans;
import com.moviereel.moviereel.presenter.Contract;
import com.moviereel.moviereel.presenter.adapter.ReelAdapter;
import com.moviereel.moviereel.presenter.singletons.IsNetwork;
import com.moviereel.moviereel.presenter.singletons.RecyclerItemClickListener;
import com.moviereel.moviereel.views.movies.MovieItemDetail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Project: MovieReel
 * Package: com.moviereel.moviereel.views.series
 * Created by lusinabrian on 09/09/16 at 22:25
 * Description:
 */
public class SeriesOTAFragment extends Fragment{
    public static final String SERIESOTA_TAG = SeriesOTAFragment.class.getSimpleName();
    private ReelAdapter reelAdapter;
    private List<SeriesBeans> SeriesBeansList;
    private CoordinatorLayout coordinatorLayout;
    private OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    /*required empty constructor*/
    public SeriesOTAFragment(){}

    /**Initialize thr fragment*/
    public static Fragment newInstance() {
        SeriesOTAFragment fragment = new SeriesOTAFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadTask().execute();
        SeriesBeansList = new ArrayList<>();
        reelAdapter = new ReelAdapter(getActivity(), SeriesBeansList, R.layout.reel_item_layout);

        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadTask().execute();
            }
        });
        if(IsNetwork.isNetworkAvailable(getActivity())) {
            new LoadTask().execute();
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
        View rootView = inflater.inflate(R.layout.reelrecycler_layout, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.reel_recy_recyclerview_id);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.reel_recy_coordinator_layout);

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.reel_recy_swiperefresh_layout_id);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(reelAdapter);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(scaleAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //implement item click listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {
                        /*TODO: test*/
                        Intent moreDetails = new Intent(getActivity(), MovieItemDetail.class);
                        startActivity(moreDetails);
                    }
                }));
    }
    /**
     * Method to load movies task, works on a separate thread*/
    private class LoadTask extends AsyncTask<String, Void, String> {
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
            String url = Contract.NOW_PLAYING;

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                try {
                    /*PASS the response to the JSONObject*/
                    JSONObject jsonObject = new JSONObject(res);
                    /*get the results array from the JSON object*/
                    JSONArray result = jsonObject.getJSONArray("results");

                    /*iterate the result array and add the loop through the objects
                    * obtain the JSONObjects storing the relevant data to variables*/
                    for(int x = 0; x < result.length(); x++){
                        JSONObject jObject = result.getJSONObject(x);
                        String poster_path = Contract.MOVIE_POSTER_PATH + jObject.getString("poster_path");
                        String backdrop_path = Contract.MOVIE_POSTER_PATH+ jObject.getString("backdrop_path");
                        String overview = jObject.getString("overview");
                        String release_date = jObject.getString("release_date");
                        JSONArray genre_ids = jObject.getJSONArray("genre_ids");
                        int id = jObject.getInt("id");
                        String title =jObject.getString("original_title");
                        double popularity = jObject.getDouble("popularity");
                        int vote_count = jObject.getInt("vote_count");

                        //Store data in sharedpreferences
                        String data = poster_path + " " + backdrop_path + " " + overview + " " + release_date + " " + genre_ids + " " + String.valueOf(id) + " " + title + " " +  String.valueOf(popularity)+ " " + String.valueOf(vote_count);

                        SeriesBeans seriesBeans = new SeriesBeans(poster_path,overview,release_date,new int[]{}, id, title,backdrop_path,popularity,vote_count);
                        SeriesBeansList.add(seriesBeans);

                        /**Get an instance of the shared preferences create and access the MovieData
                         * Store the data only to the application*/
                        SharedPreferences movieData = getActivity().getSharedPreferences("MovieData",0);

                        //create an editor
                        SharedPreferences.Editor editor = movieData.edit();

                        //add data to it
                        editor.putString("PosterPath", poster_path);
                        editor.putString("BackdropPath", backdrop_path);
                        editor.putString("Overview", overview);
                        editor.putString("ReleaseDate", release_date);
                        editor.putInt("Id", id);
                        editor.putString("Title", title);
                        editor.putInt("Popularity", (int)popularity);
                        editor.putInt("VoteCount", vote_count);

                        //apply these edits
                        editor.apply();
                        Log.d(SERIESOTA_TAG, data);
                        Log.d(SERIESOTA_TAG+"Editor", String.valueOf(editor));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(SERIESOTA_TAG, e.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(SERIESOTA_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
        }
    }
}