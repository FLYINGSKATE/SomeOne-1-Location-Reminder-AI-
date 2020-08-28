package com.someone.someone_v2.model;

public class Model {
    private String item_name;
    private int ImageSrc;

    public Model(String item_name, int imageSrc) {
        this.item_name = item_name;
        ImageSrc = imageSrc;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getImageSrc() {
        return ImageSrc;
    }

    public void setImageSrc(int imageSrc) {
        ImageSrc = imageSrc;
    }



}
