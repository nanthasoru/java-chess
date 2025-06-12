package main;

import javax.swing.JFrame;

import coregame.Board;
import testdata.Data;

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
            gamePanel = new Panel(fen == null || fen.isEmpty() ? Data.positions[0] : fen, sub);
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
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        beta.start();
    }

    static void setHighlight(boolean attack, boolean king)
    {
        if (gamePanel != null)
        {
            gamePanel.setAttacksHighlight(attack);
            gamePanel.setKingHighlight(king);
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

    static void performanceTest(int depth, int p, boolean quiet, boolean startMessage)
    {
        boolean validPosition = (p >= 0 && p < Data.positions.length);
        
        if (validPosition)
        {
            if (beta != null && beta.isAlive())
                board.loadFen(Data.positions[p]);
            else
                App.build(Data.positions[p]);
        }

        if (beta == null || !beta.isAlive())
        {
            System.out.println("Specified position isn't between 0 and 5, and no board is currently active, can't run performance test.");
            return;
        }

        board.getPerftInfo().clear();

        if (validPosition && startMessage)
            System.out.printf("Performance test, position %d, %s\n", p, Data.positions[p]);

        
        long beginning = System.currentTimeMillis();
        int nodes = board.perft(depth, quiet);
        long end = System.currentTimeMillis();

        String info = String.format("depth %2d : %10d possibilities in %10dms", depth, nodes, end - beginning);

        if (validPosition && depth < Data.nodes[p].length)
        {
            int expectedNodes = Data.nodes[p][depth];
            boolean success = expectedNodes == nodes;
            System.out.printf("%s %c %s\n", info, success ? '✅' : '❌', success ? "" : String.format("expected %d nodes", expectedNodes));
        }
        else
            System.out.println(info);

        // Collections.sort(board.getPerftInfo());

        for (String s : board.getPerftInfo())
            System.out.println(s);
    }

    static void stayOnTop()
    {
        if (gameFrame != null)
            gameFrame.setAlwaysOnTop(!gameFrame.isAlwaysOnTop());
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
