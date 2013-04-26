package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.util.BufferUtils;
import java.util.LinkedList;
import kinecttcpclient.KinectTCPClient;

/**
 *
 *
 * @author Mike Jake and Dave
 */
public class Main extends SimpleApplication {

    //lightings and physics
    BulletAppState bulletAppState;
    PssmShadowRenderer pssm;
    //Kinect stuff
    KinectTCPClient kinect;
    KinectSkeleton kinectskeleton;
    Mocap moCap;
    //Gui stuff
    gui mygui;
    Geometry geom;
    //RANSAC stuff
    KinectTCPClient kinect2;
    Node[] node;
    Mesh mesh = new Mesh();
    Geometry geo;
    Vector3f[] points;
    Vector4f[] colors;
    float clust_flag = 0;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public float[][] getData() {
        kinect2 = new KinectTCPClient("localhost", 8001);
        int[][] dummyData = null;
        if (kinect2.sayHello() != 214) {
            kinect2 = null;
        } else {
            //kinect.sendCommand(KinectTCPClient.CMD_DISABLEDEPTHXYZINDEX);
            kinect2.sendCommand(KinectTCPClient.CMD_DISABLEDEPTHXYZFULL); // important!
            dummyData = kinect2.readDepthXYZ();
        }
        float[][] kinectPointCloud = new float[dummyData.length][dummyData[0].length];
        for (int a = 0; a < dummyData.length; a++) {
            for (int b = 0; b < dummyData[0].length; b++) {
                kinectPointCloud[a][b] = (float) dummyData[a][b];
            }
        }
        return kinectPointCloud;
    }

    @Override
    public void simpleInitApp() {
        initLighting();
        physicsInit();
        //initAudio();
        KinematicObject.addListener(this);
        KinematicCylinder.addListener(this);
        InteractiveObject.addListener(this);
        Inert.addListener(this);


        /*  Kinect stuff    */
        moCap = new Mocap();
        mygui = new gui(this);
        //kinect = new KinectInterface(this);
        //kinect.getData();
        kinectskeleton = new KinectSkeleton(this);



        Environment.create();

        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        //rootNode.attachChild(geom);
        flyCam.setMoveSpeed(50);
    }
    //Here is a comment

    @Override
    public void simpleUpdate(float tpf) {
        if (mygui.getchanged()) {
            if ("Initalize Environment".equals(mygui.s)) {
                /*
                 rootNode.detachAllChildren();
                 Box b = new Box(Vector3f.ZERO, 1, 1, 1);
                 geom = new Geometry("Box", b);

                 Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                 mat.setColor("Color", ColorRGBA.Green);
                 geom.setMaterial(mat);

                 rootNode.attachChild(geom);
                 * */
                if (kinect != null) {
                    kinect.readDepthXYZ();
                }
                mygui.resetchanged();
            }
            if ("Play Game".equals(mygui.s)) {
                rootNode.detachAllChildren();
                Box b = new Box(Vector3f.ZERO, 2, 2, 2);
                geom = new Geometry("Box", b);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Blue);
                geom.setMaterial(mat);

                rootNode.attachChild(geom);

            }
            if ("Take Picture".equals(mygui.s)) {
                rootNode.detachAllChildren();
                //RANSAC and Clustering Stuff
                float[][] kinectPointCloud = getData();

                //do RANSAC -----------------------------------------------
                float noiseFactor = 50f; // noise of inliers
                int numberOfIterations = 10;
                float planeSize = 5f;
                float[] floor = RANSAC.detectPlane(kinectPointCloud, noiseFactor, numberOfIterations, (int) (kinectPointCloud.length * 0.8f));
                //remove floor and use flooding algorithm on remaining points to get objects

                // visualize result
                float[][] inliers = filterInliers();
                //rootNode.attachChild(createPointGeometry(kinectPointCloud, ColorRGBA.Red, 1));
                //rootNode.attachChild(createPointGeometry(inliers, ColorRGBA.Green, 2));
                //rootNode.attachChild(createPlaneGeometry(floor, planeSize));

                float[][] outliers = new float[kinectPointCloud.length - inliers.length][4];
                int counter, k = 0;
                for (int i = 0; i < kinectPointCloud.length; i++) {
                    counter = 0;
                    for (float[] j : inliers) {
                        if (kinectPointCloud[i][0] == j[0] && kinectPointCloud[i][1] == j[1] && kinectPointCloud[i][2] == j[2]) {
                            counter++;
                        }
                    }
                    if (counter == 0) {
                        outliers[k][0] = kinectPointCloud[i][0];
                        outliers[k][1] = kinectPointCloud[i][1];
                        outliers[k][2] = kinectPointCloud[i][2];
                        outliers[k][3] = 0;
                        k++;
                    }
                }

                float[][] euclid_dis = new float[outliers.length][outliers.length];
                for (int i = 0; i < euclid_dis.length; i++) {
                    for (int j = 0; j < euclid_dis[0].length; j++) {
                        euclid_dis[i][j] = (float) Math.sqrt(Math.abs((outliers[i][0] - outliers[j][0]) * (outliers[i][0] - outliers[j][0])
                                + (outliers[i][1] - outliers[j][1]) * (outliers[i][1] - outliers[j][1])
                                + (outliers[i][2] - outliers[j][2]) * (outliers[i][2] - outliers[j][2])));
                    }
                }
                float max_dist = 20f;
                for(int i=0;i<euclid_dis.length;i++){
                    for(int j=i;j<euclid_dis[0].length;j++){
                        if(euclid_dis[i][j] <= max_dist){
                            if(outliers[i][3] == 0 && outliers[j][3] == 0){
                                clust_flag++;
                                outliers[i][3] = clust_flag;
                                outliers[j][3] = clust_flag;
                            } else if(outliers[i][3] != 0 && outliers[j][3] == 0){
                                outliers[j][3] = outliers[i][3];
                            } else if(outliers[i][3] == 0 && outliers[j][3] != 0){
                                outliers[i][3] = outliers[j][3];
                            }
                        }
                    }
                }
                
                // create mesh
                mesh = new Mesh();
                mesh.setMode(Mesh.Mode.Points);
                mesh.setPointSize(2f);

                points = new Vector3f[outliers.length];
                float x, y, z;
                for (int i = 0; i < points.length; i++) {
                    x = -outliers[i][0] / 1000f;
                    y = -outliers[i][1] / 1000f;
                    z = -outliers[i][2] / 1000f;
                    points[i] = new Vector3f(x, y, z);
                }
                mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(points));

                // colors
                colors = new Vector4f[points.length];
                for (int count = 0; count < colors.length; count++) {
                    float r = (float) Math.random();
                    float g = (float) Math.random();
                    float b = (float) Math.random();
                    colors[count] = new Vector4f(r, g, b, 1.0f);
                    //System.out.println(outliers[count][3]);
                    //colors[count] = new Vector4f(outliers[count][3] * 6, outliers[count][3] * 6, outliers[count][3] * 6, 1.0f);
                }
                mesh.setBuffer(VertexBuffer.Type.Color, 4, BufferUtils.createFloatBuffer(colors));

                // create geomtery etc.
                mesh.updateBound();
                geo = new Geometry("OurMesh", mesh);
                Material matPC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matPC.setBoolean("VertexColor", true);
                geo.setMaterial(matPC);
                Node pivot = new Node();
                pivot.attachChild(geo);
                rootNode.attachChild(pivot);
            }
            mygui.resetchanged();
        }
        if (geom != null) {
            geom.rotate(0, 0, 5f);
        }
        //kinect.getData();
        kinectskeleton.updateMovements();

    }

    // create point-subset of inliers, from RANSAC inlier list ---
    private float[][] filterInliers() {
        LinkedList<float[]> in = RANSAC.supportingPoints;
        float[][] f = new float[in.size()][4];
        int i = 0;
        for (float[] a : in) {
            f[i][0] = a[0];
            f[i][1] = a[1];
            f[i][2] = a[2];
            f[i][3] = 0;
            i++;
        }
        //in.toArray(f);
        return (f);
    }

    // Point Geometry ---------------------------------------------
    Geometry createPointGeometry(float[][] points, ColorRGBA color, float size) {
        // create mesh
        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Points);
        mesh.setPointSize(size);

        // initial positions
        Vector3f[] pv = new Vector3f[points.length];
        for (int i = 0; i < points.length; i++) {
            pv[i] = new Vector3f(points[i][0], points[i][1], points[i][2]);
        }
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pv));

        // create geomtery etc.
        mesh.updateBound();
        Geometry geo = new Geometry("OurMesh", mesh);
        Material matPC = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPC.setColor("Color", color);
        geo.setMaterial(matPC);
        return (geo);
    }

    // tranparent plane ------------------------------------------
    private Spatial createPlaneGeometry(float[] hnf, float planeSize) {
        // create xz-plane
        Box bxy = new Box(planeSize / 2, 0.01f, planeSize / 2);
        Geometry geomxy = new Geometry("cubexy", bxy);
        Material matxy = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matxy.setColor("Color", new ColorRGBA(0.5f, 0.0f, 0f, 0.5f));
        matxy.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geomxy.setMaterial(matxy);
        geomxy.setQueueBucket(RenderQueue.Bucket.Transparent);

        // create ransac plane
        Box b = new Box(planeSize / 2, 0.01f, planeSize / 2);
        Geometry geom = new Geometry("cube", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.5f, 0.5f, 0f, 0.5f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        //
        // transform it to be the plane represented by hnf
        // create quaternion
        // (a) rotation axis from cross product (n x (0 1 0))
        Vector3f n = new Vector3f(hnf[0], hnf[1], hnf[2]);
        n = n.cross(Vector3f.UNIT_Y).normalize();
        // angle from dot product n * (0 1 0) = ny
        float angle = -FastMath.acos(hnf[1]);
        Quaternion q = new Quaternion().fromAngleAxis(angle, n);
        // set rotation
        geom.setLocalRotation(q);
        // translation: create a point in normal-direction, distance d
        Vector3f trans = new Vector3f(hnf[0] * hnf[3], hnf[1] * hnf[3], hnf[2] * hnf[3]);
        geom.setLocalTranslation(trans);

        //
        Node nd = new Node();
        nd.attachChild(geom);
        //nd.attachChild(geomxy);
        return (nd);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void initLighting() {
        pssm = new PssmShadowRenderer(assetManager, 1024, 3);
        pssm.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        viewPort.addProcessor(pssm);
    }

    private void physicsInit() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bulletAppState.getPhysicsSpace().setAccuracy(1f / 120f);
    }

    private void initAudio() {
        AudioNode game_music = new AudioNode(assetManager, "prime.ogg", false);
        game_music.setLooping(true);  // activate continuous playing
        game_music.setPositional(true);
        game_music.setLocalTranslation(Vector3f.ZERO.clone());
        game_music.setVolume(3);
        rootNode.attachChild(game_music);
        game_music.play(); // play continuously!
    }
}
