package com.alibaba.media.sdk.demo;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.media.sdk.common.MediaUtils;
import com.alibaba.media.sdk.common.Utils;
import com.alibaba.sdk.android.media.core.Constants;
import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadOptions;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.BitmapUtils;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.alibaba.sdk.android.media.utils.StringUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @Description info
 * @author yisheng.xp
 * @date 2015年9月24日 下午2:14:04
 */
public class ImageUploadFragment extends Fragment implements OnClickListener {

	View rootView;
	InitialView initialView;
	private String token = "UPLOAD_AK_TOP MjM3NDk3MzY6ZXlKa1pYUmxZM1JOYVcxbElqb2lNU0lzSW1sdWMyVnlkRTl1YkhraU9pSXdJaXdpYm1GdFpYTndZV05sSWpvaVpISnBkbVVpTENKbGVIQnBjbUYwYVc5dUlqb3RNWDA6YjUwMzlhNDQ0Nzk4MTJiYWRmZTQzMDZhN2I2M2YxNmFiYzM0OTRkYw";
	public String mFilePath;
	public String mTaskId;
	public UploadListener mListener;
	public static final String TAG = "ImageUploadFragment";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.media_image_upload_layout, container,
				false);
		initUpload();
		return rootView;
	}


	@Override
	public void onClick(View v) {
		if(R.id.imagescan_button!=v.getId() && null==DemoApp.wantuService){
			initialView.path.setText("初始化失败，无法获取多媒体服务");
			return;
		}
			switch (v.getId()) {
			case R.id.imagescan_button:
				MediaUtils.selectFile(getActivity());
				break;
			case R.id.upload_compress_button:
				uploadFile(mFilePath, true);
				break;
			case R.id.upload_button:
				uploadFile(mFilePath, false);
				break;
			case R.id.upload_pause_button:
				if(null!=mTaskId)
					DemoApp.wantuService.pauseUpload(mTaskId);
				break;
			case R.id.upload_goon_button:
				if(null!=mTaskId)
					DemoApp.wantuService.resumeUpload(mTaskId, mListener);
				break;
			case R.id.upload_cancel_button:
				if(null!=mTaskId)
					DemoApp.wantuService.cancelUpload(mTaskId);
				break;
		}
	}

	private void initUpload() {
		initialView = new InitialView();
		mListener = new UploadListener() {
			@Override
			public void onUploading(UploadTask uploadTask) {
				updateProgress(uploadTask.getCurrent(), uploadTask.getTotal());
				//Log.e(TAG, "###########onUploading"+uploadTask.getCurrent()+"  "+uploadTask.getTotal());
			}

			@Override
			public void onUploadFailed(UploadTask uploadTask,FailReason failReason) {
				if( failReason.getCode() == Constants.ErrorCode.RESUME_FAIL )
					Log.e(TAG, "恢复上传失败!");
				initialView.webUrl.setText(getStr(R.string.image_upload_faild)+ failReason.toString());
				UploadTask.Result result = uploadTask.getResult();
				String requestId = (null==result ? "null" : result.requestId);
				Log.e(TAG, "###########onUploadFailed:" + requestId );
			}
			@Override
			public void onUploadComplete(UploadTask uploadTask) {
				initialView.upTime.setText(getStr(R.string.image_upload_time)
						+ (SystemClock.elapsedRealtime() - Long.valueOf(uploadTask.getTag())));
				UploadTask.Result res = uploadTask.getResult();			//上传成功后，从服务端返回的结果都在这个对象中，根据自己需要获取
				Log.e(TAG, "requestId:" + res.requestId);
				initialView.webUrl.setText("resultUri :  "+ res.url);
				Log.e(TAG, "url:" + res.url);
				mFilePath=null;
				mTaskId=null;
			}
			@Override
			public void onUploadCancelled(UploadTask uploadTask) {
				initialView.webUrl.setText(R.string.upload_cancel_text);
				mFilePath=null;
				mTaskId=null;
			}
		};
	}

	private View findViewById(int id) {
		return rootView.findViewById(id);
	}

	public String getStr(int resId) {
		return getActivity().getString(resId);
	}

	private void updateProgress(long current, long total) {
		int progress = 100;
		if (current != total) {
			progress = (int) (100 * (double) current / (double) total);
		}
		initialView.upRate.setText(getStr(R.string.image_upload_rate)+ progress + " %");
		initialView.upSize.setText(getStr(R.string.image_upload_size) + current + " byte " + current / Constants.KB + " kb");
		initialView.totalSize.setText(getStr(R.string.image_total_size) + total + " byte  " + total / Constants.KB + " kb");
	}

		public void uploadFile(final String path, boolean compress) {
		if (TextUtils.isEmpty(path)) {
			initialView.path.setText("请选择文件!!!!");
			return;
		}
		final JSONObject itemJson = new JSONObject();
		try {
			itemJson.put("type", "item");
			itemJson.put("id", 12121212);
			itemJson.put("userId", 12121212);
		} catch (JSONException e) { e.printStackTrace(); }
		HashMap<String, Object> extendMap = new HashMap<String, Object>();
		extendMap.put("publishInfo", itemJson); //上报视频发布数据
		final String fileName = "file_" + StringUtils.getUUID();
		final File file = new File(path);
		final UploadOptions options = new UploadOptions.Builder()
				.tag(String.valueOf(SystemClock.elapsedRealtime()))
				.dir("/PENGUY/tempUpload")
				.extendMap(extendMap)
				.aliases(fileName).build();
		if (!compress) {
			mTaskId = DemoApp.wantuService.upload(new File(path), options, mListener, token);
		} else {
			Utils.SERVICE.submit(new Runnable() {
				@Override
				public void run() {
					byte[] data = BitmapUtils.getSmallBitmapBytes(path, 200, 200, 80);
					if (data != null) {
						Log.e(TAG, "compress  olderSize:" + file.length()
								/ Constants.KB + " kb" + "    newSize:"
								+ data.length / Constants.KB + "  kb");
						mTaskId = DemoApp.wantuService.upload(data, fileName, options, mListener,token);
					} else {
						Log.e(TAG, "getSmallBitmapBytes  fail");
					}
				}
			});
		}
	}

	/* 获取选中的图片信息
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && (requestCode == MediaUtils.REQUEST_FILE_CODE) ) {
			mFilePath = MediaUtils.getFilePath(data, getActivity());
			if(null!=mFilePath && !mFilePath.isEmpty()){
				File file=new File(mFilePath);
				if(file.exists() && file.isFile()){
					initialView.reset();
					initialView.path.setText(getStr(R.string.upload_filepath) + mFilePath);
					Bitmap bm=convertResToBm(mFilePath);//判断是否为图片
					ImageView imgView = (ImageView) findViewById(R.id.mImageView);
					if( null!=bm )
						imgView.setImageBitmap(bm);
					else
						imgView.setImageResource(R.drawable.default_img);
				}
			}
		}
	}

	private Bitmap convertResToBm(String path) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = 5; // 将原图缩小四分之一读取
		option.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(path, option);
		return ThumbnailUtils.extractThumbnail(bm, 480, 480); // 将图片的大小限定在480*800
	}
	/**
	 * 内部类，用于初始化该Fragment中的元素
	 */
	private final class InitialView {
		public InitialView() {
			initailButton();
		}

		TextView path, upRate, upSize, totalSize, upTime, webUrl;

		void initailButton() {
			findViewById(R.id.imagescan_button).setOnClickListener(ImageUploadFragment.this);
			findViewById(R.id.upload_button).setOnClickListener(ImageUploadFragment.this);
			findViewById(R.id.upload_compress_button).setOnClickListener(ImageUploadFragment.this);
			findViewById(R.id.upload_pause_button).setOnClickListener(ImageUploadFragment.this);
			findViewById(R.id.upload_goon_button).setOnClickListener(ImageUploadFragment.this);
			findViewById(R.id.upload_cancel_button).setOnClickListener(ImageUploadFragment.this);
			path = (TextView) findViewById(R.id.image_path);
			upRate = (TextView) findViewById(R.id.imageUpRate);
			upSize = (TextView) findViewById(R.id.imageUpSize);
			totalSize = (TextView) findViewById(R.id.imageTotalSize);
			upTime = (TextView) findViewById(R.id.imageUpTime);
			webUrl = (TextView) findViewById(R.id.imageWeburl);
		}

		void reset() {
			path.setText(R.string.upload_filepath);
			upRate.setText(R.string.image_upload_rate);
			upSize.setText(R.string.image_upload_size);
			totalSize.setText(R.string.image_total_size);
			upTime.setText(R.string.image_upload_time);
			webUrl.setText(R.string.image_web_url);
		}
	}

}