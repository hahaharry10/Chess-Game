import java.net.*;
import java.io.*;
import java.util.regex.*;  


public class Server
{
    private ServerSocket server = null;
    private Socket client1 = null;
    private Socket client2 = null;
    private BufferedReader c1Reader = null; // used to read from client 1.
    private BufferedReader c2Reader = null; // used to read from client 2.
    private PrintWriter c1Writer = null; // used to write to client 1.
    private PrintWriter c2Writer = null; // used to write to client 2.

    private ChessBoard chessBoard = null;

    private int portNumber = 6174;
    private String terminator = "$$STOP$$"; // used to mark the end of a transmission.
    private String endGameTerminator = "$$END$$"; // used to mark the game has finished.

    // Define ANSI colour codes:
    private String setTextRed = "\u001B[0;31m";
    private String setTextGreen = "\u001B[0;32m";
    private String resetTextColour = "\u001B[0m";

    /**
     * Check both clients are conencted.
     */
    private void checkConnection()
    {
        if (!client1.isConnected())
        {
            System.err.println("Client 1 has been disconnected.");
            System.exit(1);
        }
        else if (!client2.isConnected())
        {
            System.err.println("Client 2 has been disconnected.");
            System.exit(1);
        }
    }

    /**
     * Check if the string is of valid format.
     * @param move The string being tested.
     * @return true if the string is valid. false if not.
     */
    private Boolean moveIsValid(String move)
    {
        // Use regular expressions to validate input:
        return Pattern.matches("^(?:[a-h][1-8] [a-h][1-8])$", move);
    }

    /**
     * Runs the code to make the move called by the player.
     * @param current_loc The current location of the piece being moved.
     * @param new_loc The new location the piee is being moved to.
     * @param client The client number making the move.
     * @return The error message if the move cannot be made. Otherwise null is returned.
     */
    private String makeMove(String current_loc, String new_loc, int client)
    {
        String response = chessBoard.movePiece(current_loc, new_loc);

        if (response == null)
            chessBoard.makeMove(current_loc, new_loc);
        
        return response;
    }

    /**
     * Handles the logic in quitting the game and closing the server.
     */
    private void quitGame()
    {
        System.out.println("Quitting game...");
        System.exit(1);
    }

    /**
     * Sun the logic of the chess game.
     */
    private void playGame()
    {
        // Create a new chess game
        chessBoard = new ChessBoard();
        chessBoard.createNewBoard();
        
        // Start the game:
        while (true)
        {
            // Send chessboard to both players:
            c1Writer.println(chessBoard.getBoard(true));
            c1Writer.println(terminator);
            
            c2Writer.println(chessBoard.getBoard(false));

            try
            {
                String move;
                // start client 1's move:
                while (true)
                {
                    System.out.println("Waiting for client1 to move...");
                    c2Writer.println("Waiting for opponent to move...");
                    move = c1Reader.readLine();

                    if ( move.toLowerCase().equals("quit") )
                    {
                        System.out.println("Resigning client1...");
                        c1Writer.println( setTextRed + "You have resigned and therefore lost the game!" + resetTextColour);
                        c2Writer.println("Opponent has resigned." + setTextGreen + " CONGRATULATIONS! YOU HAVE WON!" + resetTextColour);
                        c1Writer.println(terminator);
                        c2Writer.println(terminator);
                        quitGame();
                        break;
                    }

                    String current_loc = move.substring(0, 2);
                    String new_loc = move.substring(3, 5);

                    if (!moveIsValid(move))
                            continue;

                    String moveResponse = makeMove(current_loc, new_loc, 1);

                    if (moveResponse == null) // if the piece can move
                    {
                        int putsSelfInCheck = chessBoard.isInCheckOrCheckmate(true);
                        if (putsSelfInCheck != 0)
                        {
                            if (chessBoard.reverseMove() != 0)
                            {
                                // ToDo: process that quits the game...
                                quitGame();
                            }
                            
                            c1Writer.println("Invalid Move!");
                            c1Writer.println(terminator);
                        }
                        else
                            break;
                    }
                    else
                    {
                        c1Writer.println(moveResponse);
                        c1Writer.println(terminator);
                    }
                }
                
                c1Writer.println(chessBoard.getBoard(true));
                c2Writer.println("Opponents move: " + move);
                int putsOppInCheck = chessBoard.isInCheckOrCheckmate(false);
                if (putsOppInCheck == 1)
                {
                    c2Writer.println(setTextRed + "You are in check!" + resetTextColour);
                    c1Writer.println(setTextGreen + "They are in check!" + resetTextColour);
                }
                else if (putsOppInCheck == 2)
                {
                    // ToDo: process that quits the game...
                    quitGame();
                }
                
                c2Writer.println(chessBoard.getBoard(false));
                c2Writer.println(terminator);

                // start client 2's move:
                while (true)
                {
                    System.out.println("Waiting for client2 to move...");
                    c1Writer.println("Waiting for opponent to move...");
                    move = c2Reader.readLine();
                    
                    if ( move.toLowerCase().equals("quit") )
                    {
                        System.out.println("Resigning client1...");
                        c2Writer.println( setTextRed + "You have resigned and therefore lost the game!" + resetTextColour);
                        c1Writer.println("Opponent has resigned." + setTextGreen + " CONGRATULATIONS! YOU HAVE WON!" + resetTextColour);
                        c2Writer.println(terminator);
                        c1Writer.println(terminator);
                        quitGame();
                        break;
                    }

                    String current_loc = move.substring(0, 2);
                    String new_loc = move.substring(3, 5);

                    if (!moveIsValid(move))
                            continue;

                    String moveResponse = makeMove(current_loc, new_loc, 2);

                    if (moveResponse == null) // if the move was allowed.
                    {
                        int putsSelfInCheck = chessBoard.isInCheckOrCheckmate(false);
                        if (putsSelfInCheck != 0)
                        {
                            if (chessBoard.reverseMove() != 0)
                            {
                                // ToDo: process that quits the game...
                                quitGame();
                            }
                            
                            c1Writer.println("Invalid Move!");
                            c1Writer.println(terminator);
                        }
                        else
                            break;
                    }
                    else
                    {
                        c2Writer.println(moveResponse);
                        c2Writer.println(terminator);
                    }
                }

                c1Writer.println("Opponents move: " + move);
                putsOppInCheck = chessBoard.isInCheckOrCheckmate(true);
                if (putsOppInCheck == 1)
                {
                    c1Writer.println(setTextRed + "You are in check!" + resetTextColour);
                    c2Writer.println(setTextGreen + "They are in check!" + resetTextColour);
                }
                else if (putsOppInCheck == 2)
                {
                    // ToDo: process that quits the game...
                    quitGame();
                }
            }
            catch ( IOException err )
            {
                System.err.println("ERROR: failed to communicate with client. Shutting down server...");
                System.exit(1);
            }
        }
    }

    /**
     * Execute the logic of the server.
     */
    private void startServer()
    {
        System.out.println("Starting server setup...");

        // try set up server:
        try
        {
            server = new ServerSocket(portNumber);
        }
        catch ( IOException err )
        {
            System.err.println("ERROR: unable to set up server.");
            System.exit(1);
        }

        System.out.println("Waiting for first client to connect...");
        try
        {
            client1 = server.accept(); // Connect to first client.
            c1Reader = new BufferedReader(new InputStreamReader( client1.getInputStream() ));
            c1Writer = new PrintWriter(client1.getOutputStream(), true);
        }
        catch ( IOException err )
        {
            System.err.println("ERROR: unable to connect to client.");
            System.exit(1);
        }

        c1Writer.println("Connection to server successful. Waiting for opponent to connect..."); // send confirmation message to client.

        System.out.println("Waiting for second client to connect...");
        try
        {
            client2 = server.accept(); // connect to second client.
            c2Reader = new BufferedReader(new InputStreamReader( client2.getInputStream() ));
            c2Writer = new PrintWriter(client2.getOutputStream(), true);
        }
        catch ( IOException err )
        {
            c1Writer.println("Failed to connect to opponent. Closing server.");
            c1Writer.println(terminator);
            System.err.println("ERROR: unable to connect to client.");
            System.exit(1);
        }

        checkConnection();

        c1Writer.println("Opponent connected. Starting Game...\n================================================================================");
        c2Writer.println("Connected to sever. Starting Game...\n================================================================================");
        c1Writer.println(terminator);
        c2Writer.println(terminator);

        System.out.println("Starting game...");
        playGame();

        c1Writer.println("Closing server...");
        c2Writer.println("Closing server...");

        c1Writer.println(terminator);
        c2Writer.println(terminator);

        System.out.println("Closing server...");
        try
        {
            c1Reader.close();
            c1Writer.close();
            c2Reader.close();
            c2Writer.close();
            client1.close();
            client2.close();
        }
        catch ( IOException err )
        {
            System.err.println("ERROR: failed to disconnect server. Forcing termination...");
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        Server s = new Server();
        s.startServer();
    }
}
