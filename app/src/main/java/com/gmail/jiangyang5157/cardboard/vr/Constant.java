package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.preference.PreferenceManager;

import com.gmail.jiangyang5157.tookit.app.DeviceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Yang
 * @since 6/5/2016
 */
public class Constant {
    public static final int DEBUG = 0;

    public static final String URL_ = "https://unimplemented/";

    private static final String KML_URL_KEY = "KML_FILENAME_KEY";
    private static final String KML_URL_DEFAULT = getKmlUrl("example.kml");

    // profile path:
    // /data/user/0/com.gmail.jiangyang5157.cardboard.vr

    private static final String DIRECTORY_KML = "kml";
    private static final String DIRECTORY_MODEL = "model";
    private static final String DIRECTORY_RESOURCE = "resource";

    public static void write(InputStream ins, File dst) throws IOException {
        dst.getParentFile().mkdirs();
        OutputStream outs = new FileOutputStream(dst);
        byte[] buffer = new byte[DeviceUtils.SIZE_UNIT];
        int length;
        while ((length = ins.read(buffer)) > 0) {
            outs.write(buffer, 0, length);
        }
        ins.close();
        outs.close();
    }

    public static void copy(File src, File dst) throws IOException {
        write(new FileInputStream(src), dst);
    }

    public static String getLastKmlUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KML_URL_KEY, KML_URL_DEFAULT);
    }

    public static String getUrl(String path) {
        return URL_ + path;
    }

    public static String getPath(String url) {
        return url.replaceFirst(URL_, "");
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
