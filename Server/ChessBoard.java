import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class ChessBoard
{
    private char[][] board = new char[10][10]; // declare the 8x8 board plus tile coordinates.
    private int boardWidth = 10;

    final char emptyTile = '·';
    final char black = 1; // Denoted as an upper case character.
    final char white = 2; // Denoted as a lower case character

    /**
     * Initialise the board with all the pieces in their starting positions.
     */
    public void createNewBoard()
    {
        // fill all tiles with blanck space:
        for (int row = 0; row < boardWidth; row++)
        {
            for (int col = 0; col < boardWidth; col++)
                board[row][col] = emptyTile;
        }

        board[0][0] = ' ';
        board[0][9] = ' ';
        board[9][0] = ' ';
        board[9][9] = ' ';

        // fill the first and last column with row letters:
        for (int i = 1; i < 9; i++)
        {
            board[i][0] = (char) ('9' - i);
            board[i][9] = (char) ('9' - i);
        }
        
        // fill the first and last row with column numbers:
        for (int i = 1; i < 9; i++)
        {
            board[0][i] = (char) ('a' + i - 1);
            board[9][i] = (char) ('a' + i - 1);
        }

        // The peices will be represented by the starting letter of the piece (i.e. pawn will be shown with a 'P' or 'p').
        // The characters will either be in capital or lower case; capital case represents white, lowercase represents black.

        // place all black pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[2][i] = 'P'; // Pawns
        board[1][1] = 'R'; board[1][8] = 'R'; // Rooks
        board[1][2] = 'N'; board[1][7] = 'N'; // Knights
        board[1][3] = 'B'; board[1][6] = 'B'; // Bishops
        board[1][4] = 'Q'; // Queen
        board[1][5] = 'K'; // King

        // place all white pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[7][i] = 'p'; // Pawns
        board[8][1] = 'r'; board[8][8] = 'r'; // Rooks
        board[8][2] = 'n'; board[8][7] = 'n'; // Knights
        board[8][3] = 'b'; board[8][6] = 'b'; // Bishops
        board[8][4] = 'q'; // Queen
        board[8][5] = 'k'; // King
    }

    /**
     * Convert a character into its ASCII symbol.
     * @param chr The character being translated into ascii representation.
     * @return The translated ASCII character.
     */
    private char translateCharacter(char chr)
    {
        switch (chr) {
            case 'P':
                return '\u265F';
            case 'p':
                return '\u2659';
            case 'R':
                return '\u265C';
            case 'r':
                return '\u2656';
            case 'N':
                return '\u265E';
            case 'n':
                return '\u2658';
            case 'B':
                return '\u265D';
            case 'b':
                return '\u2657';
            case 'Q':
                return '\u265B';
            case 'q':
                return '\u2655';
            case 'K':
                return '\u265A';
            case 'k':
                return '\u2654';
            default:
                return chr;
        }
    }

    /**
     * Convert the board into a string.
     * @param client Boolean value saying whether the board will be created in teh perspective of client 1 (white).
     * @return the chessboard in the form of a string.
     */
    public String getBoard(Boolean client1View)
    {
        String boardString = "";

        if (client1View)
        {
            for (int row = 0; row < boardWidth; row++)
            {
                boardString = boardString + translateCharacter(board[row][0]);

                for (int col = 1; col < boardWidth; col++)
                    boardString = boardString + ' ' + translateCharacter(board[row][col]);
                
                boardString = boardString + '\n';
            }
        }
        else
        {
            for (int row = boardWidth-1; row >= 0; row--)
            {
                boardString = boardString + translateCharacter(board[row][boardWidth-1]);

                for (int col = boardWidth-2; col >= 0; col--)
                    boardString = boardString + ' ' + translateCharacter(board[row][col]);
                
                boardString = boardString + '\n';
            }
        }

        return boardString;
    }

    /**
     * Get the piece that is at the location.
     * @param x The x label of the location.
     * @param y The y label of the location.
     * @return The character at the location.
     */
    public char getPieceAtLoc(char x, char y)
    {
        x = Character.toLowerCase(x);
        y = Character.toLowerCase(y);
        int row = (int) ('9' - y);
        int col = (int) (x - 'a' + 1);
        
        return board[row][col];
    }

    /**
     * Get the piece that is at the location.
     * @param location The location.
     * @return Teh character at teh location.
     */
    public char getPieceAtLoc(String location)
    {
        location = location.toLowerCase();
        int row = (int) '9' - location.charAt(1);
        int col = (int) location.charAt(0) - 'a' + 1;

        return board[row][col];
    }

    /**
     * Get the colour of a given piece.
     * @param piece The piece being tested.
     * @return The integer value of the colour of the piece.
     */
    private int getColourOfPiece(char piece)
    {
        if (Character.isUpperCase(piece))
            return black;
        else if (Character.isLowerCase(piece))
            return white;
        else
            return 0;
    }

    /**
     * Converts indexing coordinates into chess coordinates.
     */
    public String convertCoords(int row, int col)
    {
        return Character.toString((char) col - 1 + 'a') + Character.toString((char) '9' - row);
    }

    /*****************************************************************************************************/
    /*                      The following functions implement the move validators.                       */
    /*****************************************************************************************************/

    /**
     * The interface function linking all piece specific move validators.
     * @param current_loc The current location of the piece about to be moved.
     * @param new_loc The location where the piece is being moved to.
     * @return String describing the error. If no error occurs null is returned.
     */
    public String movePiece(String current_loc, String new_loc)
    {
        char pieceBeingMoved = getPieceAtLoc(current_loc);

        // System.out.println("Piece being moved: " + pieceBeingMoved); // FOR TESTING PURPOSES

        if (current_loc.toLowerCase() == new_loc)
            return "Invalid Move: piece has to move.";
        
        switch (Character.toLowerCase(pieceBeingMoved))
        {
            case 'p':
                if (!movePawn(current_loc, new_loc))
                    return "Invalid Move: cannot move Pawn there.";
                break;
            case 'r':
                if (!moveRook(current_loc, new_loc))
                    return "Invalid Move: cannot move Rook there.";
                break;
            case 'n':
                if (!moveKnight(current_loc, new_loc))
                    return "Invalid Move: cannot move Knight there.";
                break;
            case 'b':
                if (!moveBishop(current_loc, new_loc))
                    return "Invalid Move: cannot move Bishop there.";
                break;
            case 'q':
                if (!moveQueen(current_loc, new_loc))
                    return "Invalid Move: cannot move Queen there.";
                break;
            case 'k':
                if (!moveKing(current_loc, new_loc))
                    return "Invalid Move: cannot move King there.";
                break;
            default:
                return "Invalid Move: cannot move piece.";
        }

        return null;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean movePawn(String current_loc, String new_loc)
    { 
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        
        char pawn = getPieceAtLoc(current_loc);
        int pawnColour = getColourOfPiece(pawn);
        char pieceAtNewLoc = getPieceAtLoc(new_loc);
        int newPieceColour = getColourOfPiece(pieceAtNewLoc);
        Boolean moveIsLegal = false;
        
        // REMEMBER: the board perspectives of black and white are flipped.
        if (pawnColour == black)
        {
            if (current_y-1 == new_y) // is the pawn moving one tile forward?
            {
                // if the new location is forward one tile and if the next tile is empty.
                if (current_x == new_x && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward right diagonal tile?
                if (current_x-1 == new_x && newPieceColour == white)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward left diagonal tile?
                if (current_x+1 == new_x && newPieceColour == white)
                    moveIsLegal = true;
            }

            // if the pawn is at its starting row.
            else if (current_y == '7')
            {
                // if the pawn is jumping 2 steps forward and the jump path is clear.
                char nextTile = getPieceAtLoc(current_x, (char) (current_y-1));
                if (current_y-2 == new_y && nextTile == emptyTile && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;
            }
        }
        else if (pawnColour == white)
        {

            if (current_y+1 == new_y) // is the pawn moving one tile forward
            {
                // if the new location is forward one tile and if the next tile is empty.
                if (current_x == new_x && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward right diagonal tile?
                if (current_x+1 == new_x && newPieceColour == black)
                    moveIsLegal = true;
                
                // is the move capturing an opponents piece on the forward left diagonal tile?
                if (current_x-1 == new_x && newPieceColour == black)
                    moveIsLegal = true;
            }

            // if the pawn is at its starting row.
            else if (current_y == '2')
            {
                // if the pawn is jumping 2 steps forward and the jump path is clear.
                char nextTile = getPieceAtLoc(current_x, (char) (current_y+1));
                if (current_y+2 == new_y && nextTile == emptyTile && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;
            }
        }

        if (moveIsLegal)
            return true;

        return false;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveRook(String current_loc, String new_loc)
    {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);

        char rook = getPieceAtLoc(current_loc);
        int rookColour = getColourOfPiece(rook);
        char pieceAtNewLoc = getPieceAtLoc(new_loc);
        int newPieceColour = getColourOfPiece(pieceAtNewLoc);

        if (new_x == current_x) // if the rook is moving along the y axis (up and down).
        {
            char a, b;
            if (new_y > current_y)
            {
                a = new_y;
                b = current_y;
            }
            else
            {
                a = current_y;
                b = new_y;
            }

            // check path is clear
            for (int i = (int) b+1; i < (int) a; i++)
            {
                if (getPieceAtLoc(current_x, (char) i) != emptyTile)
                    return false;
            }

            // check the final destination of the new location
            if (rookColour == newPieceColour)
                return false;
        }
        else if (new_y == current_y) // if the rook is moving along the x axis (side to side).
        {
            char a, b;
            if (new_x > current_x)
            {
                a = new_x;
                b = current_x;
            }
            else
            {
                a = current_x;
                b = new_x;
            }

            // check path is clear
            for (int i = (int) b+1; i < (int) a; i++)
            {
                if (getPieceAtLoc((char) i, current_y) != emptyTile)
                    return false;
            }

            // check the final destination of the new location
            if (rookColour == newPieceColour)
                return false;
        }
        else
            return false;

        return true;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveKnight(String current_loc, String new_loc)
    {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece( getPieceAtLoc(current_loc) );
        int newColour = getColourOfPiece( getPieceAtLoc(new_loc) );

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);

        if ( ( ((x_dif == 2) && (y_dif == 1)) || ((x_dif == 1) && (y_dif == 2)) ) && currentColour != newColour )
            return true;
        else
            return false;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveBishop(String current_loc, String new_loc)
    {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece( getPieceAtLoc(current_loc) );
        int newColour = getColourOfPiece( getPieceAtLoc(new_loc) );

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);


        if ( (x_dif != y_dif) || (x_dif == 0) || (y_dif == 0) )
            return false;
        else
        {
            // check path is empty:
            char x, y;
            if ((int) current_x < (int) new_x)  { x = (char) ( (int) current_x + 1); }
            else                                { x = (char) ( (int) current_x - 1); }
            if ((int) current_y < (int) new_y)  { y = (char) ( (int) current_y + 1); }
            else                                { y = (char) ( (int) current_y - 1); }

            while (x != new_x && y != new_y)
            {
                if (getPieceAtLoc(x, y) != emptyTile)
                { return false; }

                if (x < new_x)  { x = (char) ((int) x + 1); }
                else            { x = (char) ((int) x - 1); }
                if (y < new_y)  { y = (char) ((int) y + 1); }
                else            { y = (char) ((int) y - 1); }
            }

            if (currentColour == newColour)
                { return false; }
            else
                return true;
        }
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveQueen(String current_loc, String new_loc)
    {
        // the queen can either move like a rook or a bishop.
        if (moveRook(current_loc, new_loc))
            return true;
        else if (moveBishop(current_loc, new_loc))
            return true;
        else
            return false;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveKing(String current_loc, String new_loc)
    {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece( getPieceAtLoc(current_loc) );
        int newColour = getColourOfPiece( getPieceAtLoc(new_loc) );

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);

        if ( x_dif + y_dif == 0)
            return false;
        else if ( (x_dif <= 1 && y_dif <= 1) && (currentColour != newColour) )
            return true;
        else
            return false;
    }

    /**
     * Move the piece from its current location to the new location.
     * @param current_loc The location of the piece being moved.
     * @param new_loc The new location the piece is being moved to.
     */
    public void makeMove(String current_loc, String new_loc)
    {
        current_loc = current_loc.toLowerCase();
        new_loc = new_loc.toLowerCase();
        char piece = getPieceAtLoc(current_loc);

        int current_x_index = (int) (current_loc.charAt(0) - 'a' + 1);
        int current_y_index = (int) ('9' - current_loc.charAt(1));
        int new_x_index = (int) (new_loc.charAt(0) - 'a' + 1);
        int new_y_index = (int) ('9' - new_loc.charAt(1));

        board[current_y_index][current_x_index] = emptyTile;
        board[new_y_index][new_x_index] = piece;
    }

    /*****************************************************************************************************/


    /*****************************************************************************************************/
    /*                  The following code implements the feature that tests for check.                  */
    /*****************************************************************************************************/

    /**
     * Tests if any attacking piece threatens the king - i.e. tests if a player is in checks.
     * @param forWhite Boolean value saying if white is being tested.
     * @return true if in check, false if not in check.
     */
    private Boolean isInCheck(Boolean forWhite, String kingsLoc)
    {
        int attackingColour = (forWhite ? black : white); // get the colour of the pieces that are checking the king.
        
        // Iterate through the attacking pieces:
        for (int row = 1; row < boardWidth-1; row++)
        {
            for (int col = 1; col < boardWidth-1; col++)
            {
                char piece = board[row][col];
                if ( getColourOfPiece(piece) == attackingColour)
                {
                    String current_loc = convertCoords(row, col);
                    
                    if (movePiece(current_loc, kingsLoc) == null)
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Test whether the king in check can be moved outside of check.
     * @param forwhite Boolean value saying if white is being tested.
     * @return true if the king can move out of check, false if the king cnanot.
     */
    private Boolean kingCanEscapeCheck(Boolean forWhite, String kingsLoc)
    {   
        // Create array of neighbours of the king.
        String[] neighbours = new String[8];
        neighbours[0] = Character.toString( kingsLoc.charAt(0) + 1 ) + Character.toString( kingsLoc.charAt(1) - 1 );  // Top left diagonal neighbour.
        neighbours[1] = Character.toString( kingsLoc.charAt(0) + 1 ) + Character.toString(kingsLoc.charAt(1));        // Above neighbour.
        neighbours[2] = Character.toString( kingsLoc.charAt(0) + 1 ) + Character.toString( kingsLoc.charAt(1) + 1 );  // Top right diagonal neighbour.
        neighbours[3] = Character.toString(kingsLoc.charAt(0)) + Character.toString(kingsLoc.charAt(1) + 1);          // Right neighbour.
        neighbours[4] = Character.toString( kingsLoc.charAt(0) - 1 ) + Character.toString( kingsLoc.charAt(1) + 1 );  // Bottom right neighbour.
        neighbours[5] = Character.toString( kingsLoc.charAt(0) - 1 ) + Character.toString(kingsLoc.charAt(1));        // Below neighbour.
        neighbours[6] = Character.toString( kingsLoc.charAt(0) - 1 ) + Character.toString( kingsLoc.charAt(1) - 1 );  // Bottom left neighbour.
        neighbours[7] = Character.toString(kingsLoc.charAt(0)) + Character.toString( kingsLoc.charAt(1) - 1 );        // Left neighbour.


        // Iterate through the neighbours and check if the move is valid.
        for (String neighbour : neighbours)
        {
            if ( Pattern.matches("[AaBbCcDdEeFfGgHh][1-8]", neighbour) )
            {
                if (moveKing(kingsLoc, neighbour)) // can the king move to the neighbour.
                {
                    if ( !isInCheck(forWhite, neighbour) )
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Tests whether the check can be obstructed.
     * @param forwhite Boolean value saying if white is being tested.
     * @return true if the path between the king and the piece threatening check can be obstructed, false otehrwise.
     */
    private Boolean checkCanBeObstructed(Boolean forWhite, String kingsLoc)
    {
        //
        // Create an array of tiles between the check-threatening piece and the king (including the attacking piece and excluding the king).
        //
        int attackingColour = (forWhite ? black : white); // get the colour of the pieces that are checking the king.
        List<String> path = new ArrayList<>();

        // find the piece/ pieces threatening check:
        List<String> threats = new ArrayList<>(); // create a list to hold the coordinates that threaten check.
        for (int row = 1; row < boardWidth - 1; row++)
        {
            for (int col = 1; col < boardWidth - 1; col++)
            {
                char piece = board[row][col];
                if ( getColourOfPiece(piece) == attackingColour)
                {
                    String current_loc = convertCoords(row, col);
                    if ( movePiece(current_loc, kingsLoc) == null )
                        threats.add(current_loc);
                }
            }
        }

        if (threats.size() > 1) // double check cannot be blocked.
            return false;

        // create array of tiles between the first threatening piece and the king:
        String attacking_loc = threats.get(0); // Get the first threat location.

        char king_x = kingsLoc.charAt(0);
        char king_y = kingsLoc.charAt(1);

        char threateningPiece = getPieceAtLoc(attacking_loc);

        // Hopefully by this point all threatening pieces should be checked to make sure they can reach the king. Therefore writing
        //  an abstract path mapping algorithm should allow for simpler code. The only exception is that the knght is the only piece
        //  whose path cannot be obstructed. To block a knights atack you must take the knight or move out of the way.
        switch ( Character.toLowerCase(threateningPiece) )
        {
            case 'k': // Knights path cannot be obstructed. We only need to check if the piece can be taken.
                // Iterate through all defending pieces and see if they can take the knight:
                for (int row = 1; row < boardWidth-1; row++)
                {
                    for (int col = 1; col < boardWidth-1; col++)
                    {
                        String defending_loc = convertCoords(row, col);
                        if (getColourOfPiece(getPieceAtLoc(defending_loc)) != attackingColour) // if the piece belongs to the defending player.
                        {
                            if (movePiece(defending_loc, attacking_loc) == null)
                                return true;
                        }
                    }
                }
                break;
            
            /********************************************************************************************************************************/
            /* The code csn be simplified if the algorithm searched for a check obstruction after every location in the path is found. As   */
            /* oppose to finding all location in the path and then finding if each tile can be obstructed.                                  */
            /********************************************************************************************************************************/
            default:
                char attacking_x = attacking_loc.charAt(0);
                char attacking_y = attacking_loc.charAt(1);

                while (attacking_x != king_x && attacking_y != king_y)
                {
                    path.add(convertCoords(attacking_x, attacking_y)); // add to the path list.
                    
                    if (attacking_x < king_x)   { attacking_x++; }
                    else                        { attacking_x--; }
                    if (attacking_y < king_y)   { attacking_y++; }
                    else                        { attacking_y--; }
                }

                // Check if the path can be obstructed
                for (String path_loc : path) // iterate through path.
                {
                    // Iterate through all defending pieces and see if they can take the piece:
                    for (int row = 1; row < boardWidth-1; row++)
                    {
                        for (int col = 1; col < boardWidth-1; col++)
                        {
                            String defending_loc = convertCoords(row, col);
                            if (getColourOfPiece(getPieceAtLoc(defending_loc)) != attackingColour) // if the piece belongs to the defending player.
                            {
                                if (movePiece(defending_loc, path_loc) == null)
                                    return true;
                            }
                        }
                    }
                }
                break;
            /*********************************************************************************************************************************/


                
        }
        
        return false;
    }

    /**
     * Calculate whether the player is in check, checkmate, or neither.
     * @param forWhite Boolean value 
     * @return 0 if not in check, 1 if in check, 2 if in checkmate, 3 if error occurs.
     */
    public int isInCheckOrCheckmate(Boolean forWhite)
    {
        char king = (forWhite ? 'k' : 'K');
        
        // get the location of the king:
        String kingsLoc = null;
        for (int row = 1; row < boardWidth; row++)
        {
            for (int col = 1; col < boardWidth; col++)
            {
                if ( board[row][col] == king )
                {
                    kingsLoc = Character.toString((char) col - 1 + 'a') + Character.toString((char) '9' - row);
                    break;
                }
            }
        }

        if (kingsLoc == null)
        {
            System.err.println("ERROR: King not found.");
            return 3; // return error signal
        }

        if (!isInCheck(forWhite, kingsLoc)) // If the player is in check.
            return 0;
        else if (kingCanEscapeCheck(forWhite, kingsLoc) || checkCanBeObstructed(forWhite, kingsLoc)) // If check can be avoided next move.
            return 1;
        else // If the player is in checkmate.
            return 2;
    }
    
    /*****************************************************************************************************/


    public static void main(String[] args)
    {
        ChessBoard cb = new ChessBoard();
        cb.createNewBoard();

        System.out.println(cb.getBoard(true));

        System.out.println("Test Bishops cannot jump over pieces (SHOULD ALL FAIL):");
        System.out.println("c1 -> a3: " + cb.moveBishop("c1", "a3"));
        System.out.println("c1 -> e3: " + cb.moveBishop("c1", "e3"));
        System.out.println("");
        System.out.println("f1 -> h3: " + cb.moveBishop("f1", "h3"));
        System.out.println("f1 -> d3: " + cb.moveBishop("f1", "d3"));
        System.out.println("");
        System.out.println("c8 -> a6: " + cb.moveBishop("c8", "a6"));
        System.out.println("c8 -> e6: " + cb.moveBishop("c8", "e6"));
        System.out.println("");
        System.out.println("f8 -> h6: " + cb.moveBishop("f8", "h6"));
        System.out.println("f8 -> d6: " + cb.moveBishop("f8", "d6"));
        System.out.println("------------------------------------\n");

        System.out.println("Moving pawns out of the way...\n");
        cb.makeMove("b2", "b4");
        cb.makeMove("d2", "d4");
        cb.makeMove("e2", "e4");
        cb.makeMove("g2", "g4");
        cb.makeMove("b7", "b5");
        cb.makeMove("d7", "d5");
        cb.makeMove("e7", "e5");
        cb.makeMove("g7", "g5");
        System.out.println("------------------------------------\n");

        System.out.println(cb.getBoard(true));

        System.out.println("------------------------------------\n");

        if (cb.moveBishop("c1", "a3"))
            System.out.println("c1 -> a3");
        if (cb.moveBishop("c1", "e3"))
            System.out.println("c1 -> e3");
        if (cb.moveBishop("f1", "d3"))
            System.out.println("f1 -> d3");
        if (cb.moveBishop("f1", "h3"))
            System.out.println("f1 -> h3");
        if (cb.moveBishop("c8", "a6"))
            System.out.println("c8 -> a6");
        if (cb.moveBishop("c8", "e6"))
            System.out.println("c8 -> e6");
        if (cb.moveBishop("f8", "d6"))
            System.out.println("f8 -> d6");
        if (cb.moveBishop("f8", "h6"))
            System.out.println("f8 -> h6");

        System.out.println("------------------------------------\n");        
    }
}