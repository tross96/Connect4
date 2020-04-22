package core;
import java.util.Random;

/**
*
* @author Travis Ross
* @version 1.0 March 31, 2020
*Class that contains information about a computer based player, and includes
* the logic needed for the computer to make moves */
public class Connect4ComputerPlayer {
	
	//random abj
	Random rndGen = new Random();
	
	//creates game piece
	public final char playerTag = 'O';
	
	/**Random decision making method that will return where the computer wants
	 * to place a game piece
	 * @return returns an integer value to be used for placing pice */
	public int compChoice() {
		//generates number betwwen 0 adn 6 then increments it by one due to the drop method logic
		int computerDrop = rndGen.nextInt(6);
		computerDrop++;
		return computerDrop;
	}
}
