package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.Nifty;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
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
    Box[] boxes;
    InteractiveObject interBoxes[];
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

    public float[][] getData() throws FileNotFoundException, IOException {
        float[][] kinectPointCloud = null;
        kinect2 = new KinectTCPClient("localhost", 8001);
        if (kinect2.sayHello() != 214) {
            System.out.println("no kinect found");
            FileInputStream fis = new FileInputStream("Kinect_Data/pointCloudBottles.txt");
            Scanner fileInput = new Scanner(fis);
            LinkedList<float[]> textData = new LinkedList<float[]>();
            while (fileInput.hasNextFloat()) {
                float[] point = new float[3];
                point[0] = fileInput.nextFloat();
                point[1] = fileInput.nextFloat();
                point[2] = fileInput.nextFloat();
                textData.add(point);
            }
            fis.close();
            fileInput.close();
            kinectPointCloud = new float[textData.size()][3];
            int i = 0;
            for (float[] a : textData) {
                kinectPointCloud[i][0] = a[0];
                kinectPointCloud[i][1] = a[1];
                kinectPointCloud[i][2] = a[2];
                i++;
            }
        } else {
            System.out.println("found kinect");
            int[][] dummyData = null;
            //kinect_picture.sendCommand(KinectTCPClient.CMD_DISABLEDEPTHXYZINDEX);
            kinect2.sendCommand(KinectTCPClient.CMD_DISABLEDEPTHXYZFULL); // important!
            dummyData = kinect2.readDepthXYZ();
            kinectPointCloud = new float[dummyData.length][dummyData[0].length];
            for (int a = 0; a < dummyData.length; a++) {
                for (int b = 0; b < dummyData[0].length; b++) {
                    kinectPointCloud[a][b] = (float) dummyData[a][b];
                }
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
        initAudio();
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
        kinectskeleton = new KinectSkeleton(this);

        /* World */
        //rootNode.attachChild(SkyFactory.createSky(assetManager, "/skysphere.jpg", true));

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(50);
    }
    //Here is a comment

    @Override
    public void simpleUpdate(float tpf) {
        if (startScreen.snapshot == true || startScreen.startgame == true) {

            if (startScreen.startgame == true) {
                //rootNode.detachAllChildren();
                Environment.create();
                kinectskeleton = new KinectSkeleton(this);
                startScreen.startgame = false;
                startScreen.closeNifty(); //switches to an empty </screen> with nothing happening.

            }
            if (startScreen.snapshot == true) {
                try {
                    double start_time = System.currentTimeMillis();
                    rootNode.detachAllChildren();
                    //RANSAC and Clustering Stuff
                    float[][] kinectPointCloud = getData();

                    //do RANSAC -----------------------------------------------
                    float noiseFactor = 50f; // noise of inliers
                    int numberOfIterations = 10;
                    float[] floor = RANSAC.detectPlane(kinectPointCloud, noiseFactor, numberOfIterations, (int) (kinectPointCloud.length * 0.8f));
                    //remove floor and use flooding algorithm on remaining points to get objects

                    // visualize result
                    Cluster cluster = new Cluster(kinectPointCloud);
                    float[][][] clusters;
                    clusters = cluster.clustering();


                    //boxes = new Box[(int) cluster.clust_flag];

                    interBoxes = new InteractiveObject[(int) cluster.clust_flag];

                    //Environment.create(clusters);

                    boxes = new Box[(int) cluster.clust_flag];

                    for (int t = 0; t < cluster.clust_flag; t++) {
                        Vector3f center = new Vector3f(clusters[t][0][3] / 100f, clusters[t][2][3] / 10f, clusters[t][4][3] / 10000f);
                        Vector3f size = new Vector3f(((clusters[t][1][3] - clusters[t][0][3]) / 1000f),
                                ((clusters[t][3][3] - clusters[t][2][3]) / 1000f),
                                ((clusters[t][5][3] - clusters[t][4][3]) / 10000f));
                        interBoxes[t] = new TestBox(center, size);
                        System.out.println("center " + t + ": (" + center.x + ", " + center.y + ", " + center.z + ")");
                        System.out.println("Adding Box: " + t);
                    }
                    startScreen.snapshot = false;
                    startScreen.startgame = true;
                    double end_time = System.currentTimeMillis();
                    System.out.println("time it takes to take picture:" + (end_time - start_time));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        kinectskeleton.updateMovements();
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
        AudioNode game_music = new AudioNode(assetManager, "Sounds/prime.ogg", false);
        game_music.setLooping(true);  // activate continuous playing
        game_music.setPositional(true);
        game_music.setLocalTranslation(Vector3f.ZERO.clone());
        game_music.setVolume(3);
        rootNode.attachChild(game_music);
        game_music.play(); // play continuously!
    }
}
