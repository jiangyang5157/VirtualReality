package com.gmail.jiangyang5157.cardboard.scene;

/**
 * Created by Yang on 3/21/2016.
 */
public class Sphere extends SphereLayout implements Model {

    public float[] model = new float[16];

    /**
     * @param rings   defines how many circles exists from the bottom to the top of the sphere
     * @param sectors defines how many vertexes define a single ring
     * @param radius  defines the distance of every vertex from the center of the sphere
     */
    public Sphere(int rings, int sectors, float radius) {
        super(rings, sectors, radius);
    }
}
