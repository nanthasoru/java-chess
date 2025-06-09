package main;

import java.util.LinkedList;

import javax.swing.JFrame;

import coregame.Board;

public final class App implements Runnable
{

    private boolean running;

    private static JFrame gameFrame;
    public static Panel gamePanel;

    private static App sub;
    private static Thread beta;

    private static Board board;
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
            sub = new App();
            gamePanel = new Panel(fen == null || fen.isEmpty() ? Board.DEFAULT_FEN : fen, sub);
            board = gamePanel.getBoard(sub);
        } catch (IllegalArgumentException e) {
            gamePanel = null;
            throw new IllegalArgumentException();
        }

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

    static void setHighlight(boolean choice1, boolean choice2)
    {
        if (gamePanel != null)
        {
            gamePanel.setAttacksHighlight(choice1);
            gamePanel.setKingHighlight(choice2);
        }
    }

    static String getFen()
    {
        return board != null ? board.getFen() : null;
    }

    static void requestUndo()
    {
        if (board != null) board.unMakeMove();
    }

    static void performanceTest(int maxDepth)
    {
        if (board != null)
        {
            for (int depth = 1; depth <= maxDepth; depth++)
            {
                long beginning = System.currentTimeMillis();
                int nodes = board.perft(depth);
                long end = System.currentTimeMillis();

                System.out.printf("depth %2d : %10d possibilities in %10dms\n", depth, nodes, end - beginning);
            }
        }
    }

    static void close()
    {
        if (sub != null && beta != null && gameFrame != null && gamePanel != null && board != null && beta.isAlive())
        {
            sub.running = false;
            gameFrame.remove(gamePanel);
            gameFrame.dispose();
            board = null;
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
