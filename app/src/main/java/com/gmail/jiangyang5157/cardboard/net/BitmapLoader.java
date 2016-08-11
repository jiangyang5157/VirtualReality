package com.gmail.jiangyang5157.cardboard.net;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.app.VolleyApplication;

import java.util.Map;

/**
 * @author Yang
 * @since 8/11/2016
 */
public class BitmapLoader extends NetRequest {
    private static final String TAG = "[BitmapLoader]";

    public interface ResponseListener {
        void onComplete(Map<String, String> headers, Bitmap bitmap);

        void onError(String url, VolleyError volleyError);
    }

    private BitmapRequest request;

    public BitmapLoader(final String url, int width, final ResponseListener listener) {
        request = new BitmapRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.d(TAG, "ImageRequest.onResponse: w/h: " + bitmap.getWidth() + ", " + bitmap.getHeight());
                        listener.onComplete(request.getResponseHeaders(), bitmap);
                    }
                }, width, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_4444,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "ImageRequest.onErrorResponse: " + volleyError.toString());
                        listener.onError(url, volleyError);
                    }
                }
        );
    }

    @Override
    public void start() {
        request.setRetryPolicy(new DefaultRetryPolicy(VolleyApplication.TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApplication.getInstance().addToRequestQueue(request);
    }
}
