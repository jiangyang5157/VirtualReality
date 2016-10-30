package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.util.ArrayMap;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.kml.KmlNetworkLink;
import com.gmail.jiangyang5157.cardboard.net.FilePrepare;
import com.gmail.jiangyang5157.cardboard.net.MultiFilePrepare;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.vr.AssetFile;
import com.gmail.jiangyang5157.cardboard.vr.AssetUtils;
import com.gmail.jiangyang5157.tookit.base.time.Performance;
import com.gmail.jiangyang5157.tookit.math.Vector;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Yang
 * @since 7/3/2016
 */
public class AtomMap extends GlModel implements Creation {
    private static final String TAG = "[AtomMap]";

    protected int creationState = STATE_BEFORE_PREPARE;

    private String urlLayer;

    private AtomMarkers markers;

    public AtomMap(Context context, String urlLayer) {
        super(context);
        this.urlLayer = urlLayer;
        markers = new AtomMarkers(context);
    }

    public void prepare(final Ray ray) {
        getHandler().post(() -> {
            File file = new File(AssetUtils.getAbsolutePath(context, AssetUtils.getAssetPath(urlLayer)));
            AssetFile assetFile = new AssetFile(file, urlLayer);
            new FilePrepare(assetFile, new FilePrepare.PrepareListener() {
                @Override
                public void onStart() {
                    creationState = STATE_PREPARING;
                    ray.addBusy();
                }

                @Override
                public void onComplete(AssetFile assetFile) {
                    if (assetFile.isReady()) {
                        todo++;
                        prepareLayer(ray, assetFile);
                    } else {
                        ray.subtractBusy();
                        creationState = STATE_BEFORE_PREPARE;
                    }
                }
            }).start();
        });
    }

    private void prepareNetworkLinks(final Ray ray, HashSet<KmlNetworkLink> networkLinks) {
        HashSet<AssetFile> assetFileSet = new HashSet<>();
        Iterator<KmlNetworkLink> it = networkLinks.iterator();
        while (it.hasNext()) {
            KmlNetworkLink networkLink = it.next();
            String href = networkLink.getLink().getHref();
            String url = AssetUtils.localhost2RealMachine(href);
            File file = new File(AssetUtils.getAbsolutePath(context, AssetUtils.getAssetPath(url)));
            AssetFile assetFile = new AssetFile(file, url);
            String update = networkLink.getProperty("update");
            Log.d(TAG, "prepareNetworkLinks (update=" + update + "): " + assetFile);
            // TODO: 10/2/2016 update property = 1: force update
            assetFileSet.add(assetFile);
        }

        new MultiFilePrepare(assetFileSet, new MultiFilePrepare.PrepareListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(HashSet<AssetFile> assetFileSet) {
                Iterator it = assetFileSet.iterator();
                while (it.hasNext()) {
                    AssetFile assetFile = (AssetFile) it.next();
                    if (assetFile.isReady()) {
                        todo++;
                        prepareLayer(ray, assetFile);
                    }
                    complete(ray, assetFile);
                }
            }
        }).start();
    }

    private int todo = 0;

    private void complete(Ray ray, AssetFile assetFile) {
        todo--;
        Log.d(TAG, "complete: " + assetFile.toString() + "\ntodo:" + todo);
        if (todo <= 0) {
            ray.subtractBusy();
            creationState = STATE_BEFORE_CREATE;
        }
    }

    private void prepareLayer(final Ray ray, AssetFile assetFile) {
//        Log.d(Performance.TAG, "Prepare layer=" + assetFile);
//        Performance.getInstance().addBreakpoint();
        InputStream in = null;
        try {
            in = new FileInputStream(assetFile.getFile());
            KmlLayer kmlLayer = new KmlLayer(this, in, context);
            HashSet<KmlNetworkLink> networkLinks = kmlLayer.getNetworkLinksCollection();
            kmlLayer.addLayerToMap();
            int networkLinksSize = networkLinks.size();
            if (networkLinksSize > 0) {
                todo += networkLinksSize;
                prepareNetworkLinks(ray, kmlLayer.getNetworkLinksCollection());
            }
            complete(ray, assetFile);
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
//        Performance.getInstance().addBreakpoint();
//        Performance.getInstance().printEvaluationInMilliseconds();
    }

    public void create() {
        creationState = STATE_CREATING;

        ArrayMap<Integer, Integer> markerShaders = new ArrayMap<>();
        markerShaders.put(GLES20.GL_VERTEX_SHADER, AtomMarkers.RESOURCE_ID_VERTEX_SHADER);
        markerShaders.put(GLES20.GL_FRAGMENT_SHADER, AtomMarkers.RESOURCE_ID_FRAGMENT_SHADER);
        markers.create(markerShaders);

        setCreated(true);
        setVisible(true);
        creationState = STATE_BEFORE_CREATE;
    }

    @Override
    protected void bindHandles() {
        // do nothing
    }

    @Override
    public void update(float[] view, float[] perspective) {
        markers.update(view, perspective);
    }

    @Override
    public void draw() {
        markers.draw();
    }

    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForwardFrac_vec, final float[] headView) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        return markers.getIntersection(cameraPos_vec, headForwardFrac_vec, headView);
    }

    @Override
    public int getCreationState() {
        return creationState;
    }

    public void setMarkerLighting(Lighting lighting) {
        markers.setLighting(lighting);
    }

    public void setOnMarkerClickListener(ClickListener onClickListener) {
        markers.setOnClickListener(onClickListener);
    }

    public AtomMarkers getAtomMarkers() {
        return markers;
    }

    @Override
    public void destroy() {
        markers.destroy();
        super.destroy();
    }
}
