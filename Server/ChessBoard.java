import java.lang.Math.*;

public class ChessBoard
{
    private char[][] board = new char[10][10]; // declare the 8x8 board plus tile coordinates.
    private int boardWidth = 10;

    final char emptyTile = '·';
    final char black = 1;
    final char white = 2;

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
        if (Character.isLowerCase(piece))
            return white;
        else if (Character.isUpperCase(piece))
            return black;
        else
            return 0;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    public Boolean movePawn(String current_loc, String new_loc)
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
        {
            makeMove(current_loc, new_loc);
            return true;
        }

        return false;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    public Boolean moveRook(String current_loc, String new_loc)
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

        makeMove(current_loc, new_loc);
        return true;
    }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    public Boolean moveKnight(String current_loc, String new_loc)
    {
        System.out.println("Starting Knight Move...");

        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece( getPieceAtLoc(current_loc) );
        int newColour = getColourOfPiece( getPieceAtLoc(new_loc) );

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);

        System.out.println("x_dif = " + x_dif + "\ty_dif = " + y_dif);

        if ( ( ((x_dif == 2) && (y_dif == 1)) || ((x_dif == 1) && (y_dif == 2)) ) && currentColour != newColour )
        {
            makeMove(current_loc, new_loc);
            return true;
        }
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
    public Boolean bishopCanMove(String current_loc, String new_loc) { return false; }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    public Boolean queenCanMove(String current_loc, String new_loc) { return false; }

    /**
     * Check if the piece can legally move to the new location.
     * @param current_loc The current location of the piece.
     * @param new_loc The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    public Boolean kingCanMove(String current_loc, String new_loc) { return false; }

    /*******************************************************************************************************/

    private void makeMove(String current_loc, String new_loc)
    {
        char piece = getPieceAtLoc(current_loc);

        int current_x_index = (int) (current_loc.charAt(0) - 'a' + 1);
        int current_y_index = (int) ('9' - current_loc.charAt(1));
        int new_x_index = (int) (new_loc.charAt(0) - 'a' + 1);
        int new_y_index = (int) ('9' - new_loc.charAt(1));

        board[current_y_index][current_x_index] = emptyTile;
        board[new_y_index][new_x_index] = piece;
    }

}
