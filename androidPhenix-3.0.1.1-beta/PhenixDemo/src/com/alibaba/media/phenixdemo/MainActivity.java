package com.alibaba.media.phenixdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.media.phenixdemo.util.HeadTitleView;

public class MainActivity extends Activity {

    HeadTitleView headTitleView;
    MainActivityUI mainUI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    /*初始化界面*/
    private void initUI(){
        //加载头部栏
        headTitleView = (HeadTitleView) findViewById(R.id.head_layout);
        if( null !=headTitleView ){
            headTitleView.initHeadPanel();
        }
        mainUI = new MainActivityUI();
        initClickListener();
    }
    /**初始化点击事件*/
    private void initClickListener() {
        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_load_direct_test:
                        startActivity( new Intent(MainActivity.this, TestLoadDirectActivity.class) );
                        break;
                    case R.id.button_load_phenix_test:
                        startActivity( new Intent(MainActivity.this, TestLoadPhenixActivity.class) );
                        break;
                    case R.id.button_load_define_img_test:
                        startActivity( new Intent(MainActivity.this, TestLoadDefineImgActivity.class) );
                }
            }
        };
        mainUI.buttonLoadDirect.setOnClickListener(clickListener);
        mainUI.buttonLoadPhenix.setOnClickListener(clickListener);
        mainUI.buttonLoadDefineImg.setOnClickListener(clickListener);
    }

    /**界面相关的组件*/
    private class MainActivityUI {
        Button buttonLoadDirect;
        Button buttonLoadPhenix;
        Button buttonLoadDefineImg;

        public MainActivityUI() {
            buttonLoadDirect = (Button) findViewById( R.id.button_load_direct_test );
            buttonLoadPhenix = (Button) findViewById( R.id.button_load_phenix_test );
            buttonLoadDefineImg = (Button) findViewById( R.id.button_load_define_img_test );
        }
    }
}