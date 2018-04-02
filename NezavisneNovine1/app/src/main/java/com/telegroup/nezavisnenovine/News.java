package com.telegroup.nezavisnenovine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZiB on 28.3.2018..
 */

public class News  implements Serializable{
    private String newsID;
    private String title;
    private String lid;
    private String author;
    private String date;
    private Bitmap profileImage;
    private Bitmap coverImage;
    private String body;
    private String profileImageUrl;
    private String coverImageUrl;
    private String category;
    private String color;

    public News() {}

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public News(String newsID, String title, String lid, String author, String date, Bitmap profileImage, Bitmap coverImage, String body) {
        this.newsID = newsID;
        this.title = title;
        this.lid = lid;
        this.author = author;
        this.date = date;
        this.profileImage = profileImage;
        this.coverImage = coverImage;
        this.body = body;
    }

    public News(String newsID, String title, String lid, String author, String date, String body) {
        this.newsID = newsID;
        this.title = title;
        this.lid = lid;
        this.author = author;
        this.date = date;
        this.body = body;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setProfileImage(String url)
    {
        this.profileImage= getBitmapFromURL(url);
    }

    public void setCoverImage(String url){
        this.profileImage= getBitmapFromURL(url);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public String getNewsID() {
        return newsID;
    }

    public String getTitle() {
        return title;
    }

    public String getLid() {
        return lid;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public String getBody() {
        return body;
    }
}
