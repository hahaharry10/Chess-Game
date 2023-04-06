import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket server = null;
    private Socket client1 = null;
    private Socket client2 = null;
    private BufferedReader c1Reader = null; // used to read from client 1.
    private BufferedReader c2Reader = null; // used to read from client 2.
    private PrintWriter c1Writer = null; // used to write to client 1.
    private PrintWriter c2Writer = null; // used to write to client 2.

    private int portNumber = 6174;
    private String terminator = "$$END$$";


    private void startGame()
    {}

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

        c1Writer.write("Connection to server successful. Waiting for opponent to connect..."); // send confirmation message to client.

        System.out.println("Waiting for second client to connect...");
        try
        {
            client2 = server.accept(); // connect to second client.
            c2Reader = new BufferedReader(new InputStreamReader( client2.getInputStream() ));
            c2Writer = new PrintWriter(client2.getOutputStream(), true);
        }
        catch ( IOException err )
        {
            c1Writer.write("Failed to connect to opponent. Closing server.");
            c1Writer.write(terminator);
            System.err.println("ERROR: unable to connect to client.");
            System.exit(1);
        }

        c1Writer.write("Opponent connected. Starting Game...");
        c2Writer.write("Connected to sever. Starting Game...");

        System.out.println("Starting game...");
        startGame();

        c1Writer.write("Closing server...");
        c2Writer.write("Closing server...");

        c1Writer.write(terminator);
        c2Writer.write(terminator);

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
            System.err.println("ERROR: failed to disconnect server. forcing shut down...");
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        Server s = new Server();
        s.startServer();
    }
}
