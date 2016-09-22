package com.geekworld.cheava.yummy.bean;

import java.io.Serializable;

/**
 * Created by wangzh on 2016/9/14.
 */
public class Screenshot implements Serializable {
    private String imgPath;
    private String uriString;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }
}
