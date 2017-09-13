package com.googleandroidtv.bean;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by cd on 20-07-2017.
 */

public class RelatedAppListBean implements Serializable{

    String rel_id;
    String rel_movie_thumbnail;
    String rel_movie_title;
    String rel__movie_id;

    public String getRel_id() {
        return rel_id;
    }

    public void setRel_id(String rel_id) {
        this.rel_id = rel_id;
    }

    public String getRel_movie_thumbnail() {
        return rel_movie_thumbnail;
    }

    public void setRel_movie_thumbnail(String rel_movie_thumbnail) {
        this.rel_movie_thumbnail = rel_movie_thumbnail;
    }

    public URI getRelMovie_imageeURI(String serverImageThumb) {
        try {
            return new URI(serverImageThumb+getRel_movie_thumbnail().trim().replace(" ","%20"));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String getRel_movie_title() {
        return rel_movie_title;
    }

    public void setRel_movie_title(String rel_movie_title) {
        this.rel_movie_title = rel_movie_title;
    }

    public String getRel__movie_id() {
        return rel__movie_id;
    }

    public void setRel__movie_id(String rel__movie_id) {
        this.rel__movie_id = rel__movie_id;
    }
}
