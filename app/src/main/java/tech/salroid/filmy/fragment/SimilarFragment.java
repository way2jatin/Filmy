package tech.salroid.filmy.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.SimilarMovieActivityAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.data_classes.SimilarMoviesData;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork;

/**
 * Created by salroid on 11/1/2016.
 */

public class SimilarFragment extends Fragment implements SimilarMovieActivityAdapter.ClickListener {

    private String similar_json;
    private String movieId, movieTitle;

    @BindView(R.id.similar_recycler)
    RecyclerView similar_recycler;

    @BindView(R.id.breathingProgressFragment)
    BreathingProgress breathingProgress;

    @BindView(R.id.card_holder)
    TextView card_holder;

    public static SimilarFragment newInstance(String movie_Id, String movie_Title) {
        SimilarFragment fragment = new SimilarFragment();
        Bundle args = new Bundle();
        args.putString("movie_id", movie_Id);
        args.putString("movie_title", movie_Title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.similar_fragment, container, false);
        ButterKnife.bind(this, view);

        similar_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        similar_recycler.setNestedScrollingEnabled(false);

        similar_recycler.setVisibility(View.INVISIBLE);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle savedBundle = getArguments();

        if (savedBundle != null) {

            movieId = savedBundle.getString("movie_id");
            movieTitle = savedBundle.getString("movie_title");

        }


        if (movieId != null)
            getSimilarFromNetwork(movieId);

    }


    public void getSimilarFromNetwork(String movieId) {

        final String BASE_MOVIE_CAST_DETAILS = new String(" https://api.themoviedb.org/3/movie/" +movieId+ "/similar?api_key=b640f55eb6ecc47b3433cfe98d0675b1");
        JsonObjectRequest jsonObjectRequestForMovieCastDetails = new JsonObjectRequest(Request.Method.GET, BASE_MOVIE_CAST_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        similar_json = response.toString();
                        similar_parseOutput(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

                breathingProgress.setVisibility(View.GONE);

            }
        }
        );


        TmdbVolleySingleton volleySingleton = TmdbVolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();
        requestQueue.add(jsonObjectRequestForMovieCastDetails);
    }


    private void similar_parseOutput(String similar_result) {

        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(getActivity(), similar_result);

        List<SimilarMoviesData> similar_list = par.parse_similar_movies();

        SimilarMovieActivityAdapter similar_adapter = new SimilarMovieActivityAdapter(getActivity(), similar_list, true);
        similar_adapter.setClickListener(this);
        similar_recycler.setAdapter(similar_adapter);

         if (similar_list.size() == 0) {
            card_holder.setVisibility(View.INVISIBLE);
        }

        breathingProgress.setVisibility(View.GONE);
        similar_recycler.setVisibility(View.VISIBLE);


    }

    @Override
    public void itemClicked(SimilarMoviesData setterGetter, int position, View view) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("id", setterGetter.getMovie_id());
        intent.putExtra("network_applicable", true);
        intent.putExtra("activity", false);

            startActivity(intent);

    }
}