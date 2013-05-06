package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.Properties;
import java.util.jar.Attributes;

/**
 *
 */
public class MyStartScreen extends AbstractAppState implements ScreenController {

    private Nifty nifty;
    private Application app;
    private Screen screen;
    private Element progressBarElement;
    Element element;
    private TextRenderer textRenderer;
    boolean snapshot = false;
    boolean startgame = false;
    boolean snapshotcompleted = false;
    boolean quitgame = false;
    private boolean load = false;
    private boolean gamestarted = false;

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
        startgame = true;
        gamestarted = true;
    }

    public void snapshot() {
        snapshot = true;
    }

    public void quitGame() {
        app.stop();
        quitgame = true;
    }

    public String getPlayerName() {
        return System.getProperty("user.name");
    }

    /**
     * Nifty GUI ScreenControl methods
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
    }

    public void setsnapcomplete() {
        snapshotcompleted = true;
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
        if (snapshotcompleted && !gamestarted) {
            Element niftyElement = nifty.getCurrentScreen().findElementByName("status");
            niftyElement.getRenderer(TextRenderer.class).setText("Environment Created!");
        }
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void setProgress(final float progress, String loadingText) {
        final int MIN_WIDTH = 32;
        int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
        progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        progressBarElement.getParent().layoutElements();
        textRenderer.setText(loadingText);
    }

    public void showLoadingMenu() {
        nifty.gotoScreen("loadLevel");
        load = true;
    }

    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return false;
    }

    public void bind(Nifty nifty, Screen screen, Element elmnt, Properties prprts, Attributes atrbts) {
        progressBarElement = elmnt.findElementByName("progressbar");
    }

    public void init(Element elmnt, Properties prprts, Attributes atrbts) {
        element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
        textRenderer = element.getRenderer(TextRenderer.class);

    }

    public void onFocus(boolean getFocus) {
    }

    public void closeNifty() {
        nifty.gotoScreen("empty");
    }
}
