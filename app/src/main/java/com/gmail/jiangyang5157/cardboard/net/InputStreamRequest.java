package com.gmail.jiangyang5157.cardboard.net;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class InputStreamRequest extends Request<byte[]> {
    private static final String TAG = "[InputStreamRequest]";

    private final Response.Listener<byte[]> mListener;

    private Map<String, String> responseHeaders;

    public InputStreamRequest(
            int method,
            String url,
            Response.Listener<byte[]> listener,
            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;
//        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
//            Log.d(TAG, "headers: key/value: " + entry.getKey() + ", " + entry.getValue());
//        }
//        String contentType = responseHeaders.get("Content-Type");
//        Log.d(TAG, "parseNetworkResponse.contentType: " + contentType);
        //Pass the response data here
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }
}