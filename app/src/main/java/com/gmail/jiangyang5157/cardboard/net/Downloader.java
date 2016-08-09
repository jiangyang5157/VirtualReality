package com.gmail.jiangyang5157.cardboard.net;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.app.VolleyApplication;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.tookit.base.data.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Yang
 * @since 6/19/2016
 */
public class Downloader {
    private static final String TAG = "[Downloader]";

    public interface ResponseListener {
        boolean onStart(Map<String, String> headers);

        void onComplete(Map<String, String> headers);

        void onError(String url, VolleyError volleyError);
    }

    private final InputStreamRequest request;

    public Downloader(final String url, final File file, final ResponseListener listener) {
        request = new InputStreamRequest(
                Request.Method.GET,
                url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d(TAG, "onResponse: " + url);
                        boolean c = listener.onStart(request.getResponseHeaders());
                        if (!c) {
                            return;
                        }

                        InputStream in = null;
                        try {
                            in = new ByteArrayInputStream(response);
                            IoUtils.write(in, file);
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
                        listener.onComplete(request.getResponseHeaders());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(url, volleyError);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(Constant.TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApplication.getInstance().addToRequestQueue(request);
    }
}
