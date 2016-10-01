package com.gmail.jiangyang5157.cardboard.vr;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * @author Yang
 * @since 10/1/2016
 */

public class AssetFile {

    private File file;

    private String url;

    public static final int STATUS_NOT_READY = 0;
    public static final int STATUS_ERROR = -1;
    public static final int STATUS_READY = 1;

    private int status = STATUS_NOT_READY;

    public AssetFile(@NonNull File file, @NonNull String url) {
        this.file = file;
        this.url = url;

        if (file.exists()) {
            status = STATUS_READY;
        }
    }

    public boolean exists() {
        return file.exists();
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public String getUrl() {
        return url;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public boolean isReady() {
        return status == STATUS_READY;
    }

    @Override
    public boolean equals(Object that) {
        if (that != null && that instanceof AssetFile) {
            AssetFile thatAssetFile = (AssetFile) that;
            if (this.url.equalsIgnoreCase(thatAssetFile.getUrl())
                    && this.file.getAbsolutePath().equalsIgnoreCase(thatAssetFile.file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 37 * ret + file.hashCode();
        ret = 37 * ret + url.hashCode();
        return ret;
    }

    public static int hashCode(double d) {
        long longBits = Double.doubleToLongBits(d);
        return (int) (longBits ^ (longBits >>> 32));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AssetFile").append("{");
        sb.append("\n file=").append(file.getAbsolutePath());
        sb.append(",\n url=").append(url);
        sb.append(",\n status=").append(status);
        sb.append("\n}\n");
        return sb.toString();
    }
}
