import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.lang.Exception;

public class ChessBoard {
    private char[][] board = new char[10][10]; // declare the 8x8 board plus tile coordinates.
    private int boardWidth = 10;

    final char emptyTile = '·';
    final int black = 1; // Denoted as an LOWER case character.
    final int white = 2; // Denoted as a UPPER case character

    // Following vriables relates to the undo move feature:
    char[] prevMovedPieces = { '.', '.' };
    String[] prevMovedLocs = { "00", "00" };

    /**
     * Initialise the board with all the pieces in their starting positions.
     */
    public void createNewBoard() {
        // fill all tiles with blanck space:
        for (int row = 0; row < boardWidth; row++) {
            for (int col = 0; col < boardWidth; col++)
                board[row][col] = emptyTile;
        }

        board[0][0] = ' ';
        board[0][9] = ' ';
        board[9][0] = ' ';
        board[9][9] = ' ';

        // fill the first and last column with row letters:
        for (int i = 1; i < 9; i++) {
            board[i][0] = (char) ('9' - i);
            board[i][9] = (char) ('9' - i);
        }

        // fill the first and last row with column numbers:
        for (int i = 1; i < 9; i++) {
            board[0][i] = (char) ('a' + i - 1);
            board[9][i] = (char) ('a' + i - 1);
        }

        // The peices will be represented by the starting letter of the piece (i.e. pawn
        // will be shown with a 'P' or 'p').
        // The characters will either be in capital or lower case; capital case
        // represents white, lowercase represents black.

        // place all black pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[2][i] = 'P'; // Pawns
        board[1][1] = 'R';
        board[1][8] = 'R'; // Rooks
        board[1][2] = 'N';
        board[1][7] = 'N'; // Knights
        board[1][3] = 'B';
        board[1][6] = 'B'; // Bishops
        board[1][4] = 'Q'; // Queen
        board[1][5] = 'K'; // King

        // place all white pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[7][i] = 'p'; // Pawns
        board[8][1] = 'r';
        board[8][8] = 'r'; // Rooks
        board[8][2] = 'n';
        board[8][7] = 'n'; // Knights
        board[8][3] = 'b';
        board[8][6] = 'b'; // Bishops
        board[8][4] = 'q'; // Queen
        board[8][5] = 'k'; // King
    }

    /**
     * Convert a character into its ASCII symbol.
     * 
     * @param chr The character being translated into ascii representation.
     * @return The translated ASCII character.
     */
    private char translateCharacter(char chr) {
        switch (chr) {
            case 'p':
                return '\u265F'; // Black pawn.
            case 'P':
                return '\u2659'; // White pawn.
            case 'r':
                return '\u265C'; // Black rook.
            case 'R':
                return '\u2656'; // White rook.
            case 'n':
                return '\u265E'; // Black knight.
            case 'N':
                return '\u2658'; // White knight.
            case 'b':
                return '\u265D'; // Black bishop.
            case 'B':
                return '\u2657'; // White bishop.
            case 'q':
                return '\u265B'; // Black queen.
            case 'Q':
                return '\u2655'; // White queen.
            case 'k':
                return '\u265A'; // Black king.
            case 'K':
                return '\u2654'; // White king.
            default:
                return chr;
        }
    }

    /**
     * Convert the board into a string.
     * 
     * @param client Boolean value saying whether the board will be created in teh
     *               perspective of client 1 (white).
     * @return the chessboard in the form of a string.
     */
    public String getBoard(Boolean client1View) {
        String boardString = "";

        if (client1View) {
            for (int row = 0; row < boardWidth; row++) {
                boardString = boardString + translateCharacter(board[row][0]);

                for (int col = 1; col < boardWidth; col++)
                    boardString = boardString + ' ' + translateCharacter(board[row][col]);

                boardString = boardString + '\n';
            }
        } else {
            for (int row = boardWidth - 1; row >= 0; row--) {
                boardString = boardString + translateCharacter(board[row][boardWidth - 1]);

                for (int col = boardWidth - 2; col >= 0; col--)
                    boardString = boardString + ' ' + translateCharacter(board[row][col]);

                boardString = boardString + '\n';
            }
        }

        return boardString;
    }

    /**
     * Get the piece that is at the location.
     * 
     * @param x The x label of the location.
     * @param y The y label of the location.
     * @return The character at the location.
     */
    public char getPieceAtLoc(char x, char y) {
        x = Character.toLowerCase(x);
        y = Character.toLowerCase(y);
        int row = (int) ('9' - y);
        int col = (int) (x - 'a' + 1);

        return board[row][col];
    }

    /**
     * Get the piece that is at the location.
     * 
     * @param location The location.
     * @return Teh character at teh location.
     */
    public char getPieceAtLoc(String location) {
        location = location.toLowerCase();
        int row = (int) '9' - location.charAt(1);
        int col = (int) location.charAt(0) - 'a' + 1;

        return board[row][col];
    }

    /**
     * Get the colour of a given piece.
     * 
     * @param piece The piece being tested.
     * @return The integer value of the colour of the piece.
     */
    private int getColourOfPiece(char piece) {
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
    public String convertCoords(int row, int col) {
        return Character.toString((char) col - 1 + 'a') + Character.toString((char) '9' - row);
    }

    /**
     * Converts indexing coordinates into chess coordinates.
     * 
     * @return Integer array of length 2 in format [x, y] such that
     *         <code>board</code> is accessed using <code>board[x][y]</code>
     */
    public int[] convertCoords(String coord) {
        char letter = coord.charAt(0);
        char num = coord.charAt(1);
        int[] returnArr = { (int) ('9' - num), (int) (letter + 1 - 'a') };
        return returnArr;
    }

    /**
     * Check if the string is of valid format.
     * 
     * @param input The string being tested.
     * @return true if the string is valid. false if not.
     */
    private boolean isValidCoord(String input) {
        return Pattern.matches("[AaBbCcDdEeFfGgHh][1-8]", input);
    }

    /*****************************************************************************************************/
    /* The following functions implement the move validators. */
    /*****************************************************************************************************/

    /**
     * The interface function linking all piece specific move validators.
     * 
     * @param current_loc The current location of the piece about to be moved.
     * @param new_loc     The location where the piece is being moved to.
     * @return String describing the error. If no error occurs null is returned.
     */
    public String movePiece(String current_loc, String new_loc) {
        char pieceBeingMoved = getPieceAtLoc(current_loc);

        if (current_loc.toLowerCase() == new_loc)
            return "Invalid Move: piece has to move.";

        switch (Character.toLowerCase(pieceBeingMoved)) {
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

        makeMove(current_loc, new_loc);
        return null;
    }

    /**
     * Check if the piece can legally move to the new location.
     * 
     * @param current_loc The current location of the piece.
     * @param new_loc     The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean movePawn(String current_loc, String new_loc) {
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
        if (pawnColour == black) {
            if (current_y - 1 == new_y) // is the pawn moving one tile forward?
            {
                // if the new location is forward one tile and if the next tile is empty.
                if (current_x == new_x && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward right diagonal tile?
                if (current_x - 1 == new_x && newPieceColour == white)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward left diagonal tile?
                if (current_x + 1 == new_x && newPieceColour == white)
                    moveIsLegal = true;
            }

            // if the pawn is at its starting row.
            else if (current_y == '7') {
                // if the pawn is jumping 2 steps forward and the jump path is clear.
                char nextTile = getPieceAtLoc(current_x, (char) (current_y - 1));
                if (current_y - 2 == new_y && nextTile == emptyTile && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;
            }
        } else if (pawnColour == white) {

            if (current_y + 1 == new_y) // is the pawn moving one tile forward
            {
                // if the new location is forward one tile and if the next tile is empty.
                if (current_x == new_x && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward right diagonal tile?
                if (current_x + 1 == new_x && newPieceColour == black)
                    moveIsLegal = true;

                // is the move capturing an opponents piece on the forward left diagonal tile?
                if (current_x - 1 == new_x && newPieceColour == black)
                    moveIsLegal = true;
            }

            // if the pawn is at its starting row.
            else if (current_y == '2') {
                // if the pawn is jumping 2 steps forward and the jump path is clear.
                char nextTile = getPieceAtLoc(current_x, (char) (current_y + 1));
                if (current_y + 2 == new_y && nextTile == emptyTile && pieceAtNewLoc == emptyTile)
                    moveIsLegal = true;
            }
        }

        if (moveIsLegal)
            return true;

        return false;
    }

    /**
     * Check if the piece can legally move to the new location.
     * 
     * @param current_loc The current location of the piece.
     * @param new_loc     The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveRook(String current_loc, String new_loc) {
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
            if (new_y > current_y) {
                a = new_y;
                b = current_y;
            } else {
                a = current_y;
                b = new_y;
            }

            // check path is clear
            for (int i = (int) b + 1; i < (int) a; i++) {
                if (getPieceAtLoc(current_x, (char) i) != emptyTile)
                    return false;
            }

            // check the final destination of the new location
            if (rookColour == newPieceColour)
                return false;
        } else if (new_y == current_y) // if the rook is moving along the x axis (side to side).
        {
            char a, b;
            if (new_x > current_x) {
                a = new_x;
                b = current_x;
            } else {
                a = current_x;
                b = new_x;
            }

            // check path is clear
            for (int i = (int) b + 1; i < (int) a; i++) {
                if (getPieceAtLoc((char) i, current_y) != emptyTile)
                    return false;
            }

            // check the final destination of the new location
            if (rookColour == newPieceColour)
                return false;
        } else
            return false;

        return true;
    }

    /**
     * Check if the piece can legally move to the new location.
     * 
     * @param current_loc The current location of the piece.
     * @param new_loc     The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveKnight(String current_loc, String new_loc) {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece(getPieceAtLoc(current_loc));
        int newColour = getColourOfPiece(getPieceAtLoc(new_loc));

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);

        if ((((x_dif == 2) && (y_dif == 1)) || ((x_dif == 1) && (y_dif == 2))) && currentColour != newColour)
            return true;
        else
            return false;
    }

    /**
     * Check if the piece can legally move to the new location.
     * 
     * @param current_loc The current location of the piece.
     * @param new_loc     The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveBishop(String current_loc, String new_loc) {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece(getPieceAtLoc(current_loc));
        int newColour = getColourOfPiece(getPieceAtLoc(new_loc));

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);

        if ((x_dif != y_dif) || (x_dif == 0) || (y_dif == 0))
            return false;
        else {
            // check path is empty:
            char x, y;
            if ((int) current_x < (int) new_x) {
                x = (char) ((int) current_x + 1);
            } else {
                x = (char) ((int) current_x - 1);
            }
            if ((int) current_y < (int) new_y) {
                y = (char) ((int) current_y + 1);
            } else {
                y = (char) ((int) current_y - 1);
            }

            while (x != new_x && y != new_y) {
                if (getPieceAtLoc(x, y) != emptyTile) {
                    return false;
                }

                if (x < new_x) {
                    x = (char) ((int) x + 1);
                } else {
                    x = (char) ((int) x - 1);
                }
                if (y < new_y) {
                    y = (char) ((int) y + 1);
                } else {
                    y = (char) ((int) y - 1);
                }
            }

            if (currentColour == newColour) {
                return false;
            } else
                return true;
        }
    }

    /**
     * Check if the piece can legally move to the new location.
     * 
     * @param current_loc The current location of the piece.
     * @param new_loc     The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveQueen(String current_loc, String new_loc) {
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
     * 
     * @param current_loc The current location of the piece.
     * @param new_loc     The new location the piece is being sent to.
     * 
     * @return true if the move is legal, false otherwise.
     */
    private Boolean moveKing(String current_loc, String new_loc) {
        // split locations into seperate cordinates:
        char current_x = current_loc.charAt(0);
        char current_y = current_loc.charAt(1);
        char new_x = new_loc.charAt(0);
        char new_y = new_loc.charAt(1);
        int currentColour = getColourOfPiece(getPieceAtLoc(current_loc));
        int newColour = getColourOfPiece(getPieceAtLoc(new_loc));

        int x_dif = Math.abs(current_x - new_x);
        int y_dif = Math.abs(current_y - new_y);

        if (x_dif + y_dif == 0)
            return false;
        else if ((x_dif <= 1 && y_dif <= 1) && (currentColour != newColour))
            return true;
        else
            return false;
    }

    /**
     * Move the piece from its current location to the new location.
     * 
     * @param current_loc The location of the piece being moved.
     * @param new_loc     The new location the piece is being moved to.
     */
    public void makeMove(String current_loc, String new_loc) {
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
     * 
     * @return int, 0 upon success, otherwise upon failure.
     */
    public int reverseMove() {
        if (!isValidCoord(prevMovedLocs[0]) || !isValidCoord(prevMovedLocs[1]))
            return 1;

        int[] prevStartLoc = convertCoords(prevMovedLocs[0]);
        int[] prevEndLoc = convertCoords(prevMovedLocs[1]);

        board[prevStartLoc[0]][prevStartLoc[1]] = prevMovedPieces[0];
        board[prevEndLoc[0]][prevEndLoc[1]] = prevMovedPieces[1];

        prevMovedLocs[0] = "00";
        prevMovedLocs[1] = "00"; // Reset prevMovedLocs to invalid values.

        return 0;
    }
    /*****************************************************************************************************/

    /*****************************************************************************************************/
    /* The following code implements the feature that tests for check. */
    /*****************************************************************************************************/

    /**
     * Tests if any attacking piece threatens the king - i.e. tests if a player is
     * in checks.
     * 
     * @param forWhite Boolean value saying if white is being tested.
     * @return true if in check, false if not in check.
     */
    private Boolean isInCheck(Boolean forWhite, String kingsLoc) {
        int attackingColour = (forWhite ? black : white); // get the colour of the pieces that are checking the king.

        // Iterate through the attacking pieces:
        for (int row = 1; row < boardWidth - 1; row++) {
            for (int col = 1; col < boardWidth - 1; col++) {
                char piece = board[row][col];
                if (getColourOfPiece(piece) == attackingColour) {
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
     * 
     * @param forWhite Boolean value saying if white the white king is being tested.
     * @return true if the king can move out of check, false if the king cnanot.
     */
    private Boolean kingCanEscapeCheck(Boolean forWhite, String kingsLoc) {
        // Create array of neighbours of the king.
        String[] neighbours = new String[8];
        neighbours[0] = Character.toString(kingsLoc.charAt(0) + 1) + Character.toString(kingsLoc.charAt(1) - 1); // Top
                                                                                                                 // left
                                                                                                                 // diagonal
                                                                                                                 // neighbour.
        neighbours[1] = Character.toString(kingsLoc.charAt(0) + 1) + Character.toString(kingsLoc.charAt(1)); // Above
                                                                                                             // neighbour.
        neighbours[2] = Character.toString(kingsLoc.charAt(0) + 1) + Character.toString(kingsLoc.charAt(1) + 1); // Top
                                                                                                                 // right
                                                                                                                 // diagonal
                                                                                                                 // neighbour.
        neighbours[3] = Character.toString(kingsLoc.charAt(0)) + Character.toString(kingsLoc.charAt(1) + 1); // Right
                                                                                                             // neighbour.
        neighbours[4] = Character.toString(kingsLoc.charAt(0) - 1) + Character.toString(kingsLoc.charAt(1) + 1); // Bottom
                                                                                                                 // right
                                                                                                                 // neighbour.
        neighbours[5] = Character.toString(kingsLoc.charAt(0) - 1) + Character.toString(kingsLoc.charAt(1)); // Below
                                                                                                             // neighbour.
        neighbours[6] = Character.toString(kingsLoc.charAt(0) - 1) + Character.toString(kingsLoc.charAt(1) - 1); // Bottom
                                                                                                                 // left
                                                                                                                 // neighbour.
        neighbours[7] = Character.toString(kingsLoc.charAt(0)) + Character.toString(kingsLoc.charAt(1) - 1); // Left
                                                                                                             // neighbour.

        // Iterate through the neighbours and check if the move is valid.
        for (String neighbour : neighbours) {
            if (Pattern.matches("[AaBbCcDdEeFfGgHh][1-8]", neighbour)) {
                if (moveKing(kingsLoc, neighbour)) // can the king move to the neighbour.
                {
                    makeMove(kingsLoc, neighbour);
                    if (!isInCheck(forWhite, neighbour)) {
                        reverseMove();
                        return true;
                    } else
                        reverseMove();
                }
            }
        }

        return false;
    }

    /**
     * Tests whether the check can be obstructed.
     * 
     * @param forWhite Boolean value. Is white the defending colour.
     * @return true if the path between the king and the piece threatening check can
     *         be obstructed, false otehrwise.
     */
    private Boolean checkCanBeObstructed(Boolean forWhite, String kingsLoc) {
        int attackingColour = (forWhite ? black : white); // get the colour of the pieces that are checking the king.
        int defendingColour = (forWhite ? white : black);

        // find the piece/ pieces threatening check:
        List<String> threats = new ArrayList<>(); // create a list to hold the coordinates that threaten check.
        for (int row = 1; row < boardWidth - 1; row++) {
            for (int col = 1; col < boardWidth - 1; col++) {
                char piece = board[row][col];
                if (getColourOfPiece(piece) == attackingColour) {
                    String current_loc = convertCoords(row, col);
                    if (movePiece(current_loc, kingsLoc) == null)
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

        switch (Character.toLowerCase(threateningPiece)) {
            case 'n': // Knights path cannot be obstructed. We only need to check if the piece can be
                      // taken.
                // Iterate through all defending pieces and see if they can take the knight:
                for (int row = 1; row < boardWidth - 1; row++) {
                    for (int col = 1; col < boardWidth - 1; col++) {
                        String defending_loc = convertCoords(row, col);
                        if (getColourOfPiece(getPieceAtLoc(defending_loc)) != attackingColour) // if the piece belongs
                                                                                               // to the defending
                                                                                               // player.
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

                while (attacking_x != king_x || attacking_y != king_y) {
                    String locInPath = Character.toString(attacking_x) + Character.toString(attacking_y);

                    // Iterate through all tiles on the board, and see if each defending piece can
                    // move into the path:
                    for (int row = 1; row < boardWidth - 1; row++) {
                        for (int col = 1; col < boardWidth - 1; col++) {
                            String defending_loc = convertCoords(row, col);
                            if (getColourOfPiece(getPieceAtLoc(defending_loc)) == defendingColour
                                    && Character.toLowerCase(getPieceAtLoc(defending_loc)) != 'k') {
                                if (movePiece(defending_loc, locInPath) == null) {
                                    if (!isInCheck(forWhite, kingsLoc)) {
                                        reverseMove();
                                        return true;
                                    } else
                                        reverseMove();
                                }
                            }
                        }
                    }

                    if (attacking_x < king_x) {
                        attacking_x++;
                    } else if (attacking_x > king_x) {
                        attacking_x--;
                    }
                    if (attacking_y < king_y) {
                        attacking_y++;
                    } else if (attacking_y > king_y) {
                        attacking_y--;
                    }
                }

                break;
        }

        return false;
    }

    /**
     * Calculate whether the player is in check, checkmate, or neither.
     * 
     * @param forWhite Boolean value asking if white is being tested for being in
     *                 check or checkmate.
     * @return 0 if not in check, 1 if in check, 2 if in checkmate, 3 if error
     *         occurs.
     */
    public int isInCheckOrCheckmate(Boolean forWhite, Boolean verbose) {
        char king = (forWhite ? 'K' : 'k');
        if (verbose)
            System.out.println("Searching for: " + king);

        // get the location of the king:
        String kingsLoc = null;
        for (int row = 1; row < boardWidth; row++) {
            for (int col = 1; col < boardWidth; col++) {
                if (board[row][col] == king) {
                    kingsLoc = Character.toString((char) col - 1 + 'a') + Character.toString((char) '9' - row);
                    break;
                }
            }
        }

        if (kingsLoc == null) {
            System.err.println("ERROR: King not found.");
            return 3; // return error signal
        }
        if (verbose)
            System.out.println("King Location: " + kingsLoc);

        Boolean is_in_check = isInCheck(forWhite, kingsLoc);
        if (!is_in_check) // If the player is in check.
        {
            if (verbose)
                System.out.println("isInCheck: " + is_in_check);
            return 0;
        }
        Boolean king_can_escape = kingCanEscapeCheck(forWhite, kingsLoc);
        Boolean check_can_be_obstructed = checkCanBeObstructed(forWhite, kingsLoc);
        if (king_can_escape || check_can_be_obstructed) // If check can be avoided next move.
        {
            if (verbose)
                System.out.println("kingCanEscapeCheck: " + king_can_escape + "\ncheckCanBeObstructed: "
                        + check_can_be_obstructed);
            return 1;
        } else // If the player is in checkmate.
            return 2;
    }

    /*****************************************************************************************************/

    /*****************************************************************************************************/
    /*
     * The followig functions are to do with checking for Draws (including
     * Stalemate):
     */
    /*****************************************************************************************************/

    /**
     * test whether the game is in a stalemate.
     * 
     * @param whitesTurn boolean, the player we are checking is able to move.
     * @return int, 0 = No Stalemate, 1 = Stalemate, >1 = Error.
     */
    public int stalemate(Boolean whitesTurn, Boolean verbose) {
        // This will be seen as inefficient but a key part of a player being in
        // stalemate is the fact they are not in check or checkmate:
        try {
            if (isInCheckOrCheckmate(whitesTurn, false) != 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            int returnVal = isInCheckOrCheckmate(whitesTurn, false);
            System.out.printf("ERROR: stalemate() method. ");
            if( returnVal == 0 ) { System.out.println("Exception incorrectly called."); }
            else if( returnVal == 1 ) { System.out.printf("%s already in check.\n", (whitesTurn ? "White" : "Black")); }
            else if( returnVal == 2 ) { System.out.printf("%s already in checkmate.\n", (whitesTurn ? "White" : "Black")); }
            else { System.out.printf("isInCheckOrCheckmate() returns Error (code %d).\n", returnVal); }
            
            String errorMessage = "ERROR: Stalemate check called when " + (whitesTurn ? "white" : "black")
                    + " is in check/checkmate";
            System.err.println(errorMessage);
            return 2;
        }

        int whosTurn = (whitesTurn ? white : black);
        Boolean pieceDetected = false;

        if( verbose ) {
            System.out.printf("whosTurn = %d (%s)\n", whosTurn, ((whosTurn == white) ? "White" : "Black"));
        }

        // Iterate through all the pieces and check that they can be move.
        // The algorithm will only test moves directly accessible to each piece.
        for (int row = 1; row < boardWidth - 1; row++) {
            for (int col = 1; col < boardWidth - 1; col++) {
                char piece = getPieceAtLoc(convertCoords(row, col)); // Get the piece.
                if (getColourOfPiece(piece) == whosTurn) {
                    if( verbose ) {
                        System.out.printf("Piece: %c\n", piece);
                        System.out.printf(
                            "\tPiece Colour (%d) == whosTurn (%d)\n",
                            getColourOfPiece(piece),
                            whosTurn
                        );
                    }
                    pieceDetected = true;
                    String[] neighbours = new String[8];
                    int length;
                    switch (piece) {
                        // Lower case = black and moves with increasing row value.
                        // Upper case = white and moves with decreasing row value.

                        // pawns are unidirectional:
                        case 'P':
                            length = 3;
                            neighbours[0] = convertCoords(row + 1, col - 1);
                            neighbours[1] = convertCoords(row + 1, col);
                            neighbours[2] = convertCoords(row + 1, col + 1);
                            break;

                        case 'p':
                            length = 3;
                            neighbours[0] = convertCoords(row - 1, col - 1);
                            neighbours[1] = convertCoords(row - 1, col);
                            neighbours[2] = convertCoords(row - 1, col + 1);
                            break;

                        // Rooks, Knights, Bishops, Queens, Kings are bidirectional:
                        case 'r':
                        case 'R':
                            length = 4;
                            neighbours[0] = convertCoords(row - 1, col);
                            neighbours[1] = convertCoords(row + 1, col);
                            neighbours[2] = convertCoords(row, col - 1);
                            neighbours[3] = convertCoords(row, col + 1);
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
                            length = 0;
                            break;
                    }

                    if( length == 0 ) {
                        System.err.println("length = 0");
                        return 2;
                    }

                    if( verbose ) {
                        System.out.printf("\tlength = %d\n", length);
                    }

                    for (int i = 0; i < length; i++) {
                        // Output: testing [currentPieceCoords] -> [neighbourCoords] \t\t
                        // [pieceColour]==[neighbourColour]
                        if( verbose ) {
                            System.out.printf(
                                "testing %s -> %s\t\t%d == %d\n",
                                convertCoords(row, col),
                                neighbours[i],
                                getColourOfPiece(piece),
                                getColourOfPiece(getPieceAtLoc(neighbours[i]))
                            );
                        }
                        if (movePiece(convertCoords(row, col), neighbours[i]) == null) {
                            if( verbose ) {
                                System.out.println("\tThis move is legal...");
                                System.out.printf(
                                    "\tmove %s -> %s causes check: ",
                                    convertCoords(row, col),
                                    neighbours[i]
                                );
                                System.out.println("***************************************************");
                                System.out.println(getBoard(true));
                                System.out.println("***************************************************");
                                int check_or_mate = isInCheckOrCheckmate(whitesTurn, false);
                                if( check_or_mate == 0 ) { System.out.println("No"); }
                                else if( check_or_mate == 1 ) { System.out.println("Check"); }
                                else if( check_or_mate == 2 ) { System.out.println("Checkmate"); }
                                else { System.out.println("ERROR"); }
                            }
                            if (isInCheckOrCheckmate(whitesTurn, false) == 0) {
                                if( verbose ) { System.out.println("Valid move available, Stalemate = false"); }
                                reverseMove();
                                return 0;
                            } else
                                reverseMove();
                        }
                    }
                }
            }
        }
        if( !pieceDetected )
            return 2;
        return 1;
    }

    /*****************************************************************************************************/
    /* Testing Functons:                                                                                 */
    /*****************************************************************************************************/

    public static void test_createNewBoard() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 1;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();

        String[] rows = new String[10];
        rows[0] = " abcdefgh ";
        rows[1] = "8rnbqkbnr8";
        rows[2] = "7rrrrrrrr7";
        rows[3] = "6········6";
        rows[4] = "5········5";
        rows[5] = "4········4";
        rows[6] = "3········3";
        rows[7] = "2RRRRRRRR2";
        rows[8] = "1RNBQKBNR1";
        rows[9] = " abcdefgh ";

        char[][] expectedBoard = new char[10][10];
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expectedBoard[i][j] = rows[i].charAt(j);
            }
        }

        cb.createNewBoard();
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                if( cb.board[i][j] != expectedBoard[i][j] ) {
                    System.out.printf("%sFAIL:%s Incorrect board initialisation.\n", red, reset);
                    errorCount++;
                    // Output Boards:
                    System.out.println("EXPECTED:\t\tACTUAL:");
                    for( int r = 0; r < 10; r++ ) {
                        for( int c = 0; c < 10; c++ ) { System.out.print(expectedBoard[r][c]); }
                        System.out.print("\t\t");
                        for( int c = 0; c < 10; c++ ) { System.out.print(cb.board[r][c]); }
                        System.out.print("\n");
                    }
                    System.out.print("\n");
                    i = 100; j = 100;
                }
            }
        }

        if( errorCount == 0 ) {
            System.out.printf("createNewBoard: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("createNewBoard: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    public static void test_translateCharacter() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        String chars = "KQRBNPkqrbnp ·12345678";
        int charNum = chars.length();
        char[] expectedChars = new char[charNum];

        int totalTests = charNum;
        int errorCount = 0;
        
        ChessBoard cb = new ChessBoard();

        // Special characters expecting translation:
        expectedChars[0] = '\u2654';  expectedChars[1] = '\u2655';
        expectedChars[2] = '\u2656';  expectedChars[3] = '\u2657';
        expectedChars[4] = '\u2658';  expectedChars[5] = '\u2659';
        expectedChars[6] = '\u265A';  expectedChars[7] = '\u265B';
        expectedChars[8] = '\u265C';  expectedChars[9] = '\u265D';
        expectedChars[10] = '\u265E'; expectedChars[11] = '\u265F';

        // Characters that should not be translated:
        expectedChars[12] = ' '; expectedChars[13] = '·';
        expectedChars[14] = '1'; expectedChars[15] = '2';
        expectedChars[16] = '3'; expectedChars[17] = '4';
        expectedChars[18] = '5'; expectedChars[19] = '6';
        expectedChars[20] = '7'; expectedChars[21] = '8';

        char actual;
        for( int i = 0; i < charNum; i++ ) {
            actual = cb.translateCharacter(chars.charAt(i));
            if( actual != expectedChars[i] ) {
                System.out.printf("%sFAIL:%s Expected '%c', got '%c'\n", red, reset, expectedChars[i], actual);
                errorCount++;
            }
        }
        System.out.print("\n");

        if( errorCount == 0 ) {
            System.out.printf("translateCharacter: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("translateCharacter: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    // TODO: Continue with unit testing each method.
    // 
    // NOTE: These tests only test for method correctness, not efficiency. And the
    //  tests are not written in the most efficient manner but that is a hurdle
    //  for another time.
    public static void test_getBoard() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 6;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();
        String[] rows = new String[10];
        int rw = 20; // Row Width
        String expected;
        String actual;

        // TEST: Board with no pieces (white perspective):
        rows[0] = " abcdefgh ";
        rows[1] = "8········8";
        rows[2] = "7········7";
        rows[3] = "6········6";
        rows[4] = "5········5";
        rows[5] = "4········4";
        rows[6] = "3········3";
        rows[7] = "2········2";
        rows[8] = "1········1";
        rows[9] = " abcdefgh ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        expected = "";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expected += rows[i].charAt(j);
                if( j != 9 ) { expected += " "; }
                else { expected += "\n"; }
            }
        }
        actual = cb.getBoard(true);

        for( int i = 0; i < 10*10; i++ ) {
            if( expected.charAt(i) != actual.charAt(i) ) {
                errorCount++;
                System.out.print("\n");
                System.out.printf("%sFAIL:%s Incorrect output of empty board (white perspective).\n", red, reset);
                System.out.println("Expected:\t\tActual:");
                for( int c = 0; c < 10; c++ ) {
                    System.out.print(expected.substring(c*rw, (c*rw)+rw-1)); // Ignore new line.
                    System.out.print("\t\t");
                    System.out.print(actual.substring(c*rw, (c*rw)+rw));
                }
                break;
            }
        } 

        // TEST: Board with no pieces (black perspective):
        rows[0] = " hgfedcba ";
        rows[1] = "1········1";
        rows[2] = "2········2";
        rows[3] = "3········3";
        rows[4] = "4········4";
        rows[5] = "5········5";
        rows[6] = "6········6";
        rows[7] = "7········7";
        rows[8] = "8········8";
        rows[9] = " hgfedcba ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        expected = "";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expected += rows[i].charAt(j);
                if( j != 9 ) { expected += " "; }
                else { expected += "\n"; }
            }
        }
        actual = cb.getBoard(true);

        for( int i = 0; i < 10*10; i++ ) {
            if( expected.charAt(i) != actual.charAt(i) ) {
                errorCount++;
                System.out.print("\n");
                System.out.printf("%sFAIL:%s Incorrect output of empty board (black perspective).\n", red, reset);
                System.out.println("Expected:\t\tActual:");
                for( int c = 0; c < 10; c++ ) {
                    System.out.print(expected.substring(c*rw, (c*rw)+rw-1)); // Ignore new line.
                    System.out.print("\t\t");
                    System.out.print(actual.substring(c*rw, (c*rw)+rw));
                }
                break;
            }
        } 

        // TEST: Starting board from white perspective:
        //
        // WHITE KING   = '\u2654';                     WHITE QUEEN  = '\u2655';
        // WHITE ROOK   = '\u2656';                     WHITE BISHOP = '\u2657';
        // WHITE KNIGHT = '\u2658';                     WHITE PAWN   = '\u2659';
        //
        // BLACK KING   = '\u265A';                     BLACK QUEEN  = '\u265B';
        // BLACK ROOK   = '\u265C';                     BLACK BISHOP = '\u265D';
        // BLACK KNIGHT = '\u265E';                     BLACK PAWN   = '\u265F';
        rows[0] = " abcdefgh ";
        rows[1] = "8\u265C\u265E\u265D\u265B\u265A\u265D\u265E\u265C8";
        rows[2] = "7\u265F\u265F\u265F\u265F\u265F\u265F\u265F\u265F7";
        rows[3] = "6········6";
        rows[4] = "5········5";
        rows[5] = "4········4";
        rows[6] = "3········3";
        rows[7] = "2\u2659\u2659\u2659\u2659\u2659\u2659\u2659\u26592";
        rows[8] = "1\u2656\u2658\u2657\u2655\u2654\u2657\u2658\u26561";
        rows[9] = " abcdefgh ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        expected = "";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expected += rows[i].charAt(j);
                if( j != 9 ) { expected += " "; }
                else { expected += "\n"; }
            }
        }
        actual = cb.getBoard(true);

        for( int i = 0; i < 10*10; i++ ) {
            if( expected.charAt(i) != actual.charAt(i) ) {
                errorCount++;
                System.out.print("\n");
                System.out.printf("%sFAIL:%s Incorrect output of starting board (white perspective).\n", red, reset);
                System.out.println("Expected:\t\tActual:");
                for( int c = 0; c < 10; c++ ) {
                    System.out.print(expected.substring(c*rw, (c*rw)+rw-1)); // Ignore new line.
                    System.out.print("\t\t");
                    System.out.print(actual.substring(c*rw, (c*rw)+rw));
                }
                break;
            }
        } 

        // TEST: Starting board black perspective:
        //
        // WHITE KING   = '\u2654';                     WHITE QUEEN  = '\u2655';
        // WHITE ROOK   = '\u2656';                     WHITE BISHOP = '\u2657';
        // WHITE KNIGHT = '\u2658';                     WHITE PAWN   = '\u2659';
        //
        // BLACK KING   = '\u265A';                     BLACK QUEEN  = '\u265B';
        // BLACK ROOK   = '\u265C';                     BLACK BISHOP = '\u265D';
        // BLACK KNIGHT = '\u265E';                     BLACK PAWN   = '\u265F';
        rows[0] = " hgfedcba ";
        rows[1] = "1\u2656\u2658\u2657\u2655\u2654\u2657\u2658\u26561";
        rows[2] = "2\u2659\u2659\u2659\u2659\u2659\u2659\u2659\u26592";
        rows[3] = "3········3";
        rows[4] = "4········4";
        rows[5] = "5········5";
        rows[6] = "6········6";
        rows[7] = "7\u265F\u265F\u265F\u265F\u265F\u265F\u265F\u265F7";
        rows[8] = "8\u265C\u265E\u265D\u265B\u265A\u265D\u265E\u265C8";
        rows[9] = " hgfedcba ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        expected = "";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expected += rows[i].charAt(j);
                if( j != 9 ) { expected += " "; }
                else { expected += "\n"; }
            }
        }
        actual = cb.getBoard(true);

        for( int i = 0; i < 10*10; i++ ) {
            if( expected.charAt(i) != actual.charAt(i) ) {
                errorCount++;
                System.out.print("\n");
                System.out.printf("%sFAIL:%s Incorrect output of starting board (black perspective).\n", red, reset);
                System.out.println("Expected:\t\tActual:");
                for( int c = 0; c < 10; c++ ) {
                    System.out.print(expected.substring(c*rw, (c*rw)+rw-1)); // Ignore new line.
                    System.out.print("\t\t");
                    System.out.print(actual.substring(c*rw, (c*rw)+rw));
                }
                break;
            }
        } 

        // TEST: Board with every tile containing a piece from white perspective:
        //
        // WHITE KING   = '\u2654';                     WHITE QUEEN  = '\u2655';
        // WHITE ROOK   = '\u2656';                     WHITE BISHOP = '\u2657';
        // WHITE KNIGHT = '\u2658';                     WHITE PAWN   = '\u2659';
        //
        // BLACK KING   = '\u265A';                     BLACK QUEEN  = '\u265B';
        // BLACK ROOK   = '\u265C';                     BLACK BISHOP = '\u265D';
        // BLACK KNIGHT = '\u265E';                     BLACK PAWN   = '\u265F';
        rows[0] = " abcdefgh ";
        rows[1] = "8\u265C\u265E\u265D\u265B\u265A\u265D\u265E\u265C8";
        rows[2] = "7\u265F\u265F\u265F\u265F\u265F\u265F\u265F\u265F7";
        rows[3] = "6\u2659\u265F\u2659\u265F\u2659\u265F\u2659\u265F6";
        rows[4] = "5\u265F\u2659\u265F\u2659\u265F\u2659\u265F\u26595";
        rows[5] = "4\u2659\u265F\u2659\u265F\u2659\u265F\u2659\u265F4";
        rows[6] = "3\u265F\u2659\u265F\u2659\u265F\u2659\u265F\u26593";
        rows[7] = "2\u2659\u2659\u2659\u2659\u2659\u2659\u2659\u26592";
        rows[8] = "1\u2656\u2658\u2657\u2655\u2654\u2657\u2658\u26561";
        rows[9] = " abcdefgh ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        expected = "";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expected += rows[i].charAt(j);
                if( j != 9 ) { expected += " "; }
                else { expected += "\n"; }
            }
        }
        actual = cb.getBoard(true);

        for( int i = 0; i < 10*10; i++ ) {
            if( expected.charAt(i) != actual.charAt(i) ) {
                errorCount++;
                System.out.print("\n");
                System.out.printf("%sFAIL:%s Incorrect output of board containing a piece on every tile (white perspective).\n", red, reset);
                System.out.println("Expected:\t\tActual:");
                for( int c = 0; c < 10; c++ ) {
                    System.out.print(expected.substring(c*rw, (c*rw)+rw-1)); // Ignore new line.
                    System.out.print("\t\t");
                    System.out.print(actual.substring(c*rw, (c*rw)+rw));
                }
                break;
            }
        } 

        // TEST: Board with every tile containing a piece from black perspective:
        //
        // WHITE KING   = '\u2654';                     WHITE QUEEN  = '\u2655';
        // WHITE ROOK   = '\u2656';                     WHITE BISHOP = '\u2657';
        // WHITE KNIGHT = '\u2658';                     WHITE PAWN   = '\u2659';
        //
        // BLACK KING   = '\u265A';                     BLACK QUEEN  = '\u265B';
        // BLACK ROOK   = '\u265C';                     BLACK BISHOP = '\u265D';
        // BLACK KNIGHT = '\u265E';                     BLACK PAWN   = '\u265F';
        rows[0] = " hgfedcba ";
        rows[1] = "1\u2656\u2658\u2657\u2655\u2654\u2657\u2658\u26561";
        rows[2] = "2\u2659\u2659\u2659\u2659\u2659\u2659\u2659\u26592";
        rows[3] = "3\u265F\u2659\u265F\u2659\u265F\u2659\u265F\u26593";
        rows[4] = "4\u2659\u265F\u2659\u265F\u2659\u265F\u2659\u265F4";
        rows[5] = "5\u265F\u2659\u265F\u2659\u265F\u2659\u265F\u26595";
        rows[6] = "6\u2659\u265F\u2659\u265F\u2659\u265F\u2659\u265F6";
        rows[7] = "7\u265F\u265F\u265F\u265F\u265F\u265F\u265F\u265F7";
        rows[8] = "8\u265C\u265E\u265D\u265B\u265A\u265D\u265E\u265C8";
        rows[9] = " hgfedcba ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        expected = "";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                expected += rows[i].charAt(j);
                if( j != 9 ) { expected += " "; }
                else { expected += "\n"; }
            }
        }
        actual = cb.getBoard(true);

        for( int i = 0; i < 10*10; i++ ) {
            if( expected.charAt(i) != actual.charAt(i) ) {
                errorCount++;
                System.out.print("\n");
                System.out.printf("%sFAIL:%s Incorrect output of board containing a piece on every tile (black perspective).\n", red, reset);
                System.out.println("Expected:\t\tActual:");
                for( int c = 0; c < 10; c++ ) {
                    System.out.print(expected.substring(c*rw, (c*rw)+rw-1)); // Ignore new line.
                    System.out.print("\t\t");
                    System.out.print(actual.substring(c*rw, (c*rw)+rw));
                }
                break;
            }
        } 

        System.out.print("\n");
        if( errorCount == 0 ) {
            System.out.printf("getBoard: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("getBoard: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    public static void test_getPieceAtLoc() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 0;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();

        // TEST: test retrieval of every tile on board:
        String[] rows = new String[10];
        rows[0] = " abcdefgh ";
        rows[1] = "8········8";
        rows[2] = "7········7";
        rows[3] = "6········6";
        rows[4] = "5········5";
        rows[5] = "4········4";
        rows[6] = "3········3";
        rows[7] = "2········2";
        rows[8] = "1········1";
        rows[9] = " abcdefgh ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        char[] expectedPiece = { '9', 's', 'm', 'v', 'x', 'r', 'i', 'p' };
        char actual;
        char[] coords = new char[2];
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 8; j++ ) {
                cb.board[i+1][j+1] = expectedPiece[j];
            }
            for( int j = 0; j < 8; j++ ) {
                totalTests++;
                coords[0] = (char) ('a'+ j);
                coords[1] = (char) ('8'- i);
                actual = cb.getPieceAtLoc(String.valueOf(coords));
                if( actual != expectedPiece[j] ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s incorrect piece retrieval at loc '%s'. Expected '%c', got '%c'.\n",
                        red,
                        reset,
                        String.valueOf(coords),
                        expectedPiece[j],
                        actual
                    );
                }
                totalTests++;
                actual = cb.getPieceAtLoc(coords[0], coords[1]);
                if( actual != expectedPiece[j] ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s incorrect piece retrieval at loc ( %c , %c ). Expected '%c', got '%c'.\n",
                        red,
                        reset,
                        coords[0],
                        coords[1],
                        expectedPiece[j],
                        actual
                    );
                }
            }
        }

        System.out.print("\n");
        if( errorCount == 0 ) {
            System.out.printf("getPieceAtLoc: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("getPieceAtLoc: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    public static void test_getColourOfPiece() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 0;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();

        // TEST: Test retrieval of white pieces:
        String pieces = "RNBQK";
        int actual;
        for( int i = 0; i < pieces.length(); i++ ) {
            totalTests++;
            actual = cb.getColourOfPiece(pieces.charAt(i));
            if( actual != cb.white ) {
                errorCount++;
                System.out.printf(
                    "%sFAIL:%s Failed to identify white piece. peice (%c), received colour (%d).\n",
                    red,
                    reset,
                    pieces.charAt(i),
                    actual
                );
            }
        }

        // TEST: Test retrieval of black pieces:
        pieces = "rnbqk";
        for( int i = 0; i < pieces.length(); i++ ) {
            totalTests++;
            actual = cb.getColourOfPiece(pieces.charAt(i));
            if( actual != cb.black ) {
                errorCount++;
                System.out.printf(
                    "%sFAIL:%s Failed to identify black piece. peice (%c), received colour (%d).\n",
                    red,
                    reset,
                    pieces.charAt(i),
                    actual
                );
            }
        }

        System.out.print("\n");
        if( errorCount == 0 ) {
            System.out.printf("getPieceAtLoc: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("getPieceAtLoc: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    public static void test_convertCoords() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 0;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();

        // TEST: Test conversion between chess coordinates and indexing coordinates:
        String chess;
        for( int row = 1; row < 9; row++ ) {
            for( int col = 1; col < 9; col++ ) {
                totalTests++;
                chess = String.valueOf( (char) ('a'+(col-1)) ) + String.valueOf( (char) ('9'-row) );
                int[] actual = cb.convertCoords(chess);
                int[] expected = { row, col };
                if( actual[0] != expected[0] || actual[1] != expected[1] ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Incorrect conversion of %s to indexing coordinates. Expected ( %d , %d ), got ( %d , %d ).\n",
                        red,
                        reset,
                        chess,
                        expected[0],
                        expected[1],
                        actual[0],
                        actual[1]
                    );
                }
            }
        }

        // TEST: Test conversion between indexing coordinates and chess coordinates:
        String expected;
        String actual;
        for( int row = 1; row < 9; row++ ) {
            for( int col = 1; col < 9; col++ ) {
                totalTests++;
                int[] indexing = { row , col };
                expected = String.valueOf( (char) ('a'+(col-1)) ) + String.valueOf( (char) ('9'-row) );
                //expected = "" + ( (char) 'a'+i-1 ) + ( (char) '1'+j-1 );
                actual = cb.convertCoords(indexing[0], indexing[1]);
                if( !expected.equals(actual) ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Incorrect conversion of ( %d , %d ) to chess coordinates. Expected '%s', got '%s'.\n",
                        red,
                        reset,
                        indexing[0],
                        indexing[1],
                        expected,
                        actual
                    );
                }
            }
        }

        System.out.print("\n");
        if( errorCount == 0 ) {
            System.out.printf("convertCoords: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("convertCoords: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    public static void test_isValidCoord() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 0;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();

        // TEST: test valid coordinates.
        String input;
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 8; j++ ) {
                totalTests++;
                input = String.valueOf( (char) ('a'+i) ) + String.valueOf( (char) ('1'+j) );
                if( !cb.isValidCoord(input) ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Identified '%s' as invalid.\n",
                        red,
                        reset,
                        input
                    );
                }
                input = String.valueOf( (char) ('A'+i) ) + String.valueOf( (char) ('1'+j) );
                if( !cb.isValidCoord(input) ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Identified '%s' as invalid.\n",
                        red,
                        reset,
                        input
                    );
                }
            }
        }

        // TEST: test single character strings.
        for( int i = 0; i < 10; i++ ) {
            totalTests++;
            input = String.valueOf(i);
            if( cb.isValidCoord(input) ) {
                errorCount++;
                System.out.printf(
                    "%sFAIL:%s Identified '%s' as valid.\n",
                    red,
                    reset,
                    input
                );
            }
        }
        for( int i = 0; i < 26; i++ ) {
            totalTests++;
            input = String.valueOf( (char) ('a'+i) );
            if( cb.isValidCoord(input) ) {
                errorCount++;
                System.out.printf(
                    "%sFAIL:%s Identified '%s' as valid.\n",
                    red,
                    reset,
                    input
                );
            }
        }

        // TEST: test valid letters and invalid numbers.
        for( int i = 0; i < 8; i++ ) {
            totalTests++;
            input = String.valueOf( (char) ('a'+i) ) + "0";
            if( cb.isValidCoord(input) ) {
                errorCount++;
                System.out.printf(
                    "%sFAIL:%s Identified '%s' as valid.\n",
                    red,
                    reset,
                    input
                );
            }
            for( int j = 9; j < 20; j++ ) {
                totalTests++;
                input = String.valueOf( (char) ('a'+i) ) + String.valueOf( j );
                if( cb.isValidCoord(input) ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Identified '%s' as valid.\n",
                        red,
                        reset,
                        input
                    );
                }
            }
        }

        // TEST: test invalid letters and valid numbers.
        for( int i = 8; i < 26; i++ ) {
            totalTests++;
            input = String.valueOf( (char) ('a'+i) ) + "0";
            if( cb.isValidCoord(input) ) {
                errorCount++;
                System.out.printf(
                    "%sFAIL:%s Identified '%s' as valid.\n",
                    red,
                    reset,
                    input
                );
            }
            for( int j = 9; j < 20; j++ ) {
                totalTests++;
                input = String.valueOf( (char) ('a'+i) ) + String.valueOf( j );
                if( cb.isValidCoord(input) ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Identified '%s' as valid.\n",
                        red,
                        reset,
                        input
                    );
                }
            }
        }


        // TEST: test invalid letters and invalid numbers.
        for( int i = 8; i < 26; i++ ) {
            for( int j = 0; j < 8; j++ ) {
                totalTests++;
                input = String.valueOf( (char) ('a'+i) ) + String.valueOf( (char) ('1'+j) );
                if( cb.isValidCoord(input) ) {
                    errorCount++;
                    System.out.printf(
                        "%sFAIL:%s Identified '%s' as valid.\n",
                        red,
                        reset,
                        input
                    );
                }
            }
        }

        System.out.print("\n");
        if( errorCount == 0 ) {
            System.out.printf("isValidCoord: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("isValidCoord: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }

    public static void test_movePawn() {
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        int totalTests = 0;
        int errorCount = 0;

        ChessBoard cb = new ChessBoard();

        String[] rows = new String[10];
        rows[0] = " abcdefgh ";
        rows[1] = "8········8";
        rows[2] = "7········7";
        rows[3] = "6········6";
        rows[4] = "5········5";
        rows[5] = "4········4";
        rows[6] = "3········3";
        rows[7] = "2········2";
        rows[8] = "1········1";
        rows[9] = " abcdefgh ";
        for( int i = 0; i < 10; i++ ) {
            for( int j = 0; j < 10; j++ ) {
                cb.board[i][j] = rows[i].charAt(j);
            }
        }

        // TEST:
        //      - Iterate one by one through all the tiles in the board.
        //      - Place pawn on the tile.
        //      - Test pawn movement to all surrounding spaces (only straight forward movement allowed)
        //      - Surround pawn with opposing pieces.
        //      - Test pawn movement to all surrounding spaces (only forward diagonal movement allowed)
        char[] piece = { 'P' , 'p' };
        char defaultPiece = cb.emptyTile;
        int errTileCount;
        String currTile;
        String neighbour;
        boolean fill;
        for( int i = 0; i < 4; i++ ) {
            // TEST: The white pawn is surrounded by empty spaces.
            //  When i == 2, the board stays blank and only the pawn colour changes.
            if( i == 0 ) {
                rows[0] = " abcdefgh ";
                rows[1] = "8········8";
                rows[2] = "7········7";
                rows[3] = "6········6";
                rows[4] = "5········5";
                rows[5] = "4········4";
                rows[6] = "3········3";
                rows[7] = "2········2";
                rows[8] = "1········1";
                rows[9] = " abcdefgh ";
                for( int row = 0; row < 10; row++ ) {
                    for( int col = 0; col < 10; col++ ) {
                        cb.board[row][col] = rows[row].charAt(col);
                    }
                }
                defaultPiece = cb.emptyTile;
            }
            // TEST: The white pawn is surrounded by opposing pieces.
            else if( i == 2 ) {
                //  Fill the board with black pawns.
                for( int row = 1; row < 9; row++ ) {
                    for( int col = 1; col < 9; col++ )
                        cb.board[row][col] = 'p';
                }
                defaultPiece = 'p';
            }

            // TEST: The black pawn is surrounded by opposing pieces.
            else if( i == 3 ) {
                //  Fill the board with white pawns.
                for( int row = 1; row < 9; row++ ) {
                    for( int col = 1; col < 9; col++ )
                        cb.board[row][col] = 'P';
                }
                defaultPiece = 'P';
            }

            for( int row = 0; row < 8; row++ ) {
                for( int col = 0; col < 8; col++ ) {
                    errTileCount = 0;
                    String[] errTiles = new String[16];
                    totalTests++;
                    currTile = String.valueOf( (char) ('a'+col) ) + String.valueOf( (char) ('1'+row) );
                    int[] indexCoords = cb.convertCoords(currTile);
                    cb.board[indexCoords[0]][indexCoords[1]] = piece[i%2];

                    /*
                     * TODO: test movement when neighbouring tiles contain opposition's piece.
                     */

                    // All references are from white perspective:
                    // Forward left, only allowed if white pawn is capturing opposing piece:
                    neighbour = String.valueOf( (char) (currTile.charAt(0)-1) ) + String.valueOf( (char) (currTile.charAt(1)-1) );
                    if( // This is an ugly format for this kinda multi-conditional statement. But idc :|
                            ( (!cb.movePawn(currTile, neighbour) && piece[i%2] == 'P') ||
                             (cb.movePawn(currTile, neighbour) && piece[i%2] == 'p')
                            ) &&
                            cb.isValidCoord(neighbour)
                    )
                        errTiles[errTileCount++] = neighbour;

                    // Forward, only allowed if white pawn is moving to empty tile:
                    neighbour = String.valueOf( currTile.charAt(0) ) + String.valueOf( (char) (currTile.charAt(1)-1) );
                    if(
                            ( (!cb.movePawn(currTile, neighbour) && piece[i%2] == 'P') ||
                             (cb.movePawn(currTile, neighbour) && piece[i%2] == 'p')
                            ) &&
                            cb.isValidCoord(neighbour)
                    )
                        errTiles[errTileCount++] = neighbour;
                    
                    // Forward right, only allowed if white pawn is capturing opposing piece:
                    neighbour = String.valueOf( (char) (currTile.charAt(0)+1) ) + String.valueOf( (char) (currTile.charAt(1)-1) );
                    if(
                            ( (!cb.movePawn(currTile, neighbour) && piece[i%2] == 'P') ||
                             (cb.movePawn(currTile, neighbour) && piece[i%2] == 'p')
                            ) &&
                            cb.isValidCoord(neighbour)
                    )
                        errTiles[errTileCount++] = neighbour;
                    
                    // Left:
                    neighbour = String.valueOf( (char) (currTile.charAt(0)-1) ) + String.valueOf( currTile.charAt(1) );
                    if( cb.movePawn(currTile, neighbour) && cb.isValidCoord(neighbour) ) 
                        errTiles[errTileCount++] = neighbour;
                    
                    // Right:
                    neighbour = String.valueOf( (char) (currTile.charAt(0)+1) ) + String.valueOf( currTile.charAt(1) );
                    if( cb.movePawn(currTile, neighbour) && cb.isValidCoord(neighbour) ) 
                        errTiles[errTileCount++] = neighbour;
                    
                    // Back left, only allowed if black pawn is capturing opposing piece:
                    neighbour = String.valueOf( (char) (currTile.charAt(0)-1) ) + String.valueOf( (char) (currTile.charAt(1)+1) );
                    if(
                        (
                         (!cb.movePawn(currTile, neighbour) && piece[i%2] == 'p') ||
                         (cb.movePawn(currTile, neighbour) && piece[i%2] == 'P')
                        ) &&
                        cb.isValidCoord(neighbour)
                    )
                        errTiles[errTileCount++] = neighbour;
                    
                    // Back, only allowed if black pawn is moving to empty tile:
                    neighbour = String.valueOf( currTile.charAt(0) ) + String.valueOf( (char) (currTile.charAt(1)+1) );
                    if(
                        (
                         (!cb.movePawn(currTile, neighbour) && piece[i%2] == 'p') ||
                         (cb.movePawn(currTile, neighbour) && piece[i%2] == 'P')
                        ) &&
                        cb.isValidCoord(neighbour)
                    )
                        errTiles[errTileCount++] = neighbour;
                    
                    // Back right, only allowed if black pawn is capturing opposing piece:
                    neighbour = String.valueOf( (char) (currTile.charAt(0)+1) ) + String.valueOf( (char) (currTile.charAt(1)+1) );
                    if(
                        (
                         (!cb.movePawn(currTile, neighbour) && piece[i%2] == 'p') ||
                         (cb.movePawn(currTile, neighbour) && piece[i%2] == 'P')
                        ) &&
                        cb.isValidCoord(neighbour)
                    )
                        errTiles[errTileCount++] = neighbour;
                    

                    if( errTileCount != 0 ) {
                        errorCount++;
                        System.out.printf(
                            "%sFAIL:%s Incorrect movement validation of %c from tile '%s' to %s tiles: ",
                            red,
                            reset,
                            piece[i%2],
                            currTile,
                            (i < 2 ? "EMPTY" : "OPPOSITION")
                        );
                        for( int n = 0; n < errTileCount; n++ ) {
                            System.out.printf(
                                "%s%s",
                                errTiles[n],
                                (n == errTileCount-1 ? "\n" : ", ")
                            );
                        }
                    }
                    cb.board[indexCoords[0]][indexCoords[1]] = defaultPiece;
                }
            }
        }
        
        System.out.print("\n");
        if( errorCount == 0 ) {
            System.out.printf("movePawn: %sPASSED%s all %d tests.\n", green, reset, totalTests);
        } else {
            System.out.printf("movePawn: %sFAILED%s %d of %d tests.\n", red, reset, errorCount, totalTests);
        }
    }


    /*****************************************************************************************************/
    public static void main(String[] argc) {
        System.out.println("\u2654 \u2655 \u265A \u265B");
        test_createNewBoard();
        System.out.print("\n\n");
        test_translateCharacter();
        System.out.print("\n\n");
        test_getBoard();
        System.out.print("\n\n");
        test_getPieceAtLoc();
        System.out.print("\n\n");
        test_getColourOfPiece();
        System.out.print("\n\n");
        test_convertCoords();
        System.out.print("\n\n");
        test_isValidCoord();
        System.out.print("\n\n");
        test_movePawn();

        //
        // TEST CHECKMATE:
        //
        String red = "\u001B[0;31m";
        String green = "\u001B[0;32m";
        String reset = "\u001B[0m";

        ChessBoard cb = new ChessBoard();
        cb.createNewBoard();
        int returnVal;

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++)
                cb.board[i][j] = cb.emptyTile;
        }

        // No black pieces on the board:
        cb.board[5][6] = 'P';
        cb.board[5][7] = 'P';
        cb.board[5][8] = 'P';
        cb.board[6][6] = 'P';
        cb.board[6][7] = 'K';
        cb.board[6][8] = 'P';
        cb.board[7][6] = 'P';
        cb.board[7][7] = 'P';
        cb.board[7][8] = 'P';

        System.out.println(cb.getBoard(true));
        System.out.println("No pieces for black to move...");
        returnVal = cb.stalemate(false, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(red + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(red + "False" + reset); }
        else                        { System.out.println(green + "ERROR" + reset); }
        System.out.println("Swap colours..."); // Now it's whites turn to move.
        for (int i = 1; i < cb.boardWidth - 1; i++) {
            for (int j = 1; j < cb.boardWidth; j++) {
                if (Character.isLowerCase(cb.board[i][j]))
                    cb.board[i][j] = Character.toUpperCase(cb.board[i][j]);
                else
                    cb.board[i][j] = Character.toLowerCase(cb.board[i][j]);
            }
        }
        returnVal = cb.stalemate(true, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(red + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(red + "False" + reset); }
        else                        { System.out.println(green + "ERROR" + reset); }

        System.out.println("\n\n");

        // black's only move puts themselves in check:
        cb.board[1][1] = 'K';
        cb.board[7][5] = 'P';
        cb.board[6][1] = 'R';
        cb.board[8][6] = 'p';
        cb.board[8][7] = 'p';
        cb.board[8][8] = 'p';

        System.out.println(cb.getBoard(true));
        System.out.println("Black's Only Move Results in Check...");
        returnVal = cb.stalemate(false, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(green + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(red + "False" + reset); }
        else                        { System.out.println(red + "ERROR" + reset); }

        System.out.println("Swap colours..."); // Now its blacks turn.
        for (int i = 1; i < cb.boardWidth - 1; i++) {
            for (int j = 1; j < cb.boardWidth - 1; j++) {
                if (Character.isLowerCase(cb.board[i][j]))
                    cb.board[i][j] = Character.toUpperCase(cb.board[i][j]);
                else
                    cb.board[i][j] = Character.toLowerCase(cb.board[i][j]);
            }
        }
        returnVal = cb.stalemate(true, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(green + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(red + "False" + reset); }
        else                        { System.out.println(red + "ERROR" + reset); }

        // Clear board:
        for (int i = 1; i < cb.boardWidth - 1; i++) {
            for (int j = 1; j < cb.boardWidth - 1; j++)
                cb.board[i][j] = cb.emptyTile;
        }

        System.out.println("\n\n");

        // King only piece on board and has no move that doesn't result in check:
        cb.board[3][3] = 'K';
        cb.board[2][7] = 'r';
        cb.board[4][7] = 'r';
        cb.board[6][2] = 'r';
        cb.board[6][4] = 'r';
        cb.board[7][7] = 'k';

        System.out.println(cb.getBoard(true));
        System.out.println("White king cannot move without entering check...");
        returnVal = cb.stalemate(true, true);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(green + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(red + "False" + reset); }
        else                        { System.out.println(red + "ERROR" + reset); }

        System.out.println("Swap colours..."); // Blacks turn now.
        for (int i = 1; i < cb.boardWidth - 1; i++) {
            for (int j = 1; j < cb.boardWidth - 1; j++) {
                if (Character.isLowerCase(cb.board[i][j]))
                    cb.board[i][j] = Character.toUpperCase(cb.board[i][j]);
                else
                    cb.board[i][j] = Character.toLowerCase(cb.board[i][j]);
            }
        }
        returnVal = cb.stalemate(false, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(green + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(red + "False" + reset); }
        else                        { System.out.println(red + "ERROR" + reset); }

        System.out.println("\n\n");

        // Add Bishop so legal move is available:
        cb.board[6][5] = 'b';

        System.out.println(cb.getBoard(false));
        returnVal = cb.stalemate(false, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(red + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(green + "False" + reset); }
        else                        { System.out.println(red + "ERROR" + reset); }

        System.out.println("Swap colours...");
        for (int i = 1; i < cb.boardWidth - 1; i++) {
            for (int j = 1; j < cb.boardWidth - 1; j++) {
                if (Character.isLowerCase(cb.board[i][j]))
                    cb.board[i][j] = Character.toUpperCase(cb.board[i][j]);
                else
                    cb.board[i][j] = Character.toLowerCase(cb.board[i][j]);
            }
        }
        returnVal = cb.stalemate(true, false);
        System.out.print("Stalemate: ");
        if( returnVal == 1 )        { System.out.println(red + "True" + reset); }
        else if( returnVal == 0 )   { System.out.println(green + "False" + reset); }
        else                        { System.out.println(red + "ERROR" + reset); }
    }
}
