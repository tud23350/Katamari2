package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Michael
 */
//This is where the skeleton should be created

public class KinectSkeleton {

    Main main;
    
    boolean madeSkeleton = false;
    Node skeleton = new Node(); // this is the skeleton, which connects to the root node.
    Node[] bones = new Node[13]; //13 is the number of cylinders we will have representing bones
    Vector3f boneTranslation[] = new Vector3f[13];
    KinematicObject[] boneObject = new KinematicObject[13];

    private final float scaleFactor = 800f;
    
    public void createSkeleton() {
        if (main.kinect.joint != null) {
            Material matW = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            matW.setColor("Color", ColorRGBA.White);
            //starting joints
            float[][] StartingJoint = {{(float) main.kinect.joint[10][1] / scaleFactor, (float) main.kinect.joint[10][2] / scaleFactor, (float) main.kinect.joint[10][3] / scaleFactor}, //right wrist
                {(float) main.kinect.joint[9][1] / scaleFactor, (float) main.kinect.joint[9][2] / scaleFactor, (float) main.kinect.joint[9][3] / scaleFactor}, //right elbow
                {(float) main.kinect.joint[8][1] / scaleFactor, (float) main.kinect.joint[8][2] / scaleFactor, (float) main.kinect.joint[8][3] / scaleFactor}, //right shoulder
                {(float) main.kinect.joint[2][1] / scaleFactor, (float) main.kinect.joint[2][2] / scaleFactor, (float) main.kinect.joint[2][3] / scaleFactor}, //shoulder center
                {(float) main.kinect.joint[4][1] / scaleFactor, (float) main.kinect.joint[4][2] / scaleFactor, (float) main.kinect.joint[4][3] / scaleFactor}, //left shoulder
                {(float) main.kinect.joint[5][1] / scaleFactor, (float) main.kinect.joint[5][2] / scaleFactor, (float) main.kinect.joint[5][3] / scaleFactor}, //left elbow
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[13][1] / scaleFactor, (float) main.kinect.joint[13][2] / scaleFactor, (float) main.kinect.joint[13][3] / scaleFactor}, //left knee
                {(float) main.kinect.joint[17][1] / scaleFactor, (float) main.kinect.joint[17][2] / scaleFactor, (float) main.kinect.joint[17][3] / scaleFactor}}; //right knee
            //joint the starting joints connect to
            float[][] ConnectingJoint = {{(float) main.kinect.joint[9][1] / scaleFactor, (float) main.kinect.joint[9][2] / scaleFactor, (float) main.kinect.joint[9][3] / scaleFactor}, //right elbow
                {(float) main.kinect.joint[8][1] / scaleFactor, (float) main.kinect.joint[8][2] / scaleFactor, (float) main.kinect.joint[8][3] / scaleFactor}, //right shoulder
                {(float) main.kinect.joint[2][1] / scaleFactor, (float) main.kinect.joint[2][2] / scaleFactor, (float) main.kinect.joint[2][3] / scaleFactor}, //shoulder center
                {(float) main.kinect.joint[4][1] / scaleFactor, (float) main.kinect.joint[4][2] / scaleFactor, (float) main.kinect.joint[4][3] / scaleFactor}, //left shoulder
                {(float) main.kinect.joint[5][1] / scaleFactor, (float) main.kinect.joint[5][2] / scaleFactor, (float) main.kinect.joint[5][3] / scaleFactor}, //left elbow
                {(float) main.kinect.joint[6][1] / scaleFactor, (float) main.kinect.joint[6][2] / scaleFactor, (float) main.kinect.joint[6][3] / scaleFactor}, //left wrist
                {(float) main.kinect.joint[2][1] / scaleFactor, (float) main.kinect.joint[2][2] / scaleFactor, (float) main.kinect.joint[2][3] / scaleFactor}, //shoulder center
                {(float) main.kinect.joint[8][1] / scaleFactor, (float) main.kinect.joint[8][2] / scaleFactor, (float) main.kinect.joint[8][3] / scaleFactor}, //right shoulder
                {(float) main.kinect.joint[4][1] / scaleFactor, (float) main.kinect.joint[4][2] / scaleFactor, (float) main.kinect.joint[4][3] / scaleFactor}, //left shoulder
                {(float) main.kinect.joint[13][1] / scaleFactor, (float) main.kinect.joint[13][2] / scaleFactor, (float) main.kinect.joint[13][3] / scaleFactor}, //left knee
                {(float) main.kinect.joint[17][1] / scaleFactor, (float) main.kinect.joint[17][2] / scaleFactor, (float) main.kinect.joint[17][3] / scaleFactor}, //right knee
                {(float) main.kinect.joint[14][1] / scaleFactor, (float) main.kinect.joint[14][2] / scaleFactor, (float) main.kinect.joint[14][3] / scaleFactor}, //left ankle
                {(float) main.kinect.joint[18][1] / scaleFactor, (float) main.kinect.joint[18][2] / scaleFactor, (float) main.kinect.joint[18][3] / scaleFactor}}; //right ankle
            //start loop to connect all joints
            for (int i = 0; i < bones.length; i++) {
                Cylinder c = new Cylinder(10, 10, 0.09f, 1f, true);
                //new KinematicCylinder(new Geometry("Cylinder", c), Vector3f.ZERO);
                //KinematicCylinder ko = new KinematicCylinder(c, Vector3f.ZERO);
                boneObject[i] = new KinematicObject(new Geometry("Cylinder", c), Vector3f.ZERO);
                //set geometry, connect and transform cylinder, set material
                bones[i] = boneObject[i].selfNode;//new Geometry("Cylinder", c);
                setConnectiveTransform(ConnectingJoint[i], StartingJoint[i], bones[i]);
                
                skeleton.attachChild(bones[i]);
                main.getRootNode().attachChild(skeleton);
            }
            
            madeSkeleton = true;
        } else {
            System.out.println("NULL!");

        }
    }

    // This should initalize the skeleton. Movements should be handled in update Movements
    public KinectSkeleton(Main main) {
        this.main = main;
    }

    public void updateMovements() {
        if (madeSkeleton == true && main.kinect.joint != null) {
            //starting joints
            float[][] StartingJoint = {{(float) main.kinect.joint[10][1] / scaleFactor, (float) main.kinect.joint[10][2] / scaleFactor, (float) main.kinect.joint[10][3] / scaleFactor}, //right wrist
                {(float) main.kinect.joint[9][1] / scaleFactor, (float) main.kinect.joint[9][2] / scaleFactor, (float) main.kinect.joint[9][3] / scaleFactor}, //right elbow
                {(float) main.kinect.joint[8][1] / scaleFactor, (float) main.kinect.joint[8][2] / scaleFactor, (float) main.kinect.joint[8][3] / scaleFactor}, //right shoulder
                {(float) main.kinect.joint[2][1] / scaleFactor, (float) main.kinect.joint[2][2] / scaleFactor, (float) main.kinect.joint[2][3] / scaleFactor}, //shoulder center
                {(float) main.kinect.joint[4][1] / scaleFactor, (float) main.kinect.joint[4][2] / scaleFactor, (float) main.kinect.joint[4][3] / scaleFactor}, //left shoulder
                {(float) main.kinect.joint[5][1] / scaleFactor, (float) main.kinect.joint[5][2] / scaleFactor, (float) main.kinect.joint[5][3] / scaleFactor}, //left elbow
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[0][1] / scaleFactor, (float) main.kinect.joint[0][2] / scaleFactor, (float) main.kinect.joint[0][3] / scaleFactor}, //hip center
                {(float) main.kinect.joint[13][1] / scaleFactor, (float) main.kinect.joint[13][2] / scaleFactor, (float) main.kinect.joint[13][3] / scaleFactor}, //left knee
                {(float) main.kinect.joint[17][1] / scaleFactor, (float) main.kinect.joint[17][2] / scaleFactor, (float) main.kinect.joint[17][3] / scaleFactor}}; //right knee
            //joint the starting joints connect to
            float[][] ConnectingJoint = {{(float) main.kinect.joint[9][1] / scaleFactor, (float) main.kinect.joint[9][2] / scaleFactor, (float) main.kinect.joint[9][3] / scaleFactor}, //right elbow
                {(float) main.kinect.joint[8][1] / scaleFactor, (float) main.kinect.joint[8][2] / scaleFactor, (float) main.kinect.joint[8][3] / scaleFactor}, //right shoulder
                {(float) main.kinect.joint[2][1] / scaleFactor, (float) main.kinect.joint[2][2] / scaleFactor, (float) main.kinect.joint[2][3] / scaleFactor}, //shoulder center
                {(float) main.kinect.joint[4][1] / scaleFactor, (float) main.kinect.joint[4][2] / scaleFactor, (float) main.kinect.joint[4][3] / scaleFactor}, //left shoulder
                {(float) main.kinect.joint[5][1] / scaleFactor, (float) main.kinect.joint[5][2] / scaleFactor, (float) main.kinect.joint[5][3] / scaleFactor}, //left elbow
                {(float) main.kinect.joint[6][1] / scaleFactor, (float) main.kinect.joint[6][2] / scaleFactor, (float) main.kinect.joint[6][3] / scaleFactor}, //left wrist
                {(float) main.kinect.joint[2][1] / scaleFactor, (float) main.kinect.joint[2][2] / scaleFactor, (float) main.kinect.joint[2][3] / scaleFactor}, //shoulder center
                {(float) main.kinect.joint[8][1] / scaleFactor, (float) main.kinect.joint[8][2] / scaleFactor, (float) main.kinect.joint[8][3] / scaleFactor}, //right shoulder
                {(float) main.kinect.joint[4][1] / scaleFactor, (float) main.kinect.joint[4][2] / scaleFactor, (float) main.kinect.joint[4][3] / scaleFactor}, //left shoulder
                {(float) main.kinect.joint[13][1] / scaleFactor, (float) main.kinect.joint[13][2] / scaleFactor, (float) main.kinect.joint[13][3] / scaleFactor}, //left knee
                {(float) main.kinect.joint[17][1] / scaleFactor, (float) main.kinect.joint[17][2] / scaleFactor, (float) main.kinect.joint[17][3] / scaleFactor}, //right knee
                {(float) main.kinect.joint[14][1] / scaleFactor, (float) main.kinect.joint[14][2] / scaleFactor, (float) main.kinect.joint[14][3] / scaleFactor}, //left ankle
                {(float) main.kinect.joint[18][1] / scaleFactor, (float) main.kinect.joint[18][2] / scaleFactor, (float) main.kinect.joint[18][3] / scaleFactor}}; //right ankle
            for (int i = 0; i < bones.length; i++) {
                setConnectiveTransform(ConnectingJoint[i], StartingJoint[i], bones[i]);
                bones[i] = boneObject[i].selfNode;
                float heightScale = bones[i].getLocalScale().z;
                boneObject[i].setShape(new Geometry("New Shape",new Cylinder(10, 10, 0.09f, 1f*heightScale, true)));
                
            }
        } else {
            createSkeleton();
            System.out.println("NULL!");
        }
    }

    private synchronized void setConnectiveTransform(float[] p1, float[] p2, Node bone) {
        //Find Direction
        Vector3f u = new Vector3f(p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]);
        float length = u.length();
        u = u.normalize();
        //Set Rotation
        Vector3f v = u.cross(Vector3f.UNIT_Z);
        Vector3f w = v.cross(u);
        Matrix3f m = new Matrix3f(w.x, v.x, u.x, w.y, v.y, u.y, w.z, v.z, u.z);
        bone.setLocalRotation(m);
        //Set Scaling
        bone.setLocalScale(1, 1, length);
        
        //Set Translation
        Vector3f oldCenter = bone.getLocalTranslation();
        
        float[] center = {(p1[0] + p2[0]) / 2f, (p1[1] + p2[1]) / 2f, (p1[2] + p2[2]) / 2f};
        bone.setLocalTranslation(center[0], center[1], center[2]);
        Vector3f newCenter = bone.getLocalTranslation();
        
        Vector3f  difference = new Vector3f(newCenter.x-oldCenter.x,newCenter.y-oldCenter.y,newCenter.z-oldCenter.z);
        
        List<Spatial> l = bone.getParent().getChildren();
        ListIterator<Spatial> i = l.listIterator();
        
        while(i.hasNext()){
            Spatial s = i.next();
            Node n = (Node) s;
            if(n.getName().equals("sticky")){
                n.setLocalRotation(m);
                //n.move(difference);
                //n.setLocalTranslation(center[0], center[1], center[2]);
                
            }
        }
    }
}
