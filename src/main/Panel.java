package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import coregame.Board;

class Panel extends JPanel{

    private final static Color BEIGE = new Color(250, 235, 195), BROWN = new Color(204, 138, 94);
    public final Board board;

    Panel(String fen)
    {
        super();
        setPreferredSize(new Dimension(800, 800));

        board = (fen == null) ? new Board() : new Board(fen);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (int rank = 0; rank < 8; rank++)
        {
            for (int file = 0; file < 8; file++)
            {
                g.setColor((rank + file) % 2 == 0 ? BEIGE : BROWN);
                g.fillRect(rank * 100, file * 100, 100, 100);
                
                int square = rank * 8 + file;

                
            }
        }
    }
}
