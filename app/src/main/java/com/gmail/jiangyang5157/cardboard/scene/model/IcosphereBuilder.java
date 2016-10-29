package com.gmail.jiangyang5157.cardboard.scene.model;

import android.util.ArrayMap;
import android.util.Log;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class IcosphereBuilder {
    private static final String TAG = "[IcosphereBuilder]";

    private static IcosphereBuilder instance;

    private ArrayMap<Integer, IcosphereVertex> icosphereVertices;

    public static synchronized IcosphereBuilder getInstance() {
        if (instance == null) {
            instance = new IcosphereBuilder();
        }
        return instance;
    }

    private IcosphereBuilder() {
        icosphereVertices = new ArrayMap<>();
    }

    public IcosphereVertex build(int recursionLevel) {
        final int MAX_RECURSION_LEVEL = IcosphereVertex.VERTEX_COUNTS.length - 1;
        if (recursionLevel > MAX_RECURSION_LEVEL) {
            throw new RuntimeException(TAG + ": Unable to created Icosephere with recursion level greater than " + MAX_RECURSION_LEVEL);
        }

        Integer vertexCount = IcosphereVertex.VERTEX_COUNTS[recursionLevel];
        IcosphereVertex ret = icosphereVertices.get(vertexCount);
        if (ret == null) {
            ret = new IcosphereVertex(recursionLevel);
            icosphereVertices.put(vertexCount, ret);
            Log.d(TAG, "Created Icosephere with recursionLevel=" + recursionLevel + " vertexCount=" + vertexCount);
        }
        return ret;
    }
}
