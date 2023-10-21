package org.virosms;

import org.virosms.enums.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * The GamePanel class is responsible for the game logic and rendering.
 * <p> It extends JPanel and implements ActionListener. </p>
 * <p> It contains the game loop and the logic for the snake movement, apple spawning, collision detection, and score tracking. </p>
 * <p> It also contains the logic for the game menu and game over screen. </p>
 * <p> It uses a timer to trigger the game loop. </p>
 * <p> It uses a key listener to handle user input. </p>
 *
 * @see JPanel
 * @see ActionListener
 * @see Timer
 * @see KeyListener
 * @see GameFrame
 * @see State
 * @see MyKeyAdapter
 * @see GamePanel#startMenu()
 */
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

    boolean mesh = false;
    Timer timer;
    Random random;


    State state = State.MENU;

    /**
     * Constructor for the GamePanel class.
     * <p> It sets the preferred size of the panel, the background color, and the key listener. </p>
     * <p> It also creates a new Random object. </p>
     *
     * @see Dimension
     * @see Color
     * @see KeyListener
     * @see Random
     * @see MyKeyAdapter
     */
    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startMenu();
    }

    /**
     * Start the game menu.
     * <p> It sets the game state to MENU. </p>
     * <p> It adds a key listener to handle key events for the menu. </p>
     * <p> It displays the menu text. </p>
     *
     * @see State
     * @see KeyListener
     */
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

    /**
     * Start the game.
     * <p> It sets the game state to START. </p>
     * <p> It resets the game variables. </p>
     * <p> It creates a new timer. </p>
     * <p> It creates a new apple. </p>
     * <p> It removes the key listener used for the menu. </p>
     *
     * @see State
     * @see Timer
     * @see MyKeyAdapter
     * @see GamePanel#startMenu()
     */
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

    /**
     * Paint the game panel.
     * <p> It displays the game title, menu text, instructions, and author. </p>
     * <p> It also displays the game over screen. </p>
     * <p> It also calls the draw method. </p>
     *
     * @param g Graphics object to draw with
     * @see Graphics
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == State.MENU) {
            // Display the game title
            g.setColor(Color.GREEN);
            g.setFont(new Font("Ink Free", Font.BOLD, 80));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Snake Game", (SCREEN_WIDTH - metrics1.stringWidth("Snake Game")) / 2, g.getFont().getSize());

            // Display the game menu text
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String menuText = "Press Enter to Start";
            g.drawString(menuText, (SCREEN_WIDTH - metrics.stringWidth(menuText)) / 2, SCREEN_HEIGHT / 2);

            // Display the game instructions
            g.setColor(Color.white);
            g.setFont(new Font("Roboto Condensed", Font.BOLD, 20));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            String instructions = "Use arrow keys to move";
            g.drawString(instructions, (SCREEN_WIDTH - metrics2.stringWidth(instructions)) / 2, SCREEN_HEIGHT * 2 / 3);

            //Display the game author
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.ITALIC | Font.BOLD, 20));
            FontMetrics metrics3 = getFontMetrics(g.getFont());
            String author = "Author: VirosMs";
            g.drawString(author, (SCREEN_WIDTH - metrics3.stringWidth(author)) / 2, SCREEN_HEIGHT - 100);

        } else if (state == State.START) {
            // Your existing game rendering code
            draw(g);
        } else if (state == State.GAME_OVER) {
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
    }

    /**
     * Draw the game panel.
     * <p> It displays the snake, apple, score, and mesh. </p>
     * <p> It also calls the checkScoreAndChangeColor method. </p>
     *
     *
     * @param g Graphics object to draw with
     * @see Graphics
     */
    public void draw(Graphics g) {
        if (running) {
            if (mesh) {
                for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                    g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                    g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
                }
            }


            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                checkScoreAndChangeColor(g, i);
            }

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            paintComponent(g);
        }
    }

    /**
     * Create a new apple.
     * <p> It sets the apple coordinates to a random location on the screen. </p>
     *
     * @see Random
     */
    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    /**
     * Move the snake.
     * <p> It moves the snake body parts. </p>
     * <p> It also calls the checkScoreSpeed method. </p>
     *
     * @see GamePanel#checkScoreSpeed()
     */
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

        checkScoreSpeed();
    }

    /**
     * Check if the snake has eaten an apple.
     * <p> It increases the snake body parts and apples eaten. </p>
     * <p> It also calls the newApple method. </p>
     *
     * @see GamePanel#newApple()
     */
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    /**
     * Check if the snake has collided with something.
     * <p> It checks if the snake head has collided with the snake body or the screen borders. </p>
     * <p> It sets the game state to GAME_OVER if the snake has collided with something. </p>
     *
     * @see State
     */
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

    /**
     * Check the score and change the color of the snake.
     * <p> The snake color changes every 10 apples eaten. </p>
     *
     * @param g Graphics object to draw with
     * @param i Index of the snake part to draw
     */
    public void checkScoreAndChangeColor(Graphics g, int i) {
        if (applesEaten < 10) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else if (applesEaten < 20) {
            if (i == 0) {
                g.setColor(Color.yellow);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(220, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else if (applesEaten < 30) {
            if (i == 0) {
                g.setColor(new Color(0, 180, 255));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(0, 180, 180));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            if (i == 0) {
                g.setColor(Color.MAGENTA);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    /**
     * Check the score and speed up the game if necessary.
     * <p> The game speed is increased by reducing the delay between timer events. </p>
     * <p> The game speed is increased by 1% for every 10 apples eaten. </p>
     * <p> The game speed is capped at a 20% increase. </p>
     */
    public void checkScoreSpeed() {
        if (applesEaten % 10 == 0) {
            int maxSpeedup = (int) (DELAY * 0.2); // Máximo aumento del 20%
            int newDelay = Math.max(DELAY - applesEaten / 10, DELAY - maxSpeedup);
            timer.setDelay(newDelay);
        }
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

    /**
     * The MyKeyAdapter class is responsible for handling key events.
     * Inner class of GamePanel.
     * <p> It extends KeyAdapter. </p>
     * <p> It handles key events for the game menu. </p>
     * <p> It handles key events for the game over screen. </p>
     *
     * @see KeyAdapter
     */
    public class MyKeyAdapter extends KeyAdapter {
        /**
         * Invoked when a key has been pressed.
         * <p> It handles key events for the game menu. </p>
         * <p> It handles key events for the game over screen. </p>
         *
         * @param e the event to be processed
         */
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
                case KeyEvent.VK_M -> mesh = !mesh;
            }
        }
    }
}
