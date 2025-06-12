package main;

import java.util.Scanner;

import testdata.Data;

public final class Main {

    public static final Scanner input = new Scanner(System.in);

    private static void eval(String command)
    {
        switch (command) {
            case "build":
                try {
                    App.build(ask("Forsyth-Edwards Notation (blank for default chessboard) : "));
                } catch (IllegalArgumentException e) {
                    System.out.println("The provided fen is wrong.");
                }
                break;
            case "stop":
                App.close();
                break;
            case "hide":
                App.setHighlight(false, false);
                System.out.println("Will not highlight squares");
                break;
            case "show":
                App.setHighlight(true, true);
                System.out.println("Will highlight squares");
                break;
            case "reset":
                App.setHighlight(true, false);
                break;
            case "fen":
                command = App.getFen();
                if (command != null) System.out.println(command);
                break;
            case "unmake":
                App.requestUndo();
                break;
            case "perft":
                try {
                    String position = ask("Position in range [0, 5] (see 'help' command)\nor leave the field blank if you're testing the current active board : ");
                    App.performanceTest(Integer.parseInt(ask("depth : ")), position.isBlank() ? -1 : Integer.parseInt(position), false, true);
                } catch (Exception e) {}
                break;
            case "allperft":
                for (int p = 0; p < Data.positions.length; p++)
                {
                    for (int depth = 1; depth < Data.nodes[p].length; depth++)
                    {
                        App.performanceTest(depth, p, true, depth == 1);
                    }
                }
                App.close();
                break;
            case "quit":
                App.close();
                input.close();
                System.out.println("Quitting...");
                break;
            case "toggle":
                App.stayOnTop();
                break;
            case "help":
                System.out.println("""
                        Lists of commands to interact with the chess game.

                        build    : asks for a Forsyth-Edwards Notation and creates a new board with it if valid.

                        stop     : closes the current board if active

                        hide     : removes all highlighting

                        show     : highlights everything

                        reset    : sets highlighting back to default

                        fen      : prints in the current working terminal a FEN notation of the current board

                        perft    : runs a performance test

                        unmake   : undo the last move

                        quit     : do as the commands says

                        toggle   : Window stays/won't stay on top

                        perft    : runs a performance test, if the specified position index is out of range, it runs the performance test on the current board

                                   positions (from 0 to 5) (can't gp past depth 5 on these)

                                   rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
                                   r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",
                                   8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1
                                   r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1
                                   rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8
                                   r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"

                        allperft : runs all 6 perft from depth 0 to depth 5 (WARNING this might take a long time)

                        help     : this command
                        """);
                break;
            default:
                System.out.println("unknown command, please use 'help'");
                break;
        }
    }

    private static String ask(String message)
    {
        System.out.print(message);
        return input.nextLine().trim();
    }

    public static void main(String[] args)
    {        
        String command = "";

        while (!command.equals("quit"))
        {
            command = ask("<chess>$ ");
            if (!command.isBlank()) eval(command);
        }
    }
}