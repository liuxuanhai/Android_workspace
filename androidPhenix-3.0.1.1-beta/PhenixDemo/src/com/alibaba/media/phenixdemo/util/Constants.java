package com.alibaba.media.phenixdemo.util;

/**
 * 测试用到的部分常量
 * Created by yisheng.xp on 2016/2/3.
 */
public class Constants {
    public final static String SMALL_IMAGE_URL = "http://blog.image.alimmdn.com/YiSheng/sImg.jpg";
    public final static String LARGE_IMAGE_URL = "http://blog.image.alimmdn.com/YiSheng/LImg.jpg";
    public final static int KB = 1024;
    public final static int IMAGE_SIZE_MAX = 400;
    public final static String CR = "\r";
    public final static String LF = "\n";
    public final static String CR_LF = CR+LF;
    public final static String SIZE_KB = "kb";
    public final static String TIME_MS = "ms";

    public static class LoadRes {
        public final static String LOAD_SUCCESS = "加载成功！";
        public final static String LOAD_Fail = "加载失败！";
        public final static String IMAGE_ORIGIN_WH = "原图宽高：";
        public final static String IMAGE_ORIGIN_SIZE = "原图大小：";
        public final static String LOAD_TIME = "加载耗时：";
        public final static String IMAGE_MEM_SIZE = "占用内存：";
        public final static String IMAGE_COMPRESS_SIZE = "压缩后大小：";
        public final static String IMAGE_COMPRESS_WH = "压缩后宽高：";
    }
}
