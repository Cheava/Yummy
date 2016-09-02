package com.geekworld.cheava.yummy;

import java.util.Random;

import hugo.weaving.DebugLog;

/**
 * Created by wangzh on 2016/9/1.
 */
public class RandomUtil {
    @DebugLog
    static public int Int(int end) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return random.nextInt(end);
    }
}
