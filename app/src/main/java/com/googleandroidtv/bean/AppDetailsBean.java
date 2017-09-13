package com.googleandroidtv.bean;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by cd on 20-07-2017.
 */

public class AppDetailsBean implements Serializable{

    String app_package_name;
    String category_name;
    String cid;
    String download_link;
    String download_server;
    String file_size;
    String genre_id;
    String id;
    String mirror_link;
    String mirror_server;
    String movie_casts;
    String movie_date;
    String movie_desc;
    String movie_image;
    String movie_rate_avg;
    String movie_rating;
    String movie_title;
    String movie_total_rate;
    String movie_view;
    String total_download;
    ArrayList<RelatedAppListBean> related;

    public String getApp_package_name() {
        return app_package_name;
    }

    public void setApp_package_name(String app_package_name) {
        this.app_package_name = app_package_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public String getDownload_server() {
        return download_server;
    }

    public void setDownload_server(String download_server) {
        this.download_server = download_server;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(String genre_id) {
        this.genre_id = genre_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMirror_link() {
        return mirror_link;
    }

    public void setMirror_link(String mirror_link) {
        this.mirror_link = mirror_link;
    }

    public String getMirror_server() {
        return mirror_server;
    }

    public void setMirror_server(String mirror_server) {
        this.mirror_server = mirror_server;
    }

    public String getMovie_casts() {
        return movie_casts;
    }

    public void setMovie_casts(String movie_casts) {
        this.movie_casts = movie_casts;
    }

    public String getMovie_date() {
        return movie_date;
    }

    public void setMovie_date(String movie_date) {
        this.movie_date = movie_date;
    }

    public String getMovie_desc() {
        return movie_desc;
    }

    public void setMovie_desc(String movie_desc) {
        this.movie_desc = movie_desc;
    }

    public String getMovie_image() {
        return movie_image;
    }

    public void setMovie_image(String movie_image) {
        this.movie_image = movie_image;
    }

    public URI getMovie_imageeURI(String serverImageThumb) {
        try {
            return new URI(serverImageThumb+getMovie_image().trim().replace(" ","%20"));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String getMovie_rate_avg() {
        return movie_rate_avg;
    }

    public void setMovie_rate_avg(String movie_rate_avg) {
        this.movie_rate_avg = movie_rate_avg;
    }

    public String getMovie_rating() {
        return movie_rating;
    }

    public void setMovie_rating(String movie_rating) {
        this.movie_rating = movie_rating;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getMovie_total_rate() {
        return movie_total_rate;
    }

    public void setMovie_total_rate(String movie_total_rate) {
        this.movie_total_rate = movie_total_rate;
    }

    public String getMovie_view() {
        return movie_view;
    }

    public void setMovie_view(String movie_view) {
        this.movie_view = movie_view;
    }

    public String getTotal_download() {
        return total_download;
    }

    public void setTotal_download(String total_download) {
        this.total_download = total_download;
    }

    public ArrayList<RelatedAppListBean> getRelated() {
        return related;
    }

    public void setRelated(ArrayList<RelatedAppListBean> related) {
        this.related = related;
    }
}
