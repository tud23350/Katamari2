package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
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
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import kinecttcpclient.KinectTCPClient;

/**
 *
 *
 * @author Mike Jake and Dave
 */
public class Main extends SimpleApplication implements Runnable {

    //lightings and physics
    BulletAppState bulletAppState;
    PssmShadowRenderer pssm;
    //Kinect stuff
    KinectTCPClient kinect;
    KinectSkeleton kinectskeleton;
    Mocap moCap;
    float[][] kinectPointCloud;
    float[][][] clusters;
    Cluster cluster;
    //Gui stuff
    private MyStartScreen startScreen;
    Geometry geom;
    JFrame windowFrame;
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
    BitmapText scoreText;
    //Time Stuff
    private boolean mode = true;
    private boolean started = false;
    private boolean timesUp = false;
    private boolean countUp = false;
    private long startTime;
    private long timeLimit = 1 * 26 * 1000; //in milliseconds
    private final long timerInc = 32;
    private final int spawnRate = 1;//spawns 1 box every 1 seconds
    private float timeCounter = 0;
    private float frameCount = 0;
    BitmapText Time;
    Thread timerThread;
    long tmp;

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
        setDisplayFps(false);
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

        //////////////////////////////////////////////////////////////
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
        rootNode.attachChild(SkyFactory.createSky(assetManager, "/Mordor.jpg", true));

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(50);
        cam.setLocation(new Vector3f(0f,-1f,20f));
        cam.lookAt(new Vector3f(0f, 120f, -240f), new Vector3f(0f, 120f, -200f));
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (startScreen.snapshot == true || startScreen.startgame == true) {

            if (startScreen.startgame == true) {
                Environment.create();
                if (startScreen.snapshot == true) {
                    kinectskeleton = new KinectSkeleton(this);
                    startScreen.snapshot = false;
                }
                startScreen.startgame = false;
                startScreen.closeNifty(); //switches to an empty </screen> with nothing happening.
                initTimer();
                initScore();
            } else if (startScreen.snapshot == true) {
                while (mode) {
                    try {
                        if (frameCount == 1) {
                            startScreen.init(null, null, null);
                            rootNode.detachAllChildren();
                            //RANSAC and Clustering Stuff
                            kinectPointCloud = getData();

                            //do RANSAC -----------------------------------------------
                            float noiseFactor = 50f; // noise of inliers
                            int numberOfIterations = 10;
                            float[] floor = RANSAC.detectPlane(kinectPointCloud, noiseFactor, numberOfIterations, (int) (kinectPointCloud.length * 0.8f));
                            //remove floor and use flooding algorithm on remaining points to get objects
                            startScreen.setProgress(0.3f, "Found Floor");
                        } else if (frameCount == 2) {
                            cluster = new Cluster(kinectPointCloud);
                            clusters = cluster.clustering();
                            startScreen.setProgress(0.7f, "Found Objects");
                        } else if (frameCount == 3) {
                            interBoxes = new InteractiveObject[(int) cluster.clust_flag];
                            for (int t = 0; t < cluster.clust_flag; t++) {
                                //7 is the floor extent
                                Vector3f center = new Vector3f((clusters[t][0][3] / 10f) % 7 + 5, (clusters[t][2][3] / 10f), (clusters[t][4][3] / 10000f) % 7 + 12);
                                Vector3f size = new Vector3f(((clusters[t][1][3] - clusters[t][0][3]) / 1000f),
                                        ((clusters[t][3][3] - clusters[t][2][3]) / 1000f),
                                        ((clusters[t][5][3] - clusters[t][4][3]) / 30000f));
                                interBoxes[t] = new TestBox(center, size);
                                System.out.println("center " + t + ": (" + center.x + ", " + center.y + ", " + center.z + ")");
                                System.out.println("Adding Box: " + t);
                            }
                            startScreen.startgame = true;
                            startScreen.setProgress(0.9f, "Environment Created");
                        } else if (frameCount == 4) {
                            mode = false;
                            startScreen.setProgress(1f, "Ready to Play");
                        }
                        frameCount++;
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        kinectskeleton.updateMovements();
        if (mode == true) {
            spawnBox(tpf);
            countUp = false;
        } else {
            countUp = true;
            if (allObjectsDestroyed()) {
                timesUp = true;
            }
        }
        if (startScreen.quitgame == true) {
            windowFrame.repaint();
        }
    }

    public void makeGUI(int score, long time) {
        /* LeaderBoard GUI */
        if (!countUp) {
            time = timeLimit / 1000;
            LeaderBoard lb = new LeaderBoard(300, 300, score, time);
            windowFrame = new JFrame();
            windowFrame.setLayout(new BorderLayout());
            windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            windowFrame.add(lb);
            windowFrame.pack();
            windowFrame.setVisible(true);
        } else {
            LeaderBoardClassic lbc = new LeaderBoardClassic(300, 300, score, time);
            windowFrame = new JFrame();
            windowFrame.setLayout(new BorderLayout());
            windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            windowFrame.add(lbc);
            windowFrame.pack();
            windowFrame.setVisible(true);
        }
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
        bulletAppState.getPhysicsSpace().setAccuracy(1f / 50f);
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

    private void initScore() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize() + 5);
        scoreText.setText("Score: " + score);
        scoreText.setLocalTranslation(0, 480, 0);
        guiNode.attachChild(scoreText);
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
    }

    private void initTimer() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        Time = new BitmapText(guiFont, false);
        Time.setSize(guiFont.getCharSet().getRenderedSize() + 5);
        if (countUp) {
            Time.setText("Time: " + formatTime(0));
        } else {
            Time.setText("Time: " + formatTime(timeLimit));
        }
        Time.setLocalTranslation(250, 480, 0);
        guiNode.attachChild(Time);
        started = true;
        startTime = System.currentTimeMillis();
        timerThread = new Thread(this);
        timerThread.start();
    }

    private void updateTimer() {
        if (started) {

            if (countUp) {
                tmp = System.currentTimeMillis() - startTime;
            } else {
                tmp = startTime + timeLimit - System.currentTimeMillis();
            }

            if (tmp >= 0) {
                Time.setText("Time: " + formatTime(tmp));
            } else {
                timesUp = true;
                Time.setText("Time: 0.00");
            }

            if (countUp) {
                if (tmp < 10000) {
                    Time.setColor(ColorRGBA.Green);
                } else if (tmp >= 10000 && tmp < 120 * 1000) {
                    Time.setColor(ColorRGBA.White);
                } else if (tmp > 120 * 1000) {
                    Time.setColor(ColorRGBA.Red);
                }
            } else {
                if (tmp > 9900 && tmp < 10000) {
                    Time.setColor(ColorRGBA.Red);
                }
            }
        }
    }

    private void spawnBox(float dt) {
        timeCounter += dt;
        if (timeCounter >= spawnRate) {
            Environment.createRandomBox(Vector3f.ZERO, 1f, 4f);
            timeCounter = 0;
        }
    }

    private String formatTime(long t) {
        String s = "";
        String tenths = Long.toString((t / 100) % 10);
        String hundredths = Long.toString((t / 10) % 10);
        t = t / 1000;
        if (t / 60 >= 1) {
            s = ((Long.toString(t / 60)).concat(s)).concat(":").concat(Long.toString((t % 60) / 10)).concat(Long.toString((t % 60) % 10));
        } else if (t >= 10) {
            s = s.concat(Long.toString((t % 60) / 10)).concat(Long.toString((t % 60) % 10)).concat(".").concat(tenths);
        } else {
            s = s.concat(Long.toString((t % 60) % 10)).concat(".").concat(tenths).concat(hundredths);
        }
        return s;
    }

    public void run() {
        boolean run = true;
        while (run) {

            updateTimer();
            if (timesUp) {
                makeGUI(score, tmp);
                startScreen.quitGame();
                run = false;
            }
            updateScore();

            try {
                timerThread.sleep(timerInc);//cannot be divisable by 5 or 10
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean allObjectsDestroyed() {
        if (interBoxes != null) {
            for (int i = 0; i < interBoxes.length; i++) {
                if (!interBoxes[i].removeSelf) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}