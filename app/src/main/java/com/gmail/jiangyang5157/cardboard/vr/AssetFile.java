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

    private final boolean requireUpdate;

    public AssetFile(@NonNull File file, @NonNull String url) {
        this(file, url, false);
    }

    public AssetFile(@NonNull File file, @NonNull String url, boolean requireUpdate) {
        this.file = file;
        this.url = url;
        this.requireUpdate = requireUpdate;
    }

    public File getFile() {
        return file;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRequireUpdate() {
        return requireUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof AssetFile)) {
            return false;
        } else {
            AssetFile that = (AssetFile) o;
            return this.url.equalsIgnoreCase(that.getUrl()) && this.file.getAbsolutePath().equalsIgnoreCase(that.file.getAbsolutePath());
        }
    }

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 37 * ret + file.hashCode();
        ret = 37 * ret + url.hashCode();
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AssetFile").append("{");
        sb.append("\n file=").append(file.getAbsolutePath());
        sb.append(",\n url=").append(url);
        sb.append(",\n requireUpdate=").append(requireUpdate);
        sb.append("\n}\n");
        return sb.toString();
    }
}
