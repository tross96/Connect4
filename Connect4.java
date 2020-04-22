package core;
import java.util.Arrays;

/**
*
* @author Travis Ross
* @version 1.0 March 26, 2020
* Connect4TextConsole provides a user interface for the user.
* It handles displaying content and taking input from the players.
* */
public class Connect4 {
	
	//create game board
	char [][] gameBoard;
	
	//create a value to indicate if the game is over or still running
	public boolean gameOver=false;
	
	//keeps track of the game pieces left
	int numTurns=42;
	
	/**Constructor used to initialize the gameBoard */
	public Connect4() {
		gameBoard=new char[6][7];
		
		for(int i=0; i<gameBoard.length;i++) {
			for(int j=0;j<gameBoard[i].length; j++) {
				gameBoard[i][j]=' ';
			}
		}
	}
	//getter method
	public char[][] getBoard(){
		return gameBoard;
	}
	
	/**Used to print the contents of the gameBoard to the console */
	public void printBoard() {
		for(int i=0;i<6; i++) {
			System.out.print('|');
			for(int j=0; j<7; j++) {
				System.out.print(gameBoard[i][j]);
				System.out.print('|');
			}
			System.out.print('\n');
		}
	}
	
	/** dropPiece(char playerTag, int col) takes input from the UI and uses
	 * it to add a game piece to the board in a viable location.
	 * @param playerTag; This is the gamepiece that is specific to a certain player
	 * @param col; This is the desired column that the player wants to add the piece to
	 * @return returns a boolean value to indicate that the player successfully completed their turn */
	public boolean dropPiece(char playerTag, int col) {
		//decrement the col choice so it matches computer system
		col--;
		//check if the peice has been placed
		boolean dropped=false;
		//hold the col constant but move up from the bottom to top in the array until an open spot is found
		for(int i=gameBoard.length-1; i>=0;i--) {
			if(gameBoard[i][col]==' ') {
				gameBoard[i][col]=playerTag;
				dropped=true;
				numTurns--;
				break;
			}
		}
		return dropped;
	}
	
	/**winnerCheck(char playerTag)  creates booleans to represent the different ways a game can be won
	 *  the method will check the board if the play has won and adjust the booleans as necessary
	 *  if a boolean is set to true then a play has won
	 *  @param playerTag; uses the playerTag to compare the values in the board to in order
	 *  to determine if the player has won
	 *  @return returns a boolean value to indicate that someone has won the game or not**/
	public boolean winnerCheck(char playerTag) {
		//set up boolean values for potential win by row, col, or diag
		boolean rowWin=false;
		boolean colWin=false;
		boolean diagWin = false;
		
		int count=0;
		//check the rows for a winner
		for(int i=0; i<gameBoard.length; i++) {
			for(int j=0; j<gameBoard[i].length; j++) {
				//if the element matches the players piece increment counter
				if(gameBoard[i][j]==playerTag) {
					count++;
					//if the count reachers 4 then we have a winner
					if(count==4) {
						rowWin=true;
					}
				}
				else {
					count=0;
				}
				
			}
		}
		
		//check for a win by columns
		for(int col=0;col<7; col++) {
			for (int rows=0;rows<6; rows++) {
				if(gameBoard[rows][col]==playerTag) {
					count++;
					//if the count reaches 4 then we have a winner
					if(count==4) {
						rowWin=true;
						break;
					}
				}
				else {
					count=0;
				}
				}
			}
		
		//check diagonals
		if(rowWin==false && colWin==false) {
		for(int row=0; row<gameBoard.length;row++) {
			for(int col=0; col<gameBoard[row].length;col++) {
				
				//check to be in the correct position
				if(row<=gameBoard.length-4 && col<=gameBoard[row].length-4) {
					//check for same consecutive values
					if( playerTag==gameBoard[row][col] && playerTag==gameBoard[row+1][col+1] && playerTag==gameBoard[row+2][col+2] && playerTag==gameBoard[row+3][col+3]) {
					diagWin=true;
					break;
				}
				}
				//check to be in the correct position
				if(row<=gameBoard.length-4 && col>=gameBoard[row].length-4) {
					//cehck for same consecutive values
					if(playerTag==gameBoard[row][col] && playerTag==gameBoard[row+1][col-1] && playerTag==gameBoard[row+2][col-2] && playerTag==gameBoard[row+3][col-3]) {
					diagWin=true;
					break;
			}
		}

	}
		}
}
		//returns a boolean value based on if there was a winner.
		if(rowWin==true ||colWin==true|| diagWin==true) {
			gameOver=true;
			System.out.println("");
			printBoard();
			System.out.println("Player "+playerTag+" has won the game!");
			System.out.println("Game Over.\n Termninating Game....");
			return true;
				}

		else {
			return false;
				}
	}
}
	

