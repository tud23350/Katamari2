/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class LeaderBoardClassic extends JPanel {

    int sizeX, sizeY, score;
    long time;
    String[][] rank = new String[3][3];

    public LeaderBoardClassic(int sx, int sy, int score, long time) {
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
            k++;
        }
        fileInput.close();
        fis.close();
    }

    public void putScores() throws FileNotFoundException, IOException {
        getScores();
        File data = new File("Kinect_Data/LeaderBoardClassic.txt");
        PrintWriter output = new PrintWriter(data);
        int place = rank.length;
        for (int i = 0; i < place; i++) {
            String name = "";
            if (time >= Integer.parseInt(rank[i][2])) {
                name = JOptionPane.showInputDialog(null, "HighScore!!");
                if (i == 0) {
                    for (int d = 0; d < rank[0].length; d++) {
                        rank[i + 2][d] = rank[i + 1][d];
                        rank[i + 1][d] = rank[i][d];
                    }
                } else if (i == 1) {
                    for (int d = 0; d < rank[0].length; d++) {
                        rank[i + 1][d] = rank[i][d];
                    }
                }
                rank[i][0] = name;
                rank[i][1] = Integer.toString(score);
                rank[i][2] = Long.toString(time);
                score = (int) Double.NEGATIVE_INFINITY;
            }
            output.println(rank[i][0]); //name
            output.println(rank[i][1]); //Score
            output.println(rank[i][2]); //Time
        }
        output.close();
    }

    public void paintComponent(Graphics g) {
        try {
            putScores();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(mygame.LeaderBoard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mygame.LeaderBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getHeight(), getWidth());
        g.setColor(Color.WHITE);
        int spacing = 10;
        char[] leader = {'L', 'E', 'A', 'D', 'E', 'R', 'B', 'O', 'A', 'R', 'D', ' ', 'C', 'L', 'A', 'S', 'S', 'I', 'C'};
        g.drawChars(leader, 0, leader.length, (int) (0.2 * getWidth()), spacing);
        char[] score = {'s', 'c', 'o', 'r', 'e', ':'};
        char[] time = {'t', 'i', 'm', 'e', ':'};
        for (int j = 0; j < rank.length; j++) {
            g.drawChars(rank[j][0].toCharArray(), 0, rank[j][0].toCharArray().length, 5, spacing += 20);
            g.drawChars(score, 0, score.length, 15, spacing + 20);
            g.drawChars(rank[j][1].toCharArray(), 0, rank[j][1].toCharArray().length, 15 + score.length * score.length, spacing += 20);
            g.drawChars(time, 0, time.length, 15, spacing + 20);
            g.drawChars(rank[j][2].toCharArray(), 0, rank[j][2].toCharArray().length, 18 + time.length * time.length, spacing += 20);
        }
        System.out.println("LeaderBoard");
    }
}