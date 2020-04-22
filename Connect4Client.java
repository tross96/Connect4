package core;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.List;

import core.Connect4;


/**
 * Connect4Client handles the individual threads and game viewing for the individual players
 * @author Travis
 *
 */
public class Connect4Client extends Application {
		
	private boolean playerTurn=false;
	
	private char myPlayer = ' ';
	private char otherPlayer = ' ';
	
	private int selectedCol;
	
	
	//initialize the cells for a game board
	private Cell[][] gameBoard = new Cell[6][7];
	
	//create lables for the title and game status
	private Label title = new Label();
	private Label status = new Label();
	
	private int colSelected;
	
	//IO streams
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	private boolean keepPlaying=true;
	
	private boolean turnWait=true;
	
	//host name/IP
	private String host = "localhost";
	
	/**
	 * start method that designs the client gui
	 * @param primaryStage, Stage
	 */
	@Override
	public void start(Stage primaryStage) {
		GridPane gameGrid = new GridPane();
		for(int i=0;i<6;i++) {
			for(int j=0;j<7; j++) {
				gameGrid.add(gameBoard[i][j]= new Cell(i, j), j, i);
			}
		}
		Button col1 = new Button("Column 1");
		col1.setOnAction(e-> {
			handleButtonPress(0);
		});
		Button col2 = new Button("Column 2");
		col2.setOnAction(e-> {
			handleButtonPress(1);
		});
		Button col3 = new Button("Column 3");
		col3.setOnAction(e-> {
			handleButtonPress(2);
		});
		Button col4 = new Button("Column 4");
		col4.setOnAction(e-> {
			handleButtonPress(3);
		});
		Button col5 = new Button("Column 5");
		col5.setOnAction(e-> {
			handleButtonPress(4);
		});
		Button col6 = new Button("Column 6");
		col6.setOnAction(e-> {
			handleButtonPress(5);
		});
		Button col7 = new Button("Column 7");
		col7.setOnAction(e-> {
			handleButtonPress(6);
		});
		
		HBox buttonRow = new HBox();
		buttonRow.setPadding(new Insets(10,10,10,10));
		buttonRow.setSpacing(33);
		buttonRow.getChildren().addAll(col1,col2,col3,col4,col5,col6,col7);
		
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(title);
		borderPane.setCenter(gameGrid);
		borderPane.setBottom(status);
		
		VBox vertBox = new VBox();
		vertBox.getChildren().addAll(borderPane, buttonRow);
		
		
		Scene clientScene = new Scene(vertBox, 700, 500);
		primaryStage.setTitle("Connect4 Client");
		primaryStage.setScene(clientScene);
		primaryStage.show();
		
		connectServer();
	}
	
	/**
	 * Connects clients to the server and assigns players their roles
	 */
	public void connectServer() {
		try {
			Socket socket = new Socket(host, 8000);
			
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//create threads
		new Thread(()->{
			try {
				//notifications
				int player=fromServer.readInt();
				if(player==1) {
					myPlayer = 'X';
					otherPlayer = 'O';
					Platform.runLater(()->{
						title.setText("Player 1 is X");
						status.setText("Waiting for a second player");
					});
					
					//server startup notification
					fromServer.readInt();
					
					//other player joins
					Platform.runLater(()-> 
						status.setText("A second Player has joined. Player 1 goes first!"));
					playerTurn=true;
					}
				else if(player==2) {
					myPlayer='O';
					otherPlayer='X';
					
					Platform.runLater(()-> {
						title.setText("Player 2 is O");
						status.setText("Waiting for Player 1 to complete turn");
					});
				}
				
				while(keepPlaying) {
					if(player==1) {
						waitForTurn();
						sendMove();
						//receiveInfo();
					}
					else if(player==2) {
						//receiveInfo();
						waitForTurn();
						sendMove();
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * waits for player to complete their turn
	 * @throws InterruptedException
	 */
	private void waitForTurn() throws InterruptedException{
		while(turnWait) {
			Thread.sleep(100);
		}
		turnWait=true;
	}
	
	/**
	 * sends player move to the server
	 * @throws IOException
	 */
	private void sendMove() throws IOException{
		toServer.writeInt(selectedCol);
	}
	
	/**
	 * receives info from server whether to keep playing or if game is over
	 * @throws IOException
	 */
	private void receiveInfo() throws IOException{
		//get status
		String gameStatus = fromServer.readUTF();//get status
		
		//if player 1 wins
		if(gameStatus=="P1_Won") {
			keepPlaying=false;
			if(myPlayer=='X') {
				Platform.runLater(()-> status.setText("I won! (X)"));
			}
			else if(myPlayer=='O') {
				Platform.runLater(()-> status.setText("Player 1 (X) has won!"));
				receiveMove();
			}
		}
		
		//if player 2 wins
		else if(gameStatus=="P2_Won") {
			keepPlaying=false;
			if(myPlayer=='O') {
				Platform.runLater(()-> status.setText("I won! (O)"));
			}
			else if(myPlayer=='X') {
				Platform.runLater(()-> status.setText("Player 2 (O) has won!"));
				receiveMove();
			}
		}
		
		//if it is a draw
		else if(gameStatus=="draw") {
			keepPlaying=false;
			Platform.runLater(()-> status.setText("The Game is a Tie! No Winner this time!"));
			
			if(myPlayer=='O') {
				receiveMove();
			}
		}
		
		//otherwise keep it going
		else {
			receiveMove();
			Platform.runLater(()-> status.setText("My Turn!"));
			playerTurn=true;
		}
	}
	
	/**
	 * updates board with the opponents move
	 * @throws IOException
	 */
	private void receiveMove() throws IOException{
		int[] col = {fromServer.readInt()};
		Platform.runLater(()-> {
			for(int i=gameBoard.length-1; i>=0;i--) {
				if(gameBoard[i][col[0]-1].getToken()==' ') {
					gameBoard[i][col[0]-1].setToken(otherPlayer);
				}
			}
		});
		}
	
	char gamePeice=' ';
	
	/**
	 * Logic behind what happens when a button is pressed
	 * @param col
	 */
	private void handleButtonPress(int col) {
		if(gamePeice==' ' && playerTurn) {
			for(int i=gameBoard.length-1; i>=0;i--) {
				if(gameBoard[i][col].getToken()==' ') {
					gameBoard[i][col].setToken(myPlayer);
				}
			}
			playerTurn=false;
			selectedCol=col;
			status.setText("Waiting for other player to complete their turn!");
			turnWait=false;
		}
	}
	
	/**
	 * creates the gameboard and updates the board
	 * @author Travis
	 *
	 */
	public class Cell extends Pane{
		private int row;
		private int col;
		private char gamePeice=' ';
		
		/**
		 * constructor
		 * @param row
		 * @param col
		 */
		public Cell(int row, int col) {
			this.row=row;
			this.col=col;
			this.setPrefSize(4000, 4000);
			setStyle("-fx-border-color:black");
		}
		
		/**
		 * returns the char associated with a player
		 * @return gamepeice
		 */
		public char getToken() {
		      return gamePeice;
		    }
		
		/**
		 * sets the token value and calls the paint method to update board
		 * @param c
		 */
		public void setToken(char c) {
		      gamePeice = c;
		      repaint();
		    }
		
		/**
		 * uses token value to add correct peice to board
		 */
		protected void repaint() {
		      if (gamePeice == 'X') {
		        Line line1 = new Line(10, 10, 
		          this.getWidth() - 10, this.getHeight() - 10);
		        line1.endXProperty().bind(this.widthProperty().subtract(10));
		        line1.endYProperty().bind(this.heightProperty().subtract(10));
		        Line line2 = new Line(10, this.getHeight() - 10, 
		          this.getWidth() - 10, 10);
		        line2.startYProperty().bind(
		          this.heightProperty().subtract(10));
		        line2.endXProperty().bind(this.widthProperty().subtract(10));
		        
		        // Add the lines to the pane
		        this.getChildren().addAll(line1, line2); 
		      }
		      else if (gamePeice == 'O') {
		        Ellipse ellipse = new Ellipse(this.getWidth() / 2, 
		          this.getHeight() / 2, this.getWidth() / 2 - 10, 
		          this.getHeight() / 2 - 10);
		        ellipse.centerXProperty().bind(
		          this.widthProperty().divide(2));
		        ellipse.centerYProperty().bind(
		            this.heightProperty().divide(2));
		        ellipse.radiusXProperty().bind(
		            this.widthProperty().divide(2).subtract(10));        
		        ellipse.radiusYProperty().bind(
		            this.heightProperty().divide(2).subtract(10));   
		        ellipse.setStroke(Color.BLACK);
		        ellipse.setFill(Color.WHITE);
		        
		        getChildren().add(ellipse); // Add the ellipse to the pane
		      }
		    }
		
		/**
		 * handles what happens when a button is pressed
		 * @param col
		 */
		private void handleButtonPress(int col) {
			if(gamePeice==' ' && playerTurn) {
				setToken(myPlayer);
				playerTurn=false;
				selectedCol=col;
				status.setText("Waiting for other player to complete their turn!");
				turnWait=false;
			}
		}
	}
	
	/**
	 * launches the app
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}
