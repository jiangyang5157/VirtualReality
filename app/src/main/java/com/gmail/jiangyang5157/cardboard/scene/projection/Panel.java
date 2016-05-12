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

    protected Vector tl;
    protected Vector bl;
    protected Vector tr;
    protected Vector br;

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
        if (!isVisible){
            return null;
        }

        float[] position = getPosition();
        float[] cameraPos = head.getCamera().getPosition();
        Vector positionVec = new Vector3d(position[0], position[1], position[2]);
        Vector cameraPosVec = new Vector3d(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardVec = new Vector3d(head.forward[0], head.forward[1], head.forward[2]);
        Vector rightVec = new Vector3d(head.right[0], head.right[1], head.right[2]);
        Vector upVec = new Vector3d(head.up[0], head.up[1], head.up[2]);
        Vector posToCameraVec = cameraPosVec.minus(positionVec);
        Vector posToCameraDirVec = posToCameraVec.direction();
        Vector cameraToPosVec = positionVec.minus(cameraPosVec);
        Vector cameraToPosDirVec = new Vector3d(cameraToPosVec.direction());

        //assume Panel always face camera
        double rightDis = cameraToPosVec.dot(rightVec);
        double upDis = cameraToPosVec.dot(upVec);
        double radian = ((Vector3d)forwardVec).radian((Vector3d)cameraToPosDirVec);
//        Log.i("####", "rightDis: " + rightDis);
//        Log.i("####", "upDis: " + upDis);
//        Log.i("####", "radian: " + radian);

        // TODO: 5/7/2016

        return null;
    }

    @Override
    protected void initializeHandle() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    protected void buildCorners(){
        buildCorners(width, height);
    }

    protected void buildCorners(float width, float height){
        this.width = width;
        this.height = height;

        final float HALF_WIDTH = width / 2.0f;
        final float HALF_HEIGHT = height / 2.0f;

        tl = new Vector3d(-1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f);
        bl = new Vector3d(-1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f);
        tr = new Vector3d(1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f);
        br = new Vector3d(1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f);
    }

    @Override
    protected void buildArrays() {
        vertices = new float[]{
                (float) tl.getData()[0], (float) tl.getData()[1], (float) tl.getData()[2], // tl
                (float) bl.getData()[0], (float) bl.getData()[1], (float) bl.getData()[2], // bl
                (float) tr.getData()[0], (float) tl.getData()[1], (float) tr.getData()[2], // tr
                (float) br.getData()[0], (float) br.getData()[1], (float) br.getData()[2], // br
        };

        // GL_CCW
        // more details: face culling
        indices = new short[]{
                0, 1, 2, 3
        };

        textures = new float[]{
                0.0f, 0.0f, // tl
                0.0f, 1.0f, // bl
                1.0f, 0.0f, // tr
                1.0f, 1.0f // br
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
        super.draw();

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
