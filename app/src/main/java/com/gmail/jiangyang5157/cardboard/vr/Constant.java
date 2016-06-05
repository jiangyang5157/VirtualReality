package com.gmail.jiangyang5157.cardboard.vr;

import java.io.File;

/**
 * @author Yang
 * @since 6/5/2016
 */
public class Constant {
    public static final int DEBUG = 0;

    // profile path:
    // /data/user/0/com.gmail.jiangyang5157.cardboard.vr

    public static final String DIRECTORY_KML = "kml";
    public static final String DIRECTORY_MODEL = "model";
    public static final String DIRECTORY_RESOURCE = "resource";

    public static String getResourceFilePath(String fileName) {
        return DIRECTORY_RESOURCE + File.separator + fileName;
    }

    public static String getModelFilePath(String fileName) {
        return DIRECTORY_MODEL + File.separator + fileName;
    }

    public static String getKmlFilePath(String fileName) {
        return DIRECTORY_KML + File.separator + fileName;
    }
}
