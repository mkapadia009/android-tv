package com.app.itaptv.structure;

public class MoreMenuData {

    // TODO: - Actions to be defined...
    public static int ACTION_NONE = 0;

    public String MenuName;
    public int img;
    public int action;
    public String id;
    public boolean isFocused=false;

    public MoreMenuData(String id,String menuName, int img) {
        this.id=id;
        this.MenuName = menuName;
        this.img = img;
    }

}
