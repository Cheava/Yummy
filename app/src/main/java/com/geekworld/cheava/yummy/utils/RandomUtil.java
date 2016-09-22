package com.geekworld.cheava.yummy.utils;

import java.util.Random;

import hugo.weaving.DebugLog;

/*
* @class RandomUtil
* @desc  随机数工具类
* @author wangzh
*/
public class RandomUtil {


    /**
     * Int int.
     * 生成0-end的随机整数
     * @param end the end
     * @return the int
     */
    static public int Int(int end) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return random.nextInt(end);
    }
}
