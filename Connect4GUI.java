package ui;
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
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import java.util.List;

import core.Connect4;
import core.Connect4Client;
import core.Connect4ComputerPlayer;
import core.Connect4Server;
import ui.Connect4TextConsole;


/**
 * 
 * @author Travis Ross
 * @version 1.0 4/12/2020
 * Connect4GUI provides a GUI interface for users to use to play the Connect4 game in place
 * of the console method
 *
 */
public class Connect4GUI extends Application {
	
	Stage window;
	Scene menu, board;
	private Cell[][] gameBoard = new Cell[6][7];
	
	Connect4ComputerPlayer computerLogic = new Connect4ComputerPlayer();
	Connect4TextConsole console = new Connect4TextConsole();
	Connect4 gameLogic = new Connect4();
	
	GridPane guiBoard;
	
	
	int turn=0;
	
	
	/**
	 * Main launches the game when the gui is called
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * start method builds the game environment and provides functionality to the various
	 * gui components
	 * @param Stage primaryStage: sets the primary gui window
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		window=primaryStage;
		
		Connect4Server server = new Connect4Server();
		Stage serverStage = new Stage();
		
		Connect4Client client = new Connect4Client();
		Stage clientStage = new Stage();
		
		//label for the start menu
		Label menuLabel = new Label("Welcome to Connect 4!");
		
		//button for a network two player game
		Button netTwoPlayerB = new Button("Network 2 Player Game");
		netTwoPlayerB.setOnAction(e -> {
			server.start(serverStage);
			client.start(clientStage);
		});
		
		//button for a local two player game
		Button twoPlayerB = new Button("Local 2 Player Game");
		twoPlayerB.setOnAction(e -> window.setScene(board));
		
		//button for a CPU game
		Button cpuButton = new Button("Play Computer");
		cpuButton.setOnAction(e -> window.setScene(board));
		
		//button to exit the game
		Button terminate = new Button("Exit Game");
		terminate.setOnAction(e -> System.exit(-1));
		
		//button to exit the game
		Button terminateMenu = new Button("Exit Game");
		terminateMenu.setOnAction(e -> System.exit(-1));
		
		//Menu layout
		VBox vertLayout = new VBox(20);
		vertLayout.setAlignment(Pos.TOP_CENTER);
		vertLayout.getChildren().addAll(menuLabel, twoPlayerB, netTwoPlayerB, cpuButton, terminateMenu);
		menu = new Scene(vertLayout, 300,300);
		
		//Col 1 button
		Button col1 = new Button("Column 1");
		col1.setOnAction(e ->{
			if(turn%2==0) {
				gameLogic.dropPiece('X', 1);
				gameLogic.winnerCheck('X');
				turn++;
				
			}
			else {
				gameLogic.dropPiece('O', 1);
				gameLogic.winnerCheck('O');
				turn++;
			}
		});
		
		//Col 2 button
				Button col2 = new Button("Column 2");
				col2.setOnAction(e ->{
					if(turn%2==0) {
						gameLogic.dropPiece('X', 2);
						gameLogic.winnerCheck('X');
						turn++;
					}
					else {
						gameLogic.dropPiece('O', 2);
						gameLogic.winnerCheck('O');
						turn++;
					}
				});
				//Col 3 button
				Button col3 = new Button("Column 3");
				col3.setOnAction(e ->{
					if(turn%2==0) {
						gameLogic.dropPiece('X', 3);
						gameLogic.winnerCheck('X');
						turn++;
					}
					else {
						gameLogic.dropPiece('O', 3);
						gameLogic.winnerCheck('O');
						turn++;
					}
				});
				//Col 4 button
				Button col4 = new Button("Column 4");
				col4.setOnAction(e ->{
					if(turn%2==0) {
						gameLogic.dropPiece('X', 4);
						gameLogic.winnerCheck('X');
						turn++;
					}
					else {
						gameLogic.dropPiece('O', 4);
						gameLogic.winnerCheck('O');
						turn++;
					}
				});//Col 5 button
				Button col5 = new Button("Column 5");
				col5.setOnAction(e ->{
					if(turn%2==0) {
						gameLogic.dropPiece('X', 5);
						gameLogic.winnerCheck('X');
						turn++;
					}
					else {
						gameLogic.dropPiece('O', 5);
						gameLogic.winnerCheck('O');
						turn++;
					}
				});
				//Col 6 button
				Button col6 = new Button("Column 6");
				col6.setOnAction(e ->{
					if(turn%2==0) {
						gameLogic.dropPiece('X', 6);
						gameLogic.winnerCheck('X');
						turn++;
					}
					else {
						gameLogic.dropPiece('O', 6);
						gameLogic.winnerCheck('O');
						turn++;
					}
				});
				//Col 7 button
				Button col7 = new Button("Column 7");
				col7.setOnAction(e ->{
					if(turn%2==0) {
						gameLogic.dropPiece('X', 7);
						gameLogic.winnerCheck('X');
						turn++;
					}
					else {
						gameLogic.dropPiece('O', 7);
						gameLogic.winnerCheck('O');
						turn++;
					}
				});
		
		HBox buttonRow = new HBox();
		buttonRow.setPadding(new Insets(10,10,10,10));
		buttonRow.setSpacing(33);
		buttonRow.getChildren().addAll(col1, col2,col3,col4,col5,col6,col7);
		
	
		
		//gameboard laout

			GridPane gameGrid = new GridPane();
			for(int i=0;i<6;i++) {
				for(int j=0;j<7; j++) {
					gameGrid.add(gameBoard[i][j]= new Cell(i, j), j, i);
				}
			
			
		
		
		
		VBox vertBox = new VBox();
		vertBox.getChildren().addAll(gameGrid, buttonRow);
	
		board = new Scene(vertBox);
		
		
		window.setScene(menu);
		window.setTitle("Connect 4");
		window.show();
		

	}
	
	
	
}
	public class Cell extends Pane{
		private int row;
		private int col;
		private char gamePeice=' ';
		
		public Cell(int row, int col) {
			this.row=row;
			this.col=col;
			this.setPrefSize(100, 100);
			setStyle("-fx-border-color:black");
		}
		
		public void setToken(char c) {
			gamePeice =c;
			updateBoard();
		}
		
		protected void updateBoard() {
			if(gamePeice == 'X') {
				Line first = new Line(10,10, this.getWidth()-10,this.getHeight()-10);
				first.endXProperty().bind(this.maxWidthProperty().subtract(10));
				first.endYProperty().bind(this.heightProperty().subtract(10));
				Line second = new Line(10,10,this.getHeight()-10,this.getWidth()-10);
				second.startYProperty().bind(this.heightProperty().subtract(10));
				second.endXProperty().bind(this.widthProperty().subtract(10));
				
				this.getChildren().addAll(first, second);
		}
	}
}
}