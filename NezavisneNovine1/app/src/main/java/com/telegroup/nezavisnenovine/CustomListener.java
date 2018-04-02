package com.telegroup.nezavisnenovine;

import android.support.v17.leanback.widget.ArrayObjectAdapter;

import com.android.volley.Response;

/**
 * Created by ZiB on 28.3.2018..
 */

public class CustomListener implements Response.Listener {
    private String url;
    @Override
    public void onResponse(Object response) {

    }
    public CustomListener(String url, CardPresenter cardPresenter, ArrayObjectAdapter adapter){
        this.url=url;
    }

}
