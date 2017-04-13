package com.alibaba.media.phenixdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.media.phenixdemo.util.BitmapUtils;
import com.alibaba.media.phenixdemo.util.Constants;
import com.alibaba.media.phenixdemo.util.HeadTitleView;
import com.alibaba.media.phenixdemo.util.HttpCall;
import com.alibaba.media.phenixdemo.util.MainThreadDelivery;

public class TestLoadDirectActivity extends Activity {

    private int imageSize; // 用于记录图片大小，单位KB
    private long loadTime; // 用于记录加载图片耗时，单位ms
    private int originWidth, originHeight; // 原图的宽高
    private String imageURL;
    TestActivityUI activityUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_load);
        imageURL = Constants.SMALL_IMAGE_URL;
        initUI();
        loadImageDirectTest();
    }

    /*初始化界面*/
    private void initUI(){
        //加载头部栏
        HeadTitleView headTitleView = (HeadTitleView) findViewById(R.id.head_layout);
        if( null !=headTitleView ){
            headTitleView.setMLeftImage(R.drawable.icon_left_arrow); // 设置左侧为返回图标
            headTitleView.setMiddleTitle(getResources().getString(R.string.text_title_test_load_direct));
            //设置头部栏点击事件
            HeadTitleView.HeadViewClickListener mHeadClickListener = new HeadTitleView.HeadViewClickListener() {
                public void onLeftClick() {
                    TestLoadDirectActivity.this.finish(); // 点击返回
                }
                public void onRightClick() { }
            };
            headTitleView.setClickListener(mHeadClickListener);
        }
        activityUI = new TestActivityUI();
        //加载示例说明
        String detailInfo = getResources().getString(R.string.detail_load_direct);
        detailInfo = String.format(detailInfo, imageURL);
        activityUI.textViewLoadDetail.setText(detailInfo);
    }


    /**直接加载图片的示例*/
    private void loadImageDirectTest() {
        Runnable testRun = new Runnable() {
            @Override
            public void run() {
                long startTime = SystemClock.elapsedRealtime();
                HttpCall.HttpResponse httpResponse = HttpCall.get( imageURL );
                if ( null!=httpResponse && httpResponse.isOK() ) {
                    imageSize = httpResponse.data.length / Constants.KB;
                    loadTime = SystemClock.elapsedRealtime() - startTime;
                    //Bitmap bitmap = BitmapUtils.compressFromBytes(httpResponse.data); // 压缩
                    Bitmap bitmap = BitmapUtils.Bytes2Bimap(httpResponse.data); // 不压缩
                    //原图的尺寸
                    originWidth = bitmap.getWidth();
                    originHeight = bitmap.getHeight();
                    loadImageResultShow(true, bitmap);
                } else {
                    loadImageResultShow(false, null);
                }
            }
        };
        new Thread( testRun ).start();
    }

    /**
     * 加载图片的结果展示
     */
    private void loadImageResultShow(final boolean success, final Bitmap bitmap) {
        Runnable showRun = new Runnable() {
            @Override
            public void run() {
                //根据内容改变样式
                changeTextViewStyle( activityUI.textViewLoadResult, R.style.style_textview_result_red);
                //显示结果
                if ( success) {
                    activityUI.imageViewShow.setImageBitmap(bitmap);
                    StringBuilder resultBuilder = new StringBuilder(30);
                    resultBuilder.append(Constants.LoadRes.LOAD_SUCCESS).append(Constants.LF);
                    resultBuilder.append( Constants.LoadRes.IMAGE_ORIGIN_WH ).append(originWidth).append(" × ").append(originHeight).append(Constants.LF);
                    resultBuilder.append(Constants.LoadRes.IMAGE_ORIGIN_SIZE).append(imageSize).append(Constants.SIZE_KB).append(Constants.LF);
                    resultBuilder.append(Constants.LoadRes.LOAD_TIME).append(loadTime).append(Constants.TIME_MS).append(Constants.LF);
                    //图片占用内存大小
                    int imgMemSize = bitmap.getByteCount() / Constants.KB;
                    resultBuilder.append(Constants.LoadRes.IMAGE_MEM_SIZE).append(imgMemSize).append(Constants.SIZE_KB).append(Constants.LF);
                    activityUI.textViewLoadResult.setText(resultBuilder);
                } else {
                    activityUI.imageViewShow.setImageResource(R.drawable.image_load_failed);
                    activityUI.textViewLoadResult.setText(Constants.LoadRes.LOAD_Fail);
                }
            }
        };
        MainThreadDelivery.run(showRun);
    }

    /**
     * 改变TextView的样式
     */
    private void changeTextViewStyle (TextView textView, int styleId) {
        if ( Build.VERSION.SDK_INT >= 23 )
            textView.setTextAppearance( styleId );
        else {
            //noinspection deprecation
            textView.setTextAppearance( getApplicationContext(), styleId );
        }
    }

    /**界面相关的组件*/
    private class TestActivityUI {
        TextView textViewLoadDetail;
        ImageView imageViewShow;
        TextView textViewLoadResult;

        public TestActivityUI() {
            textViewLoadDetail = (TextView) findViewById( R.id.textview_load_image_detail );
            imageViewShow = (ImageView) findViewById( R.id.imageview_load_image );
            textViewLoadResult = (TextView) findViewById( R.id.textview_load_image_result );
        }
    }
}
