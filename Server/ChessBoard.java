public class ChessBoard
{
    private char[][] board = new char[10][10]; // declare the 8x8 board plus tile coordinates.
    private int boardWidth = 10;

    /**
     * Initialise the board with all the pieces in their starting positions.
     */
    public void createNewBoard()
    {
        // fill all tiles with blanck space:
        for (int row = 0; row < boardWidth; row++)
        {
            for (int col = 0; col < boardWidth; col++)
                board[row][col] = '·';
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

        // place all black pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[2][i] = '\u265F'; // Pawns
        board[1][1] = '\u265C'; board[1][8] = '\u265C'; // Rooks
        board[1][2] = '\u265E'; board[1][7] = '\u265E'; // Knights
        board[1][3] = '\u265D'; board[1][6] = '\u265D'; // Bishops
        board[1][4] = '\u265B'; // Queen
        board[1][5] = '\u265A'; // King

        // place all white pieces onto the board:
        for (int i = 1; i < 9; i++)
            board[7][i] = '\u2659'; // Pawns
        board[8][1] = '\u2656'; board[8][8] = '\u2656'; // Rooks
        board[8][2] = '\u2658'; board[8][7] = '\u2658'; // Knights
        board[8][3] = '\u2657'; board[8][6] = '\u2657'; // Bishops
        board[8][4] = '\u2655'; // Queen
        board[8][5] = '\u2654'; // King
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
                boardString = boardString + board[row][0];

                for (int col = 1; col < boardWidth; col++)
                    boardString = boardString + ' ' + board[row][col];
                
                boardString = boardString + '\n';
            }
        }
        else
        {
            for (int row = boardWidth-1; row >= 0; row--)
            {
                boardString = boardString + board[row][boardWidth-1];

                for (int col = boardWidth-2; col >= 0; col--)
                    boardString = boardString + ' ' + board[row][col];
                
                boardString = boardString + '\n';
            }
        }

        return boardString;
    }
}
