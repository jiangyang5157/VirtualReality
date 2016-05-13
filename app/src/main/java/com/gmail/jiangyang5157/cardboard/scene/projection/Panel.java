package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Panel extends Rectangle {

    protected static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.panel_vertex_shader;
    protected static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.panel_fragment_shader;

    private final int[] buffers = new int[3];
    private final int[] texBuffers = new int[1];

    protected Vector tlVec;
    protected Vector blVec;
    protected Vector trVec;
    protected Vector brVec;

    public Panel(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
    }

    protected void create(float width, float height, int color) {
        buildCorners(width, height);
        setColor(color);

        buildArrays();
        bindBuffers();
    }

    @Override
    public AimIntersection intersect(Head head) {
        if (!isVisible) {
            return null;
        }

        float[] cameraPos = head.getCamera().getPosition();
        Vector cameraPosVec = new Vector(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardVec = new Vector(head.forward[0], head.forward[1], head.forward[2]);

        Vector tl_tr = new Vector3d(trVec.minus(tlVec));
        Vector tl_bl = new Vector3d(blVec.minus(tlVec));
        Vector n = ((Vector3d) tl_tr).cross((Vector3d) tl_bl).direction();
        Vector ray = (cameraPosVec.plus(forwardVec)).minus(cameraPosVec).direction();
        double ndotdRay = n.dot(ray);
        if (Math.abs(ndotdRay) < Vector.EPSILON) {
            // perpendicular
            return null;
        }
        double t = n.dot(tlVec.minus(cameraPosVec)) / ndotdRay;
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

        return new AimIntersection(this, cameraPosVec, cameraPosVec.plus(forwardVec.times(t)), t);
    }

    @Override
    protected void initializeHandle() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    protected void buildCorners() {
        buildCorners(width, height);
    }

    protected void buildCorners(float width, float height) {
        this.width = width;
        this.height = height;

        final float HALF_WIDTH = width / 2.0f;
        final float HALF_HEIGHT = height / 2.0f;

        tlVec = new Vector3d(-1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f);
        blVec = new Vector3d(-1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f);
        trVec = new Vector3d(1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f);
        brVec = new Vector3d(1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f);
    }

    @Override
    protected void buildArrays() {
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
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        texturesBuffer = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        texturesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);
        verticesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);
        indicesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuffer.capacity() * BYTES_PER_FLOAT, texturesBuffer, GLES20.GL_STATIC_DRAW);
        texturesBuffer.limit(0);
        texturesBuffer = null;

        texBuffers[0] = createTexture();
    }

    protected abstract int createTexture();

    @Override
    public void draw() {
        if (!isVisible) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        if (lighting != null) {
            GLES20.glUniform3fv(lightPosHandle, 1, lighting.getLightPosInCameraSpace(), 0);
        }

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
        Log.d("Panel", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLES20.glDeleteBuffers(texBuffers.length, texBuffers, 0);
    }
}
