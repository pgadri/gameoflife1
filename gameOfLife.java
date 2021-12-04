//Name: Pericles Gadri
//ID: 31528054
//Project 3_Final Project
package com.company.setReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class gameOfLife extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private final int GRIDSIZE = 50; // Size of grid: 50 x 50
    private boolean[][] activeCell; // shows active cells when true
    private Main display; // main display panel. Shows alive or dead cells base on clustering.
    private Timer timer; // timer for program
    private JButton stopGoButton; //Start/Stop button
    private JButton randomizeButton; //randomize cells
    private JButton quitButton; // exit window when activated
    private JButton pauseButton; //freeze program bound by time limit of the timer
    private JButton fillButton; //randomly populate grid with cells.
    private JButton resetButton; //resets window.

    public void mouseEntered(MouseEvent event) {} // activated when mouse exists a component
    public void mouseClicked(MouseEvent event) {} //activate when mouse is clicked on any component
    public void mouseReleased(MouseEvent event) {} //activates when mouse enters a component
    public void mouseMoved(MouseEvent event) {}
    public void mouseDragged(MouseEvent event) {mouseClicked(event);}
    public void mouseExited(MouseEvent event) {}

    //Buttons on panel and frame.
    public gameOfLife() {
        activeCell = new boolean[GRIDSIZE][GRIDSIZE];
        setLayout(new BorderLayout(5, 5));
        int indivCell = 500 / GRIDSIZE;
        display = new Main(GRIDSIZE, GRIDSIZE, indivCell, indivCell);
        if (indivCell < 3)
            display.setUse3D(false);
        add(display, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        add(bottom, BorderLayout.SOUTH);
        stopGoButton = new JButton("Start");
        quitButton = new JButton("Quit");
        randomizeButton = new JButton("Randomize");
        fillButton = new JButton("Fill");
        resetButton = new JButton("Reset");
        pauseButton = new JButton("Pause");
        bottom.add(resetButton);
        bottom.add(stopGoButton);
        bottom.add(randomizeButton);
        bottom.add(fillButton);
        bottom.add(quitButton);
        bottom.add(pauseButton);
        stopGoButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resetButton.addActionListener(this);
        quitButton.addActionListener(this);
        fillButton.addActionListener(this);
        randomizeButton.addActionListener(this);
        display.addMouseListener(this);
        display.addMouseMotionListener(this);
        timer = new Timer(15, this);
    }

    private void Window() {
        boolean[][] programWindow = new boolean[GRIDSIZE][GRIDSIZE];
        for (int row = 0; row < GRIDSIZE; row++) {
            int rowUp, rowDown, rowLeft, rowrRight;
            rowUp = row > 0 ? row - 1 : GRIDSIZE - 1;
            rowDown = row < GRIDSIZE - 1 ? row + 1 : 0;
            for (int column = 0; column < GRIDSIZE; column++) {
                rowLeft = column > 0 ? column - 1 : GRIDSIZE - 1;
                rowrRight = column < GRIDSIZE - 1 ? column + 1 : 0;
                int n = 0;
                if (activeCell[rowUp][rowLeft]) n++;
                if (activeCell[rowUp][column]) n++;
                if (activeCell[rowUp][rowrRight]) n++;
                if (activeCell[row][rowLeft]) n++;
                if (activeCell[row][rowrRight]) n++;
                if (activeCell[rowDown][rowLeft]) n++;
                if (activeCell[rowDown][column]) n++;
                if (activeCell[rowDown][rowrRight]) n++;
                if (n == 3 || (activeCell[row][column] && n == 2))
                    programWindow[row][column] = true;
                else
                    programWindow[row][column] = false;
            }
        }
        activeCell = programWindow;
    }
    private void programWindow() {
        display.setAutopaint(false);
        for (int row = 0; row < GRIDSIZE; row++) {
            for (int column = 0; column < GRIDSIZE; column++) {
                if (activeCell[row][column])
                    display.setColorFill(row, column, new Color(255, 165, 0));
                else
                    display.setColorFill(row, column, new Color(0, 0, 0));
            }
        }
        display.setAutopaint(true);
    }

    public void mousePressed(MouseEvent e) {
        if (timer.isRunning()) {
            return;
        }
        int row = display.ColumnPosition(e.getX());
        int column = display.rowPosition(e.getY());

        if (row >= 0 && row < display.getNumR() && column >= 0 && column < display.getNumC()) {
            if (e.isMetaDown() || e.isControlDown()) {
                display.setColorFill(row, column, Color.RED);
                activeCell[row][column] = false;
            } else {
                display.setColorFill(row, column, Color.WHITE);
                activeCell[row][column] = true;
            }
        }
    }

    //action event listener
    //listens to mouse events
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == quitButton) {
            System.exit(0);
        } else if (source == resetButton) {
            activeCell = new boolean[GRIDSIZE][GRIDSIZE];
            display.clear();
        } else if (source == randomizeButton) {
            Window();
            programWindow();
        } else if (source == pauseButton) {
            if (timer.isRunning()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        } else if (source == stopGoButton) {
            if (timer.isRunning()) {
                timer.stop();
                resetButton.setEnabled(true);
                fillButton.setEnabled(true);
                randomizeButton.setEnabled(true);
                stopGoButton.setText("Start");//
                pauseButton.setEnabled(true);
            } else {
                timer.start();
                resetButton.setEnabled(false);
                fillButton.setEnabled(false);
                randomizeButton.setEnabled(false);
                stopGoButton.setText("Stop");
                pauseButton.setEnabled(true);
            }
        } else if (source == fillButton) {
            // Fill the board randomly.
            for (int row = 0; row < GRIDSIZE; row++) {
                for (int column = 0; column < GRIDSIZE; column++)
                    activeCell[row][column] = (Math.random() < 0.50);
            }
            programWindow();
        } else if (source == timer) {
            Window();
            programWindow();
        }
    }
}


