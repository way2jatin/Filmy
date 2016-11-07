package tech.salroid.filmy.activities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import tech.salroid.filmy.network_stuff.VolleySingleton;

/**
 * Created by salroid on 11/5/2016.
 */

public class Rating {

    public static String imdb_rating = "0",tomatometer_rating="0",audience_rating="0",metascore_rating="0",image=null;

    public static void getRating(final Context context, String movie_id_final) {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();


        String BASE_RATING_URL = "http://www.omdbapi.com/?i=" + movie_id_final + "&tomatoes=true&r=json";

        JsonObjectRequest jsonObjectRequestForMovieDetails = new JsonObjectRequest(Request.Method.GET, BASE_RATING_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            imdb_rating = response.getString("imdbRating");
                            tomatometer_rating=response.getString("tomatoRating");
                            audience_rating=response.getString("tomatoUserRating");
                            metascore_rating=response.getString("Metascore");
                            image=response.getString("tomatoImage");

                            setRatingCallback(context,imdb_rating,tomatometer_rating,audience_rating,metascore_rating,image);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            setRatingCallback(context,imdb_rating,tomatometer_rating,audience_rating,metascore_rating,image);

                        }

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequestForMovieDetails);

    }


    private static void setRatingCallback(Context context,String imdb_rating,String tomatometer_rating,String audience_rating,String metascore_rating,String image) {

        ((MovieDetailsActivity)context).setRating(imdb_rating,tomatometer_rating,audience_rating,metascore_rating,image);

    }

}