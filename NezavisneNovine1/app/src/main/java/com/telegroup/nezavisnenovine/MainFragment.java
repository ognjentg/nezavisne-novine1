/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.telegroup.nezavisnenovine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLS = 15;
    private static News temp = null;

    private static HashMap<String, String> backgrounds = new HashMap<>();

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
   public String  REQUEST_TAG = "com.androidtutorialpoint.volleyStringRequest";
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();
        getCategories();
       // loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }
    private void getCategories(){
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        String url = "http://dtp.nezavisne.com/app/meni";
        final CardPresenter cardPresenter = new CardPresenter();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int length= response.length();
                for(int i=0; i< length; i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String name = obj.getString("Naziv");
                        String menuID = obj.getString("meniID");
                        String color= obj.getString("Boja");
                        backgrounds.put(menuID, color);
                        String url2= "http://dtp.nezavisne.com/app/rubrika/"+menuID+ "/1/10";
                        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                        HeaderItem header = new HeaderItem(i, name);
                        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                int length= response.length();
                                for(int i=0; i< length; i++){
                                    try {
                                        JSONObject obj = response.getJSONObject(i);
                                        String newsID = obj.getString("vijestID");
                                        String title = obj.getString("Naslov");
                                        String lid = obj.getString("Lid");
                                        String author = obj.getString("Autor");
                                        String date = obj.getString("Datum");
                                        String image = obj.getString("Slika");

                                        News news= new News(newsID,title, lid, author, date, null );
                                        news.setProfileImageUrl(image);

                                        listRowAdapter.add(news);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest2, REQUEST_TAG);

                       // ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                         mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest, REQUEST_TAG);
        setAdapter(mRowsAdapter);
    }


    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mBackgroundManager.setDrawable(mDefaultBackground);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
         setBadgeDrawable(getActivity().getResources().getDrawable(
        R.drawable.nezavisne_main_fragmet_bigger));
        //setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
//        setOnSearchClickedListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
//                        .show();
//            }
//        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(final Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            News xx= (News) item;
            temp = new News();
            temp.setProfileImageUrl(xx.getProfileImageUrl());
            if (item instanceof News) {

                News news =(News) item;
                Log.d(TAG, "Item: " + item.toString());

                String url2 = "http://dtp.nezavisne.com/app/v2/vijesti/" + news.getNewsID();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // int length= response.length();
                        try {
                            JSONObject obj = response;
                            JSONArray imageArray = obj.getJSONArray("Slika");
                            JSONObject image = imageArray.getJSONObject(0);
                            String imageUrl = image.getString("slikaURL");
                            String body = obj.getString("Tjelo");
                            String lid = obj.getString("Lid");
                            String id = obj.getString("vijestID");
                            String naslov = obj.getString("Naslov");
                            String autor = obj.getString("Autor");
                            String datum = obj.getString("Datum");
                            String category= obj.getString("meniRoditelj");
                            String color = obj.getString("meniRoditeljBoja");
                            Log.d(TAG, imageUrl + "\n");
                            Log.d(TAG, body + "\n");
                            Log.d(TAG, lid + "\n");
                            Log.d(TAG, id + "\n");
                            Log.d(TAG, naslov + "\n");
                            Log.d(TAG, autor + "\n");
                            Log.d(TAG, datum + "\n");

                            temp.setLid(lid);
                            temp.setBody(body);
                            temp.setCoverImageUrl(imageUrl);
                            temp.setNewsID(id);
                            temp.setTitle(naslov);
                            temp.setAuthor(autor);
                            temp.setDate(datum);
                            temp.setCategory(category);
                            temp.setColor(color);
                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                            intent.putExtra(DetailsActivity.NEWS, temp);

                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    getActivity(),
                                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                    DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                            getActivity().startActivity(intent, bundle);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest, REQUEST_TAG);

                news.setLid(temp.getLid());
                news.setBody(temp.getBody());
                news.setCoverImageUrl(temp.getCoverImageUrl());
                news.setNewsID(temp.getNewsID());
                news.setTitle(temp.getTitle());
                news.setAuthor(temp.getAuthor());
                news.setDate(temp.getDate());
                news.setCategory(temp.getCategory());
                news.setColor(temp.getColor());

            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof News) {
                mBackgroundUri = backgrounds.get(((News) item).getCategory());
                startBackgroundTimer();
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mBackgroundManager.setDrawable(mDefaultBackground);
   }

}
