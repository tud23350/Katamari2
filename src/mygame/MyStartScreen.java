package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 */
public class MyStartScreen extends AbstractAppState implements ScreenController {

    private Nifty nifty;
    private Application app;
    private Screen screen;
    boolean snapshot = false;
    boolean startgame = false;
    boolean snapshottext=false;
    boolean loadingGame = false;
    boolean quitgame = false;

    /**
     * custom 
     */
    public MyStartScreen() {
        /**
         * You custom constructor, can accept arguments
         */
    }

    public void startGame(String nextScreen) {
        nifty.gotoScreen(nextScreen);  // switch to another screen
        startgame=true;
    }
    
    public void loading() {
        nifty.gotoScreen("loading");
        loadingGame = true;        
    }

    public void snapshot() {
        snapshot=true;
        snapshottext=true;
        //loadingGame=true;
    }

    public void quitGame() {
        app.stop();
        quitgame = true;
    }

    public String getPlayerName() {
        return System.getProperty("user.name");
    }

    public String getGameStatus() {
        if (snapshottext == true) {
            return "Snapshot Taken Sucessfully!";
        } else {
            return "hello ";
        }
    }

    /**
     * Nifty GUI ScreenControl methods
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    /**
     * jME3 AppState methods
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
    }

    @Override
    public void update(float tpf) {
        /*
        if (snapshottext) {
            Element niftyElement = nifty.getCurrentScreen().findElementByName("panel_top");
            niftyElement.getRenderer(TextRenderer.class).setText("Snapshot Taken");
        }
        */
    }

    public void onStartScreen() {

    }

    public void onEndScreen() {
        //System.out.println("end!");
        
    }
    
    public void closeNifty(){
        nifty.gotoScreen("empty");
    }
}
