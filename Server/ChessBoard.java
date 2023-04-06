public class ChessBoard
{
    private char[][] board = new char[10][10]; // declare the 8x8 board.
    private int boardWidth = 10;

    public void createNewBoard()
    {
        // fill the first column with row letters:
        for (int i = 8; i > 0; i--)
            board[8-i][0] = (char) ('0' + i);
        
        // fill the last row with column numbers:
        for (int i = 1; i < 9; i++)
            board[0][i] = (char) ('a' + i);
        
        
    }

    public String show()
    {
        String boardString = "";

        for (int row = 0; row < boardWidth; row++)
        {
            for (int col = 0; col < boardWidth; col++)
                boardString = boardString + board[row][col];
            
            boardString = boardString + '\n';
        }
        boardString = boardString + '\0';

        return boardString;
    }
}
