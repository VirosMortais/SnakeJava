package org.virosms;

import lombok.Data;

import javax.swing.JFrame;
/**
 * The GameFrame class is a JFrame that contains a GamePanel.
 *
 * @see GamePanel
 * @see JFrame
 * @see SnakeGame
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author VirosMs
 *
 */
public class GameFrame extends JFrame{
    /**
     * The GameFrame constructor.
     * <p> This constructor creates a new GamePanel and adds it to the GameFrame. </p>
     * <p> It also sets the title of the GameFrame to "Snake", sets the default close operation to exit on close,
     * sets the GameFrame to not be resizable, sets the GameFrame to be visible, and sets the location of the GameFrame
     * to the center of the screen. </p>
     *
     */
    public GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);


    }
}
