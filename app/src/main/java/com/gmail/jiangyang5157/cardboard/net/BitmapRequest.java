package com.gmail.jiangyang5157.cardboard.net;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import java.util.Map;

/**
 * @author Yang
 * @since 8/11/2016
 */
public class BitmapRequest extends ImageRequest {
    private static final String TAG = "[BitmapRequest]";

    private Map<String, String> responseHeaders;

    public BitmapRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        responseHeaders = response.headers;
        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            Log.d(TAG, "headers: key/value: " + entry.getKey() + ", " + entry.getValue());
        }
        return super.parseNetworkResponse(response);
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }
}
