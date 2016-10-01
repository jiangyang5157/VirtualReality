package com.gmail.jiangyang5157.cardboard.net;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.app.VolleyApplication;
import com.gmail.jiangyang5157.cardboard.vr.AssetFile;
import com.gmail.jiangyang5157.tookit.base.data.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Yang
 * @since 6/19/2016
 */
public class Downloader extends NetRequest {
    private static final String TAG = "[Downloader]";

    private AssetFile assetFile;

    private ResponseListener listener;

    public interface ResponseListener {
        boolean onStart(Map<String, String> headers);

        void onComplete(AssetFile assetFile, Map<String, String> headers);

        void onError(AssetFile assetFile, VolleyError volleyError);
    }

    private InputStreamRequest request;

    public Downloader(@NonNull AssetFile assetFile, @NonNull ResponseListener listener) {
        this.assetFile = assetFile;
        this.listener = listener;
    }

    @Override
    public void start() {
        request = new InputStreamRequest(
                Request.Method.GET,
                assetFile.getUrl(),
                response -> {
                    Log.d(TAG, "onResponse: " + assetFile.getUrl());
                    boolean c = listener.onStart(request.getResponseHeaders());
                    if (!c) {
                        return;
                    }

                    InputStream in = null;
                    try {
                        in = new ByteArrayInputStream(response);
                        IoUtils.write(in, assetFile.getFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    assetFile.setStatus(AssetFile.STATUS_READY);
                    listener.onComplete(assetFile, request.getResponseHeaders());
                }, volleyError -> {
            assetFile.setStatus(AssetFile.STATUS_ERROR);
            listener.onError(assetFile, volleyError);
        });

        request.setRetryPolicy(new DefaultRetryPolicy(VolleyApplication.TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApplication.getInstance().addToRequestQueue(request);
    }
}
