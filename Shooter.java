package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class Shooter extends GameObject{
    
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int MAX_X = screenSize.width - 100;
    private final int MAX_Y = screenSize.height;
    private final String FILEPATH = "src\\main\\resources\\";
    private final int hitboxX = 35;
    private final int hitboxY = 30;
    private final int hitboxW = -80;
    private final int hitboxH = -80;
    private int speed;
    private int powerup = 0;
    private int health = 3;
    
    public Shooter(int x, int y, int xSpeed, int ySpeed, Image img) {
        super(x, y, xSpeed, ySpeed, img);
        this.health = health;
        speed = xSpeed;
        stop();
    }
    
    public int getX() {
        return x;//TODO figure out why i have to override
    }
    
    public int getY() {
        return y;
    }
    
    public int getPowerup() {
        return powerup;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setPowerup(int powerup) {
        this.powerup = powerup;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public void stop() {
        xSpeed = 0;
        img = new ImageIcon(FILEPATH + "L1.png").getImage();
    }
    
    public void moveRight() {
        xSpeed = speed;
        img = new ImageIcon(FILEPATH + "L7O.png").getImage();
    }
    
    public void moveLeft() {
        xSpeed = -speed;
        img = new ImageIcon(FILEPATH + "L7.png").getImage();
    }
    
    public void paintComponent(Graphics g) {
        g.drawImage(img, x, y, null);
        g.setColor(Color.RED);
        //((Graphics2D)g).draw(hitbox);
    }
    
    public void update(ControlPanel background) {        
        if (x + xSpeed >= MAX_X || x + xSpeed < 0) {//left and right
            x -= xSpeed;
        } else {
            x += xSpeed;
        }
        hitbox = new Rectangle2D.Double(x + hitboxX, y + hitboxY, new ImageIcon(img).getIconWidth() + hitboxW, new ImageIcon(img).getIconHeight() + hitboxH);
    }
}
