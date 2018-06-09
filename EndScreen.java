package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class EndScreen {

    public final String FILEPATH = "src\\main\\resources\\";
    public static GraphicsEnvironment ge = null;
    public static Font halogenFont = null;
    public static JFrame frame = new JFrame();
    private ImagePanel panel = new ImagePanel(new ImageIcon(FILEPATH + "MenuBackground.png").getImage());

    public EndScreen(int score, boolean win) {
        int MAX_X = Toolkit.getDefaultToolkit().getScreenSize().width;
        int MAX_Y = Toolkit.getDefaultToolkit().getScreenSize().height;

        //retrieve font file
        try {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(FILEPATH + "halogen-gas-lights.ttf")));
            halogenFont = Font.createFont(Font.TRUETYPE_FONT, new File(FILEPATH + "halogen-gas-lights.ttf"));
        } catch (IOException | FontFormatException e) {
            System.err.print(e);
        }

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setFocusable(true);
        frame.setSize(MAX_X, MAX_Y);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.getContentPane().add(panel);

        frame.pack();

        JButton startBtn = win ? new JButton("Play again") : new JButton("Try again");
        JButton exitBtn = new JButton("Quit");
        JLabel displayScore = new JLabel("Score   " + score * 300);
        JLabel title = win ? new JLabel("YOU WIN!") : new JLabel("GAME OVER!");

        startBtn.setAlignmentX(ImagePanel.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(ImagePanel.CENTER_ALIGNMENT);
        title.setAlignmentX(ImagePanel.CENTER_ALIGNMENT);
        displayScore.setAlignmentX(ImagePanel.CENTER_ALIGNMENT);

        startBtn.addActionListener(new Interactions());
        exitBtn.addActionListener(new Interactions());

        startBtn.setActionCommand("play");
        exitBtn.setActionCommand("exit");

        styleButton(startBtn);
        startBtn.setFont(halogenFont.deriveFont(60f));
        styleButton(exitBtn);
        exitBtn.setFont(halogenFont.deriveFont(40f));

        title.setFont(halogenFont.deriveFont(125f));
        title.setForeground(Color.decode("#fffff"));
        displayScore.setFont(halogenFont.deriveFont(60f));
        displayScore.setForeground(Color.decode("#fffff"));

        panel.add(Box.createRigidArea(new Dimension(100, 200)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(MAX_X, 75)));
        panel.add(displayScore);
        panel.add(Box.createRigidArea(new Dimension(MAX_X, 100)));
        panel.add(startBtn);
        panel.add(Box.createRigidArea(new Dimension(MAX_X, 50)));
        panel.add(exitBtn);

        frame.setVisible(true);
    }

    public static void styleButton(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.decode("#2B91AF"));
    }

    class ImagePanel extends JPanel implements ActionListener {

        private Image img;
        Timer timer = new Timer(10, this);
        private float alpha = 0f;

        public ImagePanel(Image img) {
            this.img = img;
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setPreferredSize(screenSize);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setLayout(null);
            timer.start();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            alpha += 0.01f;
            if (alpha >= 1) {
                alpha = 1f;
                timer.stop();
            }
            repaint();
        }
    }

    public static void main(String[] args) {
        StartScreen startScreen = new StartScreen();
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
        }
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(FILEPATH + file + ".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            if (sound == 1) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (Exception e) {
        }
    }

    class Interactions implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String event = e.getActionCommand();
            if (event.equals("play")) {
                ControlPanel controlPanel = new ControlPanel();
                frame.getContentPane().removeAll();
                frame.getContentPane().add(controlPanel);
                frame.revalidate();
                controlPanel.requestFocus();
                playSound(3);
            } else if (event.equals("exit")) {
                System.exit(1);
            }
        }
    }
}
