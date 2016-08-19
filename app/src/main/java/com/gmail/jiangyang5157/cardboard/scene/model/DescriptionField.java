package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.net.DescriptionLoader;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.base.data.RegularExpressionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yang
 * @since 8/6/2016
 */
public class DescriptionField extends TextField {
    private static final String TAG = "[DescriptionField]";

    private Event eventListener;

    public interface Event {
        void onPrepareComplete();
    }

    public DescriptionField(Context context) {
        super(context);
    }

    @Override
    public void prepare(final Ray ray) {
        Pattern pattern = Pattern.compile(RegularExpressionUtils.URL_TEMPLATE);
        Matcher matcher = pattern.matcher(content);
        boolean find = matcher.find();
        if (find) {
            String url = content.substring(matcher.start(), matcher.end());
            url = url.replaceFirst(".wikipedia.org/wiki/", ".wikipedia.org/w/api.php?format=json&action=query&redirects=1&prop=extracts&exintro=&explaintext=&indexpageids=&titles=");
            Log.d(TAG, "URL matcher content: " + content + "\n" + url);
            new DescriptionLoader(url, (int) width, new DescriptionLoader.ResponseListener() {
                @Override
                public void onComplete(Bitmap bitmap) {
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();
                    Log.d(TAG, "Response.Listener.onResponse: bitmap w/h: " + w + ", " + h);
                    height = h;
                    Bitmap texture = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(texture);
                    texture.eraseColor(getColorWithAlpha(ALPHA_BACKGROUND));
                    canvas.drawBitmap(bitmap, (width - w) / 2, 0, null);

                    textureBitmap[0] = texture;
                    buildData();
                    eventListener.onPrepareComplete();
                }

                @Override
                public void onComplete(String string) {
                    Log.d(TAG, "Response.Listener.onResponse: string: " + string);
                    textureBitmap[0] = buildTextBitmap(ellipsizeString(string, MAX_TEXT_LENGTH));
                    buildData();
                    eventListener.onPrepareComplete();
                }

                @Override
                public void onError(String url, VolleyError volleyError) {
                    Log.d(TAG, "Response.Listener.onErrorResponse: " + volleyError.toString());
                    // TODO: 8/18/2016
                    AppUtils.buildToast(context, "Error: Description Parser");
                    textureBitmap[0] = buildTextBitmap(ellipsizeString(content, MAX_TEXT_LENGTH));
                    buildData();
                    eventListener.onPrepareComplete();
                }
            }).start();
        } else {
            textureBitmap[0] = buildTextBitmap(ellipsizeString(content, MAX_TEXT_LENGTH));
            buildData();
            eventListener.onPrepareComplete();
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
