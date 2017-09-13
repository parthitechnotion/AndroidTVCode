package com.googleandroidtv.custom_app;

import java.io.Serializable;

/**
 * Created by cd on 04-08-2017.
 */

public class SubCategoryBean implements Serializable{

    String id;
    String subgroup_name;
    String sub_adult_content;
    String sub_geo_aval;
    String sub_tile_image;
    String sub_bg_image;
    String sub_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubgroup_name() {
        return subgroup_name;
    }

    public void setSubgroup_name(String subgroup_name) {
        this.subgroup_name = subgroup_name;
    }

    public String getSub_adult_content() {
        return sub_adult_content;
    }

    public void setSub_adult_content(String sub_adult_content) {
        this.sub_adult_content = sub_adult_content;
    }

    public String getSub_geo_aval() {
        return sub_geo_aval;
    }

    public void setSub_geo_aval(String sub_geo_aval) {
        this.sub_geo_aval = sub_geo_aval;
    }

    public String getSub_tile_image() {
        return sub_tile_image;
    }

    public void setSub_tile_image(String sub_tile_image) {
        this.sub_tile_image = sub_tile_image;
    }

    public String getSub_bg_image() {
        return sub_bg_image;
    }

    public void setSub_bg_image(String sub_bg_image) {
        this.sub_bg_image = sub_bg_image;
    }

    public String getSub_status() {
        return sub_status;
    }

    public void setSub_status(String sub_status) {
        this.sub_status = sub_status;
    }
}
