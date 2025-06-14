package coregame;

import java.util.ArrayDeque;

public class Board {

    private int[] board;
    private boolean whiteTurn;
    private boolean[] castleRights;
    private Coordinate activeEnPassant;
    private int halfMove, fullMove, blackKingSquare, whiteKingSquare;
    private ArrayDeque<String> fenHistory, perftInfo;

    public static final String castlingNotation = "KQkq";

    public Board(String fen) throws IllegalArgumentException
    {
        fenHistory = new ArrayDeque<>();
        loadFen(fen);
        perftInfo = new ArrayDeque<>();
    }

    public int get(int index) {
        return board[index];
    }

    public int getBlackKingSquare() {
        return blackKingSquare;
    }

    public int getWhiteKingSquare() {
        return whiteKingSquare;
    }

    public ArrayDeque<String> getPerftInfo() {
        return perftInfo;
    }

    public boolean whitePlaying() {
        return whiteTurn;
    }

    private boolean check(boolean whiteToPlay)
    {
        return isSquareAttacked(whiteToPlay ? blackKingSquare : whiteKingSquare, whiteTurn);
    }

    /*
        Well, see if a square is under attack
    */
    private boolean isSquareAttacked(int targetSquare, boolean byWhite)
    {
        for (int square = 0; square < 64; square++)
        {
            int piece = board[square];

            if (piece == 0) continue;

            if (Piece.isWhite(piece) == byWhite)
            {
                ArrayDeque<Integer> moves = getPseudoLegalMoves(square, false, true);
                for (int move : moves) // if a move of the piece target's our square, the square is under attack !
                    if (move == targetSquare)
                        return true;
            }
        }

        return false;
    }

    public boolean checkMate()
    {
        for (int square = 0; square < 64; square++)
        {
            if (board[square] == 0) continue;

            if (!getLegalMoves(square, true).isEmpty()) return false;
        }

        return true;
    }

    /*
        automatically gets the right function to get a piece move
    */
    public ArrayDeque<Integer> getPseudoLegalMoves(int square, boolean strict, boolean pawnAlwaysWithDiagonals)
    {

        // if strict, we get the moves ONLY if it's our turn to play
        if ((strict && (whiteTurn ^ Piece.isWhite(board[square]))) || board[square] == 0) return new ArrayDeque<>();
        int rawPiece = Piece.removeColorFromData(board[square]);

        return switch (rawPiece)
        {
            case Piece.PAWN   -> getPawnMoves(square, pawnAlwaysWithDiagonals);
            case Piece.KNIGHT -> getKnightMoves(square);
            case Piece.KING   -> getKingMoves(square);
            case Piece.BISHOP -> getBishopMoves(square);
            case Piece.ROOK   -> getRookMoves(square);
            case Piece.QUEEN  -> getQueenMoves(square);
            default           -> new ArrayDeque<>();
        };
    }

    public ArrayDeque<Integer> getLegalMoves(int square, boolean strict)
    {
        ArrayDeque<Integer> pseudoLegalMoves = getPseudoLegalMoves(square, strict, false); // first, get pseudo legal moves

        // if it's king's move : well let's add castle move
        if (Piece.removeColorFromData(board[square]) == Piece.KING && (whiteTurn == Piece.isWhite(board[square])))
            addCastleMove(square, pseudoLegalMoves);

        if (pseudoLegalMoves.isEmpty()) return pseudoLegalMoves;
        
        ArrayDeque<Integer> legalMoves = new ArrayDeque<>();

        for (int move : pseudoLegalMoves)
        {
            // for each pseudo legal move we play them on the board
            makeMove(square, move, board[square]);

            // if after playing it's not check, it's legal, i think ?
            // if we are in check, and that we play a move, and we're still in check, it's an illegal move
            if (!check(whiteTurn))
                legalMoves.push(move);

            // undo the move now that we know whether the move is legal or not
            unMakeMove();
        }

        return legalMoves;
    }

    /*
        Gets pawn's move
    */
    private ArrayDeque<Integer> getPawnMoves(int square, boolean alwaysWithDiagonals) {
        ArrayDeque<Integer> pawnMoves = new ArrayDeque<>();

        int piece = board[square];
        boolean isWhite = Piece.isWhite(piece);
    
        int d = isWhite ? -8 : 8;
        int startRank = isWhite ? 6 : 1;
    
        int step = square + d;
        if ((step >= 0 && step < 64) && board[step] == 0)
        {
            pawnMoves.push(step); // simple push
    
            step += d;
            if ((square / 8) == startRank && board[step] == 0) // double push
                pawnMoves.push(step);
        }
        
        for (int offset : new int[]{d - 1, d + 1}) // diagonals attack
        {
            int nextSquare = square + offset;
    
            boolean onLeftEdge = (square % 8 == 0);
            boolean onRightEdge = (square % 8 == 7);
    
            if ((offset == d - 1 && !onLeftEdge) || (offset == d + 1 && !onRightEdge))
            {
                if (nextSquare >= 0 && nextSquare < 64)
                {
                    int targetPiece = board[nextSquare];
                    if (alwaysWithDiagonals || (targetPiece != 0 && !Piece.sameTeam(piece, targetPiece)) || (activeEnPassant != null && activeEnPassant.ordinal() == nextSquare))
                        pawnMoves.push(nextSquare);
                }
            }
        }
    
        return pawnMoves;
    }

    /*
        Gets knight's move
    */
    private ArrayDeque<Integer> getKnightMoves(int square)
    {
        ArrayDeque<Integer> knightMoves = new ArrayDeque<>();
        int nextSquare = 0;

        int piece = board[square];

        nextSquare = square - 6;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && nextSquare%8 != 1 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 6;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 6 && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 10;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 6 && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 10;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && nextSquare%8 != 1 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 15;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 15;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square - 17;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);
        nextSquare = square + 17;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) knightMoves.push(nextSquare);

        return knightMoves;
    }

    /*
        gets king's regular move (without castle)
    */
    private ArrayDeque<Integer> getKingMoves(int square)
    {
        ArrayDeque<Integer> kingMoves = new ArrayDeque<>();
        int nextSquare = 0;
        int piece = board[square];

        nextSquare = square + 1;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 1;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square + 7;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 7;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square + 9;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 0 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 9;
        if ((nextSquare >= 0 && nextSquare < 64) && nextSquare%8 != 7 && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);

        nextSquare = square + 8;
        if ((nextSquare >= 0 && nextSquare < 64) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);
        nextSquare = square - 8;
        if ((nextSquare >= 0 && nextSquare < 64) && Piece.notAlly(board[nextSquare], piece)) kingMoves.push(nextSquare);

        return kingMoves;
    }

    /*
        Adds the possibility to castle (if possible) to pre-existing king moves
    */
    public void addCastleMove(int square, ArrayDeque<Integer> kingMoves)
    {
        int piece = board[square];
        boolean isWhite = Piece.isWhite(piece);

        if (isSquareAttacked(square, !isWhite) || square%8 != 4) return; // check ? can't castle

        // already, castled ? rook moved ? a square between the rook and the king is attacked ? or is not empty ? can't castle
        if ((isWhite ? castleRights[0] : castleRights[2]) &&  board[square+1] == 0 && board[square+2] == 0 && !isSquareAttacked(square+1, !isWhite) && !isSquareAttacked(square+2, !isWhite) && Piece.removeColorFromData(board[square+3]) == Piece.ROOK && Piece.sameTeam(board[square], board[square+3])) kingMoves.push(square+2);
        if ((isWhite ? castleRights[1] : castleRights[3]) &&  board[square-1] == 0 && board[square-2] == 0 && board[square-3] == 0 && !isSquareAttacked(square-1, !isWhite) && !isSquareAttacked(square-2, !isWhite) && Piece.removeColorFromData(board[square-4]) == Piece.ROOK && Piece.sameTeam(board[square], board[square-4])) kingMoves.push(square-2);
    }

    /*
        get bishop's pseudo legal moves
    */
    private ArrayDeque<Integer> getBishopMoves(int targetSquare)
    {
        ArrayDeque<Integer> bishopMoves = new ArrayDeque<>();

        int targetRank = targetSquare/8;
        int targetFile = targetSquare%8;

        int rank, file, square;

        for (rank = targetRank + 1, file = targetFile + 1; rank < 8  && file <  8; rank++, file++) // down right
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank - 1, file = targetFile + 1; rank >= 0 && file <  8; rank--, file++) // up right
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank + 1, file = targetFile - 1; rank < 8  && file >= 0; rank++, file--) // down left
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank - 1, file = targetFile - 1; rank >= 0 && file >= 0; rank--, file--) // up left
        {
            square = rank*8+file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            bishopMoves.push(square);
            if (board[square] != 0) break;
        }

        // works like the getRookMoves() function

        return bishopMoves;
    }

    /*
        Gets rook's pseudo legal moves
    */
    private ArrayDeque<Integer> getRookMoves(int targetSquare)
    {
        ArrayDeque<Integer> rookMoves = new ArrayDeque<>();

        int targetRank = targetSquare/8;
        int targetFile = targetSquare%8;

        int rank, file, square;

        for (rank = targetRank + 1; rank <  8; rank++) // downwards
        {
            square = rank * 8 + targetFile;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }
        for (rank = targetRank - 1; rank >= 0; rank--) // upwards
        {
            square = rank * 8 + targetFile;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }
        for (file = targetFile + 1; file <  8; file++) // right
        {
            square = targetRank * 8 + file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }
        for (file = targetFile - 1; file >= 0; file--) // left
        {
            square = targetRank * 8 + file;
            if (!Piece.notAlly(board[square], board[targetSquare])) break;
            rookMoves.push(square);
            if (board[square] != 0) break;
        }

        // if we encountered a friendly piece we break before adding the move, if it's an enemy piece we add the move the break;

        return rookMoves;
    }

    /*
        Gets queen's pseudo legal moves
    */
    private ArrayDeque<Integer> getQueenMoves(int square)
    {
        ArrayDeque<Integer> queenMoves = new ArrayDeque<>();
        
        queenMoves.addAll(getRookMoves(square));
        queenMoves.addAll(getBishopMoves(square));

        return queenMoves;
    }

    /*
        Init the board with a fen
    */
    public void loadFen(String fen) throws IllegalArgumentException
    {
        String[] parsed = fen.split(" ");
        String errorMessage = "Illegal Forsyth-Edwards Notation format";

        if (parsed.length != 6) {
            throw new IllegalArgumentException(errorMessage);
        }

        board = new int[64];

        int rank = 0, file = 0;

        for (char sym : parsed[0].toCharArray())
        {
            if (sym == '/') { // we're going to next rank
                rank++;
                file = 0;
            } else {
                if (Character.isLetter(sym)) { // OH ! Let's init a piece on the board
                    int square = rank * 8 + file;
                    board[square] = sym;

                    //to keep track of our king
                    if (sym == 'k') blackKingSquare = square;
                    if (sym == 'K') whiteKingSquare = square; 

                    file++;
                } else if (Character.isDigit(sym)) { // some empty squares, let's just skip them
                    file += Character.getNumericValue(sym);
                } else {
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

        castleRights = new boolean[4];

        try {
            whiteTurn = parsed[1].equals("w"); // who's turn ?

            // Castle eval
            String castleFen = parsed[2];
            int castleLength = castleFen.length();

            if (castleLength < 0 || castleLength > 4) throw new IllegalArgumentException(errorMessage);

            else if (!(castleLength == 1 && castleFen.equals("-"))) // if the castleFen is "-" everything is already set to false by default
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

            if (parsed[3].equals("-")) {
                activeEnPassant = null;
            } else {
                activeEnPassant = Coordinate.valueOf(parsed[3]);
            }

            halfMove = Integer.parseInt(parsed[4]);
            fullMove = Integer.parseInt(parsed[5]);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Generates our board's Forsyth-Edwards Notation
    */
    public String getFen()
    {
        String fen = "";
        int blank = 0;

        for (int square = 0; square < 64; square++) // First part of the string
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

        if (blank != 0) fen += blank;

        fen += " " + (whiteTurn ? "w" : "b") + " "; // who's turn

        String castleFen = "";
        for (int i = 0; i < 4; i++)
            castleFen += castleRights[i] ? castlingNotation.charAt(i) : ""; // generating castle rights string

        fen += castleFen.isEmpty() ? "-" : castleFen;
        
        fen += " " + (activeEnPassant == null ? "-" : activeEnPassant.name()) + " " + halfMove + " " + fullMove; // en passant target square and details on halfMove and fullMove

        return fen;
    }

    /*
        Move piece from the square(begin) to square(destination), also manages castles, en passant and pawn promotion
    */
    public void makeMove(int squareB, int squareD, int whatPieceWhenPromoted)
    {
        fenHistory.push(getFen()); // before making changes on our board, let's just save it to unmake the move later on if we wish to
        halfMove++;

        if (board[squareD] != 0)
            halfMove = 0;

        // moving our piece
        board[squareD] = board[squareB];
        board[squareB] = 0;

        int rawPiece = Piece.removeColorFromData(board[squareD]);
        boolean isWhite = Piece.isWhite(board[squareD]);

        //let's just keep track of our kings location
        blackKingSquare = (rawPiece == Piece.KING && !isWhite) ? squareD : blackKingSquare;
        whiteKingSquare = (rawPiece == Piece.KING &&  isWhite) ? squareD : whiteKingSquare;

        int d;
        
        /* KING MANAGEMENT : CASTLING */
        if (isWhite ? (castleRights[0] || castleRights[1]) : (castleRights[2] || castleRights[3])) // do they still have the right to check
        {
            int rank = squareD/8;
            int file = squareD%8;

            if (rawPiece == Piece.ROOK)
            {
                // if a rook is moving then he loses his ability to castle with his king
                if (isWhite)
                {
                    if (squareB%8 == 7) castleRights[0] = false;
                    if (squareB%8 == 0) castleRights[1] = false;
                }

                else
                {
                    if (squareB%8 == 7) castleRights[2] = false;
                    if (squareB%8 == 0) castleRights[3] = false;
                }
            }

            if (rawPiece == Piece.KING)
            {
                if (file == 2 || file == 6) // the king castled ! the rook should come here now
                {
                    d = file == 6 ? 7 : 0;

                    int d1 = rank * 8 + (file == 6 ? 5 : 3);
                    int d2 = rank * 8 + d;

                    board[d1] = board[d2];
                    board[d2] = 0;
                }
                
                // king won't castle anymore
                if (isWhite) {
                    castleRights[0] = false;
                    castleRights[1] = false;
                } else {
                    castleRights[2] = false;
                    castleRights[3] = false;
                }
            }
        }
        
        /* PAWN MANAGEMENT : EN PASSANT AND PROMOTION */
        
        boolean isPawn = rawPiece == Piece.PAWN;
        d = isWhite ? -1 : 1;
        int enPassantTarget = squareD + 8 * -d; // the dude who's going to be eaten

        if (isPawn && activeEnPassant != null && activeEnPassant.ordinal() == squareD)
        {
            board[enPassantTarget] = 0;
        }

        activeEnPassant = null; // if we move a piece, the last enPassant square is reset

        if (isPawn && squareD == squareB + 16 * d)
            activeEnPassant = Coordinate.of(enPassantTarget); // if a pawn double pushes his opponents pawn can en Passant him
            
        d = (d == -1 ? 0 : 7);

        if (isPawn && squareD/8 == d) board[squareD] = whatPieceWhenPromoted; // the pawn did it ! he can be promoted

        /* NEXT TURN */

        if (!whiteTurn) fullMove++;
        if (isPawn)
        {
            halfMove = 0;
        }
        whiteTurn = !whiteTurn;
    }

    /*
        Takes previous board fen and loads it
    */
    public void unMakeMove()
    {
        if (!fenHistory.isEmpty())
            loadFen(fenHistory.pop());
    }

    public int perft(int depth, boolean quiet)
    {
        return perft(depth, depth, quiet);
    }

    /*
        Returns the number of makeable moves at a given board, for a given depth
    */
    private int perft(int depth, int initialDepth, boolean quiet)
    {
        if (depth == 0) { // Won't go too further
            return 1;
        }
    
        int totalMoves = 0;
    
        for (int square = 0; square < 64; square++) {

            if (board[square] == 0) continue; // for each pieces on the board

            ArrayDeque<Integer> legalMoves = getLegalMoves(square, true); // we get the legal moves
    
            for (int move : legalMoves) { // And for each of them

                boolean isWhite = Piece.isWhite(board[square]);
                if ((Piece.removeColorFromData(board[square]) == Piece.PAWN) && move / 8 == (isWhite ? 0 : 7)) {
                    for (int possiblePromotion : Piece.promotionPossibilities) { // if it's a pawn promoting, we do every promotion
                        makeMove(square, move, possiblePromotion - (isWhite ? Piece.WHITE : Piece.BLACK)); // we promote the pawn and play the move
                        int nodes = perft(depth-1, initialDepth, quiet); // count the possibilities

                        if (!quiet && depth == initialDepth)
                            perftInfo.push("" + Coordinate.of(square) + Coordinate.of(move) + (char)possiblePromotion + ": " + nodes);

                        totalMoves += nodes;
                        unMakeMove(); // backtrack
                    }
                } else {
                    makeMove(square, move, board[square]); // make the move
                    int nodes = perft(depth-1, initialDepth, quiet);

                    if (!quiet && depth == initialDepth)
                        perftInfo.push("" + Coordinate.of(square) + Coordinate.of(move) + ": " + nodes);

                    totalMoves += nodes; // count the possibilities
                    unMakeMove(); // backtrack
                }
            }
        }

        return totalMoves;
    }
}
