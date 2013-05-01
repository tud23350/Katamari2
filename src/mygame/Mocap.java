package mygame;

/*Provided by Rolf for debugging purposes
 * 
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import kinecttcpclient.KinectTCPClient;

public class Mocap extends Thread implements ActionListener {

    Mocap_Panel skp;
//    KinectTCPClient kinect;
    int[][] joints;
    protected int state;
    protected final int STOP = 0, PLAY = 1, RECORD = 2;
    int frameCnt;
    LinkedList<int[][]> frames = new LinkedList<int[][]>();
    Iterator<int[][]> frameIterator;

    public Mocap() {
        // GUI
        JFrame windowFrame;
        skp = new Mocap_Panel(300, 300, this);
        windowFrame = new JFrame();
        windowFrame.setLayout(new BorderLayout());
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 3));

        JButton stop = new JButton("Stop");
        stop.setActionCommand("stop");
        stop.addActionListener(this);
        southPanel.add(stop);
        JButton play = new JButton("Play");
        play.addActionListener(this);
        play.setActionCommand("play");
        southPanel.add(play);
        JButton record = new JButton("Record");
        record.addActionListener(this);
        record.setActionCommand("record");
        southPanel.add(record);
        windowFrame.add(southPanel, "South");
        windowFrame.add(skp, "Center");

        //
        // Menu
        JMenuBar menuBar = new JMenuBar();
        // --- file
        JMenu menuFile = new JMenu("File");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemLoad = new JMenuItem("Load");
        itemSave.setActionCommand("itemSave");
        itemSave.addActionListener(this);
        menuFile.add(itemSave);
        itemLoad.setActionCommand("itemLoad");
        itemLoad.addActionListener(this);
        menuFile.add(itemLoad);
        menuBar.add(menuFile);
        windowFrame.setJMenuBar(menuBar);
        windowFrame.pack();
        windowFrame.setVisible(true);

//        //Kinect
//        kinect = new KinectTCPClient();
//        try{
//        int [][]dummy = kinect.readDepth();
//        }catch(Exception e){
//            kinect = null;
//        }
//        //
        frameCnt = 0;
        frameIterator = frames.iterator();
        state = PLAY;
        
        //load();
        state = PLAY;
        this.start();
    }

    // -------------------------------------------------------------------------
    public void run() {
        long sleepytime = 50;    // 25fps
        while (true) {
            long starttime = System.currentTimeMillis();

            switch (state) {
                case STOP:
//                    if (kinect != null) {
//                        try {
//
//                            int[] raw = kinect.readSkeleton();
//                            joints = kinect.getJointPositions(raw, 1);
//                            skp.repaint();
//
//                        } catch (Exception e) {
//                        }
//                    }
//                    break;
                case PLAY:
                    if (frameIterator.hasNext()) {
                        joints = frameIterator.next();
                        frameCnt++;
                    } else if (frames.size() > 0) {
                        frameIterator = frames.iterator();
                        frameCnt = 0;
                        joints = frameIterator.next();
                    }
                    skp.repaint();

                    break;
                case RECORD:
//                    if (kinect != null) {
//                        try {
//                            int[] raw = kinect.readSkeleton();
//                            int[][] jointFrame = kinect.getJointPositions(raw, 1);
//                            if (jointFrame != null) {
//                                joints = jointFrame;
//                                frames.addLast(jointFrame);
//                                frameCnt++;
//                            }
//                            skp.repaint();
//                        } catch (Exception e) {
//                        }
//                    }
//                    break;
            }

            try {
                Thread.sleep(sleepytime);
            } catch (InterruptedException ex) {
                Logger.getLogger(Mocap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // -------------------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        String a = e.getActionCommand();
        if (a.equalsIgnoreCase("stop")) {
            state = STOP;
        }
        if (a.equalsIgnoreCase("play")) {
            frameCnt = 0;
            frameIterator = frames.iterator();
            state = PLAY;
        }
        if (a.equalsIgnoreCase("record")) {
            frameCnt = 0;
            frames.clear();
            state = RECORD;
        }
        if (a.equalsIgnoreCase("itemSave")) {
            save();
        }
        if (a.equalsIgnoreCase("itemLoad")) {
            load();
        }

        skp.repaint();
    }

    // -------------------------------------------------------------------------
    // save
    private void save() {
        if (state == RECORD) {
            JOptionPane.showMessageDialog(null, "Can't save while recording.");
            return;
        }
        // get filename
        JFileChooser fc = new JFileChooser(".");
        int returnVal = fc.showOpenDialog(null);
        String filename;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = fc.getSelectedFile().getAbsolutePath();
            try {
                FileOutputStream fileOut =
                        new FileOutputStream(filename);
                ObjectOutputStream out =
                        new ObjectOutputStream(fileOut);
                out.writeObject(frames);
                out.close();
                fileOut.close();
            } catch (Exception i) {
            }
        } else {
        }
    }

    // -------------------------------------------------------------------------
    // load
    private void load() {

        String filename = "CannonTest.serial";

        try {
            FileInputStream fileIn =
                    new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            frames = (LinkedList<int[][]>) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception ex) {
            System.out.println("trouble");
        }

    }

    // -------------------------------------------------------------------------
    // getJoints
    public int[][] getJoints() {
        return (joints);
    }
    // -------------------------------------------------------------------------
}
