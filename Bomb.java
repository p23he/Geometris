package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class Bomb extends PowerUp {
    
    public Bomb (int x, int y, double xSpeed, double ySpeed, Image img) {
        super(x, y, xSpeed, ySpeed, img);
    }
    
    public boolean hit(Shooter shooter) {
        return hitbox.intersects(shooter.getHitbox());
    }
    
    public void paintComponent(Graphics g) {
        g.drawImage(img, x, y, null);
    }
    
    public void update(ControlPanel background) {
        y += ySpeed;
        hitbox = new Rectangle2D.Double(x, y, new ImageIcon(img).getIconWidth(), new ImageIcon(img).getIconHeight());
    }
    
}
