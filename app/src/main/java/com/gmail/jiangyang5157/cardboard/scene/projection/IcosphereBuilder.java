package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.util.ArrayMap;
import android.util.Log;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class IcosphereBuilder {

    private volatile static IcosphereBuilder uniqueInstance = null;

    private ArrayMap<Integer, IcosphereVertex> icosphereVertices = new ArrayMap<>();

    public static IcosphereBuilder getInstance() {
        if (uniqueInstance == null) {
            synchronized (IcosphereBuilder.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new IcosphereBuilder();
                }
            }
        }
        return uniqueInstance;
    }

    private IcosphereBuilder() {
    }

    protected IcosphereVertex build(int recursionLevel) {
        final int MAX_RECURSION_LEVEL = IcosphereVertex.VERTEX_COUNTS.length - 1;
        if (recursionLevel > MAX_RECURSION_LEVEL) {
            throw new RuntimeException("Unable to build a Icosphere with recursion level greater than " + MAX_RECURSION_LEVEL);
        }

        Integer vertexCount = IcosphereVertex.VERTEX_COUNTS[recursionLevel];
        IcosphereVertex ret = icosphereVertices.get(vertexCount);
        if (ret == null) {
            ret = new IcosphereVertex(recursionLevel);
            icosphereVertices.put(vertexCount, ret);
            Log.d("####", "IcosphereBuilder: created IcosephereVertex object with vertexCount=" + vertexCount);
        }
        return ret;
    }
}
