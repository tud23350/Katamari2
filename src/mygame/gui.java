/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class gui extends JFrame implements ActionListener {

    long startTime = System.currentTimeMillis();
    int wincounter = 0;
    String s;
    Main main;
    boolean changed;

    public gui(Main main) {
        this.main = main;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //3
        this.setLayout(new GridLayout(3, 1));
        this.setPreferredSize(new Dimension(300, 300));
        //4
        JLabel jt = new JLabel("Here is my simple Gui");
        add(jt);
        JButton jb2 = new JButton("Initalize Environment");
        add(jb2);
        JButton jb3 = new JButton("Play Game");
        add(jb3);
        JButton jb4 = new JButton("Take Picture");
        add(jb4);

        //5 Add Functionality

        jb2.addActionListener(this);
        jb3.addActionListener(this);
        jb4.addActionListener(this);

        //6
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        JButton b = (JButton) e.getSource();
        s = b.getText();
        changed = true;

    }

    boolean getchanged() {
        return (changed);
    }

    void resetchanged() {
        changed = false;
    }
}
