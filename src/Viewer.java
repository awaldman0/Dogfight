import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import util.Vector3f;


public class Viewer extends JPanel {
	public long CurrentAnimationTime = 0;
	private int xOffsetLand = 0;
	private int yOffsetLand = 0;
	private int xOffsetClouds = 0;
	private int yOffsetClouds = 0;
	private Color[] landColors = {new Color(74, 145, 225), new Color(217, 223, 149), new Color(85, 188, 102), new Color(56, 120, 68)};
	private Color[] cloudsLayer = {new Color(229, 225, 250, 250)};
	private Color[] healthBar = {new Color(65, 213, 31), new Color(255, 240, 68), new Color(250, 145, 9), new Color(246, 62, 13), new Color(104, 26, 5)};
	private int currRotationAngle = 0;

	private int bufferSize = 25; 
	private int startingX = 500;
	private int startingY = 500;
	private int[][] landMap = new int[1000][1000];
	private int[][] cloudMap = new int[1000][1000];
	private boolean fontSet = false;
	private Font pixel_font;
	private Font small_font;
	Image bulletOn;
	Image bulletOff;
	Image missileOn;
	Image missileOff;
	Image fireballOn;
	Image fireballOff;
	
	Image enemybullet;
	Image playerbullet;
	
	Image fireballbig;
	Image fireballsmall;
	
	Image enemytopview0;
	Image enemytopview45;
	Image enemytopview90;
	Image enemytopview135;
	Image enemytopview180;
	Image enemytopview225;
	Image enemytopview270;
	Image enemytopview315;
	
	Model gameworld = new Model(); 
	private long increaseScore;
	private boolean alreadyWrittenToFile = false;
	private ArrayList<Integer> scores = new ArrayList<Integer>();
	private int score1;
	private int score2;
	private int score3;
	private int score4;
	private int score5;

	public boolean is_htp_screen = false;
	
	
	public Viewer(Model World) {
		this.gameworld = World;
		this.increaseScore = System.currentTimeMillis();
	}

	public Viewer(LayoutManager layout) {
		super(layout);
	}

	public Viewer(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public void updateview() {
		this.repaint();		
	}
	
	public int getRotationAngle() {
		return currRotationAngle;
	}
	
	public Vector3f getMovementVec() {
		if (!gameworld.isPlayerAlive()) {
			return new Vector3f(0, 0, 0);
		}
		
		switch(currRotationAngle) { 
		case 0: 
			return new Vector3f(0, 1, 0); 
		case 45:
			return new Vector3f((float) (-1*Math.sqrt(2))/2, (float)Math.sqrt(2)/2, 0);
		case 90: 
			return new Vector3f(-1, 0, 0); 
		case 135: 
			return new Vector3f((float) (-1*Math.sqrt(2))/2, (float)(-1*Math.sqrt(2))/2, 0);
		case 180: 
			return new Vector3f(0, -1, 0);  
		case 225: 
			return new Vector3f((float) (Math.sqrt(2))/2, (float)(-1*Math.sqrt(2))/2, 0);
		case 270:
			return new Vector3f(1, 0, 0); 
		case 315: 
			return new Vector3f((float) (Math.sqrt(2))/2, (float)(Math.sqrt(2))/2, 0);
		default:
			return new Vector3f(0, 1, 0); //we'll never reach this case
		}
	}
	
	public void generateWorld() {
		for (int i = 0; i < landMap.length; i++) {
			for (int j = 0; j < landMap[0].length; j++) {
				landMap[i][j] = (int) ((Math.round(Math.abs(MainWindow.noiseGenLand.noise((double) i, (double) j) * 255))));
				cloudMap[i][j] = (int) ((Math.round(Math.abs(MainWindow.noiseGenClouds.noise((double) i, (double) j) * 255))));
			}
		}
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		CurrentAnimationTime++; // runs animation time step 

		if (gameworld.singleplayer && System.currentTimeMillis() >= increaseScore + 250 &&
				gameworld.isPlayerAlive()) {
			increaseScore = System.currentTimeMillis();
			gameworld.setScore(gameworld.getScore() + 1);
		} else if (!gameworld.singleplayer && System.currentTimeMillis() >= increaseScore + 250 &&
				!gameworld.gameOver) {
			increaseScore = System.currentTimeMillis();
			gameworld.setScore(gameworld.getScore() + 1);
		}
		
		if (is_htp_screen) {
			//draw controls 
			drawHTP(g);
		} else if (gameworld.singleplayer) {
			drawEverythingSinglePlayer(g);
		} else {
			drawEverythingMultiplayer(g);
		}
		
	}
	
	private void drawEnemies(int x, int y, int width, int height, String texture, Graphics g) {
		int currentPositionInAnimation = ((int) (CurrentAnimationTime % 4 ) * 32); 
		if (texture == "res\\enemytopview0.png") {
			g.drawImage(enemytopview0, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else if (texture == "res\\enemytopview45.png") {
			g.drawImage(enemytopview45, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else if (texture == "res\\enemytopview90.png") {
			g.drawImage(enemytopview90, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else if (texture == "res\\enemytopview135.png") {
			g.drawImage(enemytopview135, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else if (texture == "res\\enemytopview180.png") {
			g.drawImage(enemytopview180, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else if (texture == "res\\enemytopview225.png") {
			g.drawImage(enemytopview225, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else if (texture == "res\\enemytopview270.png") {
			g.drawImage(enemytopview270, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		} else {
			g.drawImage(enemytopview315, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null); 
		}
	}
	
	private void drawBackground(Graphics g) {
		int xcoord;
		int ycoord;
		int size = 12;
		int height = MainWindow.frame.getHeight();
		int width = MainWindow.frame.getWidth(); 
		
		if (CurrentAnimationTime % 7 == 0 && gameworld.isPlayerAlive()) {
			if (Controller.getInstance().isKeyAPressed()) { 
				currRotationAngle = currRotationAngle + 45;
				if (currRotationAngle % 360 == 0) {
					currRotationAngle = 0;
				}
			} else if (Controller.getInstance().isKeyDPressed()) { 
				currRotationAngle = currRotationAngle - 45;
				if (currRotationAngle % 360 == 0) {
					currRotationAngle = 0;
				} else if (currRotationAngle < 0) {
					currRotationAngle += 360;
				}
			}
		}
			
		if (gameworld.isPlayerAlive() && CurrentAnimationTime % 2==0) { 
			switch(currRotationAngle) { 
			case 0: 
				yOffsetLand += 1; 
				yOffsetClouds += 2; 
				break; 
			case 45:
				xOffsetLand -= 1; 
				yOffsetLand += 1; 
				xOffsetClouds -= 2; 
				yOffsetClouds += 2; 
				break; 
			case 90: 
				xOffsetLand -= 1; 
				xOffsetClouds -= 2; 
				break;
			case 135: 
				xOffsetLand -= 1; 
				yOffsetLand -= 1; 
				xOffsetClouds -= 2; 
				yOffsetClouds -= 2;
				break; 
			case 180: 
				yOffsetLand -= 1; 
				yOffsetClouds -= 2; 
				break; 
			case 225: 
				xOffsetLand += 1; 
				yOffsetLand -= 1; 
				xOffsetClouds += 2;
				yOffsetClouds -= 2;
				break; 
			case 270:
				xOffsetLand += 1; 
				xOffsetClouds += 2; 
				break; 
			case 315: 
				xOffsetLand += 1; 
				yOffsetLand += 1; 
				xOffsetClouds += 2; 
				yOffsetClouds += 2; 
				break;
			}
		}
		ArrayList<Point> water = new ArrayList<Point>();
		ArrayList<Point> sand = new ArrayList<Point>();
		ArrayList<Point> green1 = new ArrayList<Point>();
		ArrayList<Point> green2 = new ArrayList<Point>();
		
		ArrayList<Point> cloudColor2 = new ArrayList<Point>();
		
		for (int i = -1 * (bufferSize + ((height/size)/2)); i < ((height/size)/2) + bufferSize; i++) {
			for (int j = -1 * (bufferSize + ((width/size)/2)); j < ((width/size)/2) + bufferSize; j++) {
				//handle land
				xcoord = startingX + xOffsetLand + i;
				ycoord = startingY - yOffsetLand + j;
				
				if (xcoord % landMap.length == 0 || (xcoord < 0 && xcoord + 1 % landMap.length == 0)) {
					if (i > 0) {
						xcoord = startingX + xOffsetLand + i - 1;
					} else {
						xcoord = startingX + xOffsetLand + i + 1;
					}
				}
				
				if (Math.floorDiv(xcoord, landMap.length) % 2 == 0) {
					if (xcoord > 0) {
						xcoord = xcoord % landMap.length;
					} else if (xcoord != 0) {
						xcoord = (xcoord % landMap.length) + landMap.length;
					}
				} else {
					if (xcoord > 0) {
						xcoord = landMap.length - (xcoord % landMap.length);
					} else {
						xcoord = -1 * (xcoord % landMap.length);
					}
				}
				
				if (ycoord % landMap.length == 0 || (ycoord < 0 && ycoord + 1 % landMap.length == 0)) {
					if (j > 0) {
						ycoord = startingY - yOffsetLand + j - 1;
					} else {
						ycoord = startingY - yOffsetLand + j + 1;
					}
				} 
				
				if (Math.floorDiv(ycoord, landMap.length) % 2 == 0) {
					if (ycoord > 0) {
						ycoord = ycoord % landMap.length;
					} else if (ycoord != 0) {
						ycoord = (ycoord % landMap.length) + landMap.length;
					}
				} else {
					if (ycoord > 0) {
						ycoord = landMap.length - (ycoord % landMap.length);
					} else {
						ycoord = -1 * (ycoord % landMap.length);
					}
				
				}
				
				int tile = landMap[xcoord][ycoord];
				
				if (tile > 255) {
					tile = 255;
				}
				if (tile < 25) {
					g.setColor(landColors[0]);
				} else if (tile < 40) {
					g.setColor(landColors[1]);
				} else if (tile < 80) {
					g.setColor(landColors[2]);
				} else {
					g.setColor(landColors[3]);
				}
				Vector3f curr_pt = new Vector3f(((i + ((height/size)/2)) * size) - 500, ((j + ((width/size)/2)) * size) - 500, 0); //vector from the center of the screen to the current point
				curr_pt = turn(curr_pt); //recalculates x and y position with the rotation angle
				if (currRotationAngle % 90 == 0) {
					g.fillRect((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500, size, size);
				} else { 
					if (tile < 25) {
						water.add(new Point((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500));
					} else if (tile < 40) {
						sand.add(new Point((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500));
					} else if (tile < 80) {
						green1.add(new Point((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500));
					} else {
						green2.add(new Point((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500));
					}
				}
				
				//draw clouds
				xcoord = startingX + xOffsetClouds + i;
				ycoord = startingY - yOffsetClouds + j;
				
				if (xcoord % landMap.length == 0 || (xcoord < 0 && xcoord + 1 % landMap.length == 0)) {
					if (i > 0) {
						xcoord = startingX + xOffsetClouds + i - 1;
					} else {
						xcoord = startingX + xOffsetClouds + i + 1;
					}
				}
				
				if (Math.floorDiv(xcoord, landMap.length) % 2 == 0) {
					if (xcoord > 0) {
						xcoord = xcoord % landMap.length;
					} else if (xcoord != 0) {
						xcoord = (xcoord % landMap.length) + landMap.length;
					}
				} else {
					if (xcoord > 0) {
						xcoord = landMap.length - (xcoord % landMap.length);
					} else {
						xcoord = -1 * (xcoord % landMap.length);
					}
				}
				
				if (ycoord % landMap.length == 0 || (ycoord < 0 && ycoord + 1 % landMap.length == 0)) {
					if (j > 0) {
						ycoord = startingY - yOffsetClouds + j - 1;
					} else {
						ycoord = startingY - yOffsetClouds + j + 1;
					}
				} 
				
				if (Math.floorDiv(ycoord, landMap.length) % 2 == 0) {
					if (ycoord > 0) {
						ycoord = ycoord % landMap.length;
					} else if (ycoord != 0) {
						ycoord = (ycoord % landMap.length) + landMap.length;
					}
				} else {
					if (ycoord > 0) {
						ycoord = landMap.length - (ycoord % landMap.length);
					} else {
						ycoord = -1 * (ycoord % landMap.length);
					}
				
				}
				
				tile = cloudMap[xcoord][ycoord];
				
				if (tile > 255) {
					tile = 255;
				}
				if (tile > 130) {
					g.setColor(cloudsLayer[0]);
					curr_pt = new Vector3f(((i + ((height/size)/2)) * size) - 500, ((j + ((width/size)/2)) * size) - 500, 0); //vector from the center of the screen to the current point
					curr_pt = turn(curr_pt); //recalculates x and y position with the rotation angle
					if (currRotationAngle % 90 == 0) {
						g.fillRect((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500, size, size);
					} else {
						cloudColor2.add(new Point((int) curr_pt.getX() + 500, (int) curr_pt.getY() + 500));
					}
				}
			}
		} 
		
		if (currRotationAngle % 90 != 0) {
			g.setColor(landColors[3]);
			for (Point pt : green2) {
				g.fillRect((int) pt.getX(), (int) pt.getY(), size + 5, size + 5);
			}
			g.setColor(landColors[2]);
			for (Point pt : green1) {
				g.fillRect((int) pt.getX(), (int) pt.getY(), size + 5, size + 5);
			}
			g.setColor(landColors[1]);
			for (Point pt : sand) {
				g.fillRect((int) pt.getX(), (int) pt.getY(), size + 5, size + 5);
			}
			g.setColor(landColors[0]);
			for (Point pt : water) {
				g.fillRect((int) pt.getX(), (int) pt.getY(), size + 5, size + 5);
			}
			g.setColor(cloudsLayer[0]);
			for (Point pt : cloudColor2) {
				g.fillRect((int) pt.getX(), (int) pt.getY(), size + 5, size + 5);
			}
		}
	}

		
	public Vector3f turn(Vector3f curr) {
		float x = curr.getX();
		float y = curr.getY();
		double cosT = Math.cos(Math.toRadians(currRotationAngle));
		double sinT = Math.sin(Math.toRadians(currRotationAngle));
		return new Vector3f(((float)((x * cosT) - (y * sinT))),
				(float)(((x * sinT) + (y * cosT))), 0);
	}
	
	public Vector3f turnBy(Vector3f curr, int angle) {
		float x = curr.getX();
		float y = curr.getY();
		double cosT = Math.cos(Math.toRadians((double) angle));
		double sinT = Math.sin(Math.toRadians((double) angle));
		return new Vector3f(((float)((x * cosT) - (y * sinT))),
				(float)(((x * sinT) + (y * cosT))), 0);
	}
	
	private void drawBullet(int x, int y, int width, int height, String texture, Graphics g) {		
		if (texture == "res\\bullet.png") {
			g.drawImage(playerbullet, x - height/2, y - height/2, x + width/2, y + height/2, 0, 0, 10, 10, null); 
		} else if (texture == "res\\enemybullet.png"){
			g.drawImage(enemybullet, x - height/2, y - height/2, x + width/2, y + height/2, 0, 0, 10, 10, null); 
		} else if (texture == "res\\fireballbig.png") {
			g.drawImage(fireballbig, x - height/2, y - height/2, x + width/2, y + height/2, 0, 0, 10, 10, null); 
		} else {
			g.drawImage(fireballsmall, x - height/2, y - height/2, x + width/2, y + height/2, 0, 0, 10, 10, null); 
		}
	}
	
	
	private void drawMissile(int x, int y, int width, int height, String texture, Graphics g) {
		File TextureToLoad = new File(texture);   
		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			g.drawImage(myImage, x - width/2, y - width/2, x + width/2, y + height/2, 0, 0, width, height, null); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void drawPlayer(int x, int y, int width, int height, String texture, Graphics g) { 
		File TextureToLoad = new File(texture);   
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			
			int currentPositionInAnimation = ((int) ((CurrentAnimationTime % 40 / 10))) * 32; 
			g.drawImage(myImage, x - width/2, y - height/2, x + width/2, y + height/2, currentPositionInAnimation, 0, currentPositionInAnimation + 32, 32, null); 
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		 
	}
	
	private void drawHUD(Graphics g) {
		//display score, weapon availability/cooldown, health bar
		if (!fontSet) {
			File fontFile = new File("pixeloid-font/PixeloidSansBold-PKnYd.ttf");
			File bulletOnFile = new File("res\\bulleton.png");
			File bulletOffFile = new File("res\\bulletoff.png");
			File missileOnFile = new File("res\\missileon.png");
			File missileOffFile = new File("res\\missileoff.png");
			File fireballOnFile = new File("res\\fireballon.png");
			File fireballOffFile = new File("res\\fireballoff.png");
			
			//create rest of the files here
			File enemybulletFile = new File("res\\enemybullet.png");
			
			File playerbulletFile = new File("res\\bullet.png");
			
			File enemytopview0File = new File("res\\enemytopview0.png");
			File enemytopview45File = new File("res\\enemytopview45.png");
			File enemytopview90File = new File("res\\enemytopview90.png");
			File enemytopview135File = new File("res\\enemytopview135.png");
			File enemytopview180File = new File("res\\enemytopview180.png");
			File enemytopview225File = new File("res\\enemytopview225.png");
			File enemytopview270File = new File("res\\enemytopview270.png");
			File enemytopview315File = new File("res\\enemytopview315.png");

			File fireballbigFile = new File("res\\fireballbig.png");
			File fireballsmallFile = new File("res\\fireballsmall.png");

			try {
				bulletOn = ImageIO.read(bulletOnFile);
				bulletOff = ImageIO.read(bulletOffFile);
				missileOn = ImageIO.read(missileOnFile);
				missileOff = ImageIO.read(missileOffFile);
				fireballOn = ImageIO.read(fireballOnFile);
				fireballOff = ImageIO.read(fireballOffFile);
				
				//read in rest of files here
				enemybullet = ImageIO.read(enemybulletFile);
				playerbullet = ImageIO.read(playerbulletFile);
				enemytopview0 = ImageIO.read(enemytopview0File);
				enemytopview45 = ImageIO.read(enemytopview45File);
				enemytopview90 = ImageIO.read(enemytopview90File);
				enemytopview135 = ImageIO.read(enemytopview135File);
				enemytopview180 = ImageIO.read(enemytopview180File);
				enemytopview225 = ImageIO.read(enemytopview225File);
				enemytopview270 = ImageIO.read(enemytopview270File);
				enemytopview315 = ImageIO.read(enemytopview315File);

				fireballbig = ImageIO.read(fireballbigFile);
				fireballsmall = ImageIO.read(fireballsmallFile);
				
				pixel_font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
				small_font = pixel_font;
				fontSet = true;
			} catch (Exception e) { 
				e.printStackTrace();
			} 
			fontSet = true;
		}
		pixel_font = pixel_font.deriveFont(Font.BOLD, 40f);
		small_font = small_font.deriveFont(Font.BOLD, 28f);
		g.setFont(pixel_font);
		g.setColor(new Color(0, 0, 0));
		//draw score
		String score = "Score: " + gameworld.getScore();
		char[] word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 40);
		//draw health bar
		String hp = "HP: ";
		word = hp.toCharArray();
		g.drawChars(word, 0, hp.length(), 5, 895);
		g.setColor(new Color(0, 0, 0));
		g.fillRect(110, 905, 425, 15);
		g.fillRect(110, 845, 425, 15);
		g.fillRect(95, 860, 15, 45);
		g.fillRect(535, 860, 15, 45);
		int health = gameworld.getPlayer().getHealth();
		if (health >= 80) {
			g.setColor(healthBar[0]);
		} else if (health >= 60) {
			g.setColor(healthBar[1]);
		} else if (health >= 40) {
			g.setColor(healthBar[2]);
		} else if (health >= 20) {
			g.setColor(healthBar[3]);
		} else {
			g.setColor(healthBar[4]);
		}
		g.fillRect(110, 860, Math.round(425 * health/100) , 45);
		//draw icons, load icons as variables at the top of the class so you don't have to get files every frame
		if (System.currentTimeMillis() >= gameworld.getPlayer().lastBullet + gameworld.getPlayer().bulletDelay) {
			g.drawImage(bulletOn, 570, 820, 570 + 128, 820 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String j = "J ";
			word = j.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, j.length(), 580, 852);
		} else {
			g.drawImage(bulletOff, 570, 820, 570 + 128, 820 + 128, 0, 0, 64, 64, null);
		}
		
		if (System.currentTimeMillis() >= gameworld.getPlayer().lastFireball + gameworld.getPlayer().fireballDelay) {
			g.drawImage(fireballOn, 710, 820, 710 + 128, 820 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String k = "K ";
			word = k.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, k.length(), 720, 852);
		} else {
			g.drawImage(fireballOff, 710, 820, 710 + 128, 820 + 128, 0, 0, 64, 64, null);
		}
		
		if (System.currentTimeMillis() >= gameworld.getPlayer().lastMissile + gameworld.getPlayer().missileDelay) {
			g.drawImage(missileOn, 850, 820, 850 + 128, 820 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String l = "L ";
			word = l.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, l.length(), 860, 852);
		} else {
			g.drawImage(missileOff, 850, 820, 850 + 128, 820 + 128, 0, 0, 64, 64, null);
		}
		
		
	}
	
	public void drawEndScreen(Graphics g) {
		if (gameworld.singleplayer && !alreadyWrittenToFile) {
			try {
			      FileWriter x = new FileWriter("src\\singleplayerscores.txt", true);
			      x.write(gameworld.getScore() + "\n");
			      alreadyWrittenToFile = true;
			      x.close();
			      BufferedReader br = new BufferedReader(new FileReader("src\\singleplayerscores.txt"));
			      String line;
			      while ((line = br.readLine()) != null) {
			          scores.add(Integer.parseInt(line));
			      }
			      br.close();
			      Collections.sort(scores);
			      Collections.reverse(scores);
			} catch (IOException e) {
			      e.printStackTrace();
			}
		} else if (!gameworld.singleplayer && !alreadyWrittenToFile) {
			try {
			      FileWriter x = new FileWriter("src\\multiplayerscores.txt", true);
			      x.write(gameworld.getScore() + "\n");
			      alreadyWrittenToFile = true;
			      x.close();
			      BufferedReader br = new BufferedReader(new FileReader("src\\multiplayerscores.txt"));
			      String line;
			      while ((line = br.readLine()) != null) {
			          scores.add(Integer.parseInt(line));
			      }
			      br.close();
			      Collections.sort(scores);
			      Collections.reverse(scores);
			} catch (IOException e) {
			      e.printStackTrace();
			}
		}
		
		
		g.setColor(new Color(255, 255, 255, 50));
		g.fillRect(0, 0, 1000, 1000);
		g.setColor(new Color(201, 196, 196));
		g.fillRect(200, 100, 600, 750);
		
		g.setColor(new Color(0, 0, 0));
		g.fillRect(200, 30, 600, 70);
		g.fillRect(200, 850, 600, 70);
		g.fillRect(130, 100, 70, 750);
		g.fillRect(800, 100, 70, 750);
		
		pixel_font = pixel_font.deriveFont(Font.BOLD, 85f);
		g.setFont(pixel_font);
		g.setColor(new Color(255, 0, 0));
		String gameover = "GAME OVER";
		char[] word = gameover.toCharArray();
		g.drawChars(word, 0, gameover.length(), 210, 200);
		
		pixel_font = pixel_font.deriveFont(Font.BOLD, 45f);
		g.setFont(pixel_font);
		g.setColor(new Color(0, 0, 0));
		String score = "YOUR SCORE: " + gameworld.getScore();
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 212, 290);
		

		String hiscore = "HIGH SCORES";
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 370);
		
		g.setColor(new Color(255, 215, 0));
		if (scores.size() >= 5) {
			score1 = scores.get(0);
			score2 = scores.get(1);
			score3 = scores.get(2);
			score4 = scores.get(3);
			score5 = scores.get(4);
		} else if (scores.size() == 4) {
			score1 = scores.get(0);
			score2 = scores.get(1);
			score3 = scores.get(2);
			score4 = scores.get(3);
			score5 = 0;
		} else if (scores.size() == 3) {
			score1 = scores.get(0);
			score2 = scores.get(1);
			score3 = scores.get(2);
			score4 = 0;
			score5 = 0;
		} else if (scores.size() == 2) {
			score1 = scores.get(0);
			score2 = scores.get(1);
			score3 = 0;
			score4 = 0;
			score5 = 0;
		} else if (scores.size() == 1) {
			score1 = scores.get(0);
			score2 = 0;
			score3 = 0;
			score4 = 0;
			score5 = 0;
		} else { //shouldn't reach this case
			score1 = 0;
			score2 = 0;
			score3 = 0;
			score4 = 0;
			score5 = 0;
		}
		
		if (score1 != 0) {
			hiscore = "1. " + score1;
		} else {
			hiscore = "1. " + "-";
		}
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 430);
		
		g.setColor(new Color(128, 128, 128));
		if (score2 != 0) {
			hiscore = "2. " + score2;
		} else {
			hiscore = "2. " + "-";
		}
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 490);
		
		g.setColor(new Color(205, 127, 50));
		
		if (score3 != 0) {
			hiscore = "3. " + score3;
		} else {
			hiscore = "3. " + "-";
		}
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 550);
		
		g.setColor(new Color(0, 0, 0));
		if (score4 != 0) {
			hiscore = "4. " + score4;
		} else {
			hiscore = "4. " + "-";
		}
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 610);
		
		if (score5 != 0) {
			hiscore = "5. " + score5;
		} else {
			hiscore = "5. " + "-";
		}
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 670);
		
		hiscore = "R TO RESTART";
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 760);
		hiscore = "M TO CHANGE MODE";
		word = hiscore.toCharArray();
		g.drawChars(word, 0, hiscore.length(), 212, 820);
	}
	
	private void drawEverythingSinglePlayer(Graphics g) {
		
		drawBackground(g);
		
		//figure out proper sprite to use for enemies and if they need to shoot at player
		//but don't draw the enemy yet bc we want to draw enemies after drawing the bullets
		gameworld.getEnemies().forEach((temp) -> 
		{
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			Vector3f vecToPlayer = new Vector3f(-1 * curr_center.getX(), -1 * curr_center.getY(), 0).Normal(); //normalized vector from the enemy (post-rotation) to the player
			float ratio = Math.abs(vecToPlayer.getY() / vecToPlayer.getX());
			if (vecToPlayer.getX() > 0) {
				if (vecToPlayer.getY() < 0) { //lower left
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview0.png");
						if (vecToPlayer.dot(new Vector3f(0, -1, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview270.png");
						if (vecToPlayer.dot(new Vector3f(1, 0, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview315.png");
						if (vecToPlayer.dot(new Vector3f((float)Math.sqrt(2)/2, -1*(float)Math.sqrt(2)/2, 0)) >= 0.95 
								&& gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					}
				} else { //upper left
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview180.png");
						if (vecToPlayer.dot(new Vector3f(0, 1, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview270.png");
						if (vecToPlayer.dot(new Vector3f(1, 0, 0)) >= 0.95 
								&& gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview225.png");
						if (vecToPlayer.dot(new Vector3f((float)Math.sqrt(2)/2, (float)Math.sqrt(2)/2, 0)) >= 0.95 
								&& gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					}
				}
			} else {
				if (vecToPlayer.getY() < 0) { //lower right
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview0.png");
						if (vecToPlayer.dot(new Vector3f(0, -1, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview90.png");
						if (vecToPlayer.dot(new Vector3f(-1, 0, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview45.png");
						if (vecToPlayer.dot(new Vector3f(-1 * (float)Math.sqrt(2)/2, -1 * (float)Math.sqrt(2)/2, 0)) >= 0.95 && 
								gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					}
				} else { //upper right
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview180.png");
						if (vecToPlayer.dot(new Vector3f(0, 1, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview90.png");
						if (vecToPlayer.dot(new Vector3f(-1, 0, 0)) >= 0.95 && gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview135.png");
						if (vecToPlayer.dot(new Vector3f(-1 * (float)Math.sqrt(2)/2, (float)Math.sqrt(2)/2, 0)) >= 0.95 && 
								gameworld.isPlayerAlive()) {
							gameworld.CreateEnemyBullet(temp, vecToPlayer);
						}
					}
				}
			}		 
	    }); 
		
		gameworld.getEnemyBullets().forEach((temp) -> 
		{ 
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			drawBullet((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		});
		
		
		//Draw Bullets  
		gameworld.getBullets().forEach((temp) -> 
		{ 
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			drawBullet((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//Draw Fireballs
		gameworld.getBigFireballs().forEach((temp) -> 
		{ 
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			drawBullet((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		gameworld.getSmallFireballs().forEach((temp) -> 
		{ 
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			drawBullet((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//draw missiles
		gameworld.getMissiles().forEach((temp) -> 
		{ 
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			if (temp.getTarget() != null) {	
				Vector3f vecToEnemy = turnBy(new Vector3f(temp.getTarget().getCentre().getX() - temp.getCentre().getX(), temp.getCentre().getY() - temp.getTarget().getCentre().getY(), 0).Normal(), 360 - currRotationAngle);
				float ratio = Math.abs(vecToEnemy.getY() / vecToEnemy.getX());
	
				if (vecToEnemy.getX() > 0) {
					if (vecToEnemy.getY() < 0) {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile180.png");
							
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile270.png");
							
						} else {
							temp.setTexture("res\\missile225.png");	
						}
					} else {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile0.png");
							
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile270.png");
							
						} else {
							temp.setTexture("res\\missile315.png");	
						}
					}
				} else {
					if (vecToEnemy.getY() < 0) {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile180.png");
				
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile90.png");
							
						} else {
							temp.setTexture("res\\missile135.png");	
						}
					} else {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile0.png");
							
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile90.png");
							
						} else {
							temp.setTexture("res\\missile45.png");	
						}
					}
				}
			}
			drawMissile((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g);	 
		});
		
		//draw explosions
		gameworld.getExplosions().forEach((temp) -> 
		{ 
			long time = System.currentTimeMillis();
			if (time < temp.creationTime + 250) {
				temp.setTexture("res\\explosion1.png");
			} else if (time < temp.creationTime + 500) {
				temp.setTexture("res\\explosion2.png");
			} else if (time < temp.creationTime + 750) {
				temp.setTexture("res\\explosion3.png");
			} else {
				temp.setTexture("res\\explosion4.png");
			}
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			drawMissile((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//draw enemies
		gameworld.getEnemies().forEach((temp) -> 
		{ 
			Vector3f curr_center = new Vector3f(temp.getCentre().getX() - 500, temp.getCentre().getY() - 500, 0); //vector from the center of the screen to the current point
			curr_center = turn(curr_center); //recalculates x and y position with the rotation angle
			drawEnemies((int) curr_center.getX() + 500, (int) curr_center.getY() + 500, (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 			
		});
		
		int x = (int) gameworld.getPlayer().getCentre().getX();
		int y = (int) gameworld.getPlayer().getCentre().getY();
		int width = (int) gameworld.getPlayer().getWidth();
		int height = (int) gameworld.getPlayer().getHeight();
		String texture = gameworld.getPlayer().getTexture();
		
		//Draw player
		if (gameworld.isPlayerAlive()) {
			drawPlayer(x, y, width, height, texture,g);
			drawHUD(g);
		}		
		
		if(gameworld.gameOver) {
			drawEndScreen(g);
			if (Controller.getInstance().isKeyRPressed()) {
				//restart
				gameworld.restart(true);
			} else if (Controller.getInstance().isKeyMPressed()) {
				gameworld.restart(false);
			}
		}
	}

	private void drawEverythingMultiplayer(Graphics g) {
		drawBackgroundMultiplayer(g);
		
		gameworld.getEnemies().forEach((temp) -> 
		{
			Vector3f vecToPlayer = new Vector3f(temp.getTarget().getCentre().getX() - temp.getCentre().getX(), 
					temp.getTarget().getCentre().getY() - temp.getCentre().getY(), 0).Normal();  
			float ratio = Math.abs(vecToPlayer.getY() / vecToPlayer.getX());
			if (vecToPlayer.getX() > 0) {
				if (vecToPlayer.getY() < 0) { //lower left
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview0.png");
						if (vecToPlayer.dot(new Vector3f(0, -1, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview270.png");
						if (vecToPlayer.dot(new Vector3f(1, 0, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview315.png");
						if (vecToPlayer.dot(new Vector3f((float)Math.sqrt(2)/2, -1*(float)Math.sqrt(2)/2, 0)) >= 0.8 
								&& gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					}
				} else { //upper left
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview180.png");
						if (vecToPlayer.dot(new Vector3f(0, 1, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview270.png");
						if (vecToPlayer.dot(new Vector3f(1, 0, 0)) >= 0.8 
								&& gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview225.png");
						if (vecToPlayer.dot(new Vector3f((float)Math.sqrt(2)/2, (float)Math.sqrt(2)/2, 0)) >= 0.8 
								&& gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					}
				}
			} else {
				if (vecToPlayer.getY() < 0) { //lower right
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview0.png");
						if (vecToPlayer.dot(new Vector3f(0, -1, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview90.png");
						if (vecToPlayer.dot(new Vector3f(-1, 0, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview45.png");
						if (vecToPlayer.dot(new Vector3f(-1 * (float)Math.sqrt(2)/2, -1 * (float)Math.sqrt(2)/2, 0)) >= 0.8
								&& gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					}
				} else { //upper right
					if (ratio >= 3.3) {
						temp.setTexture("res\\enemytopview180.png");
						if (vecToPlayer.dot(new Vector3f(0, 1, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else if (ratio <= 0.285) {
						temp.setTexture("res\\enemytopview90.png");
						if (vecToPlayer.dot(new Vector3f(-1, 0, 0)) >= 0.8 && gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					} else {
						temp.setTexture("res\\enemytopview135.png");
						if (vecToPlayer.dot(new Vector3f(-1 * (float)Math.sqrt(2)/2, (float)Math.sqrt(2)/2, 0)) >= 0.8
								&& gameworld.PlayerList.size() > 0) {
							gameworld.CreateEnemyBulletMultiplayer(temp, vecToPlayer);
						}
					}
				}
			}		 
	    });
		
		gameworld.getEnemyBullets().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		});
		
		//Draw Bullets  
		gameworld.getBullets().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//Draw Fireballs
		gameworld.getBigFireballs().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		gameworld.getSmallFireballs().forEach((temp) -> 
		{ 
			drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//draw missiles
		gameworld.getMissiles().forEach((temp) -> 
		{ 
			if (temp.getTarget() != null) {	
				Vector3f vecToEnemy = turnBy(new Vector3f(temp.getTarget().getCentre().getX() - temp.getCentre().getX(), temp.getCentre().getY() - temp.getTarget().getCentre().getY(), 0).Normal(), 360 - currRotationAngle);
				float ratio = Math.abs(vecToEnemy.getY() / vecToEnemy.getX());
	
				if (vecToEnemy.getX() > 0) {
					if (vecToEnemy.getY() < 0) {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile180.png");
							
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile270.png");
							
						} else {
							temp.setTexture("res\\missile225.png");	
						}
					} else {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile0.png");
							
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile270.png");
							
						} else {
							temp.setTexture("res\\missile315.png");	
						}
					}
				} else {
					if (vecToEnemy.getY() < 0) {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile180.png");
				
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile90.png");
							
						} else {
							temp.setTexture("res\\missile135.png");	
						}
					} else {
						if (ratio >= 3.3) {
							temp.setTexture("res\\missile0.png");
							
						} else if (ratio <= 0.285) {
							temp.setTexture("res\\missile90.png");
							
						} else {
							temp.setTexture("res\\missile45.png");	
						}
					}
				}
			}
			drawMissile((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g);	 
		});
		
		//draw explosions
		gameworld.getExplosions().forEach((temp) -> 
		{ 
			long time = System.currentTimeMillis();
			if (time < temp.creationTime + 250) {
				temp.setTexture("res\\explosion1.png");
			} else if (time < temp.creationTime + 500) {
				temp.setTexture("res\\explosion2.png");
			} else if (time < temp.creationTime + 750) {
				temp.setTexture("res\\explosion3.png");
			} else {
				temp.setTexture("res\\explosion4.png");
			}
			
			drawMissile((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 
		}); 
		
		//draw enemies
		gameworld.getEnemies().forEach((temp) -> 
		{ 
			drawEnemies((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(),g);	 			
		});
		
		int x1 = (int) gameworld.getPlayer1().getCentre().getX();
		int y1 = (int) gameworld.getPlayer1().getCentre().getY();
		int width1 = (int) gameworld.getPlayer1().getWidth();
		int height1 = (int) gameworld.getPlayer1().getHeight();
		String texture1 = gameworld.getPlayer1().getTexture();
		
		int x2 = (int) gameworld.getPlayer2().getCentre().getX();
		int y2 = (int) gameworld.getPlayer2().getCentre().getY();
		int width2 = (int) gameworld.getPlayer2().getWidth();
		int height2 = (int) gameworld.getPlayer2().getHeight();
		String texture2 = gameworld.getPlayer2().getTexture();
		
		//Draw player
		if (gameworld.isPlayer1Alive()) {
			drawPlayer(x1, y1, width1, height1, texture1,g);
		}		
		if (gameworld.isPlayer2Alive()) {
			drawPlayer(x2, y2, width2, height2, texture2,g);
		}
		if (gameworld.isPlayer1Alive() || gameworld.isPlayer2Alive()) {
			drawMuliplayerHUD(g);
		}
		
		if(gameworld.gameOver) {
			drawEndScreen(g);
			if (Controller.getInstance().isKeyRPressed()) {
				gameworld.restart(false);
			} else if (Controller.getInstance().isKeyMPressed()) {
				gameworld.restart(true);
			}
		}
	}
	
	
	private void drawBackgroundMultiplayer(Graphics g) {
		int xcoord;
		int ycoord;
		int size = 12;
		int height = MainWindow.frame.getHeight();
		int width = MainWindow.frame.getWidth(); 
		if ((gameworld.isPlayer1Alive() || gameworld.isPlayer2Alive()) && CurrentAnimationTime % 20 == 0) {
			yOffsetClouds += 1;
		}
			
		for (int i = -1 * (bufferSize + ((height/size)/2)); i < ((height/size)/2) + bufferSize; i++) {
			for (int j = -1 * (bufferSize + ((width/size)/2)); j < ((width/size)/2) + bufferSize; j++) {
				//handle land
				xcoord = startingX + i;
				ycoord = startingY + j;
				int tile = landMap[xcoord][ycoord];
				
				if (tile > 255) {
					tile = 255;
				}
				if (tile < 25) {
					g.setColor(landColors[0]);
				} else if (tile < 40) {
					g.setColor(landColors[1]);
				} else if (tile < 80) {
					g.setColor(landColors[2]);
				} else {
					g.setColor(landColors[3]);
				}
				g.fillRect((i + ((height/size)/2)) * size, (j + ((height/size)/2)) * size, size, size);
				
				//handle clouds
				xcoord = startingX + i;
				ycoord = startingY - yOffsetClouds + j;
				
				if (ycoord % landMap.length == 0 || (ycoord < 0 && ycoord + 1 % landMap.length == 0)) {
					if (j > 0) {
						ycoord = startingY - yOffsetClouds + j - 1;
					} else {
						ycoord = startingY - yOffsetClouds + j + 1;
					}
				} 
				
				if (Math.floorDiv(ycoord, landMap.length) % 2 == 0) {
					if (ycoord > 0) {
						ycoord = ycoord % landMap.length;
					} else if (ycoord != 0) {
						ycoord = (ycoord % landMap.length) + landMap.length;
					}
				} else {
					if (ycoord > 0) {
						ycoord = landMap.length - (ycoord % landMap.length);
					} else {
						ycoord = -1 * (ycoord % landMap.length);
					}
				
				}
				
				tile = cloudMap[xcoord][ycoord];
				
				if (tile > 255) {
					tile = 255;
				}
				if (tile > 130) {
					g.setColor(cloudsLayer[0]);
					g.fillRect((i + ((height/size)/2)) * size, (j + ((height/size)/2)) * size, size, size);
				}
			}
		}
		
	}
	
	public void drawMuliplayerHUD(Graphics g) {
		//display score, weapon availability/cooldown, health bar
		if (!fontSet) {
			File fontFile = new File("pixeloid-font/PixeloidSansBold-PKnYd.ttf");
			File bulletOnFile = new File("res\\bulleton.png");
			File bulletOffFile = new File("res\\bulletoff.png");
			File missileOnFile = new File("res\\missileon.png");
			File missileOffFile = new File("res\\missileoff.png");
			File fireballOnFile = new File("res\\fireballon.png");
			File fireballOffFile = new File("res\\fireballoff.png");
			
			//create rest of the files here
			File enemybulletFile = new File("res\\enemybullet.png");
			
			File playerbulletFile = new File("res\\bullet.png");
			
			File enemytopview0File = new File("res\\enemytopview0.png");
			File enemytopview45File = new File("res\\enemytopview45.png");
			File enemytopview90File = new File("res\\enemytopview90.png");
			File enemytopview135File = new File("res\\enemytopview135.png");
			File enemytopview180File = new File("res\\enemytopview180.png");
			File enemytopview225File = new File("res\\enemytopview225.png");
			File enemytopview270File = new File("res\\enemytopview270.png");
			File enemytopview315File = new File("res\\enemytopview315.png");

			File fireballbigFile = new File("res\\fireballbig.png");
			File fireballsmallFile = new File("res\\fireballsmall.png");
			
			try {
				bulletOn = ImageIO.read(bulletOnFile);
				bulletOff = ImageIO.read(bulletOffFile);
				missileOn = ImageIO.read(missileOnFile);
				missileOff = ImageIO.read(missileOffFile);
				fireballOn = ImageIO.read(fireballOnFile);
				fireballOff = ImageIO.read(fireballOffFile);
				
				//read in rest of files here
				enemybullet = ImageIO.read(enemybulletFile);
				playerbullet = ImageIO.read(playerbulletFile);
				enemytopview0 = ImageIO.read(enemytopview0File);
				enemytopview45 = ImageIO.read(enemytopview45File);
				enemytopview90 = ImageIO.read(enemytopview90File);
				enemytopview135 = ImageIO.read(enemytopview135File);
				enemytopview180 = ImageIO.read(enemytopview180File);
				enemytopview225 = ImageIO.read(enemytopview225File);
				enemytopview270 = ImageIO.read(enemytopview270File);
				enemytopview315 = ImageIO.read(enemytopview315File);

				fireballbig = ImageIO.read(fireballbigFile);
				fireballsmall = ImageIO.read(fireballsmallFile);
				
				pixel_font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
				small_font = pixel_font;
				fontSet = true;
			} catch (Exception e) { 
				e.printStackTrace();
			} 
			fontSet = true;
		}
		pixel_font = pixel_font.deriveFont(Font.BOLD, 40f);
		small_font = small_font.deriveFont(Font.BOLD, 28f);
		g.setFont(pixel_font);
		g.setColor(new Color(0, 0, 0));
		
		//draw score
		String score = "Score: " + gameworld.getScore();
		char[] word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 150);
		
		//draw health bar for player 1
		String hp = "HP: ";
		word = hp.toCharArray();
		g.drawChars(word, 0, hp.length(), 5, 895);
		g.setFont(small_font);
		hp = "PLAYER 1";
		word = hp.toCharArray();
		g.drawChars(word, 0, hp.length(), 250, 950);
		g.setFont(pixel_font);
		g.setColor(new Color(0, 0, 0));
		g.fillRect(110, 905, 425, 15);
		g.fillRect(110, 845, 425, 15);
		g.fillRect(95, 860, 15, 45);
		g.fillRect(535, 860, 15, 45);
		int health = gameworld.getPlayer1().getHealth();
		if (health >= 80) {
			g.setColor(healthBar[0]);
		} else if (health >= 60) {
			g.setColor(healthBar[1]);
		} else if (health >= 40) {
			g.setColor(healthBar[2]);
		} else if (health >= 20) {
			g.setColor(healthBar[3]);
		} else {
			g.setColor(healthBar[4]);
		}
		g.fillRect(110, 860, Math.round(425 * health/100) , 45);
		//health bar for player 2
		g.setColor(new Color(0, 0, 0));
		hp = "HP: ";
		word = hp.toCharArray();
		g.drawChars(word, 0, hp.length(), 5, 85);
		g.setFont(small_font);
		hp = "PLAYER 2";
		word = hp.toCharArray();
		g.drawChars(word, 0, hp.length(), 250, 30);
		g.setFont(pixel_font);
		g.setColor(new Color(0, 0, 0));
		g.fillRect(110, 35, 425, 15);
		g.fillRect(110, 95, 425, 15);
		g.fillRect(95, 50, 15, 45);
		g.fillRect(535, 50, 15, 45);
		
		health = gameworld.getPlayer2().getHealth();
		if (health >= 80) {
			g.setColor(healthBar[0]);
		} else if (health >= 60) {
			g.setColor(healthBar[1]);
		} else if (health >= 40) {
			g.setColor(healthBar[2]);
		} else if (health >= 20) {
			g.setColor(healthBar[3]);
		} else {
			g.setColor(healthBar[4]);
		}
		g.fillRect(110, 50, Math.round(425 * health/100) , 45);
		
		//player 1 icons
		if (System.currentTimeMillis() >= gameworld.getPlayer1().lastBullet + gameworld.getPlayer1().bulletDelay) {
			g.drawImage(bulletOn, 570, 820, 570 + 128, 820 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String j = "X ";
			word = j.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, j.length(), 580, 852);
		} else {
			g.drawImage(bulletOff, 570, 820, 570 + 128, 820 + 128, 0, 0, 64, 64, null);
		}
		
		if (System.currentTimeMillis() >= gameworld.getPlayer1().lastFireball + gameworld.getPlayer1().fireballDelay) {
			g.drawImage(fireballOn, 710, 820, 710 + 128, 820 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String k = "C ";
			word = k.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, k.length(), 720, 852);
		} else {
			g.drawImage(fireballOff, 710, 820, 710 + 128, 820 + 128, 0, 0, 64, 64, null);
		}
		
		if (System.currentTimeMillis() >= gameworld.getPlayer1().lastMissile + gameworld.getPlayer1().missileDelay) {
			g.drawImage(missileOn, 850, 820, 850 + 128, 820 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String l = "V ";
			word = l.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, l.length(), 860, 852);
		} else {
			g.drawImage(missileOff, 850, 820, 850 + 128, 820 + 128, 0, 0, 64, 64, null);
		}
		
		//player2 icons
		if (System.currentTimeMillis() >= gameworld.getPlayer2().lastBullet + gameworld.getPlayer2().bulletDelay) {
			g.drawImage(bulletOn, 570, 10, 570 + 128, 10 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String j = "J ";
			word = j.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, j.length(), 580, 40);
		} else {
			g.drawImage(bulletOff, 570, 10, 570 + 128, 10 + 128, 0, 0, 64, 64, null);
		}
		
		if (System.currentTimeMillis() >= gameworld.getPlayer2().lastFireball + gameworld.getPlayer2().fireballDelay) {
			g.drawImage(fireballOn, 710, 10, 710 + 128, 10 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String k = "K ";
			word = k.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, k.length(), 720, 40);
		} else {
			g.drawImage(fireballOff, 710, 10, 710 + 128, 10 + 128, 0, 0, 64, 64, null);
		}
		
		if (System.currentTimeMillis() >= gameworld.getPlayer2().lastMissile + gameworld.getPlayer2().missileDelay) {
			g.drawImage(missileOn, 850, 10, 850 + 128, 10 + 128, 0, 0, 64, 64, null);
			g.setFont(small_font);
			String l = "L ";
			word = l.toCharArray();
			g.setColor(new Color(0, 0, 0));
			g.drawChars(word, 0, l.length(), 860, 40);
		} else {
			g.drawImage(missileOff, 850, 10, 850 + 128, 10 + 128, 0, 0, 64, 64, null);
		}
	}
	
	public void reset() {
		generateWorld();
		CurrentAnimationTime = 0;
		xOffsetLand = 0;
		yOffsetLand = 0;
		xOffsetClouds = 0;
		yOffsetClouds = 0;
		currRotationAngle = 0;

		bufferSize = 25; 
		startingX = 500;
		startingY = 500;
		fontSet = false;
		alreadyWrittenToFile = false;
		scores = new ArrayList<Integer>();
	}
	
	private void drawHTP(Graphics g) {
		if (!fontSet) {
			File fontFile = new File("pixeloid-font/PixeloidSansBold-PKnYd.ttf");
			try {
				pixel_font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
				fontSet = true;
			} catch (Exception e) { 
				e.printStackTrace();
			} 
			fontSet = true;
		}
		
		pixel_font = pixel_font.deriveFont(Font.BOLD, 35f);
		small_font = pixel_font.deriveFont(Font.BOLD, 25f);
		g.setFont(pixel_font);
		g.setColor(new Color(0, 0, 0));
		String score = "SINGLEPLAYER";
		char[] word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 40);
		
		g.setFont(small_font);
		score = "A/D- TURN LEFT/RIGHT";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 75);
		score = "J- SHOOT BULLET";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 105);
		score = "K- SHOOT FIREBALLS";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 135);
		score = "L- SHOOT MISSILE";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 165);
		
		g.setFont(pixel_font);
		score = "MULTIPLAYER";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 210);
		g.setFont(small_font);
		score = "A/D- PLAYER 1 TURN LEFT/RIGHT";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 245);
		score = "X- PLAYER 1 SHOOT BULLET";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 275);
		score = "C- PLAYER 1 SHOOT FIREBALLS";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 305);
		score = "V- PLAYER 1 SHOOT MISSILE";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 335);
		score = "{/}- PLAYER 2 TURN LEFT/RIGHT";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 365);
		score = "J- PLAYER 2 SHOOT BULLET";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 395);
		score = "K- PLAYER 2 SHOOT FIREBALLS";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 425);
		score = "L- PLAYER 2 SHOOT MISSILE";
		word = score.toCharArray();
		g.drawChars(word, 0, score.length(), 5, 455);
	}
}
