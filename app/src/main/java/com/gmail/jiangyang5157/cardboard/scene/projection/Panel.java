package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Panel extends Rectangle {

    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.panel_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.panel_fragment_shader;

    protected float[] vertices;
    protected float[] normals;
    protected short[] indices;
    protected float[] textures;

    protected Vector tlVec;
    protected Vector blVec;
    protected Vector trVec;
    protected Vector brVec;

    private final int[] buffers = new int[3];
    private final int[] texBuffers = new int[1];

    protected static final float DISTANCE = 400;

    public Panel(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
    }

    protected void create(float width, float height, int color) {
        this.width = width;
        this.height = height;
        setColor(color);
        buildArrays();

        initializeProgram();
        bindBuffers();
        isCreated = true;

        setVisible(true);
    }

    public void setPosition(float[] cameraPos, float[] forward, float[] quaternion, float[] up, float[] right) {
        Vector cameraPosVec = new Vector3d(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardVec = new Vector3d(forward[0], forward[1], forward[2]).times(DISTANCE);
        Vector positionVec = cameraPosVec.plus(forwardVec);

        double[] positionVecData = positionVec.getData();
        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0, (float) positionVecData[0], (float) positionVecData[1], (float) positionVecData[2]);

        Matrix.setIdentityM(rotation, 0);
        // it should face to eye
        float[] q = new float[]{-quaternion[0], -quaternion[1], -quaternion[2], quaternion[3]};
        Matrix.multiplyMM(rotation, 0, Head.getQquaternionMatrix(q), 0, rotation, 0);

        // build corners' vector, they are for intersect calculation
        buildCorners(up, right, positionVec);
    }

    @Override
    public Intersection onIntersect(Head head) {
        if (!isVisible || !isCreated()) {
            return null;
        }

        float[] cameraPos = head.getCamera().getPosition();
        Vector cameraPosVec = new Vector(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardVec = new Vector(head.forward[0], head.forward[1], head.forward[2]);

        Vector tl_tr = new Vector3d(trVec.minus(tlVec));
        Vector tl_bl = new Vector3d(blVec.minus(tlVec));
        Vector normal = ((Vector3d) tl_tr).cross((Vector3d) tl_bl).direction();
        Vector ray = (cameraPosVec.plus(forwardVec)).minus(cameraPosVec).direction();
        double ndotdRay = normal.dot(ray);
        if (Math.abs(ndotdRay) < Vector.EPSILON) {
            // perpendicular
            return null;
        }
        double t = normal.dot(tlVec.minus(cameraPosVec)) / ndotdRay;
        if (t < 0) {
            // eliminate squares behind the ray
            return null;
        }

        Vector iPlane = cameraPosVec.plus(ray.times(t));
        Vector tl_iPlane = iPlane.minus(tlVec);
        double u = tl_iPlane.dot(tl_tr);
        double v = tl_iPlane.dot(tl_bl);

        boolean intersecting = u >= 0 && u <= tl_tr.dot(tl_tr) && v >= 0 && v <= tl_bl.dot(tl_bl);
        if (!intersecting) {
            // intersection is out of boundary
            return null;
        }

        return new Intersection(this, cameraPosVec, cameraPosVec.plus(forwardVec.times(t)), t);
    }

    @Override
    protected void bindHandles() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    protected void buildCorners(float[] up, float[] right) {
        final float HALF_WIDTH = width / 2.0f;
        final float HALF_HEIGHT = height / 2.0f;

        Vector upVec = new Vector(up[0], up[1], up[2]);
        Vector rightVec = new Vector(right[0], right[1], right[2]);
        double[] dNormals = (new Vector3d(rightVec)).cross(new Vector3d(upVec)).getData();
        normals = new float[]{
                (float) dNormals[0], (float) dNormals[1], (float) dNormals[2]
        };

        upVec = upVec.times(HALF_HEIGHT);
        rightVec = rightVec.times(HALF_WIDTH);

        tlVec = upVec.plus(rightVec.negate());
        blVec = upVec.negate().plus(rightVec.negate());
        trVec = upVec.plus(rightVec);
        brVec = upVec.negate().plus(rightVec);
    }

    protected void buildCorners(float[] up, float[] right, Vector posVec) {
        buildCorners(up, right);
        tlVec = tlVec.plus(posVec);
        blVec = blVec.plus(posVec);
        trVec = trVec.plus(posVec);
        brVec = brVec.plus(posVec);
    }

    @Override
    protected void buildArrays() {
        buildCorners(UP, RIGHT);

        double[] tl = tlVec.getData();
        double[] bl = blVec.getData();
        double[] tr = trVec.getData();
        double[] br = brVec.getData();

        vertices = new float[]{
                (float) tl[0], (float) tl[1], (float) tl[2],
                (float) bl[0], (float) bl[1], (float) bl[2],
                (float) tr[0], (float) tr[1], (float) tr[2],
                (float) br[0], (float) br[1], (float) br[2]
        };

        // GL_CCW
        // more details: face culling
        indices = new short[]{
                0, 1, 2, 3
        };

        textures = new float[]{
                0.0f, 0.0f, // tlVec
                0.0f, 1.0f, // blVec
                1.0f, 0.0f, // trVec
                1.0f, 1.0f // brVec
        };
    }

    @Override
    protected void bindBuffers() {
        FloatBuffer verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        ShortBuffer indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        FloatBuffer texturesBuffer = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        texturesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuffer.capacity() * BYTES_PER_FLOAT, texturesBuffer, GLES20.GL_STATIC_DRAW);
        texturesBuffer.limit(0);

        texBuffers[0] = createTexture();
    }

    protected abstract int createTexture();

    @Override
    public void draw() {
        if (!isVisible || !isCreated()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
        GLES20.glUniform1i(texIdHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("Panel - draw end");
    }

    @Override
    public void destroy() {
        super.destroy();
        Log.d("Panel", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLES20.glDeleteTextures(texBuffers.length, texBuffers, 0);
    }
}
