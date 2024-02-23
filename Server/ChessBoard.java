import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class ChessBoard
{
    private char[][] board = new char[10][10]; // declare the 8x8 board plus tile coordinates.
    private int boardWidth = 10;

    final char emptyTile = '·';
    final char black = 1; // Denoted as an LOWER case character.
    final char white = 2; // Denoted as a UPPER case character
    
    // Following vriables relates to the undo move feature:
    char[] prevMovedPieces = {'.', '.'};
    String[] prevMovedLocs = {"00", "00"};

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

        // place all white pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[2][i] = 'P'; // Pawns
        board[1][1] = 'R'; board[1][8] = 'R'; // Rooks
        board[1][2] = 'N'; board[1][7] = 'N'; // Knights
        board[1][3] = 'B'; board[1][6] = 'B'; // Bishops
        board[1][4] = 'Q'; // Queen
        board[1][5] = 'K'; // King

        // place all black pieces onto the board:
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
            case 'p':
                return '\u265F';
            case 'P':
                return '\u2659';
            case 'r':
                return '\u265C';
            case 'R':
                return '\u2656';
            case 'n':
                return '\u265E';
            case 'N':
                return '\u2658';
            case 'b':
                return '\u265D';
            case 'B':
                return '\u2657';
            case 'q':
                return '\u265B';
            case 'Q':
                return '\u2655';
            case 'k':
                return '\u265A';
            case 'K':
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
            return white;
        else if (Character.isLowerCase(piece))
            return black;
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

    /**
     * Converts indexing coordinates into chess coordinates.
     * 
     * @return Integer array of length 2 in format [x, y] such that <code>board</code> is accessed using <code>board[x][y]</code>
     */
    public int[] convertCoords(String coord)
    {
        char letter = coord.charAt(0);
        char num = coord.charAt(1);
        int[] returnArr = { (int) ('9' - num), (int) (letter + 1 - 'a') };
        return returnArr;
    }

    /**
     * Check if the string is of valid format.
     * @param input The string being tested.
     * @return true if the string is valid. false if not.
     */
    private boolean isValidCoord(String input) { return Pattern.matches("[AaBbCcDdEeFfGgHh][1-8]", input); }

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
                return "Invalid Move: cannot move piece in that location.";
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
        char movingPiece = getPieceAtLoc(current_loc);
        char takenPiece = getPieceAtLoc(new_loc);

        prevMovedPieces[0] = movingPiece;
        prevMovedPieces[1] = takenPiece;

        int current_x_index = (int) (current_loc.charAt(0) - 'a' + 1);
        int current_y_index = (int) ('9' - current_loc.charAt(1));
        int new_x_index = (int) (new_loc.charAt(0) - 'a' + 1);
        int new_y_index = (int) ('9' - new_loc.charAt(1));

        board[current_y_index][current_x_index] = emptyTile;
        board[new_y_index][new_x_index] = movingPiece;

        prevMovedLocs[0] = current_loc;
        prevMovedLocs[1] = new_loc;
    }

    /**
     * Undo the most recent move.
     * @return int, 0 upon success, otherwise upon failure.
     */
    public int reverseMove()
    {
        if ( !isValidCoord(prevMovedLocs[0]) || !isValidCoord(prevMovedLocs[1]) )
            return 1;

        
        int[] prevStartLoc = convertCoords(prevMovedLocs[0]);
        int[] prevEndLoc = convertCoords(prevMovedLocs[1]);


        board[prevStartLoc[0]][prevStartLoc[1]] = prevMovedPieces[0];
        board[prevEndLoc[0]][prevEndLoc[1]] = prevMovedPieces[1];

        prevMovedLocs[0] = "00"; prevMovedLocs[1] = "00"; // Reset prevMovedLocs to invalid values.

        return 0;
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
     * @param forWhite Boolean value saying if white is being tested.
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
                    makeMove(kingsLoc, neighbour);
                    if ( !isInCheck(forWhite, neighbour) )
                    {
                        reverseMove();
                        return true;
                    }
                    else
                       reverseMove();
                }
            }
        }

        return false;
    }

    /**
     * Tests whether the check can be obstructed.
     * @param forWhite Boolean value saying if white is being tested.
     * @return true if the path between the king and the piece threatening check can be obstructed, false otehrwise.
     */
    private Boolean checkCanBeObstructed(Boolean forWhite, String kingsLoc)
    {
        int attackingColour = (forWhite ? black : white); // get the colour of the pieces that are checking the king.
        int defendingColour = (forWhite ? white : black);

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

        if (threats.size() > 1) // make sure the player is not in double check
            return false;

        // create array of tiles between the first threatening piece and the king:
        String attacking_loc = threats.get(0);

        char king_x = kingsLoc.charAt(0);
        char king_y = kingsLoc.charAt(1);

        char threateningPiece = getPieceAtLoc(attacking_loc);

        switch ( Character.toLowerCase(threateningPiece) )
        {
            case 'n': // Knights path cannot be obstructed. We only need to check if the piece can be taken.
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
            

            default:
                char attacking_x = attacking_loc.charAt(0);
                char attacking_y = attacking_loc.charAt(1);

                while (attacking_x != king_x || attacking_y != king_y)
                {
                    String locInPath = Character.toString(attacking_x) + Character.toString(attacking_y);

                    // Iterate through all tiles on the board, and see if each defending piece can move into the path:
                    for (int row = 1; row < boardWidth-1; row++)
                    {
                        for (int col = 1; col < boardWidth-1; col++)
                        {
                            String defending_loc = convertCoords(row, col);
                            if (getColourOfPiece(getPieceAtLoc(defending_loc)) == defendingColour && Character.toLowerCase(getPieceAtLoc(defending_loc)) != 'k')
                            {
                                if (movePiece(defending_loc, locInPath) == null)
                                {
                                    makeMove(defending_loc, locInPath);

                                    if (!isInCheck(forWhite, kingsLoc))
                                    {
                                        reverseMove();
                                        return true;
                                    }
                                    else
                                        reverseMove();
                                }
                            }
                        }
                    }
                    
                    if (attacking_x < king_x)       { attacking_x++; }
                    else if (attacking_x > king_x)  { attacking_x--; }
                    if (attacking_y < king_y)       { attacking_y++; }
                    else if (attacking_y > king_y)  { attacking_y--; }
                }

                break;                
        }
        
        return false;
    }

    /**
     * Calculate whether the player is in check, checkmate, or neither.
     * @param forWhite Boolean value 
     * @return 0 if not in check, 1 if in check, 2 if in checkmate, 3 if error occurs.
     */
    public int isInCheckOrCheckmate(Boolean forWhite, Boolean verbose)
    {
        char king = (forWhite ? 'K' : 'k');
        
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
        if( verbose )
            System.out.println("King Location: " + kingsLoc);

        Boolean is_in_check = isInCheck(forWhite, kingsLoc);
        if (!is_in_check) // If the player is in check.
        {
            if( verbose )
                System.out.println("isInCheck: " + is_in_check);
            return 0;
        }
        Boolean king_can_escape = kingCanEscapeCheck(forWhite, kingsLoc);
        Boolean check_can_be_obstructed = checkCanBeObstructed(forWhite, kingsLoc);
        if (king_can_escape || check_can_be_obstructed) // If check can be avoided next move.
        {
            if( verbose )
                System.out.println("kingCanEscapeCheck: " + king_can_escape + "\ncheckCanBeObstructed: " + check_can_be_obstructed);
            return 1;
        }
        else // If the player is in checkmate.
            return 2;
    }
    
    /*****************************************************************************************************/


    /*****************************************************************************************************/
    /* The followig functions are to do with checking for Draws (including Stalemate):                   */
    /*****************************************************************************************************/
    public Boolean stalemate()
    {
        // Iterate through all the pieces and check that they can be move.
        // The algorithm will only test moves directly accessible to each piece.
        for( int row = 1; row < boardWidth-1; row++)
        {
            for( int col = 1; col < boardWidth-1; col++)
            {
                char piece = getPieceAtLoc( convertCoords(row, col) );
                if( piece != emptyTile )
                {
                    String[] neighbours = new String[8];
                    int length;
                    switch( piece )
                    {
                        //  Lower case = black and moves with increasing  row value.
                        //  Upper case = white and moves with decreasing row value.

                        // Rooks are unidirectional:
                        case 'p':
                            length = 3;
                            neighbours[0] = convertCoords(row - 1, col - 1);
                            neighbours[1] = convertCoords(row - 1, col);
                            neighbours[2] = convertCoords(row - 1, col + 1);
                            break;

                        case 'P':
                            length = 3;
                            neighbours[0] = convertCoords(row + 1, col - 1);
                            neighbours[1] = convertCoords(row + 1, col);
                            neighbours[2] = convertCoords(row + 1, col + 1);
                            break;

                        // Rooks, Knights, Bishops, Queens, Kings are bidirectional:
                        case 'r':
                        case 'R':
                            length = 2;
                            neighbours[0] = convertCoords(row - 1, col);
                            neighbours[1] = convertCoords(row + 1, col);
                            break;

                        case 'n':
                        case 'N':
                            length = 8;
                            neighbours[0] = convertCoords(row - 2, col - 1); // up 2 left 1.
                            neighbours[1] = convertCoords(row - 2, col + 1); // up 2 right 1.
                            neighbours[2] = convertCoords(row - 1, col + 2); // right 2 up 1.
                            neighbours[3] = convertCoords(row + 1, col + 2); // right 2 down 1.
                            neighbours[4] = convertCoords(row + 2, col - 1); // down 2 left 1.
                            neighbours[5] = convertCoords(row + 2, col + 1); // down 2 right 1.
                            neighbours[6] = convertCoords(row + 1, col - 2); // right 2 down 1.
                            neighbours[7] = convertCoords(row - 1, col - 2); // right 2 up 1.
                            break;

                        case 'b':
                        case 'B':
                            length = 4;
                            neighbours[0] = convertCoords(row - 1, col - 1);
                            neighbours[1] = convertCoords(row - 1, col + 1);
                            neighbours[2] = convertCoords(row + 1, col - 1);
                            neighbours[3] = convertCoords(row + 1, col + 1);
                            break;

                        case 'q':
                        case 'Q':
                            length = 8;
                            neighbours[0] = convertCoords(row - 1, col - 1);
                            neighbours[1] = convertCoords(row - 1, col);
                            neighbours[2] = convertCoords(row - 1, col + 1);
                            neighbours[3] = convertCoords(row, col + 1);
                            neighbours[4] = convertCoords(row, col - 1);
                            neighbours[5] = convertCoords(row + 1, col - 1);
                            neighbours[6] = convertCoords(row + 1, col);
                            neighbours[7] = convertCoords(row + 1, col + 1);
                            break;

                        case 'k':
                        case 'K':
                            length = 8;
                            neighbours[0] = convertCoords(row - 1, col - 1);
                            neighbours[1] = convertCoords(row - 1, col);
                            neighbours[2] = convertCoords(row - 1, col + 1);
                            neighbours[3] = convertCoords(row, col + 1);
                            neighbours[4] = convertCoords(row, col - 1);
                            neighbours[5] = convertCoords(row + 1, col - 1);
                            neighbours[6] = convertCoords(row + 1, col);
                            neighbours[7] = convertCoords(row + 1, col + 1);
                            break;
                        default:
                            length = 8;
                            break;
                    }

                    for( int i = 0; i < length; i++ )
                    {
                        if( movePiece(convertCoords(row, col), neighbours[i]) == null )
                            return false;
                    }
                }
            }
        }
        return true;
    }
    /*****************************************************************************************************/


    public static void main(String[] args)
    {
        //
        // TEST STALEMATE:
        //
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";


        ChessBoard cb = new ChessBoard();
        cb.createNewBoard();

        System.out.println(cb.stalemate());
    }
}
