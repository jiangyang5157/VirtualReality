package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.kml.KmlContainer;
import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Yang
 * @since 9/29/2016
 */
public class KmlLayerCache {
    private static final String TAG = "[KmlLayerCache]";

    private HashMap<String, HashSet<KmlLayer>> containerMap;

    public static final String KEY_SEPARATOR = ",";

    public KmlLayerCache() {
        containerMap = new HashMap<>();
    }

    public HashMap<String, HashSet<KmlLayer>> getContainerMap() {
        return containerMap;
    }

    public HashSet<KmlLayer> getContainerRelatedLayers(String key) {
        return containerMap.get(key);
    }

    private void mapContainers(Iterable<KmlContainer> kmlContainers, KmlLayer layer, String name) {
        for (KmlContainer container : kmlContainers) {
            String key = container.getProperty("name");
            if (container.hasPlacemarks()) {
                if (key == null) {
                    // Empty container name, use default name to display
                    key = KEY_SEPARATOR + name;
                } else if (key.equalsIgnoreCase("Untitled layer")) {
                    // Untitled container name, use default name to display
                    key = KEY_SEPARATOR + name;
                } else {
                    //  Use container name to display
                    key = key + KEY_SEPARATOR;
                }

                if (key != null) {
                    HashSet<KmlLayer> layers = containerMap.get(key);
                    if (layers == null) {
                        layers = new HashSet<>();
                    }
                    layers.add(layer);
                    containerMap.put(key, layers);
                }
            }

            if (container.hasContainers()) {
                mapContainers(container.getContainers(), layer, name);
            }
        }
    }

    public void cache(Context context) {
        containerMap.clear();

        File[] kmlFiles = getKmlFiles(context);
        for (final File file : kmlFiles) {
            cacheLayer(file, context);
        }
    }

    KmlLayer layer;

    private void cacheLayer(File file, Context context) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            layer = new KmlLayer(null, in, context);
            mapContainers(layer.getContainers(), layer, file.getName());
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
            ret = directory.listFiles((dir, fileName) -> fileName.endsWith(AssetUtils.KML_FILE_ENDS));
        }
        return ret;
    }

    public boolean isReady() {
        return !containerMap.isEmpty();
    }
}
