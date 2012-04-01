package game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import main.Main;

import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.Timer;

public class Riddle extends GameObject{
	GameFont		font;

	BufferedImage	riddle;
	BufferedImage	arrow;

	int				option;

	boolean			blink;
	Timer			blinkTimer = new Timer(400);


	public Riddle(GameEngine parent) {
		super(parent);
	}
	
	public void initResources() {
		riddle = getImage("Fairy.png", false);
		arrow = getImage("Arrow.png");

		font = fontManager.getFont(getImage("BitmapFont.png"));
	}


	public void update(long elapsedTime) {
		if (blinkTimer.action(elapsedTime)) {
			blink = !blink;
		}

		switch (bsInput.getKeyPressed()) {
			case KeyEvent.VK_ENTER :
				if (option == 0) {
					parent.nextGameID = Main.GAME_MODE;
					finish();
					

				} else if (option == 1) {
					parent.nextGameID = Main.GAME_MODE;
					RPGGame.rid = 1;
					finish();

				} else if (option == 2) {
					parent.nextGameID = Main.GAME_MODE;
					finish();
				}
			break;

			case KeyEvent.VK_UP :
				option--;
				if (option < 0) option = 2;
			break;

			case KeyEvent.VK_DOWN :
				option++;
				if (option > 2) option = 0;
			break;

			case KeyEvent.VK_ESCAPE :
				finish();
			break;
		}
	}
	
	public void render(Graphics2D g){
		g.drawImage(riddle, 0, 0, null);
		font.drawString(g, "PROVE YOU ARE WORTHY TO UNLOCK THE DELL ORB :", 40, 80);
		font.drawString(g, "WHY DO YOU USE THE FACTORY METHOD PATTERN?", 40,130);
		font.drawString(g, "IT'S ALWAYS RIGHT", 75, 300);
		font.drawString(g, "DON'T KNOW OBJECT TYPE", 75, 320);
		font.drawString(g, "HEWNER SAYS SO", 75, 340);

		if (!blink) {
			g.drawImage(arrow, 50, 297+(option*20), null);
		}
	}

}

