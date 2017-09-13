package com.googleandroidtv.bean;

import java.io.Serializable;

/**
 * Created by cd on 04-08-2017.
 */

public class RelatedApplicationListBean implements Serializable{

     String rel_id;
    String rel_app_name;
    String rel_app_id;
    String rel_icon_image;

    public String getRel_id() {
        return rel_id;
    }

    public void setRel_id(String rel_id) {
        this.rel_id = rel_id;
    }

    public String getRel_app_name() {
        return rel_app_name;
    }

    public void setRel_app_name(String rel_app_name) {
        this.rel_app_name = rel_app_name;
    }

    public String getRel_app_id() {
        return rel_app_id;
    }

    public void setRel_app_id(String rel_app_id) {
        this.rel_app_id = rel_app_id;
    }

    public String getRel_icon_image() {
        return rel_icon_image;
    }

    public void setRel_icon_image(String rel_icon_image) {
        this.rel_icon_image = rel_icon_image;
    }
}
