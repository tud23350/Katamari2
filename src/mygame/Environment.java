/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Owner
 */
public class Environment {
    
    private static InteractiveObject testBox[], testSphere[];
    private static Inert floor;
    private static Inert tiles[];
    
    public static void create(){
        int n = 5;
        testBox = new TestBox[n*n];
        testSphere = new InteractiveObject[n*n];
        tiles = new Inert[n*n];
        float xOffset = 2.5f*n/2f;
        float zOffset = 2.5f*n/2f;
        for(int i=0;i<n*n;i++){
            //testBox[i] = new TestBox( new Vector3f((i/n)*0.7f-2f,1f,(i%n)*0.7f+1), new Vector3f(0.2f, 0.2f, 0.2f));
            //testSphere[i] = new TestBox( new Vector3f((i/n)*3,3,(i%n)*3), new Vector3f(0.1f, 0.1f, 0.1f));//new InteractiveObject(new Geometry("Lulz",new Sphere(10, 10, 1f)), new Vector3f((i/n)*3,3,(i%n)*3));
            ColorRGBA color = (i%2)>0 ? ColorRGBA.White : ColorRGBA.LightGray;
            tiles[i] = new Inert(new Geometry("Lulz",new Box(2.5f,0.1f,2.5f)) , new Vector3f(5f*(i%n)-xOffset,-1.25f,2.5f+i/5*5f-zOffset),color);
        }
        //floor = new Inert(new Geometry("Lulz",new Box(2.5f,0.1f,2.5f)) , new Vector3f(0,-1.25f,2.5f));
    }
    
}
