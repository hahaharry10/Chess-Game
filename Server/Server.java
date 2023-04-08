import java.net.*;
import java.io.*;

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
    private String terminator = "$$END$$"; // used to mark the end of a transmission.

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
     * Runs the code to make the move called by the player.
     * @param current_loc The current location of the piece being moved.
     * @param new_loc The new location the piee is being moved to.
     * @param client The client number making the move.
     * @return The error message if the move cannot be made. Otherwise null is returned.
     */
    private String makeMove(String current_loc, String new_loc, int client)
    {
        char pieceBeingMoved = chessBoard.getPieceAtLoc(current_loc);
        
        if (client == 1)
        {
            switch (pieceBeingMoved)
            {
                case 'p':
                    if (!chessBoard.movePawn(current_loc, new_loc))
                        return "Invalid Move: cannot move pawn there.";
                    break;
                default:
                    return "Invalid Move: cannot move piece";
            }
        }
        else
        {
            switch(pieceBeingMoved)
            {
                case 'P':
                    if (!chessBoard.movePawn(current_loc, new_loc))
                        return "Invalid Move: cannot move pawn there.";
                    break;
                default:
                    return "Invalid Move: cannot move piece";
            }
        }

        return null;
    }

    /**
     * Sun the logic of the chess game.
     */
    private void startGame()
    {
        chessBoard = new ChessBoard();
        chessBoard.createNewBoard();
        
        // Start the game:
        while (true)
        {
            // c1Writer.println(chessBoard.getBoard(true));
            c1Writer.println(terminator);
            
            // c2Writer.println(chessBoard.getBoard(false));

            try
            {
                // start client 1's move:
                while (true)
                {
                    System.out.println("Waiting for client1 to move...");
                    c2Writer.println("Waiting for opponent to move...");
                    String move = c1Reader.readLine(); // read client1's move and split it into an array of locations.
                    if (move.equals("help"))
                    {
                        c1Writer.println("REJECTED");
                        c1Writer.println("==========================================================\nThe accepted format of a move is:\n\t$ current_tile new_tile\n==========================================================");
                        c1Writer.println(terminator);
                        continue;
                    }

                    /////////////////////////////////////////
                    c2Writer.println("Opponents move: " + move);
                    /////////////////////////////////////////

                    // make move

                    // was the move rejected?
                        // Send rejection message to client.
                        
                        // Otherwise send confirmation message.
                    
                    break;
                }

                // c1Writer.println(chessBoard.getBoard(false));
                
                // c2Writer.println(chessBoard.getBoard(true));
                c2Writer.println(terminator);
                
                // start client 2's move:
                while (true)
                {
                    System.out.println("Waiting for client2 to move...");
                    c1Writer.println("Waiting for opponent to move...");
                    String move = c2Reader.readLine(); // read client1's move and split it into an array of locations.
                    if (move.equals("help"))
                    {
                        c2Writer.println("REJECTED");
                        c2Writer.println("==========================================================\nThe accepted format of a move is:\n\t$ current_tile new_tile\n==========================================================");
                        c2Writer.println(terminator);
                        continue;
                    }

                    /////////////////////////////////////////
                    c1Writer.println("Opponents move: " + move);
                    /////////////////////////////////////////

                    // make move

                    // was the move rejected?
                        // Send rejection message to client.
                        
                        // Otherwise send confirmation message.
                    
                    break;
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
        startGame();

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
