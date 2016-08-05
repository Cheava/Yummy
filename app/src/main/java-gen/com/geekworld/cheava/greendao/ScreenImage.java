package com.geekworld.cheava.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "SCREEN_IMAGE".
 */
public class ScreenImage {

    private Long id;
    /**
     * Not-null value.
     */
    private String image;

    public ScreenImage() {
    }

    public ScreenImage(Long id) {
        this.id = id;
    }

    public ScreenImage(Long id, String image) {
        this.id = id;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Not-null value.
     */
    public String getImage() {
        return image;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setImage(String image) {
        this.image = image;
    }

}
