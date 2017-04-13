package com.alibaba.media.sdk.demo;

import com.alibaba.media.sdk.demo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Description info
 * @author yisheng.xp
 * @date 2015年9月24日 下午2:14:20
 */
public class MediaServerActivity extends Activity  {
	TabHost tabHost;
	TabHost.TabSpec spec;

	private long exitTime = 0;			//进行按两次退出系统的判断
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_server_layout);
		
		//不显示顶部栏ActionBar的内容
		final ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		bar.hide();
		tabHost = (TabHost)this.findViewById(R.id.tabHost);
		tabHost.setup();//实例化了tabWidget和tabContent。如果不加这一句，在执行TabHost.TabSpec.setContent()时会有Exception
		
		// 初始化两个TabSpec并将其添加到TabHost
//		Drawable tempDraw = this.getResources().getDrawable(R.drawable.dark_hosttab_lable);
        spec = tabHost.newTabSpec("tab1").setIndicator("图片上传").setContent(R.id.fragmenttab1);
        tabHost.addTab(spec);
        
        spec = tabHost.newTabSpec("tab2").setIndicator("图片下载").setContent(R.id.fragmenttab2);
        tabHost.addTab(spec);
        tabHost.setCurrentTabByTag("tab1");
        //设置标签样式
        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i =0; i < tabWidget.getChildCount(); i++) {
        	View tempView = tabWidget.getChildTabViewAt(i);
        	tempView.setBackgroundColor(0xFFBB8800);
        	TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
        	tv.setPadding(0, 5, 0, 15);
        	tv.setGravity(Gravity.CENTER);
        	tv.setTextSize(15);
        }
        tabHost.getCurrentTabView().setBackgroundColor(0xFFFF9F00);
        tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {
				TabWidget tabWidget = tabHost.getTabWidget();
				int tab1Color=0xFFBB8800, tab2Color=0xFFBB8800;
				if("tab1".equals(tabId)){
					tab1Color=0xFFFF9F00;
				}else if("tab2".equals(tabId)){
					tab2Color=0xFFFF9F00;
				}
				tabWidget.getChildTabViewAt(0).setBackgroundColor(tab1Color);
				tabWidget.getChildTabViewAt(1).setBackgroundColor(tab2Color);
			}
        });
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitSystem();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void exitSystem(){
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		}else{
			finish();
            System.exit(0);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//该结果传递给对应的Fragment，进行图片或视频信息的获取
		String tabTag = tabHost.getCurrentTabTag();
		Fragment currentFrag=null;
		if("tab1".equals(tabTag)){
			currentFrag =(Fragment) getFragmentManager().findFragmentById(R.id.fragmenttab1);
		}else if("tab2".equals(tabTag)){
			currentFrag =(Fragment) getFragmentManager().findFragmentById(R.id.fragmenttab2);
		}
		if(null!=currentFrag){
			currentFrag.onActivityResult(requestCode, resultCode, data);
		}
	}
}
