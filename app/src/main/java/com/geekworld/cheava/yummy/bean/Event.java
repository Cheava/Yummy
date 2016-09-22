package com.geekworld.cheava.yummy.bean;

/**
 * Created by Cheava on 2016/9/19 0019.
 */
public class Event  {
    public class TimeEvent {
        public String time;

        public TimeEvent(String time) {
            this.time = time;
        }
    }
    public class ImageEvent {
        public String path;

        public ImageEvent(String path) {
            this.path = path;
        }
    }

    public class WordEvent{
        public String word;

        public WordEvent(String word) {
            this.word = word;
        }
    }
}