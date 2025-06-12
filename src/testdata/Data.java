package testdata;

public class Data {
    
    public static final String[] positions = // used in App.performanceTest()
    {
        // Official fen from chesswiki
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
        "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",
        "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",
        "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
        "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
        "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",

        // More fen to test out
        "4k3/8/8/3pPp2/8/8/8/4K3 w - f6 0 1",
        "4k3/3P4/8/8/8/8/8/4K3 w - - 0 1",
        "r3k2r/8/8/3pPp2/8/8/8/R3K2R w KQkq f6 0 1",
        "6k1/5P2/8/8/8/8/8/7K w - - 0 1"
    };

    public static final int[][] nodes =
    {
        // Official nodes number from chesswiki
        { // positions[0]
            1, // depth = 0
            20, // depth = 1
            400, // depth = 2 etc...
            8_902,
            197_281,
            4_865_609 // depth 5
        },
        {
            1,
            48,
            2_039,
            97_862,
            4_085_603,
            193_690_690
        },
        {
            1,
            14,
            191,
            2_812,
            43_238,
            674_624
        },
        {
            1,
            6,
            264,
            9_467,
            422_333,
            15_833_292
        },
        {
            1,
            44,
            1_486,
            62_379,
            2_103_487,
            89_941_194
        },
        {
            1,
            46,
            2_079,
            89_890,
            3_894_594,
            164_075_551
        },

        // Nodes number calculated with Stockfish 17.1
        {
            1,
            7,
            45,
            323,
            2_343,
            16_824,
            122_973,
            872_705,
            6_498_356
        },
        {
            1,
            13,
            38,
            377,
            2_018,
            24_854,
            142_358,
            2_096_638,
            11_724_128
        },
        {
            1,
            28,
            680,
            17_023,
            412_510,
            10_258_753,
        },
        {
            1,
            11,
            28,
            247,
            1_048,
            11_920,
            58_758,
            812_489,
            4_127_315,
        }
    };
}
