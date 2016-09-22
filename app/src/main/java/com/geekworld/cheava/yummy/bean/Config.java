package com.geekworld.cheava.yummy.bean;

import java.io.Serializable;

/**
 * Created by wangzh on 2016/9/14.
 */
public class Config implements Serializable {
    public class ScreenSize implements Serializable {
        int width;
        int height;

        public ScreenSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public class LastRefreshTime implements Serializable{
        String imgTime;
        String wordTime;

        public LastRefreshTime(String imgTime, String wordTime) {
            this.imgTime = imgTime;
            this.wordTime = wordTime;
        }

        public String getImgTime() {
            return imgTime;
        }

        public void setImgTime(String imgTime) {
            this.imgTime = imgTime;
        }

        public String getWordTime() {
            return wordTime;
        }

        public void setWordTime(String wordTime) {
            this.wordTime = wordTime;
        }
    }

    public class NeedRefresh implements Serializable{
        boolean imgNeed;
        boolean wordNeed;

        public NeedRefresh(boolean imgNeed, boolean wordNeed) {
            this.imgNeed = imgNeed;
            this.wordNeed = wordNeed;
        }

        public boolean isImgNeed() {
            return imgNeed;
        }

        public void setImgNeed(boolean imgNeed) {
            this.imgNeed = imgNeed;
        }

        public boolean isWordNeed() {
            return wordNeed;
        }

        public void setWordNeed(boolean wordNeed) {
            this.wordNeed = wordNeed;
        }
    }

}
