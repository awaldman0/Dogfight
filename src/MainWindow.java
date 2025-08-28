import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.UnitTests;
import main.ru.aengine.noise.*;

public class MainWindow {
	public static JFrame frame = new JFrame("Dogfight");   // Change to the name of your game
	public static NoiseGenerator noiseGenLand = new NoiseGenerator();
	public static NoiseGenerator noiseGenClouds = new NoiseGenerator();
	private static Model gameworld = new Model();
	public static Viewer canvas = new Viewer(gameworld);
	private KeyListener Controller = new Controller(); 
	private static int TargetFPS = 90;
	public static boolean startGame = false; 
	private JLabel BackgroundImageForStartMenu;
	
	private static JFrame htpframe = new JFrame("How To Play");
	public static Viewer canvas_htp = new Viewer(gameworld);
	  
	public MainWindow() {
		canvas_htp.is_htp_screen = true;
		htpframe.setTitle("How To Play"); 
		htpframe.setMaximumSize(new Dimension(600, 600));
		htpframe.setSize(600, 600);
		htpframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);   
		htpframe.setLayout(null);
		htpframe.add(canvas_htp);
		canvas_htp.setBounds(0, 0, 600, 600); 
		canvas_htp.setBackground(new Color(255,255,240)); 
		canvas_htp.setVisible(false); 
		
		gameworld.setCanvas(canvas);
		frame.setTitle("Dogfight"); 
		frame.setMaximumSize(new Dimension(1000, 1000));
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
		frame.setLayout(null);
		frame.add(canvas);  
		canvas.setBounds(0, 0, 1000, 1000); 
		canvas.setBackground(new Color(255,255,255)); 
		canvas.setVisible(false);    
	
		JButton singleplayer = new JButton("Single Player");  // button for single player 
		JButton multiplayer = new JButton("Multiplayer");  // button for single player 
		JButton howtoplay = new JButton("How To Play"); //how to play button
		JButton clear = new JButton("Reset High Scores"); //reset hi score button
		ActionListener x = new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) { 
				if (e.getSource() == singleplayer) {
					singleplayer.setVisible(false);
					multiplayer.setVisible(false);
					howtoplay.setVisible(false);
					clear.setVisible(false);
					gameworld.singleplayer = true;
					BackgroundImageForStartMenu.setVisible(false);
					canvas.setVisible(true); 
					canvas.addKeyListener(Controller);    //adding the controller to the Canvas  
					canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
					Sound.playStartSound();
					startGame = true;
				} else if (e.getSource() == multiplayer){
					singleplayer.setVisible(false);
					multiplayer.setVisible(false);
					howtoplay.setVisible(false);
					clear.setVisible(false);
					gameworld.singleplayer = false;
					BackgroundImageForStartMenu.setVisible(false);
					canvas.setVisible(true); 
					canvas.addKeyListener(Controller);    //adding the controller to the Canvas  
					canvas.requestFocusInWindow();   // making sure that the Canvas is in focus so keyboard input will be taking in .
					Sound.playStartSound();
					startGame = true;
				} else if (e.getSource() == howtoplay) {
					canvas_htp.setVisible(true); 
					htpframe.setVisible(true);
				} else {
					 try {
					      FileWriter x = new FileWriter("src\\singleplayerscores.txt", false);
					      x.close();
					      FileWriter y = new FileWriter("src\\multiplayerscores.txt", false);
					      y.close();
					    } catch (IOException e1) {
					      e1.printStackTrace();
					    }
				}
			}
		};
		singleplayer.setBounds(140, 515, 300, 60); 
		singleplayer.addActionListener(x);  
		frame.add(singleplayer);
		singleplayer.setVisible(true);
		
		multiplayer.setBounds(550, 515, 300, 60); 
		multiplayer.addActionListener(x);
		frame.add(multiplayer);
		multiplayer.setVisible(true);
		
		howtoplay.setBounds(140, 615, 300, 60);
		howtoplay.addActionListener(x);
		frame.add(howtoplay);
		howtoplay.setVisible(true);
		
		clear.setBounds(550, 615, 300, 60);
		clear.addActionListener(x);
		frame.add(clear);
		clear.setVisible(true);

		//loading background image 
		File BackroundToLoad = new File("res\\titleScreen.png");  
		try {
			 BufferedImage myPicture = ImageIO.read(BackroundToLoad);
			 BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
			 BackgroundImageForStartMenu.setBounds(-7, -25, 1000, 1000);
			 frame.add(BackgroundImageForStartMenu); 
		}  catch (IOException e) { 
			e.printStackTrace();
		} 
		
		frame.setVisible(true);   
	}

	public static void main(String[] args) {
			MainWindow hello = new MainWindow();  //sets up environment 
			canvas.generateWorld(); 
			while(true) { 	
				int TimeBetweenFrames = 1000 / TargetFPS;
				long FrameCheck = System.currentTimeMillis() + (long) TimeBetweenFrames; 
					
				//wait till next time step 
				while (FrameCheck > System.currentTimeMillis()) {} 
					
				if(startGame) {
					gameloop();
				} 
				  
			}
	} 
	
	//Basic Model-View-Controller pattern 
	private static void gameloop() { 
		// GAMELOOP  
		
		// model update   
		gameworld.gamelogic();
		// view update 
		canvas.updateview(); 
	}
}