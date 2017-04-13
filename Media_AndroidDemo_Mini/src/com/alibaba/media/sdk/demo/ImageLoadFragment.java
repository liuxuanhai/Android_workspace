package com.alibaba.media.sdk.demo;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.alibaba.sdk.android.media.core.Constants;
import com.alibaba.sdk.android.media.imageloader.ImageLoaderTask;
import com.alibaba.sdk.android.media.imageloader.ImageOptions;
import com.alibaba.sdk.android.media.imageloader.LoaderOptions;
import com.alibaba.sdk.android.media.imageloader.LoadingListener;
import com.alibaba.sdk.android.media.imageloader.ImageOptions.WaterMark;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @Description info
 * @author yisheng.xp
 * @date 2015年9月24日 下午2:13:57
 */
public class ImageLoadFragment extends Fragment {

	View rootView;
	InitialView initialView;
	View.OnClickListener clickListener;
	private LoadingListener mListener;
	public static final String TAG = "ImageUploadFragment";
	String imgURL = "http://penguy.image.alimmdn.com/1.jpg";
	boolean needWaterMark = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.media_image_load_layout, container,false);
		initFragment();
		initImageLoaderListener();
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void initFragment() {
		clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(R.id.imageload_button==v.getId()){
					loadImage();
				}
			}
		};
		initialView = new InitialView();
	}
	/*第一步：调用加载图片的方法*/
	public void loadImage() {
		Bitmap loadingImg = BitmapFactory.decodeResource(getResources(), R.drawable.image_loading_logo, null);
		initialView.mImg.setImageBitmap(loadingImg);
		if(DemoApp.wantuService!=null){
			ImageOptions imgOptions;
			if(needWaterMark){
				//给图片添加水印
				WaterMark mark = new WaterMark(getStr(R.string.wartermask_text), Constants.ImageLoader.FontType.HEITI);
				mark.setPosition(Constants.ImageLoader.WatermarkPosition.CENTER_CENTER);
				mark.setFontSize(40);
				mark.setFontColor("#FFFFEE");
				imgOptions =  new ImageOptions.Builder().width(initialView.mScreenHeight).height(initialView.mScreenWitdh).waterMark(mark).build();
			}else{
				imgOptions = new ImageOptions.Builder().width(initialView.mScreenHeight).height(initialView.mScreenWitdh).build();
			}
			String uri = DemoApp.wantuService.getImageUri(this.imgURL, imgOptions);
			LoaderOptions options = new LoaderOptions.Builder().tag(System.currentTimeMillis()).build();
			DemoApp.wantuService.loadImage(uri, options, mListener);
			needWaterMark=!needWaterMark;
		}else{
			initialView.mText.setText("无法获取服务,点击重试");
			Log.e(TAG, "Get MediaService failed!!!");
		}
	}

	private void initImageLoaderListener() {
		/*第二步：加载图片时监听状态*/
		mListener = new LoadingListener() {
			@Override
			public void onLoadingFailed(ImageLoaderTask loaderTask,
					String failMessage) {
				initialView.mText.setText("图片加载失败,点击重试");
				Log.e(TAG, loaderTask.getTag()+"Load image failed!!! Reason:"+failMessage);
			}
			@Override
			public void onLoadingComplete(ImageLoaderTask loaderTask) {
				if (loaderTask.getTag() != null) {
					try{
						long beginTime = (Long) loaderTask.getTag();
						long loadTime = System.currentTimeMillis()-beginTime;
						Bitmap bitmap = loaderTask.getImageInfo().getBitmap();
						initialView.mImg.setImageBitmap(bitmap);
						initialView.mText.setText("图片加载耗时:"+loadTime+"ms");
					}catch(Throwable e){
						try {
							StringWriter sw = new StringWriter();
				            e.printStackTrace(new PrintWriter(sw));
							initialView.mText.setText("加载图片发生异常"+sw.toString());
				        } catch (Exception e2) {
				        	initialView.mText.setText("处理异常时发生异常"+e2.toString());
				        } 
					}
				}
			}
		};
	}
	private View findViewById(int id) {
		return rootView.findViewById(id);
	}

	public String getStr(int resId) {
		return getActivity().getString(resId);
	}

	/**
	 * 用于初始化该Fragment中的元素
	 */
	private final class InitialView {
		Button topButton;
		ImageView mImg;
		TextView mText;
		int mScreenWitdh,mScreenHeight;
		public InitialView() {
			topButton = (Button) rootView.findViewById(R.id.imageload_button);
			mImg = (ImageView) findViewById(R.id.id_image_show_panel);
			mText = (TextView) findViewById(R.id.id_URLView);
			mText.setMovementMethod(ScrollingMovementMethod.getInstance());
			topButton.setOnClickListener(clickListener);
			//获取屏幕的大小
			WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			mScreenWitdh = outMetrics.widthPixels;
			mScreenHeight = outMetrics.heightPixels;
		}
	}
}