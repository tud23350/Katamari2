package mygame;

// ========================================================================
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

// Class: Mocap_Panel
// ========================================================================
/** Skeleton Panel, shows skeleton data.
 *
 */
public class Mocap_Panel extends JPanel {

    int sizeX, sizeY;
    double scale;
    private Mocap moc;

    public Mocap_Panel(int sx, int sy, Mocap m) {
        super();
        moc = m;
        sizeX = sx;
        sizeY = sy;
        setPreferredSize(new Dimension(sx, sy));
    }

    // --------------------------------------------------------------------
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getHeight(), getWidth());
        g.setColor(Color.white);

        int[][] joints = moc.joints;

        if (joints != null) {
            try {
                for (int skelIndex = 1; skelIndex <= 2; skelIndex++) {
                    int[][] segments = new int[19][5];
                    // head to shoulder center
                    segments[0][0] = joints[3][1];
                    segments[0][1] = joints[3][2];
                    segments[0][2] = joints[2][1];
                    segments[0][3] = joints[2][2];
                    segments[0][4] = joints[3][0] * joints[2][0];

                    //shoulder center to shoulder right
                    segments[1][0] = joints[8][1];
                    segments[1][1] = joints[8][2];
                    segments[1][2] = joints[2][1];
                    segments[1][3] = joints[2][2];
                    segments[1][4] = joints[8][0] * joints[2][0];

                    //shoulder right to elbow right
                    segments[2][0] = joints[8][1];
                    segments[2][1] = joints[8][2];
                    segments[2][2] = joints[9][1];
                    segments[2][3] = joints[9][2];
                    segments[2][4] = joints[8][0] * joints[9][0];

                    //elbow right to wrist right
                    segments[3][0] = joints[10][1];
                    segments[3][1] = joints[10][2];
                    segments[3][2] = joints[9][1];
                    segments[3][3] = joints[9][2];
                    segments[3][4] = joints[10][0] * joints[9][0];

                    //wrist right to hand right
                    segments[4][0] = joints[10][1];
                    segments[4][1] = joints[10][2];
                    segments[4][2] = joints[11][1];
                    segments[4][3] = joints[11][2];
                    segments[4][4] = joints[10][0] * joints[11][0];

                    //
                    //shoulder center to shoulder left
                    segments[5][0] = joints[4][1];
                    segments[5][1] = joints[4][2];
                    segments[5][2] = joints[2][1];
                    segments[5][3] = joints[2][2];
                    segments[5][4] = joints[2][0] * joints[4][0];

                    //shoulder left to elbow left
                    segments[6][0] = joints[4][1];
                    segments[6][1] = joints[4][2];
                    segments[6][2] = joints[5][1];
                    segments[6][3] = joints[5][2];
                    segments[6][4] = joints[4][0] * joints[5][0];

                    //elbow left to wrist left
                    segments[7][0] = joints[5][1];
                    segments[7][1] = joints[5][2];
                    segments[7][2] = joints[6][1];
                    segments[7][3] = joints[6][2];
                    segments[7][4] = joints[6][0] * joints[5][0];

                    //wrist left to hand left
                    segments[8][0] = joints[6][1];
                    segments[8][1] = joints[6][2];
                    segments[8][2] = joints[7][1];
                    segments[8][3] = joints[7][2];
                    segments[8][4] = joints[7][0] * joints[6][0];

                    // spine
                    segments[9][0] = joints[2][1];
                    segments[9][1] = joints[2][2];
                    segments[9][2] = joints[1][1];
                    segments[9][3] = joints[1][2];
                    segments[9][4] = joints[1][0] * joints[2][0];

                    segments[10][0] = joints[1][1];
                    segments[10][1] = joints[1][2];
                    segments[10][2] = joints[0][1];
                    segments[10][3] = joints[0][2];
                    segments[10][4] = joints[0][0] * joints[1][0];

                    //right leg
                    segments[11][0] = joints[16][1];
                    segments[11][1] = joints[16][2];
                    segments[11][2] = joints[0][1];
                    segments[11][3] = joints[0][2];
                    segments[11][4] = joints[0][0] * joints[16][0];

                    segments[12][0] = joints[16][1];
                    segments[12][1] = joints[16][2];
                    segments[12][2] = joints[17][1];
                    segments[12][3] = joints[17][2];
                    segments[12][4] = joints[17][0] * joints[16][0];

                    segments[13][0] = joints[17][1];
                    segments[13][1] = joints[17][2];
                    segments[13][2] = joints[18][1];
                    segments[13][3] = joints[18][2];
                    segments[13][4] = joints[18][0] * joints[17][0];

                    segments[14][0] = joints[18][1];
                    segments[14][1] = joints[18][2];
                    segments[14][2] = joints[19][1];
                    segments[14][3] = joints[19][2];
                    segments[14][4] = joints[19][0] * joints[18][0];

                    //left leg
                    segments[15][0] = joints[12][1];
                    segments[15][1] = joints[12][2];
                    segments[15][2] = joints[0][1];
                    segments[15][3] = joints[0][2];
                    segments[15][4] = joints[0][0] * joints[12][0];

                    segments[16][0] = joints[12][1];
                    segments[16][1] = joints[12][2];
                    segments[16][2] = joints[13][1];
                    segments[16][3] = joints[13][2];
                    segments[16][4] = joints[13][0] * joints[12][0];

                    segments[17][0] = joints[13][1];
                    segments[17][1] = joints[13][2];
                    segments[17][2] = joints[14][1];
                    segments[17][3] = joints[14][2];
                    segments[17][4] = joints[14][0] * joints[13][0];

                    segments[18][0] = joints[14][1];
                    segments[18][1] = joints[14][2];
                    segments[18][2] = joints[15][1];
                    segments[18][3] = joints[15][2];
                    segments[18][4] = joints[15][0] * joints[14][0];

                    if (skelIndex == 1) {
                        g.setColor(Color.red);
                    } else {
                        g.setColor(Color.green);
                    }

                    int w = getWidth() / 2;
                    int h = getHeight() / 2;
                    double s = 1.8 / 1800 * h;
                    for (int i = 0; i < segments.length; i++) {
                        if (segments[i][4] != 0) {
                            int x1 = (int) (segments[i][0] * s + w);
                            int y1 = (int) (-segments[i][1] * s + h);
                            int x2 = (int) (segments[i][2] * s + w);
                            int y2 = (int) (-segments[i][3] * s + h);
                            g.drawLine(x1, y1, x2, y2);
                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
            }
        }
        g.setColor(Color.black);
        g.fillRect(0, 0, 55, 15);
        g.setColor(Color.green);
        String s = "";
        if (moc.state == moc.STOP) {
            s = "STOP";
        }
        if (moc.state == moc.PLAY) {
            s = "PLAY, Frame = "+moc.frameCnt;
        }
        if (moc.state == moc.RECORD) {
            g.setColor(Color.red);
            s = "RECORD, Frame = "+moc.frameCnt;
        }
        g.drawString(s, 5, 10);
    }
}
