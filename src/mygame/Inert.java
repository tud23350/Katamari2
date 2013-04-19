/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

//import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 */
public class Inert{

    Vector3f size;
    Vector3f position;
    
    Material mat;
    Geometry geom;
    private static boolean hasMain = false;
    private static Main main;
    private static AssetManager assetManager;
    
    CollisionShape shape;
    RigidBodyControl rigidBody;
    GhostControl ghost;
    
    Node topLayerNode;
    Node selfNode;
    
    
    public static void addListener(Main main){
        hasMain = true;
        Inert.main = main;
        assetManager = main.getAssetManager();
    }
    

    
    Inert(Geometry geom, Vector3f position) {
        this.position = position;
        
        selfNode = new Node();
        topLayerNode = new Node();
        
        //initialize box
        this.geom = geom;
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White); // purple
        
        geom.setMaterial(mat);
        geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geom.setCullHint(Spatial.CullHint.Never);
        selfNode.attachChild(geom);
        
        //initialize physics
        shape = new HullCollisionShape(geom.getMesh());
        ghost = new GhostControl(shape);
        selfNode.addControl(ghost);
        
        selfNode.setLocalTranslation(position);
        rigidBody = new RigidBodyControl(shape, 1f);
        rigidBody.setFriction(1f);
        selfNode.addControl(rigidBody);
        rigidBody.setKinematic(true);
 
        selfNode.setName("inert");
        
        
        
        main.bulletAppState.getPhysicsSpace().add(rigidBody);
        main.bulletAppState.getPhysicsSpace().add(ghost);
        //main.bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        topLayerNode.attachChild(selfNode);
        main.getRootNode().attachChild(topLayerNode);
    }
    
    public String toString(){
        return "InertObject";
    }

    public void collision(PhysicsCollisionEvent event) {
        
    }

}
