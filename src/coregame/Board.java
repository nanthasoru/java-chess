package coregame;

import java.util.LinkedList;

public class Board {

    private int[] board;
    
    private boolean whiteTurn;
    private boolean[] castleRights;
    private Coordinate activeEnPassant;
    private int halfMove, fullMove;

    private static final LinkedList<Integer> NULL_LIST = new LinkedList<>();
    private static final String DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", castlingNotation = "KQkq";

    public Board(String fen) throws IllegalArgumentException
    {

        if (fen == null) fen = DEFAULT_FEN;

        String[] parsed = fen.split(" ");
        String errorMessage = "Illegal Forsyth-Edwards Notation format";

        if (parsed.length != 6) {
            throw new IllegalArgumentException(errorMessage);
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
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

        castleRights = new boolean[4];

        try {
            whiteTurn = parsed[1].equals("w");

            // Castle eval
            String castleFen = parsed[2];
            int castleLength = castleFen.length();

            if (castleLength < 0 || castleLength > 4) throw new IllegalArgumentException(errorMessage);

            else if (!(castleLength == 1 && castleFen.equals("-")))
            {
                for (int i = 0; i < castleLength; i++)
                {
                    char sym = castleFen.charAt(i);
                    int index = castlingNotation.indexOf(sym);

                    if (index != -1)
                        castleRights[index] = true;
                    else
                        throw new IllegalArgumentException(errorMessage);
                }
            }

            if (!parsed[3].equals("-")) {
                activeEnPassant = Coordinate.valueOf(parsed[3]);
            }

            halfMove = Integer.parseInt(parsed[4]);
            fullMove = Integer.parseInt(parsed[5]);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int get(int index) {
        return board[index];
    }

    private boolean valid(int index) {
        return index >= 0 && index < 64;
    }

    public LinkedList<Integer> getMoves(int square)
    {
        if (whiteTurn ^ Piece.isWhite(board[square])) return NULL_LIST;

        int piece = Piece.removeColorFromData(board[square]);

        return switch (piece)
        {
            case Piece.PAWN   -> getPawnMoves(square);
            case Piece.KNIGHT -> getKnightMoves(square);
            case Piece.KING   -> getKingMoves(square);
            case Piece.BISHOP -> getBishopMoves(square);
            case Piece.ROOK   -> getRookMoves(square);
            case Piece.QUEEN  -> getQueenMoves(square);
            default           -> NULL_LIST;
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
                    if ((targetPiece != 0 && !Piece.sameTeam(piece, targetPiece)) || (activeEnPassant != null && activeEnPassant.ordinal() == target))
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

        // Castle moves

        boolean isWhite = Piece.isWhite(piece);

        if (isWhite ? castleRights[0] || castleRights[1] : castleRights[2] || castleRights[3] && square%8 == 4)
        {
            if (isWhite ? castleRights[0] : castleRights[2])
                for (nextSquare = square + 1; nextSquare%8 != 7; nextSquare++)
                {
                    if (board[nextSquare] != 0) break;
                    if (nextSquare%8 == 6 && Piece.removeColorFromData(board[nextSquare+1]) == Piece.ROOK && Piece.sameTeam(board[nextSquare+1], board[square])) kingMoves.push(nextSquare);
                }

            if (isWhite ? castleRights[1] : castleRights[3])
                for (nextSquare = square - 1; nextSquare%8 != 0; nextSquare--)
                {
                    if (board[nextSquare] != 0) break;
                    if (nextSquare%8 == 1 && Piece.removeColorFromData(board[nextSquare-1]) == Piece.ROOK && Piece.sameTeam(board[nextSquare-1], board[square])) kingMoves.push(nextSquare);
                }
        }

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

    public String getFen()
    {
        String fen = "";
        int blank = 0;

        for (int square = 0; square < 64; square++)
        {
            int piece = board[square];
            
            if (square % 8 == 0 && square != 0)
            {
                fen += (blank == 0 ? "" : blank) + "/";
                blank = 0;
            }
            
            if (piece != 0)
            {
                fen += (blank == 0 ? "" : blank) + "" + (char)piece;
                if (blank != 0) blank = 0;
            }

            if (piece == 0) blank++;
        }

        fen += " " + (whiteTurn ? "w" : "b") + " ";

        for (int i = 0; i < 4; i++)
            fen += castleRights[i] ? castlingNotation.charAt(i) : "";
        
        fen += " " + (activeEnPassant == null ? "-" : activeEnPassant.name()) + " " + halfMove + " " + fullMove;

        return fen;
    }

    public void movePiece(int squareB, int squareD)
    {
        halfMove++;
        if (board[squareD] != 0) halfMove = 0;

        board[squareD] = board[squareB];
        board[squareB] = 0;

        int rawPiece = Piece.removeColorFromData(board[squareD]);
        boolean isWhite = Piece.isWhite(board[squareD]);
        int d;
        
        /* KING MANAGEMENT : CASTLING */
        if (isWhite ? (castleRights[0] || castleRights[1]) : (castleRights[2] || castleRights[3]))
        {
            int rank = squareD/8;
            int file = squareD%8;

            if (rawPiece == Piece.ROOK)
            {
                castleRights[isWhite ? (file == 6 ? 0 : 1) : (file == 1 ? 2 : 3)] = false;
            }

            if (rawPiece == Piece.KING)
            {

                if (file == 1 || file == 6)
                {
                    d = file == 6 ? 7 : 0;
                    board[rank * 8 + (file == 6 ? 5 : 2)] = board[rank * 8 + d];
                    board[rank * 8 + d] = 0;
                }
                
                d = isWhite ? 0 : 1;

                castleRights[0 + d] = false;
                castleRights[1 + d] = false;
            }
        }
        
        /* PAWN MANAGEMENT : EN PASSANT AND PROMOTION */
        
        boolean isPawn = rawPiece == Piece.PAWN;
        d = isWhite ? -1 : 1;
        int enPassantTarget = squareD + 8 * -d;

        if (activeEnPassant != null && activeEnPassant.ordinal() == squareD && isPawn)
            board[enPassantTarget] = 0;

        activeEnPassant = null;

        if (isPawn && squareD == squareB + 16 * d)
            activeEnPassant = Coordinate.coordinateOf(enPassantTarget);

        d = (d == -1 ? 0 : 7);

        if (isPawn && squareD/8 == d) board[squareD] = Piece.QUEEN - (d == 0 ? Piece.WHITE : Piece.BLACK);

        /* NEXT TURN */

        if (!whiteTurn) fullMove++;
        if (isPawn) halfMove = 0;
        whiteTurn = !whiteTurn;
    }
}
