package com.gmail.jiangyang5157.cardboard.net;

import com.android.volley.VolleyError;

import java.util.Map;

/**
 * @author Yang
 * @since 8/11/2016
 */
public interface ResponseListener {
    boolean onStart(Map<String, String> headers);

    void onComplete(Map<String, String> headers);

    void onError(String url, VolleyError volleyError);
}
