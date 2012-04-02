package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.StringTokenizer;


import main.Main;

import com.golden.gamedev.GameObject;
import com.golden.gamedev.GameEngine;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.PlayField;
import com.golden.gamedev.util.FileUtil;


public class RPGGame extends GameObject {

	public static final int PLAYING = 0, TALKING = 1, STOP = 2;
	int gameState = PLAYING;
	
	int 			soldier;

	PlayField		playfield;
	Map				map;
	RPGSprite		hero;

	Dialog		dialog;
	Fight		fight;
	Riddle		riddle;
	LogicUpdater	attackHero;
	LogicUpdater 	stayStill;
	BufferedImage	deadSnake = getImage("snake_dead.gif");

	NPC				talkToNPC;			// the NPC we talk to
	int				talkToNPCDirection;	// old NPC direction before
										// we talk to him/her
	public static int 			rid;


	public RPGGame(GameEngine parent) {
		super(parent);
	}


	public void initResources() {
		map = new Map(bsLoader, bsIO);
		playfield = new PlayField(map);
		playfield.setComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				// sort based on y-order
				return (int) (((Sprite) o1).getY()-((Sprite) o2).getY());
			}
		} );

		hero = new RPGSprite(this, getImages("Chara1.png",3,4), 10, 10, 3, RPGSprite.DOWN);

		playfield.add(hero);
		
		String[] event = FileUtil.fileRead(bsIO.getStream("map00.evt"));
		stayStill = new StayStill();
		LogicUpdater randomMovement = new RandomMovement();
		LogicUpdater cycleUpDown = new CycleUpDown();
		LogicUpdater cycleLeftRight = new CycleLeftRight();
		attackHero = new AttackHero();
		
		/******************************* Old Wise Guy ********************************/
		
		String[] dialogFirst = new String[4];
		dialogFirst[0] = "HELLO TRAVELER!";
		dialogFirst[1] = "IN ORDER TO DEFEAT THE TITAN ANDREW, ";
		dialogFirst[2] = "YOU MUST FIRST VISIT PRINCESS VIOLET.";
		dialogFirst[3] = "FOLLOW THIS PATH TO HER CASTLE!!";
			
		BufferedImage[] firstImage = getImages("Chara2.png",3,4);
		RPGSprite first = new NPC(this,firstImage,12,13,1,0,0, stayStill,dialogFirst);
		playfield.add(first);
		
		/******************************* Violet's Sign ********************************/
		
		String[] dialogViolet = new String[2];
		dialogViolet[0] = "WELCOME TO";
		dialogViolet[1] = "PRINCESS VIOLET'S CASTLE!!";
			
		BufferedImage[] violetSign = getImages("ChipSet3.png",3,4);
		RPGSprite vSign = new NPC(this,violetSign,29,10,1,2,0, stayStill,dialogViolet);
		playfield.add(vSign);
		
		/******************************* Random Happy Girl ********************************/
		
		String[] dialogHappy = new String[1];
		dialogHappy[0] = "I LOVE PRINCESS VIOLET!!";
			
		BufferedImage[] happy = getImages("Chara3.png",3,4);
		RPGSprite hGirl = new NPC(this,happy,20,8,1,3,7, randomMovement,dialogHappy);
		playfield.add(hGirl);
		
		/******************************* Guard ********************************/
		
		String[] dialogGuard = new String[2];
		dialogGuard[0] = "HALT!";
		dialogGuard[1] = "ONLY THE WORTHY MAY VISIT PRINCESS VIOLET!";
			
		BufferedImage[] guardSign = getImages("Chara4.png",3,4);
		RPGSprite gSign = new NPC(this,guardSign,29,12,1,2,10, cycleLeftRight,dialogGuard);
		playfield.add(gSign);
		
		/******************************* Up down Purple ********************************/
		
		String[] dialogPurple= new String[3];
		dialogPurple[0] = "HELP!!!";
		dialogPurple[1] = "TITAN ANDREW HAS TAKEN OVER!";
		dialogPurple[2] = "HELP PRINCESS VIOLET RECLAIM THE LAND!";
			
		BufferedImage[] purpleSign = getImages("Chara5.png",3,4);
		RPGSprite pSign = new NPC(this,purpleSign,24,18,1,2,10, cycleUpDown,dialogPurple);
		playfield.add(pSign);
		
		for (int i=0;i < event.length;i++) {
			if (event[i].startsWith("#") == false) {
				StringTokenizer token = new StringTokenizer(event[i], ",");
				String type 	= token.nextToken();
				String image 	= token.nextToken();
				int posx 		= Integer.parseInt(token.nextToken());
				int posy 		= Integer.parseInt(token.nextToken());
				int direction 	= Integer.parseInt(token.nextToken());
				int speed 		= Integer.parseInt(token.nextToken());
				int frequence 	= Integer.parseInt(token.nextToken());

				String logicUpdater = token.nextToken();
				LogicUpdater logic = stayStill;
				if (logicUpdater.equals("random")) {
					logic = randomMovement;
				} else if (logicUpdater.equals("updown")) {
					logic = cycleUpDown;
				} else if (logicUpdater.equals("leftright")) {
					logic = cycleLeftRight;
				} else if (logicUpdater.equals("enemy")) {
					logic = attackHero;
				}

				String[] dialogNPC = null;
				if (token.hasMoreTokens()) {
					StringTokenizer dialogToken = new StringTokenizer(token.nextToken(),"+");
					dialogNPC = new String[dialogToken.countTokens()];
					for (int j=0;j < dialogNPC.length;j++) {
						dialogNPC[j] = dialogToken.nextToken().toUpperCase();
					}
				}


				BufferedImage[] npcImage = null;
				if (image.equals("none") == false) {
					npcImage = getImages(image,3,4);
				}

				RPGSprite npc = new NPC(this,
										npcImage, posx, posy,
										speed, direction,
										frequence, logic,
										dialogNPC);
				if (type.equals("stepping")) {
					npc.setAnimate(true);
					npc.setLoopAnim(true);
				}

				playfield.add(npc);
			}
		}

		dialog = new Dialog(fontManager.getFont(getImage("BitmapFont.png")),
							   getImage("DialogBox.png", false),
							   getImage("DialogArrow.png"));
	
	}


	public void update(long elapsedTime) {
		playfield.update(elapsedTime);
		if (rid !=1)
			map.layer2[8][16] = 70;
		else
		{
			map.layer2[8][16] = -1;
			map.layer2[31][2] = -1;
		}
		
		
		
		switch (gameState) {
			
			case PLAYING:
				if (hero.getStatus() == RPGSprite.STANDING) {
					if (keyDown(KeyEvent.VK_LEFT)) {
						hero.walkTo(RPGSprite.LEFT, -1, 0);

					} else if (keyDown(KeyEvent.VK_RIGHT)) {
						hero.walkTo(RPGSprite.RIGHT, 1, 0);

					} else if (keyDown(KeyEvent.VK_UP)) {
						hero.walkTo(RPGSprite.UP, 0, -1);

					} else if (keyDown(KeyEvent.VK_DOWN)) {
						hero.walkTo(RPGSprite.DOWN, 0, 1);
					}


					// action key
					if (keyPressed(KeyEvent.VK_ENTER)) {
						int targetX = hero.tileX,
							targetY = hero.tileY;
						switch (hero.getDirection()) {
							case RPGSprite.LEFT:  targetX = hero.tileX - 1; break;
							case RPGSprite.RIGHT: targetX = hero.tileX + 1; break;
							case RPGSprite.UP:    targetY = hero.tileY - 1; break;
							case RPGSprite.DOWN:  targetY = hero.tileY + 1; break;
						}

						talkToNPC = (NPC) map.getLayer3(targetX, targetY);
						
						if(talkToNPC != null && talkToNPC.getType() == attackHero)
						{
							talkToNPC.reduceHealth();
							if(talkToNPC.getHealth() <= 0)
							{
								talkToNPC.logic = stayStill;
								talkToNPC.setImage(deadSnake);
							}
						} 
						else if (talkToNPC != null && talkToNPC.dialog != null) {
							dialog.setDialog(talkToNPC.dialog,
								(hero.getScreenY()+hero.getHeight() < 320));

							// make NPC and hero, face to face!
							// we store the old NPC direction first
							talkToNPCDirection = talkToNPC.getDirection();
							switch (hero.getDirection()) {
								case RPGSprite.LEFT:  talkToNPC.setDirection(RPGSprite.RIGHT); break;
								case RPGSprite.RIGHT: talkToNPC.setDirection(RPGSprite.LEFT); break;
								case RPGSprite.UP:    talkToNPC.setDirection(RPGSprite.DOWN); break;
								case RPGSprite.DOWN:  talkToNPC.setDirection(RPGSprite.UP); break;
							}

							gameState = TALKING;
						}
					}
					
					//soldier
					if(((hero.tileX == 30 && hero.tileY == 11)  || (hero.tileX == 31 && hero.tileY == 11)) && soldier !=1){
						soldier = 1;
						String[] dia = new String[3];
						dia[0] = "HALT!";
						dia[1] = "PROCEED ONLY IF YOU ARE";
						dia[2] = "WORTHY TO SEE PRINCESS VIOLET!";
						dialog.setDialog(dia, true);
						gameState = STOP;
					}
					
					//riddle
					if((hero.tileX == 30 && hero.tileY == 5)  || (hero.tileX == 31 && hero.tileY == 5)){
						parent.nextGameID = Main.RIDDLE;
						finish();
					}
					
					//fighting
					if((hero.tileX == 2 && hero.tileY == 20) || (hero.tileX == 3 && hero.tileY == 20)){
						parent.nextGameID = Main.FIGHT;
						finish();
					}

					// quit key
					if (keyPressed(KeyEvent.VK_ESCAPE)) {
						finish();
					}
				}
			break;

			// talking to npc
			case TALKING:
				if (dialog.endDialog) {
					if (keyPressed(KeyEvent.VK_ENTER)) {
						// back to old direction
						talkToNPC.setDirection(talkToNPCDirection);
						gameState = PLAYING;
					}
				}

				dialog.update(elapsedTime);
			break;

			case STOP:
				if (dialog.endDialog) {
					if (keyPressed(KeyEvent.VK_ENTER)) {
						gameState = PLAYING;
					}
				}

				dialog.update(elapsedTime);
			break;
			
		}

		map.setToCenter(hero);
	}


	public void render(Graphics2D g) {
		playfield.render(g);

		if (gameState == TALKING || gameState == STOP) {
			dialog.render(g);
		}
		
		
		
	}


}


