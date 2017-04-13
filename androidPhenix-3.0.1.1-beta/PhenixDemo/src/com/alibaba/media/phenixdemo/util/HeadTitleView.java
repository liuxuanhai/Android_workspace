package com.alibaba.media.phenixdemo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.media.phenixdemo.R;

/**
 * 顶部栏组件
 */
public class HeadTitleView extends RelativeLayout {

	private ImageView mLeftImage;
	private TextView mMidleTitle;
	private TextView mRightTitle;
	private HeadViewClickListener mHeadClickListener;

	public HeadTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if( !isInEditMode() ) {
			//isInEditMode()是为了在Eclipse中编辑时显示
			View parentView = inflater.inflate(R.layout.head_title_layout, this, true);
			mLeftImage = (ImageView)parentView.findViewById(R.id.head_image_logo);
			mMidleTitle = (TextView)parentView.findViewById(R.id.head_middle_text);
			mRightTitle = (TextView)parentView.findViewById(R.id.head_right_text);
			initClickListener();
		}
	}
	/**初始化点击事件*/
	private void initClickListener() {
		View.OnClickListener mClickListener = new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (null==mHeadClickListener)
					return;
				switch (v.getId()) {
					case R.id.head_image_logo:
						mHeadClickListener.onLeftClick();
						break;
					case R.id.head_right_text:
						mHeadClickListener.onRightClick();
						break;
				}
			}
		};
		mLeftImage.setOnClickListener(mClickListener);
		mRightTitle.setOnClickListener(mClickListener);
	}

	public void initHeadPanel(){
		if(mMidleTitle != null){
			setMiddleTitle(this.getResources().getString(R.string.head_middle_default));
		}
	}

	/**设置左边图片*/
	public void setMLeftImage(int rid) {
		mLeftImage.setImageResource(rid);
	}

	/**设置中间文字*/
	public void setMiddleTitle(String s) {
		mMidleTitle.setText(s);
	}

	/**设置右边文字*/
	public void setRightTitle(String s) {
		mRightTitle.setText(s);
	}

	/**设置点击顶部栏的事件*/
	public void setClickListener(HeadViewClickListener clickListener) {
		mHeadClickListener = clickListener;
	}

	/**用于响应顶部栏被点击时的事件*/
	public interface HeadViewClickListener {
		void onLeftClick();
		void onRightClick();
	}
}
