/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailsDescriptionPresenter extends NewsAbstractPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        //Movie movie = (Movie) item;
        News news = (News) item;
        String desc= Html.fromHtml(news.getBody()).toString();
        if (news != null) {

            viewHolder.getmTitle().setText(news.getTitle());
            String authorDate = "Autor: " + news.getAuthor() + " | Datum: " + news.getDate();
            viewHolder.getmAuthor().setText(authorDate);
            viewHolder.getmLid().setText(news.getLid());
            viewHolder.getmDesc().setText(desc);
            viewHolder.getmDesc().setMovementMethod(new ScrollingMovementMethod());


        }
    }
}
