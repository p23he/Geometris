package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import java.awt.event.KeyEvent;

public class Player extends JPanel implements Runnable {

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int MAX_X = screenSize.width - 100;
    private final int MAX_Y = screenSize.height;
    private boolean run = false;
    int x = MAX_X / 2; //start position
    int y = MAX_Y / 2;
    int xSpeed = 20;

    public Player() {

        JFrame frame = new JFrame("Shooter");

        //set frame to fill whole screen   
        setPreferredSize(screenSize);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.addKeyListener(new KeyReleased());

    }

    public void start() {
        System.out.println("Thread started");
        Thread thread = new Thread(this);
        run = true;
        thread.start();
    }

    public void stop() {
        run = false;
    }

    public void run() {

    }


    public void left() {
        if (x <= 0) {
            x += xSpeed;
        } else {
            x -= xSpeed;
        }
        repaint();
        try {
            Thread.sleep(17);
        } catch (Exception e) {
        }

    }

    public void right() {
        if (x >= MAX_X + 10) {
            x -= xSpeed;
        } else {
            x += xSpeed;
        }
        repaint();
        try {
            Thread.sleep(2);
        } catch (Exception e) {
        }
    }

    public void paintComponent(Graphics g) {
        Ellipse2D circle = new Ellipse2D.Double(x, y, 100, 100);
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(circle);
        g2.draw(circle);
    }

    public class KeyReleased implements KeyListener {

        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                left();
                System.out.println(x);
                // System.out.println(MAX_X);
            } else if (key == KeyEvent.VK_RIGHT) {
                right();
                System.out.println(x);
            }

        }

        public void keyReleased(KeyEvent e) {

        }

        public void keyTyped(KeyEvent e) {

        }
        
        
    }

    public static void main(String[] args) {
        Player p = new Player();
    }
}