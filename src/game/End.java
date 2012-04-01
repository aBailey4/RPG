package game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import com.golden.gamedev.object.GameFont;

public class End extends GameObject{
	GameFont		font;

	BufferedImage	win;

	int				option = 0;



	public End(GameEngine parent) {
		super(parent);
	}
	
	public void initResources() {
		win = getImage("deadTitan.png", false);

		font = fontManager.getFont(getImage("BitmapFont.png"));
	}


	public void update(long elapsedTime) {

		switch (bsInput.getKeyPressed()) {
			case KeyEvent.VK_ENTER :
				if (option == 0) {
					finish();
				}
			break;

		}
	}
	
	public void render(Graphics2D g){
		g.drawImage(win, 0, 0, null);
		font.drawString(g, "THE TITAN HAS BEEN DEFEATED!!", 150,25);
		font.drawString(g, "YOU WIN!!!", 250, 150);

	}

}

