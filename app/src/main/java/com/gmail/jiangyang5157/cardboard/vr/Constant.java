package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.app.DeviceUtils;
import com.gmail.jiangyang5157.tookit.app.RegularExpression;
import com.gmail.jiangyang5157.tookit.data.io.IoUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Yang
 * @since 6/5/2016
 */
public class Constant {
    private static final String TAG = "[Constant]";
    public static final int DEBUG = 0;

    public static final String URL_ = "http://192.168.1.68:8080/assets/";

    // profile path:
    // /data/user/0/com.gmail.jiangyang5157.cardboard.vr

    public static final String DIRECTORY_STATIC = "static";
    public static final String DIRECTORY_KML = DIRECTORY_STATIC + File.separator + "kml";
    public static final String DIRECTORY_MODEL = DIRECTORY_STATIC + File.separator + "model";
    public static final String DIRECTORY_RESOURCE = DIRECTORY_STATIC + File.separator + "resource";

    public static final String KML_FILE_NAME_KEY = "KML_FILE_NAME_KEY";
    public static final String KML_FILE_NAME_DEFAULT = "example.kml";

    public static final String PATCH_FILE_NAME = "static.zip";

    public static final String PATCH_LAST_MODIFIED_TIME_KEY = "PATCH_LAST_MODIFIED_KEY";
    public static final long PATCH_LAST_MODIFIED_TIME_DEFAULT = 0;

    public static final String EARTH_TEXTURE_FILE_NAME = "world_map.jpg";

    public static boolean setLastKmlFileName(Context context, String fileName) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KML_FILE_NAME_KEY, fileName).commit();
    }

    public static String getLastKmlFileName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KML_FILE_NAME_KEY, KML_FILE_NAME_DEFAULT);
    }

    public static boolean setLastPatchLastModifiedTime(Context context, long time) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PATCH_LAST_MODIFIED_TIME_KEY, time).commit();
    }

    public static long getLastPatchLastModifiedTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PATCH_LAST_MODIFIED_TIME_KEY, PATCH_LAST_MODIFIED_TIME_DEFAULT);
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

    public static String getPatchPath() {
        return PATCH_FILE_NAME;
    }

    public static String getPatchUrl() {
        return getUrl(getPatchPath());
    }

    public static long getHttpDateTime(String httpDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(RegularExpression.DATE_TEMPLATE_HTTP_DATE);
        return format.parse(httpDate).getTime();
    }
}
