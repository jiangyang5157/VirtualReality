package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;

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
    protected Bitmap buildBitmap() {

//        "^((https?|ftp)://|(www|ftp)\\\\.)?[a-z0-9-]+(\\\\.[a-z0-9-]+)+([/?].*)?$";
//        Pattern pattern = Pattern.compile(RegularExpressionUtils.URL_TEMPLATE);
//        Matcher match = pattern.matcher("example.com");//replace with string to compare
//        if (match.find()) {
//            System.out.println("String contains URL");
//        }



        return super.buildBitmap();
    }


}
