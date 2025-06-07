package main;

import javax.swing.JFrame;

public final class App implements Runnable
{

    private boolean running;

    private static JFrame gameFrame;
    public static Panel gamePanel;

    private static App sub;
    private static Thread beta;

    private App()
    {
        running = true;
    }

    static void build(String fen) throws IllegalArgumentException
    {
        if (beta != null && beta.isAlive())
        {
            System.out.println("Rebuilding on existing app, closing first.");
            App.close();
        }

        try {
            gamePanel = new Panel(fen);
        } catch (IllegalArgumentException e) {
            gamePanel = null;
            throw new IllegalArgumentException();
        }

        sub = new App();
        beta = new Thread(sub);
        
        gameFrame = new JFrame("chess");
        gameFrame.add(gamePanel);
        gameFrame.pack();

        Inputs inputs = new Inputs();
        gamePanel.addMouseListener(inputs);
        gamePanel.addMouseMotionListener(inputs);
        gamePanel.requestFocus();
        
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setVisible(true);

        beta.start();
    }

    static void setHighlight(boolean choice)
    {
        if (gamePanel != null)
            gamePanel.setAttacksHighlight(choice);
    }

    public static String getFen()
    {
        return gamePanel != null ? gamePanel.getFen() : null;
    }

    static void close()
    {
        if (sub != null && beta != null && gameFrame != null && gamePanel != null && beta.isAlive())
        {
            sub.running = false;
            gameFrame.remove(gamePanel);
            gameFrame.dispose();
            gamePanel = null;
            sub = null;
            beta = null;
            gameFrame = null;
        }
    }

    @Override
    public void run()
    {
        double timePerFrame = 1000000000/60;
        long lastFrame = 0;

        while (running)
        {
            long now = System.nanoTime();
            if (now - lastFrame >= timePerFrame) {
                gamePanel.repaint();
                lastFrame = now;
            }
        }
    }
}
