package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.PssmShadowRenderer;

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
    KinectInterface kinect;
    KinectSkeleton kinectskeleton;
    Mocap moCap;
    
    //Gui stuff
    gui mygui;
    Geometry geom;

    
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initLighting();
        physicsInit();
        KinematicObject.addListener(this);
        KinematicCylinder.addListener(this);
        InteractiveObject.addListener(this);
        Inert.addListener(this);
        
        
        /*  Kinect stuff    */
        moCap = new Mocap();
        mygui = new gui(this);
        kinect = new KinectInterface(this);
        kinect.getData();
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
            if ("green".equals(mygui.s)) {
                rootNode.detachAllChildren();
                Box b = new Box(Vector3f.ZERO, 1, 1, 1);
                geom = new Geometry("Box", b);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Green);
                geom.setMaterial(mat);

                rootNode.attachChild(geom);
            }
            if ("blue".equals(mygui.s)) {
                rootNode.detachAllChildren();
                Box b = new Box(Vector3f.ZERO, 2, 2, 2);
                geom = new Geometry("Box", b);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Blue);
                geom.setMaterial(mat);

                rootNode.attachChild(geom);
            }
            geom.rotate(0, 0, 5f);
            mygui.resetchanged();
        }
        if (geom != null) {
            geom.rotate(0, 0, 5f);
        }
        kinect.getData();
        kinectskeleton.updateMovements();
        
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
    
    private void physicsInit(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bulletAppState.getPhysicsSpace().setAccuracy(1f/240f);
    }
}
