package com.alibaba.media.sdk.demo;

import com.alibaba.media.sdk.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;

/**
 * @Description info
 * @author yisheng.xp
 * @date 2015��9��24�� ����2:14:11
 */
public class MediaMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_main_layout);
		
		new CountDownTimer(500,500) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				Intent intent = new Intent();
				intent.setClass(MediaMainActivity.this, MediaServerActivity.class);
				startActivity(intent);
				MediaMainActivity.this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				finish();
			}
		}.start();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
