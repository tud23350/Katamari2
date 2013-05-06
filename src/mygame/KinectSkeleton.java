package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import java.util.List;
import java.util.ListIterator;
import kinecttcpclient.KinectTCPClient;

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
    int[][] joint;
    private final float scaleFactor = 500f;

    private int[][] getJoints() {
        int[][] tmp;
        if (main.kinect != null) {
            int[] skRaw = main.kinect.readSkeleton();
            tmp = KinectTCPClient.getJointPositions(skRaw, 1);
        } else {
            tmp = main.moCap.getJoints();
        }
        return tmp;
    }

    public void createSkeleton() {

        joint = getJoints();
        if (joint != null) {
            //starting joints
            float[][] StartingJoint = {{(float) joint[10][1] / scaleFactor, (float) joint[10][2] / scaleFactor, (float) joint[10][3] / scaleFactor}, //right wrist
                {(float) joint[9][1] / scaleFactor, (float) joint[9][2] / scaleFactor, (float) joint[9][3] / scaleFactor}, //right elbow
                {(float) joint[8][1] / scaleFactor, (float) joint[8][2] / scaleFactor, (float) joint[8][3] / scaleFactor}, //right shoulder
                {(float) joint[2][1] / scaleFactor, (float) joint[2][2] / scaleFactor, (float) joint[2][3] / scaleFactor}, //shoulder center
                {(float) joint[4][1] / scaleFactor, (float) joint[4][2] / scaleFactor, (float) joint[4][3] / scaleFactor}, //left shoulder
                {(float) joint[5][1] / scaleFactor, (float) joint[5][2] / scaleFactor, (float) joint[5][3] / scaleFactor}, //left elbow
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[13][1] / scaleFactor, (float) joint[13][2] / scaleFactor, (float) joint[13][3] / scaleFactor}, //left knee
                {(float) joint[17][1] / scaleFactor, (float) joint[17][2] / scaleFactor, (float) joint[17][3] / scaleFactor}}; //right knee
            //joint the starting joints connect to
            float[][] ConnectingJoint = {{(float) joint[9][1] / scaleFactor, (float) joint[9][2] / scaleFactor, (float) joint[9][3] / scaleFactor}, //right elbow
                {(float) joint[8][1] / scaleFactor, (float) joint[8][2] / scaleFactor, (float) joint[8][3] / scaleFactor}, //right shoulder
                {(float) joint[2][1] / scaleFactor, (float) joint[2][2] / scaleFactor, (float) joint[2][3] / scaleFactor}, //shoulder center
                {(float) joint[4][1] / scaleFactor, (float) joint[4][2] / scaleFactor, (float) joint[4][3] / scaleFactor}, //left shoulder
                {(float) joint[5][1] / scaleFactor, (float) joint[5][2] / scaleFactor, (float) joint[5][3] / scaleFactor}, //left elbow
                {(float) joint[6][1] / scaleFactor, (float) joint[6][2] / scaleFactor, (float) joint[6][3] / scaleFactor}, //left wrist
                {(float) joint[2][1] / scaleFactor, (float) joint[2][2] / scaleFactor, (float) joint[2][3] / scaleFactor}, //shoulder center
                {(float) joint[8][1] / scaleFactor, (float) joint[8][2] / scaleFactor, (float) joint[8][3] / scaleFactor}, //right shoulder
                {(float) joint[4][1] / scaleFactor, (float) joint[4][2] / scaleFactor, (float) joint[4][3] / scaleFactor}, //left shoulder
                {(float) joint[13][1] / scaleFactor, (float) joint[13][2] / scaleFactor, (float) joint[13][3] / scaleFactor}, //left knee
                {(float) joint[17][1] / scaleFactor, (float) joint[17][2] / scaleFactor, (float) joint[17][3] / scaleFactor}, //right knee
                {(float) joint[14][1] / scaleFactor, (float) joint[14][2] / scaleFactor, (float) joint[14][3] / scaleFactor}, //left ankle
                {(float) joint[18][1] / scaleFactor, (float) joint[18][2] / scaleFactor, (float) joint[18][3] / scaleFactor}}; //right ankle
            //start loop to connect all joints
            for (int i = 0; i < bones.length; i++) {
                Cylinder c = new Cylinder(10, 10, 0.09f, 1f, true);
                boneObject[i] = new KinematicObject(new Geometry("Cylinder", c), Vector3f.ZERO);
                //set geometry, connect and transform cylinder, set material
                bones[i] = boneObject[i].selfNode;
                setConnectiveTransform(ConnectingJoint[i], StartingJoint[i], bones[i]);
                float heightScale = bones[i].getLocalScale().z;

                boneObject[i].setShape(new Geometry("New Shape", new Cylinder(10, 10, 0.09f, 1f * heightScale, true)));
                skeleton.attachChild(bones[i]);
                main.getRootNode().attachChild(skeleton);
            }

            madeSkeleton = true;
        } else {
        }

    }

    // This should initalize the skeleton. Movements should be handled in update Movements
    public KinectSkeleton(Main main) {
        this.main = main;
    }

    public void updateMovements() {
        if (madeSkeleton == true && joint != null) {
            joint = getJoints();
            //starting joints
            float[][] StartingJoint = {{(float) joint[10][1] / scaleFactor, (float) joint[10][2] / scaleFactor, (float) joint[10][3] / scaleFactor}, //right wrist
                {(float) joint[9][1] / scaleFactor, (float) joint[9][2] / scaleFactor, (float) joint[9][3] / scaleFactor}, //right elbow
                {(float) joint[8][1] / scaleFactor, (float) joint[8][2] / scaleFactor, (float) joint[8][3] / scaleFactor}, //right shoulder
                {(float) joint[2][1] / scaleFactor, (float) joint[2][2] / scaleFactor, (float) joint[2][3] / scaleFactor}, //shoulder center
                {(float) joint[4][1] / scaleFactor, (float) joint[4][2] / scaleFactor, (float) joint[4][3] / scaleFactor}, //left shoulder
                {(float) joint[5][1] / scaleFactor, (float) joint[5][2] / scaleFactor, (float) joint[5][3] / scaleFactor}, //left elbow
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[0][1] / scaleFactor, (float) joint[0][2] / scaleFactor, (float) joint[0][3] / scaleFactor}, //hip center
                {(float) joint[13][1] / scaleFactor, (float) joint[13][2] / scaleFactor, (float) joint[13][3] / scaleFactor}, //left knee
                {(float) joint[17][1] / scaleFactor, (float) joint[17][2] / scaleFactor, (float) joint[17][3] / scaleFactor}}; //right knee
            //joint the starting joints connect to
            float[][] ConnectingJoint = {{(float) joint[9][1] / scaleFactor, (float) joint[9][2] / scaleFactor, (float) joint[9][3] / scaleFactor}, //right elbow
                {(float) joint[8][1] / scaleFactor, (float) joint[8][2] / scaleFactor, (float) joint[8][3] / scaleFactor}, //right shoulder
                {(float) joint[2][1] / scaleFactor, (float) joint[2][2] / scaleFactor, (float) joint[2][3] / scaleFactor}, //shoulder center
                {(float) joint[4][1] / scaleFactor, (float) joint[4][2] / scaleFactor, (float) joint[4][3] / scaleFactor}, //left shoulder
                {(float) joint[5][1] / scaleFactor, (float) joint[5][2] / scaleFactor, (float) joint[5][3] / scaleFactor}, //left elbow
                {(float) joint[6][1] / scaleFactor, (float) joint[6][2] / scaleFactor, (float) joint[6][3] / scaleFactor}, //left wrist
                {(float) joint[2][1] / scaleFactor, (float) joint[2][2] / scaleFactor, (float) joint[2][3] / scaleFactor}, //shoulder center
                {(float) joint[8][1] / scaleFactor, (float) joint[8][2] / scaleFactor, (float) joint[8][3] / scaleFactor}, //right shoulder
                {(float) joint[4][1] / scaleFactor, (float) joint[4][2] / scaleFactor, (float) joint[4][3] / scaleFactor}, //left shoulder
                {(float) joint[13][1] / scaleFactor, (float) joint[13][2] / scaleFactor, (float) joint[13][3] / scaleFactor}, //left knee
                {(float) joint[17][1] / scaleFactor, (float) joint[17][2] / scaleFactor, (float) joint[17][3] / scaleFactor}, //right knee
                {(float) joint[14][1] / scaleFactor, (float) joint[14][2] / scaleFactor, (float) joint[14][3] / scaleFactor}, //left ankle
                {(float) joint[18][1] / scaleFactor, (float) joint[18][2] / scaleFactor, (float) joint[18][3] / scaleFactor}}; //right ankle

            for (int i = 0; i < bones.length; i++) {
                setConnectiveTransform(ConnectingJoint[i], StartingJoint[i], bones[i]);
                bones[i] = boneObject[i].selfNode;
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

        float[] center = {(p1[0] + p2[0]) / 2f, (p1[1] + p2[1]) / 2f, (p1[2] + p2[2]) / 2f};
        bone.setLocalTranslation(center[0], center[1], center[2]);

        List<Spatial> l = bone.getChildren();
        ListIterator<Spatial> i = l.listIterator();

        while (i.hasNext()) {
            Spatial s = i.next();
            if (s.getName().equals("stuck")) {
                s.setLocalScale(1, 1, (float) 1. / length);
            }
        }
    }
}
