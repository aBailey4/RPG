package game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import main.Main;

import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.Timer;

public class Fight extends GameObject{
	
	public static final int PLAYING = 0, TALKING = 1;
	int gameState = PLAYING;
	
	GameFont		font;

	BufferedImage	fight;
	BufferedImage 	win;
	BufferedImage	arrow;
	
	boolean inDialog;
	
	Dialog		dialog;

	int				option, titanPick;
	int				userHealth = 100, titanHealth = 100;

	boolean			blink;
	Timer			blinkTimer = new Timer(400);


	public Fight(GameEngine parent) {
		super(parent);
	}


	public void initResources() {
		fight = getImage("Titan.png", false);
		win = getImage("deadTitan.png", false);
		arrow = getImage("Arrow.png");

		font = fontManager.getFont(getImage("BitmapFont.png"));
	}


	public void update(long elapsedTime) {
		if (blinkTimer.action(elapsedTime)) {
			blink = !blink;
		}
		
		switch(gameState){
		
			case PLAYING:
			{

				if (userHealth <1) {
					parent.nextGameID = Main.GAME_MODE;
					finish();
				}

				if (titanHealth <1) {
					parent.nextGameID = Main.END;
					finish();
				}


				titanPick = (int)(Math.random() * 3);

				dialog = new Dialog(fontManager.getFont(getImage("BitmapFont.png")),
						getImage("DialogBox.png", false),
						getImage("DialogArrow.png"));

				switch (bsInput.getKeyPressed()) {
					case KeyEvent.VK_ENTER :
						if ((option + 1 == titanPick) || ((option == 2) && (titanPick == 0))) {
							// Titan wins
							userHealth -= 25;
							String[] dia = new String[2];
							if (userHealth > 0){
								dia[0] = "TITAN HURT YOU! YOUR HEALTH IS REDUCED 25 POINTS!";
								dia[1] = "";
							}
							else
							{
								
								dia[0] = "TITAN HURT YOU! YOUR HEALTH IS REDUCED 25 POINTS!";
								dia[1] = "YOU LOSE! TRY AGAIN!";
							}
							dialog.setDialog(dia, true);
							gameState = TALKING;

						} else if ((titanPick + 1 == option) || ((titanPick == 2) && (option == 0))) {
							// user wins
							String[] dia = new String[1];
							dia[0] = "YOU HURT TITAN! HIS HEALTH IS REDUCED 25 POINTS!";
							titanHealth -= 25;
							dialog.setDialog(dia, true);
							gameState = TALKING;

						} else if (titanPick == option) {
							// tie
							String[] dia = new String[1];
							dia[0] = "YOU TIED! BOTH HEALTHS ARE REDUCED 10 POINTS!";
							titanHealth -= 10; userHealth -= 10;
							dialog.setDialog(dia, true);
							gameState = TALKING;
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
			break;
			
			case TALKING:
			{
				if (dialog.endDialog) {
					if (keyPressed(KeyEvent.VK_ENTER)) {
						gameState = PLAYING;
					}
				}

				dialog.update(elapsedTime);
			break;
			}
		}
	}
	
	public void render(Graphics2D g){
		g.drawImage(fight, 0, 0, null);
		font.drawString(g, "JUMP KICK", 450, 300);
		font.drawString(g, "PUNCH", 450, 320);
		font.drawString(g, "KICK", 450, 340);
		
		font.drawString(g, "TITAN: " + titanHealth,50,50);
		font.drawString(g, "HERO: " + userHealth, 450, 50);

		if (!blink) {
			g.drawImage(arrow, 434, 297+(option*20), null);
		}
		
		if (gameState == TALKING) {
			dialog.render(g);
		}
	}
	
	
}

