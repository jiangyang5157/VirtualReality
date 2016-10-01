package com.gmail.jiangyang5157.cardboard.net;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.app.VolleyApplication;

/**
 * @author Yang
 * @since 8/11/2016
 */
public class DescriptionLoader extends NetRequest {
    private static final String TAG = "[DescriptionLoader]";

    public interface ResponseListener {
        void onComplete(Bitmap bitmap);

        void onComplete(String string);

        void onError(String url, VolleyError volleyError);
    }

    private DescriptionRequest request;

    public DescriptionLoader(final String url, int width, final ResponseListener listener) {
        request = new DescriptionRequest(url,
                object -> {
                    switch (request.responseType) {
                        case DescriptionRequest.RESPONSE_TYPE_BITMAP:
                            listener.onComplete((Bitmap) object);
                            break;
                        case DescriptionRequest.RESPONSE_TYPE_STRING:
                            listener.onComplete((String) object);
                            break;
                        default:
                            Log.e(TAG, "DescriptionLoader.onResponse returns an unknown type.");
                            break;
                    }
                }, width, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_4444,
                volleyError -> listener.onError(url, volleyError)
        );
    }

    @Override
    public void start() {
        request.setRetryPolicy(new DefaultRetryPolicy(VolleyApplication.TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApplication.getInstance().addToRequestQueue(request);
    }
}
