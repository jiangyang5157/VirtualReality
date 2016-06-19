package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.app.DeviceUtils;
import com.gmail.jiangyang5157.tookit.data.io.IoUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Yang
 * @since 6/5/2016
 */
public class Constant {
    private static final String TAG = "[Constant]";
    public static final int DEBUG = 0;

    public static final String URL_ = "http://192.168.1.68:8080/";

    // profile path:
    // /data/user/0/com.gmail.jiangyang5157.cardboard.vr

    public static final String DIRECTORY_STATIC = "static";
    public static final String DIRECTORY_KML = DIRECTORY_STATIC + File.separator + "kml";
    public static final String DIRECTORY_MODEL = DIRECTORY_STATIC + File.separator + "model";
    public static final String DIRECTORY_RESOURCE = DIRECTORY_STATIC + File.separator + "resource";

    public static final String KML_URL_KEY = "KML_FILENAME_KEY";
    public static final String KML_URL_DEFAULT = getKmlUrl("example.kml");

    public static String getLastKmlUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KML_URL_KEY, KML_URL_DEFAULT);
    }

    public static String getUrl(String path) {
        return URL_ + path;
    }

    public static String getPath(String url) {
        return url.replaceFirst(URL_, "");
    }

    public static String getAbsolutePath(Context context, String path) {
        return AppUtils.getProfilePath(context) + File.separator + path;
    }

    public static String getResourcePath(String fileName) {
        return DIRECTORY_RESOURCE + File.separator + fileName;
    }

    public static String getResourceUrl(String fileName) {
        return getUrl(getResourcePath(fileName));
    }

    public static String getModelPath(String fileName) {
        return DIRECTORY_MODEL + File.separator + fileName;
    }

    public static String getModelUrl(String fileName) {
        return getUrl(getModelPath(fileName));
    }

    public static String getKmlPath(String fileName) {
        return DIRECTORY_KML + File.separator + fileName;
    }

    public static String getKmlUrl(String fileName) {
        return getUrl(getKmlPath(fileName));
    }
}
