package com.gmail.jiangyang5157.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author Yang
 * @since 6/18/2016
 */
public class VolleyApplication extends Application {
    private static final String TAG = "[VolleyApplication]";

    public static final int TIMEOUT_MS = 1000;

    private static VolleyApplication instance = null;

    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized VolleyApplication getInstance() {
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        addToRequestQueue(request, TAG);
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests() {
        cancelPendingRequests(TAG);
    }

    public void cancelPendingRequests(String tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
