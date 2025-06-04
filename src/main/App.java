package main;

import javax.swing.JFrame;

public final class App implements Runnable
{
    private static JFrame gameFrame = new JFrame();
    public static Panel gamePanel;

    private static Thread beta = new Thread(new App());

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
        gamePanel.addMouseListener(inputs);
        gamePanel.addMouseMotionListener(inputs);

        gameFrame.requestFocus();
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        beta.start();
    }

    @Override
    public void run()
    {
        double timePerFrame = 1000000000/60;
        long lastFrame = 0;

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
