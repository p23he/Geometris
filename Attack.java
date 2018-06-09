package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Attack extends GameObject {

    private int dmg;
    private boolean homing;
    private ArrayList<Enemy> enemies;

    public Attack(int x, int y, int xSpeed, int ySpeed, Image img, int dmg) {
        super(x, y, xSpeed, ySpeed, img);
        this.dmg = dmg;
    }

    public Attack(int x, int y, int xSpeed, int ySpeed, Image img, int dmg, boolean homing, ArrayList<Enemy> enemies) {
        super(x, y, xSpeed, ySpeed, img);
        this.dmg = dmg;
        this.homing = homing;
        this.enemies = enemies;
    }

    public int getDmg() {
        return dmg;
    }

    public boolean hit(Enemy enemy) {
        return hitbox.intersects(enemy.getHitbox());
    }

    public boolean hit(Boss boss) {
        return hitbox.intersects(boss.getHitbox());
    }

    public void paintComponent(Graphics g) {
        g.drawImage(img, x, y, null);
        g.setColor(Color.RED);
    }

    public void update(ControlPanel background) {
        if (homing) {
            int smallest = -1;
            double smallestD = 9999;

            smallest = (enemies.size() - 1) / 4;
            if (smallest < enemies.size()) {
                smallestD = Math.sqrt(Math.pow((double) (x - enemies.get(smallest).getX()), 2) + Math.pow((double) (y - enemies.get(smallest).getY()), 2));
                xSpeed = (enemies.get(smallest).getX() - x) * 10 / smallestD;
                ySpeed = (enemies.get(smallest).getY() - y) * 10 / smallestD;
            }
        }
        x += xSpeed;
        y -= ySpeed;
        hitbox = new Rectangle2D.Double(x, y, new ImageIcon(img).getIconWidth(), new ImageIcon(img).getIconHeight());
    }
}
