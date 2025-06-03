package coregame;

import java.util.LinkedList;

public class Board {

    private final static LinkedList<Integer> fileACoordinates;
    private final static LinkedList<Integer> fileHCoordinates;
    
    private int[] board;
    
    private boolean whiteTurn;
    private boolean[] castleRights;
    private Coordinate lastMove;
    private int halfMove, fullMove;

    public Board(String fen)
    {
        String[] parsed = fen.split(" ");

        IllegalArgumentException wrongFen = new IllegalArgumentException("Illegal Forsyth-Edwards Notation format");

        if (parsed.length != 6) {
            throw wrongFen;
        }

        board = new int[64];
        castleRights = new boolean[4];

        int rank = 0, file = 0;

        for (char sym : parsed[0].toCharArray())
        {
            if (sym == '/') {
                rank++;
                file = 0;
            } else {
                if (Character.isLetter(sym)) {
                    board[rank * 8 + file] = sym;
                    file++;
                } else if (Character.isDigit(sym)) {
                    file += Character.getNumericValue(sym);
                } else {
                    throw wrongFen;
                }
            }
        }

        try {
            whiteTurn = parsed[1].equals("w");

            for (int i = 0; i < 4; i++) {
                castleRights[i] = parsed[2].charAt(i) != '-';
            }

            if (!parsed[3].equals("-")) {
                lastMove = Coordinate.valueOf(parsed[3]);
            }

            halfMove = Integer.parseInt(parsed[4]);
            fullMove = Integer.parseInt(parsed[5]);

        }
        catch(Exception e) {
            throw wrongFen;
        }
    }

    public Board() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public int get(int index) {
        return board[index];
    }

    @Override
    public String toString()
    {
        String boardView = "\n";
        
        for (int rank = 0; rank < 8; rank++)
        {
            boardView += " " + (8 - rank) + "   ";
            for (int file = 0; file < 8; file++)
            {
                int square = board[rank * 8 + file];
                boardView += (square == 0 ? '.' : (char)square) + " ";
            }
            boardView += "\n";
        }

        boardView += "\n     a b c d e f g h\n";

        return boardView;
    }

    public LinkedList<Integer> getPawnAttacks(int square, int color)
    {
        LinkedList<Integer> pawnAttacks = new LinkedList<>();
        int nextSquare = 0;

        if (color == Piece.WHITE)
        {
            nextSquare = square + 7;
            if (!fileACoordinates.contains(nextSquare)) pawnAttacks.push(nextSquare);
            nextSquare += 2;
            if (!fileACoordinates.contains(nextSquare)) pawnAttacks.push(nextSquare);
        }

        else if (color == Piece.BLACK)
        {
            nextSquare = square - 7;
            if (!fileACoordinates.contains(nextSquare)) pawnAttacks.push(nextSquare);
            nextSquare -= 2;
            if (!fileACoordinates.contains(nextSquare)) pawnAttacks.push(nextSquare);
        }

        return pawnAttacks;
    }

    static
    {
        fileACoordinates = new LinkedList<>();
        fileHCoordinates = new LinkedList<>();

        for (int i = 0; i < 8; i++)
        {
            int r = i * 8;
            fileACoordinates.push(r);
            fileHCoordinates.push(r + 7);
        }
    }
}
