package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.preference.PreferenceManager;

import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.base.data.RegularExpressionUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Yang
 * @since 6/5/2016
 */
public class AssetUtils {


    public static final String URL_PROTOCOL = "http";
    public static final String PORT = "5157";
    /**
     * For a convenience of editing URLs in the KML, always use the localhost IP to decorate URLs.
     * App will extract the localhost IP (if found) and replace it with real IP address.
     */
    public static final String IP_ADDRESS_LOCALHOST = "localhost";
    public static String IP_ADDRESS = "192.168.1.101";

    // App Profile Path: /data/user/0/com.gmail.jiangyang5157.cardboard.vr
    public static final String DIRECTORY_STATIC = "static";
    public static final String DIRECTORY_LAYER = DIRECTORY_STATIC + File.separator + "layer";
    public static final String DIRECTORY_KML = DIRECTORY_STATIC + File.separator + "kml";
    public static final String DIRECTORY_MODEL = DIRECTORY_STATIC + File.separator + "model";
    public static final String DIRECTORY_RESOURCE = DIRECTORY_STATIC + File.separator + "resource";

    public static final String PATCH_FILE_NAME = "patch.zip";
    public static final String EARTH_TEXTURE_FILE_NAME = "world_map.jpg";

    public static final String PATCH_LAST_MODIFIED_TIME_KEY = "PATCH_LAST_MODIFIED_KEY";
    public static final long PATCH_LAST_MODIFIED_TIME_DEFAULT = 0;

    public static final String KML_FILE_ENDS = ".kml";
    public static final String LAYER_FILE_NAME_KEY = "LAYER_FILE_NAME_KEY";
    public static final String LAYER_FILE_NAME_DEFAULT = "example" + KML_FILE_ENDS;

    public static String getApiUrlPrefix(String ipAddress) {
        return URL_PROTOCOL + "://" + ipAddress + ":" + PORT + "/api/";
    }

    public static String getAssetsUrlPrefix(String ipAddress) {
        return URL_PROTOCOL + "://" + ipAddress + ":" + PORT + "/assets/";
    }

    public static String localhost2RealMachine(String url) {
        return url.replaceFirst(getAssetsUrlPrefix(IP_ADDRESS_LOCALHOST), getAssetsUrlPrefix(IP_ADDRESS));
    }

    public static boolean setLastLayerFileName(Context context, String fileName) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LAYER_FILE_NAME_KEY, fileName).commit();
    }

    public static String getLastLayerFileName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LAYER_FILE_NAME_KEY, LAYER_FILE_NAME_DEFAULT);
    }

    public static boolean setLastPatchLastModifiedTime(Context context, long time) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PATCH_LAST_MODIFIED_TIME_KEY, time).commit();
    }

    public static long getLastPatchLastModifiedTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PATCH_LAST_MODIFIED_TIME_KEY, PATCH_LAST_MODIFIED_TIME_DEFAULT);
    }

    public static String getAssetUrl(String path) {
        return getAssetsUrlPrefix(IP_ADDRESS) + path;
    }

    public static String getAssetPath(String url) {
        return url.replaceFirst(getAssetsUrlPrefix(IP_ADDRESS), "");
    }

    public static String getAbsolutePath(Context context, String path) {
        return AppUtils.getProfilePath(context) + File.separator + path;
    }

    public static String getResourcePath(String fileName) {
        return DIRECTORY_RESOURCE + File.separator + fileName;
    }

    public static String getResourceUrl(String fileName) {
        return getAssetUrl(getResourcePath(fileName));
    }

    public static String getModelPath(String fileName) {
        return DIRECTORY_MODEL + File.separator + fileName;
    }

    public static String getModelUrl(String fileName) {
        return getAssetUrl(getModelPath(fileName));
    }

    public static String getLayerPath(String fileName) {
        return DIRECTORY_LAYER + File.separator + fileName;
    }

    public static String getLayerUrl(String fileName) {
        return getAssetUrl(getLayerPath(fileName));
    }

    public static String getKmlPath(String fileName) {
        return DIRECTORY_KML + File.separator + fileName;
    }

    public static String getKmlUrl(String fileName) {
        return getAssetUrl(getKmlPath(fileName));
    }

    public static String getPatchPath() {
        return PATCH_FILE_NAME;
    }

    public static String getPatchUrl() {
        return getAssetUrl(getPatchPath());
    }

    public static long getHttpDateTime(String httpDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(RegularExpressionUtils.DATE_REGEX_HTTP_DATE, Locale.ENGLISH);
        return format.parse(httpDate).getTime();
    }

    public static String getKmlSimpleFileName(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(KML_FILE_ENDS)) {
            return fileName.substring(0, fileName.length() - KML_FILE_ENDS.length());
        } else {
            return fileName;
        }
    }
}
