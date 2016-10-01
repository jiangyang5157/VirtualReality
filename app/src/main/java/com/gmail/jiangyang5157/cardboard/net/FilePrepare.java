package com.gmail.jiangyang5157.cardboard.net;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.vr.AssetUtils;

import java.io.File;
import java.util.Map;

/**
 * @author Yang
 * @since 10/1/2016
 */

public class FilePrepare extends NetRequest {
    private static final String TAG = "[FilePrepare]";

    private File file;

    public interface PrepareListener {
        void onStart();
        void onComplete(File file);
    }

    private final PrepareListener prepareListener;

    public FilePrepare(File file, @NonNull PrepareListener prepareListener) {
        this.file = file;
        this.prepareListener = prepareListener;
    }

    @Override
    public void start() {
        prepareListener.onStart();
        if (file.exists()){
            prepareListener.onComplete(file);
        } else{
            new Downloader(AssetUtils.getUrl(file.getAbsolutePath()), file, responseListener).start();
        }
    }

    private Downloader.ResponseListener responseListener = new Downloader.ResponseListener() {
        @Override
        public boolean onStart(Map<String, String> headers) {
            prepareListener.onStart();
            return true;
        }

        @Override
        public void onComplete(Map<String, String> headers) {
            prepareListener.onComplete(file);
        }

        @Override
        public void onError(String url, VolleyError volleyError) {
            Log.d(TAG, "onError:" + url + " " + volleyError.toString());
            prepareListener.onComplete(null);
        }
    };
}
