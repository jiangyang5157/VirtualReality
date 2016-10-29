package com.gmail.jiangyang5157.cardboard.net;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.vr.AssetFile;

import java.util.Map;

/**
 * @author Yang
 * @since 10/1/2016
 */

public class FilePrepare extends NetRequest {

    private AssetFile assetFile;

    public interface PrepareListener {
        void onStart();
        void onComplete(AssetFile assetFile);
    }

    private final PrepareListener prepareListener;

    public FilePrepare(@NonNull AssetFile assetFile, @NonNull PrepareListener prepareListener) {
        this.assetFile = assetFile;
        this.prepareListener = prepareListener;
    }

    @Override
    public void start() {
        prepareListener.onStart();
        if (assetFile.isReady()){
            prepareListener.onComplete(assetFile);
        } else{
            new Downloader(assetFile, responseListener).start();
        }
    }

    private Downloader.ResponseListener responseListener = new Downloader.ResponseListener() {
        @Override
        public boolean onStart(Map<String, String> headers) {
            return true;
        }

        @Override
        public void onComplete(AssetFile assetFile, Map<String, String> headers) {
            prepareListener.onComplete(assetFile);
        }

        @Override
        public void onError(AssetFile assetFile, VolleyError volleyError) {
            prepareListener.onComplete(assetFile);
        }
    };
}
