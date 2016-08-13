/*
Reference: com.android.volley.toolbox.ImageRequest
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gmail.jiangyang5157.cardboard.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
 * parseNetworkResponse returns either bitmap or string
 *
 * @author Yang
 * @since 8/12/2016
 */
public class DescriptionRequest extends Request<Object> {
    private static final String TAG = "[DescriptionRequest]";

    private Map<String, String> responseHeaders;

    protected static final int RESPONSE_TYPE_BITMAP = 1;
    protected static final int RESPONSE_TYPE_STRING = 2;
    protected int responseType;

    /**
     * Socket timeout in milliseconds for image requests
     */
    public static final int DEFAULT_IMAGE_TIMEOUT_MS = 1000;

    /**
     * Default number of retries for image requests
     */
    public static final int DEFAULT_IMAGE_MAX_RETRIES = 2;

    /**
     * Default backoff multiplier for image requests
     */
    public static final float DEFAULT_IMAGE_BACKOFF_MULT = 2f;

    private final Response.Listener<Object> mListener;
    private final Bitmap.Config mDecodeConfig;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private ImageView.ScaleType mScaleType;

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();

    /**
     * Creates a new image request, decoding to a maximum specified width and
     * height. If both width and height are zero, the image will be decoded to
     * its natural size. If one of the two is nonzero, that dimension will be
     * clamped and the other one will be set to preserve the image's aspect
     * ratio. If both width and height are nonzero, the image will be decoded to
     * be fit in the rectangle of dimensions width x height while keeping its
     * aspect ratio.
     *
     * @param url           URL of the image
     * @param listener      Listener to receive the decoded bitmap
     * @param maxWidth      Maximum width to decode this bitmap to, or zero for none
     * @param maxHeight     Maximum height to decode this bitmap to, or zero for
     *                      none
     * @param scaleType     The ImageViews ScaleType used to calculate the needed image size.
     * @param decodeConfig  Format to decode the bitmap to
     * @param errorListener Error listener, or null to ignore errors
     */
    public DescriptionRequest(String url, Response.Listener<Object> listener, int maxWidth, int maxHeight,
                              ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_IMAGE_TIMEOUT_MS, DEFAULT_IMAGE_MAX_RETRIES,
                DEFAULT_IMAGE_BACKOFF_MULT));
        mListener = listener;
        mDecodeConfig = decodeConfig;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mScaleType = scaleType;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for
     *                        max width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to
     *                        maintain aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     * @param scaleType       The ScaleType used to calculate the needed image size.
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary, ImageView.ScaleType scaleType) {

        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    @Override
    protected Response<Object> parseNetworkResponse(NetworkResponse response) {
        responseHeaders = response.headers;
//        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
//            Log.d(TAG, "headers: key/value: " + entry.getKey() + ", " + entry.getValue());
//        }
        String contentType = responseHeaders.get("Content-Type");
        Log.d(TAG, "headers.contentType: " + contentType);

        if (contentType.startsWith("image/jpeg")) {
            responseType = RESPONSE_TYPE_BITMAP;
            // Serialize all decode on a global lock to reduce concurrent heap usage.
            synchronized (sDecodeLock) {
                try {
                    return doBitmapParse(response);
                } catch (OutOfMemoryError e) {
                    VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                    return Response.error(new ParseError(e));
                }
            }
        } else if (contentType.startsWith("text/html")) {
            responseType = RESPONSE_TYPE_STRING;

            // TODO: 8/12/2016
//            String parsed;
//            try {
//                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            } catch (UnsupportedEncodingException e) {
//                parsed = new String(response.data);
//            }
//            Log.d(TAG, parsed);
//            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
//            return Response.error(new ParseError());

//            try {
//                final String PROTOCOL_CHARSET = "utf-8";
//                String jsonString = new String(response.data,
//                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
//                Log.d(TAG, jsonString);
//                Log.d(TAG, "======================================");
//                JSONArray jsonArray = new JSONArray(jsonString);
//                int size = jsonArray.length();
//                for (int i = 0; i < size; i++) {
//                    String jsonObj = jsonArray.getString(i);
//                    Log.d(TAG, jsonObj.toString());
//                    Log.d(TAG, "################");
//                    JSONObject o = jsonArray.getJSONObject(i);
//                    Log.d(TAG, o.toString());
//                }
//                Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                Log.d(TAG, jsonArray.toString());
//
//                return Response.error(new ParseError());
//                return Response.success(new JSONArray(jsonString),
//                        HttpHeaderParser.parseCacheHeaders(response));
//            } catch (UnsupportedEncodingException e) {
//                return Response.error(new ParseError(e));
//            } catch (JSONException je) {
//                return Response.error(new ParseError(je));
//            }

            return Response.error(new ParseError());
        } else {
            return Response.error(new ParseError());
        }
    }

    /**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    private Response<Object> doBitmapParse(NetworkResponse response) {
        byte[] data = response.data;
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            decodeOptions.inPreferredConfig = mDecodeConfig;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                    actualWidth, actualHeight, mScaleType);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                    actualHeight, actualWidth, mScaleType);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
            // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap =
                    BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        if (bitmap == null) {
            return Response.error(new ParseError(response));
        } else {
            return Response.success(bitmap, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    @Override
    protected void deliverResponse(Object response) {
        mListener.onResponse(response);
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }
}
