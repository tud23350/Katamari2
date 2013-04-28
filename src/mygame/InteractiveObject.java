/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

//import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
 * @author Owner
 */
public class InteractiveObject implements PhysicsCollisionListener, PhysicsTickListener{

    Vector3f position;
    
    //Geometry geom;
    
    private Material mat;
    CollisionShape shape;
    RigidBodyControl rigidBody;
    
    Node parasiticNode;
    GhostControl ghost;
    boolean kinematic=false;
    
    boolean removeSelf = false;
    
    private static Main main;
    private static AssetManager assetManager;
    
    
    public static void addListener(Main main){
        InteractiveObject.main = main;
        assetManager = main.getAssetManager();
    }
    
    InteractiveObject(Geometry geom,Vector3f position) {
        this.position = position;
        parasiticNode = new Node();
        
        //initialize box
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White); // purple
        
        geom.setMaterial(mat);
        geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geom.setCullHint(Spatial.CullHint.Never);
        parasiticNode.attachChild(geom);
        
        //initialize physics
        shape = new HullCollisionShape(geom.getMesh());
        parasiticNode.setLocalTranslation(position);
        rigidBody = new RigidBodyControl(shape, 1f);
        rigidBody.setFriction(1f);
        rigidBody.setKinematic(kinematic);
 
        ghost = new GhostControl(shape);
        parasiticNode.addControl(ghost);
        parasiticNode.addControl(rigidBody);
        
        parasiticNode.setName("sticky");
        
        main.bulletAppState.getPhysicsSpace().add(rigidBody);
        main.bulletAppState.getPhysicsSpace().add(ghost);
        main.bulletAppState.getPhysicsSpace().addCollisionListener(this);
        main.bulletAppState.getPhysicsSpace().addTickListener(this);
        
        main.getRootNode().attachChild(parasiticNode);
    }
    
    public String toString(){
        return "StickyObject";
    }
    
    public void collision(PhysicsCollisionEvent event) {
         Node a = (Node) event.getNodeA();
         Node b = (Node) event.getNodeB();
         
         Node topLayerNode;
         
         if(a.getName().equals("physical") && b.equals(parasiticNode)){
            System.out.println("Sticky object hit floor "+System.currentTimeMillis());
            rigidBody.setKinematic(true);
            main.getRootNode().detachChild(parasiticNode);
            //topLayerNode = a.getParent();
            b.attachChild(parasiticNode);
            
            removeSelf = true;
            
         }else if(b.getName().equals("physical") && a.equals(parasiticNode)){
            System.out.println("Stick object hit floor "+System.currentTimeMillis()); 
            rigidBody.setKinematic(true);
            main.getRootNode().detachChild(parasiticNode);
            
            //topLayerNode = b.getParent();
            b.attachChild(parasiticNode);
            
            removeSelf = true;
            
         }
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        //do nothing
    }

    //removes any remaining listeners (otherwise it's exceptions/throws out the ass)
    public void physicsTick(PhysicsSpace space, float tpf) {
        if(removeSelf){
            System.out.println("removing self");
            mat.setColor("Color", ColorRGBA.Red);
            main.bulletAppState.getPhysicsSpace().remove(ghost);
            main.bulletAppState.getPhysicsSpace().removeCollisionListener(this);
            main.bulletAppState.getPhysicsSpace().removeTickListener(this);
            removeSelf = false;
        }
        
    }


}
