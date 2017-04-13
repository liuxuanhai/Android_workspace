package com.alibaba.media.phenixdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.media.phenixdemo.util.Constants;
import com.alibaba.media.phenixdemo.util.HeadTitleView;
import com.taobao.phenix.decode.BitmapDecodeHelper;
import com.taobao.phenix.intf.Phenix;
import com.taobao.phenix.intf.event.FailPhenixEvent;
import com.taobao.phenix.intf.event.IPhenixListener;
import com.taobao.phenix.intf.event.SuccPhenixEvent;
import com.taobao.tao.image.IImageStrategySupport;
import com.taobao.tao.image.ImageInitBusinss;
import com.taobao.tao.util.ImageStrategyDecider;

public class TestLoadDefineImgActivity extends Activity {

    private long loadTime; // 用于记录加载图片耗时，单位ms
    private String imageURL;
    TestActivityUI activityUI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_load);
        imageURL = Constants.SMALL_IMAGE_URL;
        initImageStrategy();
        initUI();
        loadDefineImageByPhenixTest();
    }

    private void initImageStrategy() {
        /**
         * 初始化策略库环境
         * getConfigString，用以获取最新的配置项，或者没有配置中心接入，可直接返回默认值
         * isSupportWebP，当前环境是否支持WebP，使用来自Phenix的方法，用以得到当前图片库能否支持WebP格式
         * isNetworkSlow，当前是否为弱网，这里只简单判断了下是否为WiFi，准确的做法是借助网络库测速判断
         * **/
        ImageInitBusinss.newInstance(getApplication(), new IImageStrategySupport() {
            @Override
            public String getConfigString(String group, String key, String defaultVal) {
                return defaultVal;
            }
            @Override
            public boolean isSupportWebP() {
                return BitmapDecodeHelper.isSupportWebp();
            }
            @Override
            public boolean isNetworkSlow() {
                return !isWifi();
            }
        }).notifyConfigsChange();
    }

    /*初始化界面*/
    private void initUI(){
        //加载头部栏
        HeadTitleView headTitleView = (HeadTitleView) findViewById(R.id.head_layout);
        if( null !=headTitleView ){
            headTitleView.setMLeftImage(R.drawable.icon_left_arrow); // 设置左侧为返回图标
            headTitleView.setMiddleTitle(getResources().getString(R.string.text_title_test_load_define_img));
            //设置头部栏点击事件
            HeadTitleView.HeadViewClickListener mHeadClickListener = new HeadTitleView.HeadViewClickListener() {
                public void onLeftClick() {
                    TestLoadDefineImgActivity.this.finish(); // 点击返回
                }
                public void onRightClick() { }
            };
            headTitleView.setClickListener(mHeadClickListener);
        }
        activityUI = new TestActivityUI();
    }

    /**
     * 加载自定义图片的示例
     */
    private void loadDefineImageByPhenixTest() {
        /**
         * 这里通过LayoutParams来获取宽高，因为ImageView在布局xml已经指定了固定宽高
         * 如果布局中没有指定宽高，那一定需要保证在ImageView布局完成的正确时机，使用getWidth()/getHeight()来获取准确宽高
         * @info 参数可以进行自定义的控制。ImageStrategyConfig.Builder config = ImageStrategyConfig.newBuilderWithName("test");
         * **/
        String decidedUrl = ImageStrategyDecider.decideUrl( imageURL, Constants.IMAGE_SIZE_MAX, Constants.IMAGE_SIZE_MAX, null );
        //加载示例说明
        String detailInfo = getResources().getString(R.string.detail_load_define_img);
        detailInfo = String.format(detailInfo, decidedUrl);
        activityUI.textViewLoadDetail.setText(detailInfo);

        // 清除相关缓存，保证从网络再加载。该方法仅用于测试，为了每次都从网络中加载图片。
        Phenix.instance().clearCache(decidedUrl);

        loadTime = SystemClock.elapsedRealtime();
        //使用如下的方式，判断加载成功还是失败，并显示
        Phenix.instance()
                .load(decidedUrl)
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

    /**判断是否为Wifi网络*/
    private boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
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
