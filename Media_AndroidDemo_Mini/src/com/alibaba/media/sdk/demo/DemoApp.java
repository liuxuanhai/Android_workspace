package com.alibaba.media.sdk.demo;

import android.app.Application;

import com.alibaba.sdk.android.media.WantuService;

public class DemoApp extends Application {
	
	public static WantuService wantuService;	//图片服务
	
	@Override
	public void onCreate() {
		super.onCreate();
		initService();
	}
	public void initService() {
		/*############# new. 完成多媒体服务的异步初始化##############*/
		WantuService.enableHttpDNS(); // 可选。为了避免域名劫持，建议开发者启用HttpDNS
		WantuService.enableLog(); //在调试时，可以打开日志。正式上线前可以关闭

		wantuService = WantuService.getInstance();
		wantuService.asyncInit(this);
	}
}
