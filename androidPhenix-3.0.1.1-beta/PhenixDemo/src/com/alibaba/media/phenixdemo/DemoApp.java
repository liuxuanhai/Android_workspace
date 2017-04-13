package com.alibaba.media.phenixdemo;

import android.app.Application;
import android.util.Log;

import com.taobao.phenix.common.Constant;
import com.taobao.phenix.compat.NonCatalogDiskCache;
import com.taobao.phenix.intf.Phenix;
import com.taobao.tcommon.log.FLog;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initPhenix();
    }

    private void initPhenix () {
        FLog.setMinLevel(Log.DEBUG); // 开发者可以设置打印日志级别
        Phenix.instance().with(this);  // 初始化
        //下面是设置SDK默认提供的磁盘缓存。 开发者可以将其替换为自己实现的磁盘缓存
        Phenix.instance().diskCacheBuilder().with(new NonCatalogDiskCache()).maxSize(30 * Constant.MB);
        Phenix.instance().diskCacheBuilder().build().open(this);
    }
}
