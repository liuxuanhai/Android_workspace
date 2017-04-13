package com.alibaba.media.phenixdemo.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yisheng.xp on 2016/2/2.
 */
public class HttpCall {

    private static final int RESOLVE_TIMEOUT_IN_SEC = 10; //请求HttpDNS服务器连接超时时间
    private static final String TAG = "HttpCall";

    /**执行Get请求*/
    public static HttpResponse get(String url) {
        return httpRequest(url, "GET");
    }


    private static HttpResponse httpRequest(String url, String method) {
        try {
            HttpURLConnection conn = openConnection( url );
            conn.setRequestMethod(method);
            if( "GET".equals(method) ) {
                conn.setUseCaches(false); //不使用缓存，每次都从网络下载
            }
            return new HttpResponse(conn);
        } catch (Exception e) {
            Log.e(TAG, "[HttpCall.httpRequest] - Exception: " + e.toString());
        }
        return null;
    }

    /**创建HttpURLConnection连接*/
    private static HttpURLConnection openConnection( String url ) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL( url ).openConnection();
        conn.setUseCaches(true);
        conn.setDoOutput(false);
        conn.setConnectTimeout(HttpCall.RESOLVE_TIMEOUT_IN_SEC * 1000);
        conn.setReadTimeout(HttpCall.RESOLVE_TIMEOUT_IN_SEC * 1000);
        return conn;
    }
    /**
     * Http请求的响应体
     */
    public static class HttpResponse {

        private int statusCode;
        public byte[] data;

        public HttpResponse( HttpURLConnection conn ) {
            try {
                statusCode = conn.getResponseCode();
            } catch (IOException ignored) {
                statusCode = -1;
            }
            data = getData( conn );
        }

        public boolean isOK() {
            if ( 200==this.statusCode)
                return true;
            return false;
        }

        private byte[] getData(HttpURLConnection urlCon) {
            InputStream input;
            try {
                input = urlCon.getInputStream();
            } catch (IOException ioe) {
                input = urlCon.getErrorStream();
                Log.e(TAG, "[HttpCall.HttpResponse.getData] - Exception: " + ioe.toString() );
            }
            return toBytes(input);
        }

        private byte[] toBytes(InputStream input) {
            if (null==input)
                return null;
            try {
                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int n;
                while (-1 != (n = input.read(buffer))) {
                    swapStream.write(buffer, 0, n);
                }
                return swapStream.toByteArray();
            } catch (Exception e) {
                Log.e(TAG, "[HttpCall.HttpResponse.toBytes] - Exception: " + e.toString());
            } finally {
                try {
                    input.close();
                } catch (IOException ignored) {}
            }
            return null;
        }
    }
}
