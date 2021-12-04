package com.company.setReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Main extends JPanel {
    public static void main(String[] args) {
        // write your code here
        JFrame frame = new JFrame("gameOfLife");
        JPanel panel = new gameOfLife() {
            @Override
            public void mouseEntered (MouseEvent event){}
        };
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocation(200, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private int rows;
    private int columns;
    private Color backgroundColor;
    private Color cellColor;
    private Color[][] grid;
    private boolean DrawCellColor;
    private boolean needsRedraw;
    private boolean autopaint = true;
    private boolean use3D = true;
    private Graphics OSG;
    private BufferedImage OSI;

    public Main(int row, int column, int length, int height) {
        this(row, column, length, height, null, 0);
        //constructor
    }

    public Main(int width, int height, int horLines, int verLines, Color heightcolor, int edgeofWidth) {
        this.rows = horLines;
        this.columns = verLines;
        grid = new Color[rows][columns];
        Color cells = new Color(225, 0, 0);
        DrawCellColor = false;
        JPanel backgroundColor = new JPanel();
        backgroundColor.setBackground(new Color(0, 0, 0));

        if (heightcolor != null) {
            if (edgeofWidth < 1) {
                edgeofWidth = 1;
                setBorder(BorderFactory.createLineBorder(heightcolor, edgeofWidth));
            } else
                edgeofWidth = 0;
        }
    }

    public void setUse3D(boolean use3D) {this.use3D = use3D;}
    public boolean getUse3D() {return use3D;}
    public void cellWindow(int rows, int columns, boolean source) {
        if (rows > 0 && columns > 0) {
            Color[][] newGrid = new Color[rows][columns];
            if (source) {
                int r = Math.min(rows, this.rows);
                int c = Math.min(columns, this.columns);
                for (int i = 0; i < r; i++) {
                    for (int j = 0; j < c; j++)
                        newGrid[i][j] = grid[i][j];
                }
                grid = newGrid;
                this.rows = rows;
                this.columns = columns;
                forceRedraw();
            }
        }
    }

    public void GridSize(int rows, int columns, boolean source) {
        if (rows > 0 && columns > 0) {
            Color[][] newGrid = new Color[rows][columns];
            if (source) {
                int r = Math.min(rows, this.rows);
                int c = Math.min(columns, this.columns);
                for (int i = 0; i < r; i++)
                    for (int j = 0; j < c; j++)
                        newGrid[i][j] = grid[i][j];
            }
            grid = newGrid;
            this.rows = rows;
            this.columns = columns;
            forceRedraw();
        }
    }

    public int getNumR() {return rows;}
    public int getNumC() {return columns;}
    public void setColorFill(int i, int j, Color c) {
        if (i >= 0 && i < rows && j >= 0 && j < columns) {
            grid[i][j] = c;
            if (OSI == null) {
                repaint();
            } else {
                drawWindow(i, j, true);
            }
        }
    }
    private Color getColorFill(int i, int j) {
        if (i >= 0 && j < rows && j >= 0 && j < columns) {
            return grid[i][j];
        } else {
            return null;
        }
    }

    public void fillColor(Color c) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                grid[i][j] = c;
        forceRedraw();
    }

    public void clear() { fillColor(new Color(0, 0, 0));} //clearpage to black
    public boolean getAutopaint() {return autopaint;}
    public void setAutopaint(boolean autopaint) {
        if (this.autopaint == autopaint)
            return;
        this.autopaint = autopaint;
        if (autopaint)
            forceRedraw();
    }
    //paint and repaint page after clear

    final public void forceRedraw() {needsRedraw = true;
        repaint();}

    public int ColumnPosition(int c) {
        Insets i = getInsets();
        //getBorders
        if (c < i.right) {
            return -1;
        }
        double columnSize = (getWidth() - i.left - i.right) / columns;
        int C = (int) ((c - i.left) / columnSize);
        if (C >= columns) {
            return columns;
        } else {
            return C;
        }
    }//https://www.codota.com/code/java/methods/java.awt.Insets/%3Cinit%3E

    public int rowPosition(int r) {
        Insets i = getInsets();
        if (r < i.left)
            return -1;
        double rowSize = (getHeight() - i.top - i.bottom) / rows;
        int R = (int) ((r - i.top) / rowSize);
        if (R >= rows) {
            return rows;
        } else {
            return R;
        }
    }

    public BufferedImage getImage() {
        return OSI;
    }
    //constructor. https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferedImage.html

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        synchronized (this) {
            if ((OSI == null) || OSI.getWidth() != getWidth() || OSI.getHeight() != getHeight()) {
                OSI = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_BGR);
                OSG = OSI.getGraphics();
                needsRedraw = true;
            }
        }
        if (needsRedraw) {
            for (int m = 0; m < rows; m++)
                for (int n = 0; n < columns; n++)
                    drawWindow(m, n, false);
            needsRedraw = false;
        }
        graphics.drawImage(OSI, 0, 0, null);
    }

    private void drawWindow(int m, int n, boolean paintWindow) {
        double leftToRight;
        double topToDown;
        if (paintWindow && !autopaint)
            return;
        Insets i = getInsets();
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/GridPane.html
        leftToRight = (double) (getHeight() - i.left - i.right) / rows;
        int l = i.left;
        topToDown = (double) (getWidth() - i.top - i.bottom) / columns;
        int t = i.top;
        int width1 = t + (int) Math.round(leftToRight * m);

        int width2 = Math.max(1, (int) Math.round(leftToRight * (m + 1)) + t - width1);
        int height1 = l + (int) Math.round(topToDown * n);
        int height2 = Math.max(1, (int) Math.round(topToDown * (n + 1)) + l - height1);
        Color c = grid[m][n];
        OSG.setColor((c == null) ? backgroundColor : c);
        if (cellColor == null || (c == null && !DrawCellColor)) {
            if (!use3D || c == null)
                OSG.fillRect(height1, width1, height2, width2);
            else
                OSG.fill3DRect(height1, width1, height2, width2, true);

        }
    }
}
