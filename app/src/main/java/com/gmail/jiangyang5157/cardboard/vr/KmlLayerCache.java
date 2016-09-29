package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Yang
 * @since 9/29/2016
 */
public class KmlLayerCache {
    private static final String TAG = "[KmlLayerCache]";

    private ArrayList<KmlLayer> layers;

    public KmlLayerCache() {
        layers = new ArrayList<>();
    }

    public void cache(Context context) {
        layers.clear();

        File[] kmlFiles = getKmlFiles(context);
        for (final File file : kmlFiles) {
            cacheLayer(file, context);
        }
    }

    private void cacheLayer(File file, Context context) {
        Log.d(TAG, "cacheLayer: " + file.getAbsolutePath());
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            layers.add(new KmlLayer(null, in, context));
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File[] getKmlFiles(Context context) {
        File[] ret = null;

        File directory = new File(AssetUtils.getAbsolutePath(context, AssetUtils.getKmlPath("")));
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        } else {
            ret = directory.listFiles((dir, filename) -> filename.endsWith(".kml"));
        }
        return ret;
    }

    public boolean isReady() {
        return !layers.isEmpty();
    }
}
