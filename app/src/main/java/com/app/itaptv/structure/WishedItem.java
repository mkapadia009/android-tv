package com.app.itaptv.structure;


public class WishedItem {

    String id = "";
    boolean isAddedToWishList=false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAddedToWishList() {
        return isAddedToWishList;
    }

    public void setAddedToWishList(boolean isAddedToWishList) {
        this.isAddedToWishList = isAddedToWishList;
    }

}
