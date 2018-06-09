package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import static main.StartScreen.frame;
import static main.StartScreen.ge;
import static main.StartScreen.halogenFont;

public class ControlPanel extends JPanel implements Runnable {

    private final String FILEPATH = "src\\main\\resources\\";
    private Image img = new ImageIcon(FILEPATH + "background.png").getImage();
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final int MAX_X = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int MAX_Y = (new ImageIcon(img)).getIconHeight();
    private final int MOUSE_ALIGNMENT = 100;
    private final int X_ATTACK_ALIGNMENT = 85;
    private final int speed = 3;//background speed

    private Clip musicClip;
    private Clip bossClip;
    private boolean paused = false;
    private boolean leftIsDown = false;
    private boolean rightIsDown = false;
    private Random random = new Random();

    Shooter shooter = new Shooter(800, 800, 15, 0, new ImageIcon(FILEPATH + "L1.png").getImage());
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();//use later
    private ArrayList<EnemyAttack> enemyAttacks = new ArrayList<EnemyAttack>();
    private ArrayList<Attack> attacks = new ArrayList<Attack>();
    private ArrayList<PowerUp> bombs = new ArrayList<PowerUp>();
    private ArrayList<PowerUp> powerups = new ArrayList<PowerUp>();
    private ArrayList<GameObject> removal = new ArrayList<GameObject>();//life changer
    private Boss boss;
    private boolean bossSpawned = false;

    private int enemyType = 0;
    private int playerBombs = 3;
    private int score = 0;
    private int enemyCounter = 0;
    private int position = 0;
    private int position2 = -MAX_Y;
    private boolean run = false;

    public ControlPanel() {
        addKeyListener(new ControlPlayer());
        addMouseListener(new ControlClick());
        addMouseMotionListener(new ControlMouse());
        setFocusable(true);
        playSound(1);
        start();
    }

    public void start() {
        Thread thread = new Thread(this);
        run = true;
        thread.start();
    }

    public void stop() {
        run = false;
    }

    public void run() {
        while (run) {
            position += speed;//moving background
            position2 += speed;
            if (enemyCounter % 50 == 0 && score < 60) {//update this constant to make enemies spawn slower/faster
                int enemySpawn = random.nextInt(10);
                if (enemySpawn >= 2) {//probability of the harder enemy spawning vs the easier one
                    enemies.add(new Enemy(random.nextInt(1800), -100, 0, 5, new ImageIcon(FILEPATH + "enemy" + enemyType + ".png").getImage(), enemyType, shooter));
                } else {
                    enemies.add(new Enemy(random.nextInt(1800), -100, 0, 5, new ImageIcon(FILEPATH + "enemy" + (enemyType + 1) + ".png").getImage(), enemyType + 1, shooter));
                }
            } else if (score >= 60 && !bossSpawned) {
                boss = new Boss(800, -100, 5, 1, new ImageIcon(FILEPATH + "boss.png").getImage(), 4, shooter);
                bossSpawned = true;
                playSound(5);
            }
            enemyCounter++;

            shooter.update(this);

            for (int i = 0; i < enemies.size(); i++) {//enemy attacks
                enemies.get(i).update(this);
                if (enemies.get(i).getAttackCounter() % enemies.get(i).getAttackRate() == 0) {//periodically fire attacks for every enemy
                    switch (enemies.get(i).getEnemyType()) {
                        case 0:
                            enemyAttacks.add(new EnemyAttack(enemies.get(i).getX() + 25, enemies.get(i).getY() + 60, 0, 10, new ImageIcon(FILEPATH + "enemyPew.PNG").getImage()));
                            break;
                        case 1:
                            enemyAttacks.add(new EnemyAttack(enemies.get(i).getX(), enemies.get(i).getY() + 60, 0, 8, new ImageIcon(FILEPATH + "enemyPew2.PNG").getImage()));
                            enemyAttacks.add(new EnemyAttack(enemies.get(i).getX(), enemies.get(i).getY() + 60, 3, 8, new ImageIcon(FILEPATH + "enemyPew2.PNG").getImage()));
                            enemyAttacks.add(new EnemyAttack(enemies.get(i).getX(), enemies.get(i).getY() + 60, -3, 8, new ImageIcon(FILEPATH + "enemyPew2.PNG").getImage()));
                            break;
                        case 2:
                            enemyAttacks.add(new EnemyAttack(enemies.get(i).getX() + 50, enemies.get(i).getY() + 60, 0, 50, new ImageIcon(FILEPATH + "enemyPew3.PNG").getImage()));
                            break;
                    }
                }
                if (enemies.get(i).hit(shooter)) {//more death
                    shooter.setHealth(shooter.getHealth() - 1);
                    if (shooter.getHealth() == 0) {//death
                        endGame(false);
                    }
                    enemyDeath(i);

                }
                if (enemies.get(i).getY() > MAX_Y) {
                    removal.add(enemies.get(i));
                }
            }

            if (bossSpawned) {
                boss.update(this);
                for (int i = 0; i < attacks.size(); i++) {
                    if (attacks.get(i).hit(boss)) {
                        boss.setHealth(boss.getHealth() - attacks.get(i).getDmg());
                        removal.add(attacks.get(i));
                    }
                    if (boss.getHealth() <= 0) {
                        endGame(true); //player wins
                    }
                }
                int t = boss.getT();
                if (boss.getAttackCounter() % boss.getAttackRate() == 0) {
                    if (t > 200 && t < 1400) {
                        enemyAttacks.add(new EnemyAttack(boss.getX() + 175, boss.getY(), 0, 8, new ImageIcon(FILEPATH + "bossPew.PNG").getImage()));
                    } else if (t > 1400 && t < 2400) {
                        enemyAttacks.add(new EnemyAttack(boss.getX() + 175, boss.getY(), -5, 8, new ImageIcon(FILEPATH + "bossPew.PNG").getImage()));
                        enemyAttacks.add(new EnemyAttack(boss.getX() + 175, boss.getY(), 0, 8, new ImageIcon(FILEPATH + "bossPew.PNG").getImage()));
                        enemyAttacks.add(new EnemyAttack(boss.getX() + 175, boss.getY(), 5, 8, new ImageIcon(FILEPATH + "bossPew.PNG").getImage()));
                    } else if (t > 2450 && t < 2600 || t < 2750 && t > 2650) {
                        enemyAttacks.add(new EnemyAttack(boss.getX() + 175, boss.getY(), 0, 40, new ImageIcon(FILEPATH + "bossPew.PNG").getImage()));
                    }
                }
            }
            for (int i = 0; i < enemyAttacks.size(); i++) {
                enemyAttacks.get(i).update(this);
                if (enemyAttacks.get(i).hit(shooter)) {
                    shooter.setHealth(shooter.getHealth() - 1);
                    if (shooter.getHealth() == 0) {// player death
                        endGame(false);
                    }
                    removal.add(enemyAttacks.get(i));
                    playSound(2);
                }
                if (enemyAttacks.get(i).getY() > screenSize.getHeight()) {//remove the object if it goes out of bounds
                    removal.add(enemyAttacks.get(i));
                }
            }

            for (int i = 0; i < bombs.size(); i++) {
                if (bombs.get(i).hit(shooter)) {//getting a bomb
                    removal.add(bombs.get(i));
                    playerBombs++;
                }
                bombs.get(i).update(this);
            }

            for (int i = 0; i < attacks.size(); i++) {//any attack hitting any enemy
                for (int j = 0; j < enemies.size(); j++) {
                    if (attacks.get(i).hit(enemies.get(j))) {
                        removal.add(attacks.get(i));
                        enemies.get(j).setHealth(enemies.get(j).getHealth() - attacks.get(i).getDmg());
                        if (enemies.get(j).getHealth() <= 0) {//enemy death
                            enemyDeath(j);
                        }
                    }
                }
                attacks.get(i).update(this);
                if (attacks.get(i).getY() < 0) {//remove any attacks we don't see anymore
                    removal.add(attacks.get(i));
                }
            }

            for (int i = 0; i < powerups.size(); i++) {
                powerups.get(i).update(this);
                if (powerups.get(i).hit(shooter) && shooter.getPowerup() < 4) {
                    removal.add(powerups.get(i));
                    shooter.setPowerup(shooter.getPowerup() + 1);
                    playSound(4);
                }
                if (powerups.get(i).getY() > MAX_Y) {
                    removal.add(powerups.get(i));
                }
            }

            powerups.removeAll(removal);
            enemyAttacks.removeAll(removal);
            bombs.removeAll(removal);
            attacks.removeAll(removal);
            enemies.removeAll(removal);
            repaint();
            try {
                Thread.sleep(17);
            } catch (Exception exception) {
                System.err.print(exception);
            }
        }
    }

    public void endGame(boolean win) {
        if (bossClip != null) {
            bossClip.stop();
        }
        musicClip.stop();
        stop();
        frame.getContentPane().removeAll();
        frame.removeAll();
        frame.setVisible(false);
        EndScreen endScreen = new EndScreen(score, win);
        frame.repaint();
        frame.revalidate();
    }

    public void drawText(Graphics2D g) {
        try {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(FILEPATH + "halogen-gas-lights.ttf")));
            halogenFont = Font.createFont(Font.TRUETYPE_FONT, new File(FILEPATH + "halogen-gas-lights.ttf"));
        } catch (IOException | FontFormatException e) {
            System.err.print(e);
        }
        g.setFont(halogenFont.deriveFont(30f));
        g.drawString("Score: " + Integer.toString((score - enemyType)), 50, 50);//counts for incrementing the score earlier
        g.drawString("Bombs: " + Integer.toString(playerBombs), MAX_X - 200, 100);
        g.drawString("Health: " + Integer.toString(shooter.getHealth()), MAX_X - 200, 50);
        if (bossSpawned) {
            g.drawString("BOSS HEALTH: " + Integer.toString(boss.getHealth()), MAX_X - 300, 1000);
        }
        if (paused) {
            g.drawString("PAUSED", 1735, 150);
        }
    }

    public void enemyDeath(int j) {
        int powerup = random.nextInt(12);
        if (powerup == 0) {
            bombs.add(new PowerUp(enemies.get(j).getX(), enemies.get(j).getY(), 0, 2, new ImageIcon(FILEPATH + "bomb.png").getImage()));
        } else if (powerup == 1) {
            powerups.add(new PowerUp(enemies.get(j).getX(), enemies.get(j).getY(), 0, 2, new ImageIcon(FILEPATH + "powerup.png").getImage()));
        }
        removal.add(enemies.get(j));
        score++;
        playSound(2);
        if (score % 20 == 0 && score <= 60 && score != 0) {
            score++;//makes sure this block only goes off once, handles consequence of this later
            enemyType++;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, position, null);
        g.drawImage(img, 0, position2, null);
        shooter.paintComponent(g);
        drawText((Graphics2D) g);

        for (int i = 0; i < attacks.size(); i++) {
            attacks.get(i).paintComponent(g);
        }
        for (int i = 0; i < enemies.size(); i++) {//using a for each loop will raise exceptions, idk why
            enemies.get(i).paintComponent(g);
        }
        for (int i = 0; i < bombs.size(); i++) {
            bombs.get(i).paintComponent(g);
        }
        for (int i = 0; i < enemyAttacks.size(); i++) {
            enemyAttacks.get(i).paintComponent(g);
        }
        for (int i = 0; i < powerups.size(); i++) {
            powerups.get(i).paintComponent(g);
        }

        if (bossSpawned) {
            boss.paintComponent(g);
        }

        //background
        if (position > MAX_Y) {//loop the background pic over and over
            position = -MAX_Y + 10;
        } else if (position2 > MAX_Y) {
            position2 = -MAX_Y + 10;
        }
    }

    public void playSound(int sound) {
        String file = "";

        switch (sound) {
            case 0:
                file = "attack";
                break;
            case 1:
                file = "music";
                break;
            case 2:
                file = "crash";//not in use atm
                break;
            case 3:
                file = "play";
                break;
            case 4:
                file = "powerup";
                break;
            case 5:
                file = "boss";
        }
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(FILEPATH + file + ".wav"));
            if (sound == 1) {
                musicClip = AudioSystem.getClip();
                musicClip.open(audioIn);
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else if (sound != 5) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
                musicClip.stop();
                bossClip = AudioSystem.getClip();
                bossClip.open(audioIn);
                bossClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
        }
    }

    public void shooterAttack() {
        switch (shooter.getPowerup()) {
            case 0:
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 0, 20, new ImageIcon(FILEPATH + "bullet0.png").getImage(), 1));
                break;
            case 1:
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 0, 20, new ImageIcon(FILEPATH + "bullet2.png").getImage(), 2));
                break;
            case 2:
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 0, 20, new ImageIcon(FILEPATH + "bullet0.png").getImage(), 1));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), -10, 20, new ImageIcon(FILEPATH + "bullet3.png").getImage(), 1));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 10, 20, new ImageIcon(FILEPATH + "bullet1.png").getImage(), 1));
                break;
            case 3:
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT - 50, shooter.getY(), 0, 20, new ImageIcon(FILEPATH + "bullet2.png").getImage(), 2));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT - 50, shooter.getY(), -10, 20, new ImageIcon(FILEPATH + "bullet2.png").getImage(), 2));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT - 50, shooter.getY(), 10, 20, new ImageIcon(FILEPATH + "bullet2.png").getImage(), 2));
                break;
            case 4:
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 0, 20, new ImageIcon(FILEPATH + "bullet0.png").getImage(), 1));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), -10, 20, new ImageIcon(FILEPATH + "bullet3.png").getImage(), 1));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 10, 20, new ImageIcon(FILEPATH + "bullet1.png").getImage(), 1));
                attacks.add(new Attack(shooter.getX() + X_ATTACK_ALIGNMENT, shooter.getY(), 0, 20, new ImageIcon(FILEPATH + "bullet2.png").getImage(), 1, true, enemies));
                break;
        }
        playSound(0);
    }

    public void shooterBomb() {
        playerBombs--;
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setHealth(enemies.get(i).getHealth() - 3);
            if (enemies.get(i).getHealth() <= 0) {//enemy death
                enemyDeath(i);
            }
        }
        if (bossSpawned) {
            boss.setHealth(boss.getHealth() - 3);
        }
        playSound(2);
    }

    private class ControlPlayer implements KeyListener {

        long lastPressProcessed = 0;

        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    shooter.moveLeft();
                    leftIsDown = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    shooter.moveRight();
                    rightIsDown = true;
                    break;
                case KeyEvent.VK_SPACE:
                    if (System.currentTimeMillis() - lastPressProcessed > 150) {
                        shooterAttack();
                        lastPressProcessed = System.currentTimeMillis();
                    }
            }
            if (event.getKeyCode() == KeyEvent.VK_B && playerBombs > 0) {
                shooterBomb();
            } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE && !paused) {
                stop();
                paused = true;

            } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE && paused) {
                start();
                paused = false;
            }
        }

        public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_LEFT) {//this stuff allows for smoother movement
                leftIsDown = false;
            } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightIsDown = false;
            }
            if (!(leftIsDown || rightIsDown)) {
                shooter.stop();
                shooter.setImage(new ImageIcon(FILEPATH + "L1.png").getImage());
            }
        }

        public void keyTyped(KeyEvent event) {
            //nothing
        }
    }

    private class ControlClick implements MouseListener {

        long lastPressProcessed = 0;

        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                if (System.currentTimeMillis() - lastPressProcessed > 150) {
                    shooterAttack();
                    lastPressProcessed = System.currentTimeMillis();
                }
            } else if (event.getButton() == MouseEvent.BUTTON3 && playerBombs > 0) {
                shooterBomb();
            }
        }

        public void mousePressed(MouseEvent event) {
            //nothing
        }

        public void mouseReleased(MouseEvent event) {
            //nothing
        }

        public void mouseEntered(MouseEvent event) {
            //nothing
        }

        public void mouseExited(MouseEvent event) {
            //nothing
        }
    }

    private class ControlMouse implements MouseMotionListener {

        public void mouseMoved(MouseEvent event) {
            if (event.getX() - MOUSE_ALIGNMENT < shooter.getX()) {
                shooter.setImage(new ImageIcon(FILEPATH + "L7.png").getImage());
            } else if (event.getX() - MOUSE_ALIGNMENT > shooter.getX()) {
                shooter.setImage(new ImageIcon(FILEPATH + "L7O.png").getImage());
            } else if (event.getX() - MOUSE_ALIGNMENT == shooter.getX()) {
                shooter.setImage(new ImageIcon(FILEPATH + "L1.png").getImage());//TODO figure this out
            }
            shooter.setX(event.getX() - MOUSE_ALIGNMENT);
        }

        public void mouseDragged(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                shooterAttack();
            }
        }
    }
}
