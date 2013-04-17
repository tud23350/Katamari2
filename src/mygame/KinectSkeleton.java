package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Michael
 */
//This is where the skeleton should be created
public class KinectSkeleton {
    Main main;
    boolean madeSkeleton = false;
    Node skeleton = new Node(); // this is the skeleton, which connects to the root node.
    Geometry[] bones = new Geometry[13]; //13 is the number of cylinders we will have representing bones
    
    public void createSkeleton(){
        if (main.kinect.joint != null) {
            Material matW = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            matW.setColor("Color", ColorRGBA.White);
            //starting joints
            float[][] StartingJoint = {{(float) main.kinect.joint[10][1] / 1000f, (float) main.kinect.joint[10][2] / 1000f, (float) main.kinect.joint[10][3] / 1000f}, //right wrist
                {(float) main.kinect.joint[9][1] / 1000f, (float) main.kinect.joint[9][2] / 1000f, (float) main.kinect.joint[9][3] / 1000f}, //right elbow
                {(float) main.kinect.joint[8][1] / 1000f, (float) main.kinect.joint[8][2] / 1000f, (float) main.kinect.joint[8][3] / 1000f}, //right shoulder
                {(float) main.kinect.joint[2][1] / 1000f, (float) main.kinect.joint[2][2] / 1000f, (float) main.kinect.joint[2][3] / 1000f}, //shoulder center
                {(float) main.kinect.joint[4][1] / 1000f, (float) main.kinect.joint[4][2] / 1000f, (float) main.kinect.joint[4][3] / 1000f}, //left shoulder
                {(float) main.kinect.joint[5][1] / 1000f, (float) main.kinect.joint[5][2] / 1000f, (float) main.kinect.joint[5][3] / 1000f}, //left elbow
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[13][1] / 1000f, (float) main.kinect.joint[13][2] / 1000f, (float) main.kinect.joint[13][3] / 1000f}, //left knee
                {(float) main.kinect.joint[17][1] / 1000f, (float) main.kinect.joint[17][2] / 1000f, (float) main.kinect.joint[17][3] / 1000f}}; //right knee
            //joint the starting joints connect to
            float[][] ConnectingJoint = {{(float) main.kinect.joint[9][1] / 1000f, (float) main.kinect.joint[9][2] / 1000f, (float) main.kinect.joint[9][3] / 1000f}, //right elbow
                {(float) main.kinect.joint[8][1] / 1000f, (float) main.kinect.joint[8][2] / 1000f, (float) main.kinect.joint[8][3] / 1000f}, //right shoulder
                {(float) main.kinect.joint[2][1] / 1000f, (float) main.kinect.joint[2][2] / 1000f, (float) main.kinect.joint[2][3] / 1000f}, //shoulder center
                {(float) main.kinect.joint[4][1] / 1000f, (float) main.kinect.joint[4][2] / 1000f, (float) main.kinect.joint[4][3] / 1000f}, //left shoulder
                {(float) main.kinect.joint[5][1] / 1000f, (float) main.kinect.joint[5][2] / 1000f, (float) main.kinect.joint[5][3] / 1000f}, //left elbow
                {(float) main.kinect.joint[6][1] / 1000f, (float) main.kinect.joint[6][2] / 1000f, (float) main.kinect.joint[6][3] / 1000f}, //left wrist
                {(float) main.kinect.joint[2][1] / 1000f, (float) main.kinect.joint[2][2] / 1000f, (float) main.kinect.joint[2][3] / 1000f}, //shoulder center
                {(float) main.kinect.joint[8][1] / 1000f, (float) main.kinect.joint[8][2] / 1000f, (float) main.kinect.joint[8][3] / 1000f}, //right shoulder
                {(float) main.kinect.joint[4][1] / 1000f, (float) main.kinect.joint[4][2] / 1000f, (float) main.kinect.joint[4][3] / 1000f}, //left shoulder
                {(float) main.kinect.joint[13][1] / 1000f, (float) main.kinect.joint[13][2] / 1000f, (float) main.kinect.joint[13][3] / 1000f}, //left knee
                {(float) main.kinect.joint[17][1] / 1000f, (float) main.kinect.joint[17][2] / 1000f, (float) main.kinect.joint[17][3] / 1000f}, //right knee
                {(float) main.kinect.joint[14][1] / 1000f, (float) main.kinect.joint[14][2] / 1000f, (float) main.kinect.joint[14][3] / 1000f}, //left ankle
                {(float) main.kinect.joint[18][1] / 1000f, (float) main.kinect.joint[18][2] / 1000f, (float) main.kinect.joint[18][3] / 1000f}}; //right ankle
            //start loop to connect all joints
            for (int i = 0; i < bones.length; i++) {
                Cylinder c = new Cylinder(10, 10, 0.04f, 1f, true);
                //set geometry, connect and transform cylinder, set material
                bones[i] = new Geometry("Cylinder", c);
                setConnectiveTransform(ConnectingJoint[i], StartingJoint[i], bones[i]);
                bones[i].setMaterial(matW);
                //attach physics to bones
                RigidBodyControl phy = new RigidBodyControl(1f); //0f = 0 mass
                bones[i].addControl(phy);
                phy.setKinematic(true);
                //attach physics to world
                main.bulletAppState.getPhysicsSpace().add(phy);
                //attach to node so we can play
                skeleton.attachChild(bones[i]);
                main.getRootNode().attachChild(skeleton);
            }
            //float y = (float) main.kinect.joint[0][3] / 1000f+(float) main.kinect.joint[0][3] / 1000f;
            skeleton.move(0, 0.75f, 0);
            madeSkeleton = true;
        }else{
                        System.out.println("NULL!");

        }
    }
    
    // This should initalize the skeleton. Movements should be handled in update Movements
    public KinectSkeleton(Main main) {
        this.main = main;
        
        
    }

    public void updateMovements() {
        if (madeSkeleton==true && main.kinect.joint != null) {
            //starting joints
            float[][] StartingJoint = {{(float) main.kinect.joint[10][1] / 1000f, (float) main.kinect.joint[10][2] / 1000f, (float) main.kinect.joint[10][3] / 1000f}, //right wrist
                {(float) main.kinect.joint[9][1] / 1000f, (float) main.kinect.joint[9][2] / 1000f, (float) main.kinect.joint[9][3] / 1000f}, //right elbow
                {(float) main.kinect.joint[8][1] / 1000f, (float) main.kinect.joint[8][2] / 1000f, (float) main.kinect.joint[8][3] / 1000f}, //right shoulder
                {(float) main.kinect.joint[2][1] / 1000f, (float) main.kinect.joint[2][2] / 1000f, (float) main.kinect.joint[2][3] / 1000f}, //shoulder center
                {(float) main.kinect.joint[4][1] / 1000f, (float) main.kinect.joint[4][2] / 1000f, (float) main.kinect.joint[4][3] / 1000f}, //left shoulder
                {(float) main.kinect.joint[5][1] / 1000f, (float) main.kinect.joint[5][2] / 1000f, (float) main.kinect.joint[5][3] / 1000f}, //left elbow
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[0][1] / 1000f, (float) main.kinect.joint[0][2] / 1000f, (float) main.kinect.joint[0][3] / 1000f}, //hip center
                {(float) main.kinect.joint[13][1] / 1000f, (float) main.kinect.joint[13][2] / 1000f, (float) main.kinect.joint[13][3] / 1000f}, //left knee
                {(float) main.kinect.joint[17][1] / 1000f, (float) main.kinect.joint[17][2] / 1000f, (float) main.kinect.joint[17][3] / 1000f}}; //right knee
            //joint the starting joints connect to
            float[][] ConnectingJoint = {{(float) main.kinect.joint[9][1] / 1000f, (float) main.kinect.joint[9][2] / 1000f, (float) main.kinect.joint[9][3] / 1000f}, //right elbow
                {(float) main.kinect.joint[8][1] / 1000f, (float) main.kinect.joint[8][2] / 1000f, (float) main.kinect.joint[8][3] / 1000f}, //right shoulder
                {(float) main.kinect.joint[2][1] / 1000f, (float) main.kinect.joint[2][2] / 1000f, (float) main.kinect.joint[2][3] / 1000f}, //shoulder center
                {(float) main.kinect.joint[4][1] / 1000f, (float) main.kinect.joint[4][2] / 1000f, (float) main.kinect.joint[4][3] / 1000f}, //left shoulder
                {(float) main.kinect.joint[5][1] / 1000f, (float) main.kinect.joint[5][2] / 1000f, (float) main.kinect.joint[5][3] / 1000f}, //left elbow
                {(float) main.kinect.joint[6][1] / 1000f, (float) main.kinect.joint[6][2] / 1000f, (float) main.kinect.joint[6][3] / 1000f}, //left wrist
                {(float) main.kinect.joint[2][1] / 1000f, (float) main.kinect.joint[2][2] / 1000f, (float) main.kinect.joint[2][3] / 1000f}, //shoulder center
                {(float) main.kinect.joint[8][1] / 1000f, (float) main.kinect.joint[8][2] / 1000f, (float) main.kinect.joint[8][3] / 1000f}, //right shoulder
                {(float) main.kinect.joint[4][1] / 1000f, (float) main.kinect.joint[4][2] / 1000f, (float) main.kinect.joint[4][3] / 1000f}, //left shoulder
                {(float) main.kinect.joint[13][1] / 1000f, (float) main.kinect.joint[13][2] / 1000f, (float) main.kinect.joint[13][3] / 1000f}, //left knee
                {(float) main.kinect.joint[17][1] / 1000f, (float) main.kinect.joint[17][2] / 1000f, (float) main.kinect.joint[17][3] / 1000f}, //right knee
                {(float) main.kinect.joint[14][1] / 1000f, (float) main.kinect.joint[14][2] / 1000f, (float) main.kinect.joint[14][3] / 1000f}, //left ankle
                {(float) main.kinect.joint[18][1] / 1000f, (float) main.kinect.joint[18][2] / 1000f, (float) main.kinect.joint[18][3] / 1000f}}; //right ankle
            for (int i = 0; i < bones.length; i++) {
                setConnectiveTransform(ConnectingJoint[i], StartingJoint[i], bones[i]);
            }
        } else {
            createSkeleton();
            System.out.println("NULL!");
        }
    }

    private void setConnectiveTransform(float[] p1, float[] p2, Geometry c) {
        //Find Direction
        Vector3f u = new Vector3f(p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]);
        float length = u.length();
        u = u.normalize();
        //Set Rotation
        Vector3f v = u.cross(Vector3f.UNIT_Z);
        Vector3f w = v.cross(u);
        Matrix3f m = new Matrix3f(w.x, v.x, u.x, w.y, v.y, u.y, w.z, v.z, u.z);
        c.setLocalRotation(m);
        //Set Scaling
        c.setLocalScale(1, 1, length);
        //Set Translation
        float[] center = {(p1[0] + p2[0]) / 2f, (p1[1] + p2[1]) / 2f, (p1[2] + p2[2]) / 2f};
        c.setLocalTranslation(center[0], center[1], center[2]);
    }
}
