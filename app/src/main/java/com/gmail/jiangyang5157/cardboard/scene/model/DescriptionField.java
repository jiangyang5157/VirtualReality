package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.base.data.RegularExpressionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yang
 * @since 8/6/2016
 */
public class DescriptionField extends TextField {
    private static final String TAG = "[DescriptionField]";

    public DescriptionField(Context context) {
        super(context);
    }

    @Override
    protected void buildTextureBuffers() {
        String text = null;
        Pattern pattern = Pattern.compile(RegularExpressionUtils.URL_TEMPLATE);
        Matcher matcher = pattern.matcher(content);
        boolean find = matcher.find();
        Log.d(TAG, "matcher find " + find + ": " + content);
        if (find) {
            Log.d(TAG, "start, end: " + matcher.start() + ", " + matcher.end());
            String url = content.substring(matcher.start(), matcher.end());
            text = limitedString(url, MAX_TEXT_LENGTH);
        } else {
            text = limitedString(content, MAX_TEXT_LENGTH);
        }
        textureBitmap[0] = buildTextBitmap(text);


    }


}
