package org.virosms;

import org.virosms.enums.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    State state = State.MENU;



    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startMenu();
    }

    public void startMenu() {
        // Set the game state to MENU
        state = State.MENU;

        // Handle key events for the menu
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (state == State.MENU) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        startGame(); // Start the game when Enter is pressed
                    }
                }
            }
        });

        // Display the menu text
        repaint();
    }

    public void startGame() {
        // Reset game variables
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // Initialize or reset the timer
        if (timer != null) {
            timer.stop();
        }
        // Create a new timer
        timer = new Timer(DELAY, this);
        timer.start();

        // Set the game state to GAME
        state = State.START;

        newApple(); // Create a new apple

        // Remove the key listener used for the menu

        this.requestFocus();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == State.MENU) {
            // Display the game title
            g.setColor(Color.GREEN);
            g.setFont(new Font("Ink Free", Font.BOLD, 80));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Snake Game" , (SCREEN_WIDTH - metrics1.stringWidth("Snake Game")) / 2, g.getFont().getSize());

            // Display the game menu text
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String menuText = "Press Enter to Start";
            g.drawString(menuText, (SCREEN_WIDTH - metrics.stringWidth(menuText)) / 2, SCREEN_HEIGHT / 2);

            // Display the game instructions
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.ITALIC, 20));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            String instructions = "Use arrow keys to move";
            g.drawString(instructions, (SCREEN_WIDTH - metrics2.stringWidth(instructions)) / 2, SCREEN_HEIGHT / 2 + 50);

            //Display the game author
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.ITALIC|Font.BOLD, 20));
            FontMetrics metrics3 = getFontMetrics(g.getFont());
            String author = "Author: VirosMs";
            g.drawString(author, (SCREEN_WIDTH - metrics3.stringWidth(author)) / 2, SCREEN_HEIGHT -100);

        } else if (state == State.START) {
            // Your existing game rendering code
            draw(g);
        } else if (state == State.GAME_OVER) {
            // Your existing game over code
            gameOver(g);
        }
    }

    public void draw(Graphics g) {
        if (running) {
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }*/


            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            paintComponent(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //this checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        //this checks if head touches left border
        if (x[0] < 0) {
            running = false;
        }

        //this checks if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }

        //this checks if head touches top border
        if (y[0] < 0) {
            running = false;
        }

        //this checks if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            state = State.GAME_OVER;
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Score text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == State.START) {
            if (running) {
                move();
                checkApple();
                checkCollisions();
            }
            repaint();
        }
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
            }
        }
    }
}
