package coregame;

import java.util.LinkedList;

public class Board {

    private final static LinkedList<Integer> fileACoordinates, fileHCoordinates, fileABCoordinates, fileGHCoordinates;
    
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

    private boolean valid(int index) {
        return index >= 0 && index < 64;
    }


    public LinkedList<Integer> getMoves(int square)
    {
        int piece = Piece.removeColorFromData(board[square]);

        return switch (piece)
        {
            case Piece.PAWN   -> getPawnMoves(square, piece == board[square] ? Piece.BLACK : Piece.WHITE);
            case Piece.KNIGHT -> getKnightMoves(square);
            case Piece.KING   -> getKingMoves(square);
            case Piece.BISHOP -> getBishopMoves(square);
            case Piece.ROOK   -> getRookMoves(square);
            case Piece.QUEEN  -> getQueenMoves(square);
            default -> new LinkedList<>();
        };
    }

    private LinkedList<Integer> getPawnMoves(int square, int color)
    {
        LinkedList<Integer> pawnMoves = new LinkedList<>();
        int nextSquare = 0;
        int piece = board[square];

        boolean isWhite = color == Piece.WHITE;
        int d = isWhite ? -1 : 1;

        nextSquare = square + 7*d;
        if ((!isWhite && !fileHCoordinates.contains(nextSquare) || isWhite && !fileACoordinates.contains(nextSquare)) && valid(nextSquare) && board[nextSquare] != 0 && !Piece.sameTeam(board[nextSquare], piece)) pawnMoves.push(nextSquare);
        nextSquare += d;
        if (valid(nextSquare) && board[nextSquare] == 0) pawnMoves.push(nextSquare);
        nextSquare += d;
        if ((!isWhite && !fileACoordinates.contains(nextSquare) || isWhite && !fileHCoordinates.contains(nextSquare)) && valid(nextSquare) && board[nextSquare] != 0 && !Piece.sameTeam(board[nextSquare], piece)) pawnMoves.push(nextSquare);

        return pawnMoves;
    }

    private LinkedList<Integer> getKnightMoves(int square)
    {
        LinkedList<Integer> knightMoves = new LinkedList<>();
        int nextSquare = 0;

        int piece = board[square];

        nextSquare = square - 6;
        if (valid(nextSquare) && !fileABCoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 6;
        if (valid(nextSquare) && !fileGHCoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 10;
        if (valid(nextSquare) && !fileGHCoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 10;
        if (valid(nextSquare) && !fileABCoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 15;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 15;
        if (valid(nextSquare) && !fileHCoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 17;
        if (valid(nextSquare) && !fileHCoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 17;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);

        return knightMoves;
    }

    private LinkedList<Integer> getKingMoves(int square)
    {
        LinkedList<Integer> kingMoves = new LinkedList<>();
        int nextSquare = 0;
        int piece = board[square];

        nextSquare = square + 1;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 1;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square + 7;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 7;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square + 9;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 9;
        if (valid(nextSquare) && !fileACoordinates.contains(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);

        nextSquare = square + 8;
        if (valid(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 8;
        if (valid(nextSquare) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);

        return kingMoves;
    }

    private LinkedList<Integer> getBishopMoves(int targetSquare)
    {
        LinkedList<Integer> bishopMoves = new LinkedList<>();

        int targetRank = targetSquare/8;
        int targetFile = targetSquare%8;

        int rank, file, square;

        for (rank = targetRank + 1, file = targetFile + 1; rank < 8  && file <  8; rank++, file++)
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank - 1, file = targetFile + 1; rank >= 0 && file <  8; rank--, file++)
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank + 1, file = targetFile - 1; rank < 8  && file >= 0; rank++, file--)
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank - 1, file = targetFile - 1; rank >= 0 && file >= 0; rank--, file--)
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }

        return bishopMoves;
    }

    private LinkedList<Integer> getRookMoves(int targetSquare)
    {
        LinkedList<Integer> rookMoves = new LinkedList<>();

        int targetRank = targetSquare/8;
        int targetFile = targetSquare%8;

        int rank, file, square;

        for (rank = targetRank + 1; rank <  8; rank++)
        {
            square = rank * 8 + targetFile;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank - 1; rank >= 0; rank--)
        {
            square = rank * 8 + targetFile;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }
        for (file = targetFile + 1; file <  8; file++)
        {
            square = targetRank * 8 + file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }
        for (file = targetFile - 1; file >= 0; file--)
        {
            square = targetRank * 8 + file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }

        return rookMoves;
    }

    private LinkedList<Integer> getQueenMoves(int square)
    {
        LinkedList<Integer> queenMoves = new LinkedList<>();
        
        queenMoves.addAll(getRookMoves(square));
        queenMoves.addAll(getBishopMoves(square));

        return queenMoves;
    }

    public void movePiece(int squareB, int squareD)
    {
        board[squareD] = board[squareB];
        board[squareB] = 0;
    }

    static
    {
        fileACoordinates  = new LinkedList<>();
        fileHCoordinates  = new LinkedList<>();
        fileABCoordinates = new LinkedList<>();
        fileGHCoordinates = new LinkedList<>();

        for (int i = 0; i < 8; i++)
        {
            int r = i * 8;
            fileACoordinates.push(r);
            fileHCoordinates.push(r + 7);
            fileABCoordinates.push(r);
            fileABCoordinates.push(r + 1);
            fileGHCoordinates.push(r + 6);
            fileGHCoordinates.push(r + 7);
        }
    }
}
