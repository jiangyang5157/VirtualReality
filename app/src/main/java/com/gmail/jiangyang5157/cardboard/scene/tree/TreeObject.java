package com.gmail.jiangyang5157.cardboard.scene.tree;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class TreeObject {

    protected float[] center;

    protected float radius;

    public TreeObject(float[] center, float radius) {
        this.center = center;
        this.radius = radius;
    }

//    @Override
//    public int hashCode() {
//        int ret = 17;
//        for (float data : center) {
//            ret = 37 * ret + hashCode(data);
//        }
//        ret = 37 * ret + hashCode(radius);
//        return ret;
//    }
//
//    private int hashCode(double d) {
//        long longBits = Double.doubleToLongBits(d);
//        return (int) (longBits ^ longBits >>> 32);
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null || this.getClass() != obj.getClass()) {
//            return false;
//        }
//        if (this == obj) {
//            return true;
//        }
//        TreeObject that = (TreeObject) obj;
//        return this.center[0] == that.center[0]
//                && this.center[1] == that.center[1]
//                && this.center[2] == that.center[2]
//                && this.radius == that.radius;
//    }
}
