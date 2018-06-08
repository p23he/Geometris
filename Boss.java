package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private Shooter shooter;
    private int health;
    private int t = 0;
    private int speed;
    private int attackCounter = 0;
    private int attackRate = 50;

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int MAX_X = screenSize.width - 100;

    public Boss(int x, int y, int xSpeed, int ySpeed, Image img, int enemyType, Shooter shooter) {
        super(x, y, xSpeed, ySpeed, img, enemyType, shooter);
        this.shooter = shooter;
        speed = xSpeed;
        health = 50;
    }

    public void paintComponent(Graphics g) {
        g.drawImage(img, x, y, null);
    }

    public int getT() {
        return t;
    }

    public int getAttackCounter() {
        return attackCounter;
    }

    public int getAttackRate() {
        return attackRate;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }

    public void update(ControlPanel background) {
        if (t < 200) {//slowly move down
            y += ySpeed;
        } else if (t < 1400) {//move left to right shooting straight bullets
            attackRate = 30;
            if (x + xSpeed >= MAX_X - 250 || x + xSpeed < 0) {//left and right
                xSpeed *= -1;
            }
            x += xSpeed;
        } else if (t < 2400) {//move a little left to right spread shot
            if (x + xSpeed >= MAX_X || x + xSpeed < 0) {//left and right
                xSpeed *= -1;
            }
            x += xSpeed;
        } else if (t < 2450) {//move quickly to the left
            attackRate = 10;
            double d = Math.sqrt(Math.pow((double) (shooter.getX() - x), 2) + Math.pow((double) (shooter.getY() - y), 2));
            xSpeed = (shooter.getX() - x) * 20 / d;
            x += xSpeed;
        } else if (t < 2600) {//pause for a bit
            //do nothing
        } else if (t < 2650) {//move quickly to the right
            double d = Math.sqrt(Math.pow((double) (shooter.getX() - x), 2) + Math.pow((double) (shooter.getY() - y), 2));
            xSpeed = (shooter.getX() - x) * 20 / d;
            x += xSpeed;
        } else if (t < 2750) {//pause and shoot
            //do nothing
        } else if (t > 2750) {
            xSpeed = speed;
            t = 200;
        }
        t++;
        attackCounter++;
        hitbox = new Rectangle2D.Double(x, y, new ImageIcon(img).getIconWidth(), new ImageIcon(img).getIconHeight());
    }

}
