package com.googleandroidtv.bean;

import java.io.Serializable;

/**
 * Created by cd on 11-09-2017.
 */

public class CategoryBean implements Serializable {

    String cid;
    String group_name;
    String  adult_content;
    String  geo_aval;
    String tile_image;
    String bg_image;
    String status;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getAdult_content() {
        return adult_content;
    }

    public void setAdult_content(String adult_content) {
        this.adult_content = adult_content;
    }

    public String getGeo_aval() {
        return geo_aval;
    }

    public void setGeo_aval(String geo_aval) {
        this.geo_aval = geo_aval;
    }

    public String getTile_image() {
        return tile_image;
    }

    public void setTile_image(String tile_image) {
        this.tile_image = tile_image;
    }

    public String getBg_image() {
        return bg_image;
    }

    public void setBg_image(String bg_image) {
        this.bg_image = bg_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
