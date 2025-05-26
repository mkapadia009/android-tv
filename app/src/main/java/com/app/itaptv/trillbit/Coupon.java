package com.app.itaptv.trillbit;

import java.io.Serializable;

/**
 * Created by adity on 11/8/2018.
 */

public class Coupon implements Serializable{

    private Integer id;
    private String title;
    private String image;
    private String code;
    private String description;
    private String link;
    private String trilltones;


    public Coupon(Integer id, String title, String image, String code, String description, String link, String trilltones) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.code = code;
        this.description = description;
        this.link = link;
        this.trilltones = trilltones;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getTrilltones() {
        return trilltones;
    }
}
