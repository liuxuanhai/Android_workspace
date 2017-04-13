package com.alibaba.media.phenixdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.media.phenixdemo.util.Constants;
import com.alibaba.media.phenixdemo.util.HeadTitleView;
import com.taobao.phenix.intf.Phenix;
import com.taobao.phenix.intf.event.FailPhenixEvent;
import com.taobao.phenix.intf.event.IPhenixListener;
import com.taobao.phenix.intf.event.SuccPhenixEvent;

public class TestLoadPhenixActivity extends Activity {

    private long loadTime; // 用于记录加载图片耗时，单位ms
    private String imageURL;
    TestActivityUI activityUI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_load);
        imageURL = Constants.SMALL_IMAGE_URL;
        initUI();
        loadImageByPhenixTest();
    }

    /*初始化界面*/
    private void initUI(){
        //加载头部栏
        HeadTitleView headTitleView = (HeadTitleView) findViewById(R.id.head_layout);
        if( null !=headTitleView ){
            headTitleView.setMLeftImage(R.drawable.icon_left_arrow); // 设置左侧为返回图标
            headTitleView.setMiddleTitle(getResources().getString(R.string.text_title_test_load_phenix));
            //设置头部栏点击事件
            HeadTitleView.HeadViewClickListener mHeadClickListener = new HeadTitleView.HeadViewClickListener() {
                public void onLeftClick() {
                    TestLoadPhenixActivity.this.finish(); // 点击返回
                }
                public void onRightClick() { }
            };
            headTitleView.setClickListener(mHeadClickListener);
        }
        activityUI = new TestActivityUI();
        //加载示例说明
        String detailInfo = getResources().getString(R.string.detail_load_phnix);
        detailInfo = String.format(detailInfo, imageURL);
        activityUI.textViewLoadDetail.setText(detailInfo);
    }

    /**
     * 通过图片库加载图片的示例
     */
    private void loadImageByPhenixTest() {
        loadTime = SystemClock.elapsedRealtime();
        //第一种：可以使用如下的方式，判断加载成功还是失败
        Phenix.instance()
                .load(imageURL)
                .succListener(new IPhenixListener<SuccPhenixEvent>() {
                    @Override
                    public boolean onHappen(SuccPhenixEvent succPhenixEvent) {
                        if (succPhenixEvent.getDrawable() != null) {
                            Bitmap bitmap = succPhenixEvent.getDrawable().getBitmap();
                            loadTime = SystemClock.elapsedRealtime()-loadTime;
                            loadImageResultShow(true, bitmap);
                        }
                        return false;
                }
            })
            .failListener(new IPhenixListener<FailPhenixEvent>() {
                @Override
                public boolean onHappen(FailPhenixEvent failPhenixEvent) {
                    loadImageResultShow(false, null);
                    return false;
                }
            }).fetch();
        //第二种：可以通过下面简单的方式，将图片直接加载到ImageView中
        /*Phenix.instance()
                .load(imageURL)
                .into(activityUI.imageViewShow);*/
    }

    /**
     * 加载图片的结果展示
     */
    private void loadImageResultShow(final boolean success, final Bitmap bitmap) {
        //根据内容改变样式
        changeTextViewStyle(activityUI.textViewLoadResult, R.style.style_textview_result_red);
        //显示结果
        if ( success ) {
            activityUI.imageViewShow.setImageBitmap(bitmap);
            StringBuilder resultBuilder = new StringBuilder(30);
            resultBuilder.append(Constants.LoadRes.LOAD_SUCCESS).append(Constants.LF);
            int originWidth=bitmap.getWidth(), originHeight=bitmap.getHeight(); // 原图的宽高
            resultBuilder.append( Constants.LoadRes.IMAGE_ORIGIN_WH ).append(originWidth).append(" × ").append(originHeight).append(Constants.LF);
            resultBuilder.append(Constants.LoadRes.LOAD_TIME).append( loadTime ).append(Constants.TIME_MS).append(Constants.LF);
            //图片占用内存大小
            int imgMemSize = bitmap.getByteCount() / Constants.KB;
            resultBuilder.append(Constants.LoadRes.IMAGE_MEM_SIZE).append(imgMemSize).append(Constants.SIZE_KB).append(Constants.LF);
            activityUI.textViewLoadResult.setText(resultBuilder);
        } else {
            activityUI.imageViewShow.setImageResource(R.drawable.image_load_failed);
            activityUI.textViewLoadResult.setText(Constants.LoadRes.LOAD_Fail);
        }
    }

    /**
     * 改变TextView的样式
     */
    private void changeTextViewStyle (TextView textView, int styleId) {
        if ( Build.VERSION.SDK_INT >= 23 )
            textView.setTextAppearance( styleId );
        else {
            //noinspection deprecation
            textView.setTextAppearance(getApplicationContext(), styleId);
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
