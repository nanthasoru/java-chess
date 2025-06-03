package main;

import javax.swing.JFrame;
import coregame.Board;

public final class App implements Runnable{

    private static JFrame gameFrame = new JFrame();
    private static Panel gamePanel;

    private static Thread beta = new Thread(new App());
    private static Board board = new Board();

    private App() {}

    static void build(String fen)
    {
        if (beta.isAlive()) {
            return;
        }

        gamePanel = new Panel(fen);

        gameFrame.add(gamePanel);
        gameFrame.pack();

        Inputs inputs = new Inputs();
        gameFrame.addMouseListener(inputs);
        gameFrame.addMouseMotionListener(inputs);

        gameFrame.requestFocus();
        gameFrame.setResizable(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        beta.start();
    }

    @Override
    public void run()
    {
        double timePerFrame = 1000000000/60;
        long lastFrame = System.nanoTime();

        while (true)
        {
            long now = System.nanoTime();
            if (now - lastFrame >= timePerFrame) {
                gamePanel.repaint();
                lastFrame = now;
            }
        }
    }
}
