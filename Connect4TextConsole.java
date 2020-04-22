/**
 * 
 */
package ui;
import java.util.Scanner;
import core.Connect4;
import core.Connect4ComputerPlayer;
import core.Connect4Server;

import java.util.Random;
import javafx.stage.Stage;
import javafx.application.Application;
/**
 *
 * @author Travis Ross
 * @version 1.0 March 26, 2020
 * Connect4TextConsole provides a user interface for the user.
 * It handles displaying content and taking input from the players.
 * */
public class Connect4TextConsole {
	//Global Vars
	//create game object here
	static Connect4 currentGame = new Connect4();
	
	//create a scanner obj
	static Scanner playerInput = new Scanner(System.in);
	
	//create random number generator
	static Random rand = new Random();
	
	//define random value to be used to determine player turns
	static int playerTurn=rand.nextInt();
	
	static Connect4ComputerPlayer computer = new Connect4ComputerPlayer();
	
	Connect4Server server = new Connect4Server();
	Stage serverStage = new Stage();
	
	
	/**Takes input from the player during their turn and inputs their decision into the game */
	public static void playerTurn() {
		char playerTag='X';
		System.out.println("\nPlayer "+ playerTag + " it is your turn. Please choose a column between 1 and 7");
		boolean turnover = false;
		while(turnover==false) {
			try{
				turnover=currentGame.dropPiece(playerTag, playerInput.nextInt() );
				
			//if logic returns a false value send an error try again message to user
			if(!turnover) {
				System.out.println("Game Peice could not be dropped there. Try again.");
				
			}
			}catch(Exception e) {
				System.out.println("Invalid. Try Again");
				playerInput.nextLine();
			}
		}
		currentGame.printBoard();
		if(currentGame.winnerCheck(playerTag)==true) {
			System.exit(-1);
		}
	}
	
	/**Uses the computer object to randomly make decisions for each of their turns */
	public static void compTurn() {
		System.out.println("\nComputer "+ computer.playerTag + " it is your turn. Please choose a column between 1 and 7");
		boolean turnover = false;
		while(turnover==false) {
			try{
				turnover=currentGame.dropPiece(computer.playerTag, computer.compChoice() );
			
			//if logic returns a false value send an error try again message to user
			if(!turnover) {
				System.out.println("Game Peice could not be dropped there. Try again.");
			}
			}catch(Exception e) {
				System.out.println("Invalid. Try Again");
				computer.compChoice();
			}
		}
		currentGame.printBoard();
		if(currentGame.winnerCheck(computer.playerTag)==true) {
			System.exit(-1);
		}
	}
	
	/**
	 * playerPref is a method used to determine whether the user would like to use the 
	 * GUI or the console to play the game. If console is chosen the method returns and moves
	 * on as normal, if the gui is chosen the gui is launched
	 * @throws Exception
	 */
	public static void playPref() throws Exception {
		int pref;
		Scanner decision = new Scanner(System.in);
		System.out.println("Select your display preference:\n 1)Console\n 2)GUI");
		pref=decision.nextInt();
		if(pref == 1) {
			return;
		}
		else if(pref==2) {
			Application.launch(Connect4GUI.class);
			System.exit(-1);
		}
		else {
			System.out.println("Please try again");
			decision.nextLine();
	}
	}
	
	
	/** void method to display the main menu*/
	public static void displayMenu() {
		System.out.println("--------------------");
	    System.out.println("     Welcome to     ");
	    System.out.println("      Connect4!     ");
	    System.out.println("--------------------");
	    System.out.println("Please pick an option below:");
	    System.out.println("");
	    System.out.println("0) Network 2 Player");
	    System.out.println("1) Local 2 Player");
	    System.out.println("2) Play a Computer");
	    System.out.println("3) Exit Game\n");
	}
	
	/**Void method to navigate the menu */
	  //logic for navigating menu
	  public static int menuNav(){
	    int menuChoice;
	    Scanner userInput = new Scanner(System.in);
	    menuChoice = userInput.nextInt();

	    //logic for the decision
	    //ends if the user chooses 3
	    if(menuChoice==3){
	      System.out.println("Terminating Program...");
	      System.exit(-1);
	      return 3;
	    }
	    else if(menuChoice==1){
	      System.out.println("Loading 2 Player Game....");
	      return 1;
	    }
	    else if(menuChoice==2) {
	    	System.out.println("Loading AI.....");
	    	return 2;
	    }
	    else if(menuChoice==0) {
	    	//////////////////////////////
	    	//////START NETWORK GAME//////
	    	Application.launch(Connect4Server.class);
	    }
	    else {
	      System.out.println("Option not valid. Please select an option from the menu.");
	      displayMenu();
	      menuNav();
	    }
	    return -1;
	  }
	
	/**main method starts and runs the program to completion
	 * @throws Exception */
	public static void main(String[] args) throws Exception {
		
		playPref();
		
		//display the menu
		displayMenu();
		int gameMode = menuNav();
		
		//network enabled game
		if(gameMode==0) {
			
		}
				
		//2 player option
		if(gameMode==1) {	
		
		//repeat everything until the game is won, or ends in tie
		while(currentGame.gameOver==false) {
			
			playerTurn++;
			char playerTag;
			
			//define players-->Evens are O, odds are X
			if(playerTurn%2==0) {
				playerTag='O';
			}
			else {
				playerTag='X';
			}
			
			//print the game board using the game obj
			currentGame.printBoard();
			
			System.out.println("\nPlayer "+ playerTag + " it is your turn. Please choose a column between 1 and 7");

			boolean turnOver=false;
			//sends info to the game logic to drop a peice onto the board
			//logic will return a true value when complete indicating the turn is over
			while(!turnOver) {
				try{
					turnOver=currentGame.dropPiece(playerTag, playerInput.nextInt() );
				
				//if logic returns a false value send an error try again message to user
				if(!turnOver) {
					System.out.println("Game Peice could not be dropped there. Try again.");
				}
				}catch(Exception e) {
					System.out.println("Invalid");
					playerInput.nextLine();
				}
			}

			
			//check for a winner..
			if(currentGame.winnerCheck(playerTag)==true) {
				System.exit(-1);
			}

			}
	}
		
		
		//Computer Game start
	else {
		currentGame.printBoard();
		//player goes first
		if(playerTurn%2==0) {
			while(currentGame.gameOver==false) {
				playerTurn();
				compTurn();
			}
		}
		else {
			while(currentGame.gameOver==false) {
				compTurn();
				playerTurn();
		}
		}
		
			}//ai game start
		
	}//main
	}//class
	
