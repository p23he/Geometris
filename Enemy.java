package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class Enemy extends GameObject {

    private int hitboxX = 0;
    private int hitboxY = 0;
    private int hitboxW = 0;
    private int hitboxH = 0;
    private int health;
    private int enemyType;
    private int attackCounter = -20;
    private int attackRate = 100;
    private Shooter shooter;

    public Enemy(int x, int y, double xSpeed, double ySpeed, Image img, int enemyType, Shooter shooter) {
        super(x, y, xSpeed, ySpeed, img);
        this.enemyType = enemyType;
        this.shooter = shooter;
        switch (enemyType) {
            case 0:
            case 1:
                health = 3;
                ySpeed = 5;
                xSpeed = 0;
                break;
            case 2:
                health = 2;
                ySpeed = 5;
                xSpeed = 0;
                break;
            case 3:
                health = 2;
                break;
        }
        if (enemyType == 2) {
            attackCounter = 40;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttackCounter() {
        return attackCounter;
    }

    public int getAttackRate() {
        return attackRate;
    }

    public int getEnemyType() {
        return enemyType;
    }

    public boolean hit(Shooter shooter) {
        return hitbox.intersects(shooter.getHitbox());
    }

    public void paintComponent(Graphics g) {
        g.drawImage(img, x, y, null);
    }

    public void update(ControlPanel background) {
        attackCounter++;
        switch (enemyType) {
            case 0:
                hitboxX = 20;
                hitboxY = 15;
                hitboxW = -40;
                hitboxH = -50;
                break;
            case 3:
                double d = Math.sqrt(Math.pow((double) (shooter.getX() - x), 2) + Math.pow((double) (shooter.getY() - y), 2));
                xSpeed = (shooter.getX() - x) * 5 / d;
                break;
        }
        x += xSpeed;
        y += ySpeed;
        hitbox = new Rectangle2D.Double(x + hitboxX, y + hitboxY, new ImageIcon(img).getIconWidth() + hitboxW, new ImageIcon(img).getIconHeight() + hitboxH);
    }
}
