package Client;

import java.net.*;
import java.io.*;

import java.util.regex.*;  

public class Client
{
    // Define attributes:
    private Socket socket = null;
    private PrintWriter writer = null;
    private BufferedReader serverReader = null;
    private BufferedReader stdin = null; // used to read from standard input.

    private int portNumber = 6174;
    private String gameOverSymbol = "$$END$$";

    /**
     * Display the output of the server to the client.
     */
    private void show()
    {
        String row;

        try
        {
            row = serverReader.readLine();

            System.out.println(row);
        }
        catch ( IOException err )
        {
            System.err.println("ERROR: failed to read the server.");
            System.exit(1);
        }
    }

    /**
     * Check if the string is of valid format.
     * @param input The string being tested.
     * @return true if the string is valid. false if not.
     */
    private boolean checkInputValidity(String input)
    {
        // Use regular expressions to validate input:
        return Pattern.matches("[a-zA-Z][0-8]\s[a-zA-Z][0-8]", input);
    }


    /**
     * Execute client logic.
     */
    private void runClient()
    {
        // Connect to the server:
        try
        {
            socket = new Socket("localhost", portNumber);
            writer = new PrintWriter(socket.getOutputStream(), true);
            serverReader = new BufferedReader(new InputStreamReader( socket.getInputStream() ));
        }
        catch ( UnknownHostException err )
        {
            System.out.println("ERROR: server not found.");
            System.exit(1);
        }
        catch ( IOException err )
        {
            System.out.println("ERROR: failed to connect to host.");
            System.exit(1);
        }

        show(); // show the initial state of the board.

        stdin = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            String userInput = "";

            System.out.print("Enter move: ");
            try
            {
                userInput = stdin.readLine(); // read from the terminal.
            }
            catch ( IOException err )
            {
                System.err.println("ERROR: failed to read user input.");
                System.exit(1);
            }

            if (userInput.equals("help"))
            {
                System.out.println("==========================================================");
                System.out.println("The accepted format of a move is:\n\t$ current_tile new_tile");
                System.out.println("==========================================================");
                continue;
            }

            if ( checkInputValidity(userInput) )
            {
                writer.println(userInput); // Send input to the server
                show(); // output server response
            }
            else
            {
                System.out.println("TRY AGAIN: input was invalid");
            }
        }
    }

    public void main()
    {
        Client c = new Client();
        c.runClient();
    }
}