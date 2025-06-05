package coregame;

import java.util.LinkedList;

public class Board {

    private int[] board;
    
    private boolean whiteTurn;
    private boolean[] castleRights, enPassant;
    private Coordinate lastMove;
    private int halfMove, fullMove, lastEnPassantSquare;

    public Board(String fen)
    {
        String[] parsed = fen.split(" ");

        IllegalArgumentException wrongFen = new IllegalArgumentException("Illegal Forsyth-Edwards Notation format");

        if (parsed.length != 6) {
            throw wrongFen;
        }

        board = new int[64];

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

        castleRights = new boolean[2];

        try {
            whiteTurn = parsed[1].equals("w");

            // Castle eval
            String castleFen = parsed[2];
            int castleLength = castleFen.length();

            if (castleLength == 4 && castleFen.equals("KQkq"))
            {
                castleRights[0] = true;
                castleRights[1] = true;
            }
            else if (castleLength == 2)
            {
                castleRights[castleFen.equals(castleFen.toUpperCase()) ? 0 : 1] = true;
            }
            else if (castleLength == 1 && castleFen.equals("-"))
            {
                // The rights are set false by default
            }
            else
            {
                throw new Exception();
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

        enPassant = new boolean[64];
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
        LinkedList<Integer> error = new LinkedList<>();

        if (whiteTurn ^ Piece.isWhite(board[square])) return error;

        int piece = Piece.removeColorFromData(board[square]);

        return switch (piece)
        {
            case Piece.PAWN   -> getPawnMoves(square);
            case Piece.KNIGHT -> getKnightMoves(square);
            case Piece.KING   -> getKingMoves(square);
            case Piece.BISHOP -> getBishopMoves(square);
            case Piece.ROOK   -> getRookMoves(square);
            case Piece.QUEEN  -> getQueenMoves(square);
            default           -> error;
        };
    }

    private LinkedList<Integer> getPawnMoves(int square) {
        LinkedList<Integer> pawnMoves = new LinkedList<>();

        int piece = board[square];
        boolean isWhite = Piece.isWhite(piece);
    
        int d = isWhite ? -8 : 8;
        int startRank = isWhite ? 6 : 1;
    
        int step = square + d;
        if (valid(step) && board[step] == 0)
        {
            pawnMoves.push(step);
    
            step += d;
            if ((square / 8) == startRank && board[step] == 0)
                pawnMoves.push(step);
        }
        
        for (int offset : new int[]{d - 1, d + 1})
        {
            int target = square + offset;
    
            boolean onLeftEdge = (square % 8 == 0);
            boolean onRightEdge = (square % 8 == 7);
    
            if ((offset == d - 1 && !onLeftEdge) || (offset == d + 1 && !onRightEdge))
            {
                if (valid(target))
                {
                    int targetPiece = board[target];
                    if ((targetPiece != 0 && !Piece.sameTeam(piece, targetPiece)) || enPassant[target])
                        pawnMoves.push(target);
                }
            }
        }
    
        return pawnMoves;
    }

    private LinkedList<Integer> getKnightMoves(int square)
    {
        LinkedList<Integer> knightMoves = new LinkedList<>();
        int nextSquare = 0;

        int piece = board[square];

        nextSquare = square - 6;
        if (valid(nextSquare) && nextSquare%8 != 0 && nextSquare%8 != 1 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 6;
        if (valid(nextSquare) && nextSquare%8 != 6 && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 10;
        if (valid(nextSquare) && nextSquare%8 != 6 && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 10;
        if (valid(nextSquare) && nextSquare%8 != 0 && nextSquare%8 != 1 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 15;
        if (valid(nextSquare) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 15;
        if (valid(nextSquare) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 17;
        if (valid(nextSquare) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 17;
        if (valid(nextSquare) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);

        return knightMoves;
    }

    private LinkedList<Integer> getKingMoves(int square)
    {
        LinkedList<Integer> kingMoves = new LinkedList<>();
        int nextSquare = 0;
        int piece = board[square];

        nextSquare = square + 1;
        if (valid(nextSquare) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 1;
        if (valid(nextSquare) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square + 7;
        if (valid(nextSquare) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 7;
        if (valid(nextSquare) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square + 9;
        if (valid(nextSquare) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 9;
        if (valid(nextSquare) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);

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

        // Initialize var for possible en passant
        int d = Piece.isWhite(board[squareD]) ? -1 : 1;
        boolean isPawn = Piece.removeColorFromData(board[squareD]) == Piece.PAWN;
        int enPassantTarget = squareD + 8 * -d;

        if (lastEnPassantSquare == squareD && isPawn) board[enPassantTarget] = 0;

        // Resets any possible en passant
        enPassant[lastEnPassantSquare] = false;

        // Checking if there is a future en passant ?
        if (isPawn && squareD == squareB + 16 * d)
        {
            lastEnPassantSquare = enPassantTarget;
            enPassant[enPassantTarget] = true;
        }


        // Checking for pawn promotion
        d = (d == -1 ? 0 : 7);

        if (isPawn && squareD/8 == d) board[squareD] = Piece.QUEEN - (d == 0 ? Piece.WHITE : Piece.BLACK);

        if (!whiteTurn) fullMove++;
        whiteTurn = !whiteTurn;
    }
}
