package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.gmail.jiangyang5157.tookit.base.data.RegularExpressionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yang
 * @since 8/6/2016
 */
public class DescriptionField extends TextField {

    public DescriptionField(Context context) {
        super(context);
    }

    @Override
    protected void buildTextureBuffers() {
        super.buildTextureBuffers();
//        Pattern pattern = Pattern.compile(RegularExpressionUtils.URL_TEMPLATE);
//        Matcher matcher = pattern.matcher(content);
//        boolean find = matcher.find();
//        System.out.println("content: " + content + " matcher.find=" + find);
//        if (find) {
//            System.out.println("start, end: " + matcher.start() + ", " + matcher.end());
//        }
    }


}
