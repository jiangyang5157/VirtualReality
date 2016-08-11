package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.net.BitmapLoader;
import com.gmail.jiangyang5157.tookit.base.data.RegularExpressionUtils;

import java.util.Map;
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
            Log.d(TAG, "URL matcher content: " + content + "\n" + url);
            new BitmapLoader(url, (int) width, new BitmapLoader.ResponseListener() {
                @Override
                public void onComplete(Map<String, String> headers, Bitmap bitmap) {
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();
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
                public void onError(String url, VolleyError volleyError) {
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
