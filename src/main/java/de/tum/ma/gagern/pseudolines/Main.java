package de.tum.ma.gagern.pseudolines;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) throws LinearSystemException {
        new Main(args);
    }

    public Main(String... args) throws LinearSystemException {
        JFrame frm = new JFrame("Pseudolines");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(600, 600);
        frm.setLocationByPlatform(true);
        frm.getContentPane().add(new PseudoLineView());
        frm.setVisible(true);
    }

}
