package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.tookit.data.buffer.BufferUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;
import com.gmail.jiangyang5157.tookit.render.GlesUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Panel extends Rectangle implements GlModel.BindableBuffer, GlModel.BindableTextureBuffer {
    private static final String TAG = "[Panel]";

    protected float[] vertices;
    protected float[] normals;
    protected short[] indices;
    protected float[] textures;

    private Vector tl_vec;
    private Vector bl_vec;
    private Vector tr_vec;
    private Vector br_vec;

    private Vector tl_tr_vec;
    private Vector tl_bl_vec;
    private Vector normal_vec;

    protected final int[] buffers = new int[3];

    protected Panel(Context context) {
        super(context);
    }

    @Override
    public void create(int program) {
        bindTextureBuffers();
        buildData();

        super.create(program);
        bindHandles();
        bindBuffers();
    }

    public void setPosition(float[] cameraPos, float[] forward, float distance, float[] quaternion, float[] up, float[] right) {
        float[] position = new float[]{
                cameraPos[0] + forward[0] * distance,
                cameraPos[1] + forward[1] * distance,
                cameraPos[2] + forward[2] * distance,
        };

        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0, position[0], position[1], position[2]);

        Matrix.setIdentityM(rotationMatrix, 0);
        // it should face to eye
        float[] q = new float[]{-quaternion[0], -quaternion[1], -quaternion[2], quaternion[3]};
        Matrix.multiplyMM(rotationMatrix, 0, Head.getQquaternionMatrix(q), 0, rotationMatrix, 0);

        modelRequireUpdate = true;

        // build corners' vector, they are for intersect calculation
        buildCorners(up, right, position);
    }

    /*
      ################################################################
       The original algorithm as follow:
       Ray: R(t) = Ro + Rd * t

             u
       s1 +----------------------+ s2
          | \  |                 |
         v|   \|                 |
          |--- m                 |
          |          n           |
          |                      |
          |                      |
       s3 +----------------------+ s4

       if m belongs to Plane: n * (m - s1) = 0
       if m belongs to Ray: m = Ro + Rd * t
       solve t = (-n * (Ro - s1)) / (n * Rd)
       if abs(n * Rd) < EPSILON the plane is parallel to the ray, and there is no intersection

       u = (n - s1) . (s2 - s1)
       v = (n - s1) . (s3 - s1)

       if
       u belongs to [0, dot(s2 - s1, s2 - s1)]
       v belongs to [0, dot(s3 - s1, s3 - s1)]
       then the point of intersection M lies inside the square, else it's outside.
     */
    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForward_vec) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        Vector ray_vec = (cameraPos_vec.plus(headForward_vec)).minus(cameraPos_vec).direction();
        double ndotdRay = normal_vec.dot(ray_vec);
        if (Math.abs(ndotdRay) < Vector.EPSILON) {
            return null; // perpendicular
        }
        double t = normal_vec.dot(tl_vec.minus(cameraPos_vec)) / ndotdRay;
        if (t <= 0) {
            return null; // behind the ray
        }

        Vector m = cameraPos_vec.plus(ray_vec.times(t));
        Vector tl_iPlane = m.minus(tl_vec);
        double u = tl_iPlane.dot(tl_tr_vec);
        double v = tl_iPlane.dot(tl_bl_vec);

        boolean intersecting = u >= 0 && u <= tl_tr_vec.dot(tl_tr_vec) && v >= 0 && v <= tl_bl_vec.dot(tl_bl_vec);
        if (!intersecting) {
            return null; // intersection is out of boundary
        }

        return new RayIntersection(this, t);
    }

    @Override
    protected void bindHandles() {
        modelHandle = GLES20.glGetUniformLocation(program, MODEL_HANDLE);
        viewHandle = GLES20.glGetUniformLocation(program, VIEW_HANDLE);
        perspectiveHandle = GLES20.glGetUniformLocation(program, PERSPECTIVE_HANDLE);

        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    protected void buildCorners(float[] up, float[] right) {
        Vector up_vec = new Vector(up[0], up[1], up[2]);
        Vector right_vec = new Vector(right[0], right[1], right[2]);
        double[] normals_temp = (new Vector3d(right_vec)).cross(new Vector3d(up_vec)).getData();
        normals = new float[]{
                (float) normals_temp[0], (float) normals_temp[1], (float) normals_temp[2]
        };

        final float HALF_WIDTH = width / 2.0f;
        final float HALF_HEIGHT = height / 2.0f;
        up_vec = up_vec.times(HALF_HEIGHT);
        right_vec = right_vec.times(HALF_WIDTH);

        tl_vec = up_vec.plus(right_vec.negate());
        bl_vec = up_vec.negate().plus(right_vec.negate());
        tr_vec = up_vec.plus(right_vec);
        br_vec = up_vec.negate().plus(right_vec);
    }

    protected void buildCorners(float[] up, float[] right, float[] position) {
        buildCorners(up, right);

        tl_vec = tl_vec.times(scale);
        bl_vec = bl_vec.times(scale);
        tr_vec = tr_vec.times(scale);
        br_vec = br_vec.times(scale);

        Vector pos_vec = new Vector(position[0], position[1], position[2]);
        tl_vec = tl_vec.plus(pos_vec);
        bl_vec = bl_vec.plus(pos_vec);
        tr_vec = tr_vec.plus(pos_vec);
        br_vec = br_vec.plus(pos_vec);

        tl_tr_vec = new Vector3d(tr_vec.minus(tl_vec));
        tl_bl_vec = new Vector3d(bl_vec.minus(tl_vec));
        normal_vec = ((Vector3d) tl_tr_vec).cross((Vector3d) tl_bl_vec).direction();
    }

    @Override
    protected void buildData() {
        buildCorners(UP, RIGHT);

        double[] tl = tl_vec.getData();
        double[] bl = bl_vec.getData();
        double[] tr = tr_vec.getData();
        double[] br = br_vec.getData();

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
                0.0f, 0.0f, // tl_vec
                0.0f, 1.0f, // bl_vec
                1.0f, 0.0f, // tr_vec
                1.0f, 1.0f // br_vec
        };
    }

    @Override
    public void bindBuffers() {
        FloatBuffer verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        ShortBuffer indicesBuffer = ByteBuffer.allocateDirect(indices.length * BufferUtils.BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        FloatBuffer texturesBuffer = ByteBuffer.allocateDirect(textures.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        texturesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BufferUtils.BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, texturesBuffer, GLES20.GL_STATIC_DRAW);
        texturesBuffer.limit(0);
    }

    @Override
    public void destroy() {
        super.destroy();
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }
}
