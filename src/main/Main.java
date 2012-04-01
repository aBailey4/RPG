package main;

import game.End;
import game.Fight;
import game.RPGGame;
import game.Riddle;

import java.awt.*;
import com.golden.gamedev.*;

public class Main extends GameEngine {

	public static final int TITLE = 0, GAME_MODE = 1, FIGHT = 2, RIDDLE = 3, END = 4;

	public void initResources() {
		nextGameID = TITLE;
	}

	public GameObject getGame(int GameID) {
		switch (GameID) {
			case TITLE : return new Title(this);
			case GAME_MODE : return new RPGGame(this);
			case FIGHT : return new Fight(this);
			case RIDDLE : return new Riddle(this);
			case END : return new End(this);
		}

		return null;
	}

	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new Main(), new Dimension(640, 480), false);
		game.start();
	}
}
