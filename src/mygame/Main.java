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
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.Nifty;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private MyStartScreen startScreen;
    Geometry geom;
    //RANSAC stuff
    KinectTCPClient kinect2;
    Node[] node;
    Mesh mesh = new Mesh();
    Geometry geo;
    Vector3f[] points;
    Vector4f[] colors;
    float clust_flag = 0;
    //Scoring stuff
    public int score = 0;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(640, 480);
        Main app = new Main();
        app.setShowSettings(false); // splashscreen
        app.setSettings(settings);
        app.start();
    }

    public float[][] getData() {
        kinect2 = new KinectTCPClient("localhost", 8001);
        int[][] dummyData = null;
        if (kinect2.sayHello() != 214) {
            kinect2 = null;
        } else {
            //kinect2.sendCommand(KinectTCPClient.CMD_DISABLEDEPTHXYZINDEX);
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
        inputManager.setCursorVisible(true);
        setDisplayFps(true);
        setDisplayStatView(false);
        Logger.getAnonymousLogger().getParent().setLevel(Level.SEVERE);
        Logger.getLogger("de.lessvoid.nifty.*").setLevel(Level.SEVERE);
        startScreen = new MyStartScreen();
        stateManager.attach(startScreen);

        /**
         * Åctivate the Nifty-JME integration:
         */
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/tutorial/screen3.xml", "start", startScreen);
        //////////////////////////////////////////////////////////////////////

        initLighting();
        physicsInit();
        //initAudio();
        KinematicObject.addListener(this);
        KinematicCylinder.addListener(this);
        InteractiveObject.addListener(this);
        Inert.addListener(this);

        kinect = new KinectTCPClient("localhost", 8001);
        if (kinect.sayHello() != 214) {
            kinect = null;
        }

        /*  Kinect stuff    */
        moCap = new Mocap();

        //kinect = new KinectInterface(this);
        //kinect.getData();
        kinectskeleton = new KinectSkeleton(this);

        //rootNode.attachChild(geom);
        //flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);
    }
    //Here is a comment

    @Override
    public void simpleUpdate(float tpf) {
        if (startScreen.snapshot == true || startScreen.startgame == true) {

            if (startScreen.startgame == true) {
                rootNode.detachAllChildren();
                Environment.create();
                kinectskeleton = new KinectSkeleton(this);
                startScreen.startgame = false;
                startScreen.closeNifty(); //switches to an empty </screen> with nothing happening.

            }
            if (startScreen.snapshot == true) {
                double start_time = System.currentTimeMillis();
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
                float max_dist = 40f;
                for (int i = 0; i < euclid_dis.length; i++) {
                    for (int j = i; j < euclid_dis[0].length; j++) {
                        if (euclid_dis[i][j] <= max_dist) {
                            if (outliers[i][3] == 0 && outliers[j][3] == 0) {
                                clust_flag++;
                                outliers[i][3] = clust_flag;
                                outliers[j][3] = clust_flag;
                            } else if (outliers[i][3] != 0 && outliers[j][3] == 0) {
                                outliers[j][3] = outliers[i][3];
                            } else if (outliers[i][3] == 0 && outliers[j][3] != 0) {
                                outliers[i][3] = outliers[j][3];
                            }
                        }
                    }
                }

                float[][][] clusters = new float[(int) clust_flag][outliers.length][outliers[0].length];
                for (int i = 0; i < clust_flag; i++) {
                    k = 0;
                    for (float[] a : outliers) {
                        if (a[3] == i) {
                            clusters[i][k][0] = a[0];
                            clusters[i][k][1] = a[1];
                            clusters[i][k][2] = a[2];
                            k++;
                        }
                    }
                }

                k = 0;
                for (float[][] object : clusters) {
                    float min_X = (float) Double.POSITIVE_INFINITY;
                    float max_X = (float) Double.NEGATIVE_INFINITY;
                    float min_Y = (float) Double.POSITIVE_INFINITY;
                    float max_Y = (float) Double.NEGATIVE_INFINITY;
                    float min_Z = (float) Double.POSITIVE_INFINITY;
                    float max_Z = (float) Double.NEGATIVE_INFINITY;
                    for (float[] point : object) {
                        if (point[0] < min_X) {
                            min_X = point[0];
                        }
                        if (point[0] > max_X) {
                            max_X = point[0];
                        }
                        if (point[1] < min_Y) {
                            min_Y = point[1];
                        }
                        if (point[1] > max_Y) {
                            max_Y = point[1];
                        }
                        if (point[2] < min_Z) {
                            min_Z = point[2];
                        }
                        if (point[2] > max_Z) {
                            max_Z = point[2];
                        }

                    }
                    clusters[k][0][3] = min_X;
                    clusters[k][1][3] = max_X;
                    clusters[k][2][3] = min_Y;
                    clusters[k][3][3] = max_Y;
                    clusters[k][4][3] = min_Z;
                    clusters[k][5][3] = max_Z;
                    k++;
                }

                Box[] b = new Box[(int) clust_flag];
                for (int t = 0; t < clust_flag; t++) {
                    b[t] = new Box(new Vector3f(clusters[t][0][3] / 10f, clusters[t][2][3] / 10f, clusters[t][4][3] / 10000f),((clusters[t][1][3] - clusters[t][0][3]) / 1000f), ((clusters[t][3][3] - clusters[t][2][3]) / 1000f), ((clusters[t][5][3] - clusters[t][4][3]) / 10000f));
                    //b[t] = new Box(new Vector3f(((clusters[t][1][3] + clusters[t][0][3]) / 500f),((clusters[t][3][3] + clusters[t][2][3]) / 500f),((clusters[t][5][3] + clusters[t][4][3]) / 15000f)),((clusters[t][1][3] - clusters[t][0][3]) / 1000f), ((clusters[t][3][3] - clusters[t][2][3]) / 1000f), ((clusters[t][5][3] - clusters[t][4][3]) / 30000f));
                    geom = new Geometry("Box", b[t]);
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mat.setColor("Color", ColorRGBA.Blue);
                    geom.setMaterial(mat);
                    rootNode.attachChild(geom);
                }
                System.out.println("boxes created");

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
                    //float r_c = (float) Math.random();
                    //float g_c = (float) Math.random();
                    //float b_c = (float) Math.random();
                    //colors[count] = new Vector4f(r_c, g_c, b_c, 1.0f);
                    //System.out.println(outliers[count][3]);
                    colors[count] = new Vector4f(outliers[count][3] / (float) (clust_flag), outliers[count][3] / (float) (clust_flag), outliers[count][3] / (float) (clust_flag), 1.0f);
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
                System.out.println("points should be created.");
                startScreen.snapshot = false;
                double end_time = System.currentTimeMillis();
                System.out.println("time it takes to take picture:" + (end_time - start_time));
            }
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
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bulletAppState.getPhysicsSpace().setAccuracy(1f / 60f);
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
