package game;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.util.*;


/**
 * e			: create an enemy
 * Arrow key	: navigate
 * Space		: switch lower/upper tile
 * Page down	: next tile
 * Page up		: prev tile
 * End			: fast next tile
 * Home			: fast prev tile
 * Right click	: select tile
 * Click		: put tile
 * Ctrl + S		: save
 */
public class MapEditor extends Game {

	Map 	map;
	int 	tilenum;
	int		tilemode;
	int 	NPCmode;
	String	uprMap, lwrMap;


	public void initResources() {
		map = new Map(bsLoader, bsIO);
	}


	public void update(long elapsedTime) {
		map.update(elapsedTime);

		// navigate
		if (keyDown(KeyEvent.VK_LEFT)) {
			map.move(-0.2*elapsedTime, 0);
		}
		if (keyDown(KeyEvent.VK_RIGHT)) {
			map.move(0.2*elapsedTime, 0);
		}
		if (keyDown(KeyEvent.VK_UP)) {
			map.move(0, -0.2*elapsedTime);
		}
		if (keyDown(KeyEvent.VK_DOWN)) {
			map.move(0, 0.2*elapsedTime);
		}

		// switch to enemy
		if (keyPressed(KeyEvent.VK_E)) {
			if(++NPCmode >1){
				NPCmode = 0;
			}
			
			
		}
		
		// switch lower/upper tile
		if (keyPressed(KeyEvent.VK_SPACE)) {
			if (++tilemode > 1) {
				tilemode = 0;
			}

			// validate current mode tile count
			if (tilenum > getChipsetLength()) {
				tilenum = getChipsetLength();
			}
		}

		// next/prev tile
		if (keyPressed(KeyEvent.VK_PAGE_DOWN) || keyDown(KeyEvent.VK_END)) {
			if (++tilenum > getChipsetLength()) {
				tilenum = getChipsetLength();
			}
		}
		if (keyPressed(KeyEvent.VK_PAGE_UP) || keyDown(KeyEvent.VK_HOME)) {
			if (--tilenum < 0) {
				tilenum = 0;
			}
		}


		Point tileAt = map.getTileAt(getMouseX(), getMouseY());
		if (tileAt != null) {
			// put tile
			if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
				getLayer() [tileAt.x] [tileAt.y] = tilenum;
			}


			// select tile
			if (rightClick()) {
				tilenum = getLayer() [tileAt.x] [tileAt.y];
			}
		}


		// save to file with name map00.lwr and map00.upr
		if (keyPressed(KeyEvent.VK_S) && keyDown(KeyEvent.VK_CONTROL)) {
			String[] lowerTile = new String[map.layer1[0].length];
			String[] upperTile = new String[map.layer1[0].length];
			for (int i=0;i < map.layer1.length;i++)
			for (int j=0;j < map.layer1[0].length;j++) {
				if (lowerTile[j] == null) lowerTile[j] = "";
				lowerTile[j] += String.valueOf(map.layer1[i][j])+" ";

				if (upperTile[j] == null) upperTile[j] = "";
				upperTile[j] += String.valueOf(map.layer2[i][j])+" ";
			}
			FileUtil.fileWrite(lowerTile, bsIO.setFile("map00.lwr"));
			FileUtil.fileWrite(upperTile, bsIO.setFile("map00.upr"));
			
		}
	}

	private int getChipsetLength() {
		switch (tilemode) {
			case 0: return map.chipsetE.image.length + map.chipset.length - 2;	// lower mode
			case 1: return map.chipsetF.image.length - 1;	// upper mode
		}
		return 0;
	}

	private BufferedImage getChipsetImage(int num) {
		if (num == -1) {
			return null;
		}

		switch (tilemode) {
			// lower mode
			case 0:
				if (num < map.chipsetE.image.length) {
					return map.chipsetE.image[num];
				} else {
					return map.chipset[num-map.chipsetE.image.length].image[2];
				}

			// upper mode
			case 1:
				return map.chipsetF.image[num];
		}

		return null;
	}


	// get tiles
	private int[][] getLayer() {
		switch (tilemode) {
			case 0: return map.layer1;	// lower mode
			case 1: return map.layer2;	// upper mode
		}

		return null;
	}


	public void render(Graphics2D g) {
		map.render(g);

		// selected tile
		if (getChipsetImage(tilenum) != null) {
			g.drawImage(getChipsetImage(tilenum), 600, 40, null);
		}
		g.setColor(Color.BLACK);
		g.drawRect(600, 40, 32, 32);

		Point tileAt = map.getTileAt(getMouseX(), getMouseY());
		if (tileAt != null) {
			g.setColor(Color.WHITE);
			int posX = (tileAt.x - map.getTileX()) * 32,
				posY = (tileAt.y-map.getTileY()) * 32;
			g.drawRect(posX - map.getOffsetX() + map.getClip().x,
					   posY - map.getOffsetY() + map.getClip().y,
					   32, 32);
		}
	}


	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new MapEditor(), new Dimension(640,480), false);
		game.start();
	}

}