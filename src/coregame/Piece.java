package coregame;

public class Piece {
    
    public static final int
        ROOK = 'r',
        BISHOP = 'b',
        KNIGHT = 'n',
        QUEEN = 'q',
        KING = 'k',
        PAWN = 'p',
        BLACK = 0,
        WHITE = 32;

    public static int removeColorFromData(int piece)
    {
        return piece + (Character.isUpperCase(piece) ? WHITE : BLACK);
    }

    public static boolean sameTeam(int p1, int p2)
    {
        return (p1 & WHITE) == (p2 & WHITE);
    }

    public static boolean notAlly(int p1, int p2)
    {
        return p1 == 0 || p2 == 0 || !sameTeam(p1, p2);
    }

}
