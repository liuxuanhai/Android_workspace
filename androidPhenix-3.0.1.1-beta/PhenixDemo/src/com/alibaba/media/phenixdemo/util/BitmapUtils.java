package com.alibaba.media.phenixdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 图片Bitmap转化类
 * Created by yisheng.xp on 2016/2/3.
 */
public class BitmapUtils {

    /**
     * Bitmap转字节
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 字节转换为Bitmap
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if ( null!=b && b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 从图片字节中获取图片的长宽
     */
    public static BitmapFactory.Options getImageSizeFromBytes( byte[] data ) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;  //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        BitmapFactory.decodeByteArray(data, 0, data.length, newOpts); // 对字节编码，获取图片尺寸
        return newOpts;
    }

    /**
     * 从Bitmap中获取图片的大小
     */
    public static int getImageSizeFromBitmap( Bitmap image ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        return baos.toByteArray().length;
    }
    /**
     * 质量压缩法压缩图片
     */
    private static Bitmap compressImage( Bitmap image ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
    }
    /**
     * 图片按比例大小压缩，再安装质量方法压缩
     * @param data byte[] 图片字节
     */
    public static Bitmap compressFromBytes(byte[] data) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;  //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        BitmapFactory.decodeByteArray(data, 0, data.length, newOpts); // 对字节编码，获取图片尺寸
        newOpts.inSampleSize = calculateInSampleSize(newOpts, Constants.IMAGE_SIZE_MAX, Constants.IMAGE_SIZE_MAX); //计算缩放比
        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 图片按比例大小压缩，再安装质量方法压缩
     * @param image Bitmap 待压缩的图片
     */
    public static Bitmap compressFromBitmap( Bitmap image ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();
            image.compress(Bitmap.CompressFormat.PNG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        // 计算缩放尺寸
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inSampleSize = calculateInSampleSize(newOpts, Constants.IMAGE_SIZE_MAX, Constants.IMAGE_SIZE_MAX); //计算缩放比
        // 进行缩放
        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
    /**
     * 计算缩放比例
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
//            int heightRatio = Math.round((float) height / reqHeight);
//            int widthRatio = Math.round((float) width / reqWidth);
//            inSampleSize = Math.min(heightRatio, widthRatio);
            float heightRatio = ((float) height / reqHeight);
            float widthRatio = ((float) width / reqWidth);
            inSampleSize = (int) (heightRatio+widthRatio)/2;
        }
        return inSampleSize;
    }
}
