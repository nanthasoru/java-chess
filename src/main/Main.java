package main;

import coregame.*;
import java.util.Scanner;

public final class Main {

    private static final Scanner input = new Scanner(System.in);

    private static void eval(String command)
    {
        switch (command) {
            case "build":
                String fen = ask("Forsyth-Edwards Notation (blank for default chessboard) : ");
                try {
                    App.build(fen.equals("") ? null : fen);
                } catch (IllegalArgumentException e) {
                    System.out.println("The provided fen is wrong.");
                }
                break;
            case "stop":
                App.close();
                break;
            case "hide":
                App.setHighlight(false);
                System.out.println("Will not highlight squares");
                break;
            case "show":
                App.setHighlight(true);
                System.out.println("Will highlight squares");
                break;
            case "fen":
                command = App.getFen();
                if (command != null) System.out.println(command);
                break;
            case "help":
                System.out.println("""
                        Lists of commands to interact with the chess game.

                        build : asks for a Forsyth-Edwards Notation and creates a new board with it if valid.

                        stop  : closes the current board if active

                        hide  : removes the red highlighting

                        show  : adds back the red highlighting

                        fen   : prints in the current working terminal a FEN notation of the current board
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

        App.close();
        input.close();
        System.out.println("Quitting...");
    }
}