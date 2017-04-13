package com.alibaba.media.sdk.common;
import java.io.FileDescriptor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

public class Utils {

	static final String TAG = "Utils";

	public static final String DIRECTORY;
	public static final ExecutorService SERVICE;

	static {
		SERVICE = Executors.newCachedThreadPool();
		String DCIM = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).toString();
		DIRECTORY = DCIM + "/Camera";
	}

	/**
	 * 频繁获取同文件，同时间的截图，可以尝试Memory cache bitmap，提高速度，
	 * 
	 * @param filePath
	 * @param fd
	 * @param timeUs
	 * @return
	 */
	public static Bitmap createVideoThumbnail(String filePath,
			FileDescriptor fd, long timeUs) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			if (filePath != null) {
				retriever.setDataSource(filePath);
			} else {
				retriever.setDataSource(fd);
			}
			bitmap = retriever.getFrameAtTime(timeUs);
		} catch (Throwable throwable) {
			// Assume this is a corrupt video file
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
			}
		}
		return bitmap;
	}

}
