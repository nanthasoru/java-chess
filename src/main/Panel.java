package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import coregame.Board;
import coregame.Piece;

class Panel extends JPanel{

    private final static Color
        BEIGE = new Color(250, 235, 195),
        BROWN = new Color(204, 138, 94),
        RED = new Color(235, 64, 52, 150),
        GREEN = new Color(121, 232, 150, 150),
        BLUE = new Color(57, 61, 250, 150);

    private final static BufferedImage[] pieceImages = new BufferedImage[12];
    private final App password;

    private int lastSquare, x, y;
    private BufferedImage lastImage;
    private Board board;
    private ArrayDeque<Integer> moves, opponentMoves;
    private boolean attacksHighlight, slide, kingHighlight;

    Panel(String fen, App key) throws IllegalArgumentException
    {
        super();
        setPreferredSize(new Dimension(800, 800));
        password = key;
        attacksHighlight = true;
        kingHighlight = false;
        board = new Board(fen);
        lastSquare = -1;
    }

    public void setSlide(boolean slide) {
        this.slide = slide;
    }

    public void setXY(int x, int y) {
        this.x = x; this.y = y;
    }

    public void setAttacksHighlight(boolean attacksHighlight) {
        this.attacksHighlight = attacksHighlight;
    }

    public void setKingHighlight(boolean kingHighlight) {
        this.kingHighlight = kingHighlight;
    }

    public Board getBoard(App key) {
        return (key == password) ? board : null;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (int file = 0; file < 8; file++)
        {
            for (int rank = 0; rank < 8; rank++)
            {
                g.setColor((rank + file) % 2 == 0 ? BEIGE : BROWN);
                g.fillRect(file * 100, rank * 100, 100, 100);
             
                int square = rank * 8 + file;
                int piece = board.get(square);
                BufferedImage image = evalImage(piece);

                if (slide && square == lastSquare) {
                    continue;
                }

                g.drawImage(image, file * 100, rank * 100, 100, 100, null);

                if (attacksHighlight && moves != null && moves.contains(square))
                {
                    g.setColor(RED);
                    g.fillRect(file * 100, rank * 100, 100, 100);
                }

                if (attacksHighlight && opponentMoves != null && opponentMoves.contains(square))
                {
                    g.setColor(BLUE);
                    g.fillRect(file * 100, rank * 100, 100, 100);
                }

                if (kingHighlight && (board.getBlackKingSquare() == square || board.getWhiteKingSquare() == square))
                {
                    g.setColor(GREEN);
                    g.fillRect(file * 100, rank * 100, 100, 100);
                }
            }
        }

        if (slide && lastSquare != -1) {
            if (lastImage == null) lastImage = evalImage(board.get(lastSquare));
            g.drawImage(lastImage, x - 50, y - 50, 100, 100, null);
        }

        if (!slide) {
            lastImage = null;
        }
    }

    void setActiveSquare(int square)
    {
        if (board.get(square) == 0 && moves != null && moves.contains(square))
            putPiece(square);

        else if (board.get(square) == 0)
            callClear();
        else
        {
            if (Piece.isWhite(board.get(square)) == board.whitePlaying()) {
                moves = board.getLegalMoves(square, false);
                lastSquare = square;
            }
            else
            {
                opponentMoves = board.getPseudoLegalMoves(square, false, false);
                if (Piece.removeColorFromData(board.get(square)) == Piece.KING) board.addCastleMove(square, opponentMoves);
            }
        }
    }

    void callClear()
    {
        opponentMoves = null;
        moves = null;
        lastSquare = -1;
    }

    void putPiece(int square)
    {
        if (moves != null && lastSquare != -1 && moves.contains(square) && Piece.isWhite(board.get(lastSquare)) == board.whitePlaying())
        {
            board.makeMove(lastSquare, square, Piece.QUEEN - (Piece.isWhite(board.get(lastSquare)) ? Piece.WHITE : Piece.BLACK));
            callClear();
        }

        if (board.checkMate())
            System.out.println("Checkmate!");
    }

    private BufferedImage evalImage(int piece)
    {
        if (piece == 0) {
            return null;
        }

        int d = 6;

        if (Character.isUpperCase(piece)) {
            piece += Piece.WHITE;
            d = 0;
        }

        int index = switch (piece) {
            case Piece.KING -> 0;
            case Piece.QUEEN -> 1;
            case Piece.BISHOP -> 2;
            case Piece.KNIGHT -> 3;
            case Piece.ROOK -> 4;
            case Piece.PAWN -> 5;
            default -> -1;
        };

        if (index == -1) {
            return null;
        }

        return pieceImages[index + d];
    }

    static
    {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("res/sprites.svg.png"));
            int pieceWidth = 320;
            int pieceHeight = 320;

            for (int i = 0; i < 6; i++) {
                pieceImages[i] = spriteSheet.getSubimage(i * pieceWidth, 0, pieceWidth, pieceHeight);
                pieceImages[i + 6] = spriteSheet.getSubimage(i * pieceWidth, pieceHeight, pieceWidth, pieceHeight);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
