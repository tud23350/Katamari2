package mygame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jake
 */
public class LeaderBoard extends JPanel {

    int sizeX, sizeY, score, time;
    String[][] rank = new String[3][3];

    public LeaderBoard(int sx, int sy, int score, int time) {
        super();
        sizeX = sx;
        sizeY = sy;
        this.score = score;
        this.time = time;
        setPreferredSize(new Dimension(sx, sy));
    }

    public void getScores() throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream("Kinect_Data/LeaderBoard.txt");
        Scanner fileInput = new Scanner(fis);
        int k = 0;
        while (fileInput.hasNextLine()) {
            rank[k][0] = fileInput.nextLine(); //name
            rank[k][1] = fileInput.nextLine(); //score
            rank[k][2] = fileInput.nextLine(); //time
            System.out.println(rank[k][0]);
            System.out.print(rank[k][1]);
            System.out.println(rank[k][2]);
            k++;
        }
        fileInput.close();
        fis.close();
    }

    public void putScores() throws FileNotFoundException, IOException {
        getScores();
        File data = new File("Kinect_Data/LeaderBoard.txt");
        PrintWriter output = new PrintWriter(data);
        for (int i = 0; i < rank.length; i++) {
            if (score > Integer.valueOf(rank[i][1])) {
                rank[i][0] = "JEN";
                rank[i][1] = Integer.toString(score);
                rank[i][2] = Integer.toString(time);
            }
            output.println(rank[i][0]); //name
            output.println(rank[i][1]); //Score
            output.println(rank[i][2]); //Time
            output.close();
        }
    }

    public void paintComponent(Graphics g) {
        try {
            getScores();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LeaderBoard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LeaderBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
        //JOptionPane jp = new JOptionPane();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getHeight(), getWidth());
        g.setColor(Color.WHITE);
        char[] leader = {'l','e','a','d','e','r',' ','b','o','a','r','d'};
        g.drawChars(leader, 0, leader.length, (int)(0.2*getWidth()), (int)(0.5*getHeight()));
        for (int j = 0; j < rank.length; j++) {
            g.drawChars(rank[j][0].toCharArray(), 0, rank[j][0].toCharArray().length, (j+1)*(int)(0.2*getHeight()), (j+1)*(int)(0.5*getWidth()));
            g.drawChars(rank[j][1].toCharArray(), 0, rank[j][1].toCharArray().length, (j+1)*(int)(0.2*getHeight())+10, (j+1)*(int)(0.5*getWidth())+10);
            g.drawChars(rank[j][2].toCharArray(), 0, rank[j][2].toCharArray().length, (j+1)*(int)(0.2*getHeight())+10, (j+1)*(int)(0.5*getWidth())+10);
        }
        System.out.println("LeaderBoard");
    }
}
