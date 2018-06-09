package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public abstract class GameObject {

    protected int x;
    protected int y;
    protected double xSpeed;
    protected double ySpeed;
    protected Image img;
    protected Rectangle2D hitbox;

    public GameObject(int x, int y, double xSpeed, double ySpeed, Image img) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.img = img;
        hitbox = new Rectangle2D.Double(x, y, new ImageIcon(img).getIconWidth(), new ImageIcon(img).getIconHeight());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle2D getHitbox() {
        return hitbox;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public abstract void update(ControlPanel background);

    public abstract void paintComponent(Graphics g);
}
