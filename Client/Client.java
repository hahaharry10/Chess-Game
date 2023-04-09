import java.net.*;
import java.io.*;

import java.util.regex.*;  

public class Client
{
    // Define attributes:
    private Socket socket = null;
    private PrintWriter writer = null;
    private BufferedReader serverReader = null;

    private int portNumber = 6174;
    private String terminator = "$$END$$";

    /**
     * Display the output of the server to the client.
     */
    private void printResponse()
    {
        String row;

        try
        {
            while (true)
            {
                row = serverReader.readLine();
                if (row.equals(terminator))
                    break;
                else
                    System.out.println(row);
            }
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
        return Pattern.matches("[AaBbCcDdEeFfGgHh][1-8]\s[AaBbCcDdEeFfGgHh][1-8]", input);
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

        printResponse(); // output the server response.

        printResponse(); // output the chess board.

        while (true)
        {
            String userInput = "";

            System.out.print("Enter move: ");
            try
            {
                userInput = System.console().readLine(); // read from the terminal.
            }
            catch ( IOError err )
            {
                System.err.println("ERROR: failed to read user input.");
                System.exit(1);
            }

            if (userInput.toLowerCase().equals("help"))
            {
                System.out.println("==========================================================");
                System.out.println("The accepted format of a move is:");
                System.out.println("\t$ current_tile new_tile");
                System.out.println("==========================================================");
                continue;
            }

            if ( checkInputValidity(userInput) )
            {
                writer.println(userInput); // Send input to the server
                printResponse(); // output server response
            }
            else
            {
                System.out.println("TRY AGAIN: input was invalid");
            }

            // break;
        }

        // free resources:
        // try
        // {
        //     serverReader.close();
        //     // stdin.close();
        //     writer.close();
        //     socket.close();
        // }
        // catch ( IOException err )
        // {
        //     System.err.println("ERROR: failed to close connection. Forcing termination...");
		// 	System.exit(1);
        // }
    }

    public static void main(String[] args)
    {
        Client c = new Client();
        c.runClient();
    }
}