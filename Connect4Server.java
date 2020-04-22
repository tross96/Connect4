package core;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;
import java.util.*;
import core.Connect4;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;


/**
 * 
 * @author Travis
 *Connect4Server creates the server to handle a networked game that uses
 *multithreading
 */
public class Connect4Server extends Application {
	
	private int sessionNum=1;
	
	/**
	 * start designs the displayes the server window
	 * @param primaryStage, Stage
	 */
	@Override
	public void start(Stage primaryStage) {
		TextArea serverLog = new TextArea();    // Create a scene and place it in the stage    
		Scene scene = new Scene(new ScrollPane(serverLog), 500, 500);
		primaryStage.setTitle("Connect4 Server");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		new Thread(() -> {
			
			try {
				ServerSocket serverSock = new ServerSocket(8000);
				Platform.runLater(()-> serverLog.appendText(new Date() +
									": Server started at socket 8000\n"));
				
				while(true) {
					Platform.runLater(()-> serverLog.appendText(new Date() + 
										": Waiting for players..." + sessionNum + '\n'));
					
					//connecting player 1
					Socket player1 = serverSock.accept();
					Platform.runLater(()-> {
						serverLog.appendText(new Date() + ": PLayer 1 has joined session "
									+sessionNum+'\n');
						serverLog.appendText("Player 1's IP Address: "
									+player1.getInetAddress().getHostAddress()+'\n');
					});
					//notify player 1 that they are player1
					new DataOutputStream(
					player1.getOutputStream()).writeInt(1);
					
					//connecting player 2
					Socket player2 = serverSock.accept();
					Platform.runLater(()-> {
						serverLog.appendText(new Date() + ": PLayer 2 has joined session "
									+sessionNum+'\n');
						serverLog.appendText("Player 2's IP Address: "
									+player2.getInetAddress().getHostAddress()+'\n');
					});
					
					//notify player 2 that they are player2
					new DataOutputStream(
					player2.getOutputStream()).writeInt(2);
					
					//display/increment session
					sessionNum++;
					Platform.runLater(()-> 
							serverLog.appendText(new Date()+ ": Starting new thread for session "+sessionNum+"\n"));
					//create new thread for the players
					new Thread(new processGameFlow(player1,player2)).start();
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	
	/**start inner class for handeling the game flow
	 * @author Travis
	 *
	 */
	class processGameFlow implements Runnable{
		//create logic object
		Connect4 logic = new Connect4();
		
		//create the sockets for the players
		private Socket player1;
		private Socket player2;
		//creates the board size
		private char [][] board= new char[6][7];
		
		//IO streams
		private DataInputStream fromP1;
		private DataOutputStream toP1;
		private DataInputStream fromP2;
		private DataOutputStream toP2;
		
		//bool to check if game is still going
		private boolean stillPlaying=true;
		
		//cstor
		public processGameFlow(Socket player1, Socket player2) {
			this.player1=player1;
			this.player2=player2;
			
			//initialize the values of the board
			for(int i=0;i<6;i++) {
				for(int j=0;j<7; j++) {
					board[i][j]=' ';
				}
			}
		}
		
		/**
		 * run() handles the input/output of the game data passed from 
		 * player to player as well as the logic of the game.
		 */
		public void run() {
			try {
				//initialize IO Streams
				DataInputStream fromP1 = new DataInputStream(player1.getInputStream());
				DataOutputStream toP1 = new DataOutputStream(player1.getOutputStream());
				DataInputStream fromP2 = new DataInputStream(player2.getInputStream());
				DataOutputStream toP2= new DataOutputStream(player2.getOutputStream());
				
				
				//player 1 starts
				toP1.writeInt(1);
				
				
				//keep the game going until someone wins
				while(true) {
					//player1 move info
					int colNum = fromP1.readInt();
					boolean success = logic.dropPiece('X', colNum);
					//checks for a successful drop and updates board
					if(success==true) {
						board = logic.gameBoard;
						success=false;//set value to false again
					}
					//check for winner
					if(logic.winnerCheck('X')==true) {
						toP1.writeBytes("P1_Won");
						toP2.writeBytes("P1_Won");
						sendOpponentMove(toP2, colNum);
						break;
					}
					//check for a tie
					else if(logic.numTurns==0) {
						toP1.writeBytes("Draw");
						toP2.writeBytes("Draw");
						sendOpponentMove(toP2, colNum);
						break;
					}					
					//move onto player 2
					else {
						toP2.writeBytes("continue");
						//send p1 move to p2
					}
					
					//start p2 turn
					colNum=fromP2.readInt();
					success=logic.dropPiece('O', colNum);
					if(success==true) {
						board=logic.gameBoard;
						success=false;
					}
					//check for winner
					if(logic.winnerCheck('O')==true) {
						toP1.writeBytes("P2_Won");
						toP2.writeBytes("P2_Won");
						sendOpponentMove(toP1, colNum);
						break;
					}
					//check for a tie
					else if(logic.numTurns==0) {
						toP1.writeBytes("Draw");
						toP2.writeBytes("Draw");
						sendOpponentMove(toP1, colNum);
						break;
					}
					//give player 1 turn back
					toP1.writeBytes("continue");
					sendOpponentMove(toP1, colNum);
					
					}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}//end run
		
		/**
		 * sendOpponentMove sends the opposing player the selected column made by the current player
		 * @param out
		 * @param col
		 * @throws IOException
		 */
		private void sendOpponentMove(DataOutputStream out, int col) throws IOException{
			out.writeInt(col);
		}
	}//end innerclass
	
	/**
	 * Main launches the server class
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
