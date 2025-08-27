import java.util.ArrayList;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import util.GameObject;
import util.Point3f;
import util.Vector3f; 

public class Model {
	
	private GameObject Player;
	private GameObject Player1;
	private GameObject Player2;
	private Controller controller = Controller.getInstance();
	
	public Viewer canvas;
	
	public CopyOnWriteArrayList<GameObject> PlayerList  = new CopyOnWriteArrayList<GameObject>();
	public CopyOnWriteArrayList<GameObject> EnemiesList  = new CopyOnWriteArrayList<GameObject>();
	public CopyOnWriteArrayList<GameObject> ExplosionList  = new CopyOnWriteArrayList<GameObject>();
	
	private CopyOnWriteArrayList<GameObject> playerBulletList  = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<GameObject> playerMissileList  = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<GameObject> playerBigFireballList  = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<GameObject> playerSmallFireballList  = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<GameObject> enemyBulletList  = new CopyOnWriteArrayList<GameObject>();
	
	private int Score = 0; 
	private static final int MAXDISTANCE = 523900;
	Random r = new Random();
	int x = ThreadLocalRandom.current().nextInt();
	private int enemyBulletDelay = 500;
	private long lastEnemySpawned;
	private int spawnDelay = 500;
	private int[] spawnTimes = {500, 1000, 1500, 2000, 3000};
	
	private boolean playerAlive = true;
	private boolean player1Alive = true;
	private boolean player2Alive = true;
	public boolean gameOver = false;

	public boolean singleplayer;
	
	public Model() {
		//Player 
		Player = new GameObject("res/topviewnormal.png",50,50,new Point3f(500,500,0), new Vector3f(0, 0, 0));
		Player.setHealth(100);
		Player1 = new GameObject("res/player1_0.png",50,50,new Point3f(400,500,0), new Vector3f(0, 0, 0));
		Player1.setHealth(100);
		Player1.setVec(new Vector3f(0, 2, 0));
		Player1.playerAngle = 0;
		Player2 = new GameObject("res/player2_0.png",50,50,new Point3f(600,500,0), new Vector3f(0, 0, 0));
		Player2.setHealth(100);
		Player2.setVec(new Vector3f(0, 2, 0));
		Player2.playerAngle = 0;
		lastEnemySpawned = System.currentTimeMillis();
		PlayerList.add(Player1);
		PlayerList.add(Player2);
	}
	
	// This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly. 
	public void gamelogic() {		
		if(singleplayer && playerAlive) {	
			// Player Logic first 
			playerLogic(); 
			// Enemy Logic next
			spawnEnemy();
			enemyLogic();
			explosionLogic();
		} else if (!singleplayer && (player1Alive || player2Alive)) {
			// Player Logic first 
			playerLogicMultiplayer(); 
			// Enemy Logic next
			spawnEnemyMultiplayer();
			enemyLogicMultiplayer();
			explosionLogicMultiplayer();
		}

		// Bullets move next 
		playerBulletLogic();
		playerMissileLogic();
		playerBigFireballLogic();
		playerSmallFireballLogic();
		enemyBulletLogic();
		// interactions between objects 
		gameLogic();
	}

	private void gameLogic() { 
			
		for (GameObject temp : EnemiesList) {
			for (GameObject bullet : playerBulletList) {
				if (collision(temp, bullet, 1.5f)) {
					playerBulletList.remove(bullet);
					if (temp.getHealth() <= 0) {
						EnemiesList.remove(temp);
						CreateExplosion(temp);
						if (!gameOver) { 
							Sound.playExplosionSound();
							Score += 100;
						}
					} else {
						temp.setHealth(temp.getHealth() - 25);
						if (temp.getHealth() <= 0) {
							EnemiesList.remove(temp);
							CreateExplosion(temp);
							if (!gameOver) { 
								Sound.playExplosionSound();
								Score += 100;
							}
						}
					}
				}  
			}
			for (GameObject ball : playerBigFireballList) {
				if (collision(temp, ball, 1.5f)) {
					playerBigFireballList.remove(ball);
					if (temp.getHealth() <= 0) {
						EnemiesList.remove(temp);
						CreateExplosion(temp);
						if (!gameOver) { 
							Sound.playExplosionSound();
							Score += 100;
						}
					} else {
						temp.setHealth(temp.getHealth() - 65);
						if (temp.getHealth() <= 0) {
							EnemiesList.remove(temp);
							CreateExplosion(temp);
							if (!gameOver) { 
								Sound.playExplosionSound();
								Score += 100;
							}
						}
					}
				}  
			}
			for (GameObject ball : playerSmallFireballList) {
				if (collision(temp, ball, 1.5f)) {
					playerSmallFireballList.remove(ball);
					if (temp.getHealth() <= 0) {
						EnemiesList.remove(temp);
						CreateExplosion(temp);
						if (!gameOver) { 
							Sound.playExplosionSound();
							Score += 100;
						}
					} else {
						temp.setHealth(temp.getHealth() - 35);
						if (temp.getHealth() <= 0) {
							EnemiesList.remove(temp);
							CreateExplosion(temp);
							if (!gameOver) {
								Sound.playExplosionSound();
								Score += 100;
							}
						}
					}
				}  
			}
		}
		
		for (GameObject missile : playerMissileList) {
			GameObject target = missile.getTarget();
			if (collision(missile, target, 1.5f)) {
				playerMissileList.remove(missile);
				EnemiesList.remove(target);
				CreateExplosion(target);
				if (!gameOver) { 
					Sound.playExplosionSound();
					Score += 100;
				}
			}  
		}
		if (singleplayer) {
			for (GameObject bullet : enemyBulletList) {
				if (collision(bullet, Player, 1)) {
					enemyBulletList.remove(bullet);
					Player.setHealth(Player.getHealth() - 4);
					if (Player.getHealth() > 0) {
						Sound.playHitSound();						
					}
					if (Player.getHealth() <= 0 && !gameOver) {
						playerAlive = false;
						CreateExplosion(Player);
						Sound.playExplosionSound();
						Sound.playLoseSound();
						gameOver = true;
						//end the game
					}
				}
			}
		} else {
			for (GameObject bullet : enemyBulletList) {
				if (PlayerList.contains(Player1) && collision(bullet, Player1, 1)) {
					enemyBulletList.remove(bullet);
					Player1.setHealth(Player1.getHealth() - 4);
					if (Player1.getHealth() > 0) {
						Sound.playHitSound();						
					}
					if (Player1.getHealth() <= 0 && !gameOver) {
						player1Alive = false;
						CreateExplosion(Player1);
						PlayerList.remove(Player1);
						Sound.playExplosionSound();
						if (PlayerList.size() == 0) {
							Sound.playLoseSound();
							gameOver = true;
						}
						//end the game
					}
				}
				if (PlayerList.contains(Player2) && collision(bullet, Player2, 1)) {
					enemyBulletList.remove(bullet);
					Player2.setHealth(Player2.getHealth() - 4);
					if (Player2.getHealth() > 0) {
						Sound.playHitSound();						
					}
					if (Player2.getHealth() <= 0 && !gameOver) {
						player2Alive = false;
						CreateExplosion(Player2);
						PlayerList.remove(Player2);
						Sound.playExplosionSound();
						if (PlayerList.size() == 0) {
							Sound.playLoseSound();
							gameOver = true;
						}
						//end the game
					}
				}
			}
		}
	}

	private void enemyLogic() {
		// TODO Auto-generated method stub
		for (GameObject temp : EnemiesList) {
		    // Move enemies 
			
			int playerx = (int) Player.getCentre().getX();
			int playery = (int) Player.getCentre().getY();
			float enemyx = temp.getCentre().getX();
			float enemyy = temp.getCentre().getY();
			Vector3f vecToPlayer = new Vector3f(playerx - enemyx, enemyy - playery, 0).Normal().byScalar(0.4f);
			Vector3f offset = MainWindow.canvas.getMovementVec().NegateVector();
			temp.getCentre().ApplyVector(temp.getVec().PlusVector(offset));
			temp.setVec(vecToPlayer);
			
			//delete the enemy if it is offscreen
			if ((Math.abs(playerx - enemyx)*Math.abs(playerx - enemyx)) +
					(Math.abs(playery - enemyy)*Math.abs(playery - enemyy)) > MAXDISTANCE) {
				EnemiesList.remove(temp);
			} 
		}
		
	}

	
	
	private void playerBulletLogic() {
		for (GameObject temp : playerBulletList) {
			float bulletx = temp.getCentre().getX();
			float bullety = temp.getCentre().getY(); 
			//check to move them
			temp.getCentre().ApplyVector(temp.getVec());
			//see if they hit anything 
			//see if they get to the top of the screen ( remember 0 is the top 
			//delete the enemy if it is offscreen
			if ((Math.abs(500 - bulletx)*Math.abs(500 - bulletx)) +
					(Math.abs(500 - bullety)*Math.abs(500 - bullety)) > MAXDISTANCE) {
				playerBulletList.remove(temp);
			}
		} 
	}
	
	private void playerMissileLogic() {
		// move bullets 
		for (GameObject temp : playerMissileList) {
		    //check to move them
			float missilex = temp.getCentre().getX();
			float missiley = temp.getCentre().getY(); 
			if (temp.getTarget() != null) {
				Vector3f vecToEnemy = new Vector3f(temp.getTarget().getCentre().getX() - missilex, missiley - temp.getTarget().getCentre().getY(), 0).Normal().byScalar(0.4f);
				temp.getCentre().ApplyVector(vecToEnemy.Normal().byScalar(3.5f));
				
				if ((Math.abs(500 - missilex)*Math.abs(500 - missilex)) +
						(Math.abs(500 - missiley)*Math.abs(500 - missiley)) > MAXDISTANCE) {
					playerMissileList.remove(temp);
				} 
			}
		} 
	}
	
	private void playerBigFireballLogic() {
		// move bullets 
		for (GameObject temp : playerBigFireballList) {
		    //check to move them
			float fireballx = temp.getCentre().getX();
			float firebally = temp.getCentre().getY(); 
			temp.getCentre().ApplyVector(temp.getVec());
			//see if they hit anything 
			//see if they get to the top of the screen ( remember 0 is the top 
			if ((Math.abs(500 - fireballx)*Math.abs(500 - fireballx)) +
					(Math.abs(500 - firebally)*Math.abs(500 - firebally)) > MAXDISTANCE) {
				playerBigFireballList.remove(temp);
			} 
		} 
	}
	private void playerSmallFireballLogic() {
		// move bullets 
		for (GameObject temp : playerSmallFireballList) {
		    //check to move them
			float fireballx = temp.getCentre().getX();
			float firebally = temp.getCentre().getY(); 
			temp.getCentre().ApplyVector(temp.getVec());
			//see if they hit anything 
			//see if they get to the top of the screen ( remember 0 is the top 
			if ((Math.abs(500 - fireballx)*Math.abs(500 - fireballx)) +
					(Math.abs(500 - firebally)*Math.abs(500 - firebally)) > MAXDISTANCE) {
				playerSmallFireballList.remove(temp);
			} 
		} 
	}
	
	private void explosionLogic() {	
		for (GameObject temp : ExplosionList) {
			Vector3f offset = MainWindow.canvas.getMovementVec().NegateVector();
			temp.getCentre().ApplyVector(temp.getVec().PlusVector(offset));
			if (System.currentTimeMillis() >= temp.getCreationTime() + 1700) {
				ExplosionList.remove(temp);
			}
		}
	}
	
	private void explosionLogicMultiplayer() {	
		for (GameObject temp : ExplosionList) {
			if (System.currentTimeMillis() >= temp.getCreationTime() + 1700) {
				ExplosionList.remove(temp);
			}
		}
	}
	
	private void enemyBulletLogic() {
		// move bullets 
		for (GameObject temp : enemyBulletList) {
		    //check to move them
			float bulletx = temp.getCentre().getX();
			float bullety = temp.getCentre().getY(); 
			//check to move them
			temp.getCentre().ApplyVector(temp.getVec());
			if ((Math.abs(500 - bulletx)*Math.abs(500 - bulletx)) +
					(Math.abs(500 - bullety)*Math.abs(500 - bullety)) > MAXDISTANCE) {
				enemyBulletList.remove(temp);
			}
		} 
	}
	
	private void playerLogic() {
		//smoother animation is possible if we make a target position  // done but may try to change things for students  
		//check for movement and if you fired a bullet 
		Player.setTexture("res/topviewnormal.png");  
		
		if (Controller.getInstance().isKeyAPressed()) {
			Player.setTexture("res/leftturnnormal.png");
		}
		
		if (Controller.getInstance().isKeyDPressed()) {
			Player.setTexture("res/rightturnnormal.png");
		}
		
		if (Controller.getInstance().isKeyJPressed()) {
			if (System.currentTimeMillis() >= getPlayer().lastBullet + getPlayer().bulletDelay) {
				CreatePlayerBullet();
				getPlayer().lastBullet = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyJPressed(false);
		} 
		
		if (Controller.getInstance().isKeyKPressed()) {
			if (System.currentTimeMillis() >= getPlayer().lastFireball + getPlayer().fireballDelay) {
				CreateFireball();
				getPlayer().lastFireball = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyKPressed(false);
		}
		
		if (Controller.getInstance().isKeyLPressed()) {
			if (System.currentTimeMillis() >= getPlayer().lastMissile + getPlayer().missileDelay) {
				CreateMissile();
				getPlayer().lastMissile = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyLPressed(false);
		} 
		
	}
	
	public void CreateEnemyBullet(GameObject obj, Vector3f vec) {
		if (System.currentTimeMillis() >= obj.lastBullet + obj.bulletDelay &&
				obj.getCentre().getX() > 10 && obj.getCentre().getX() < MainWindow.frame.getWidth() - 10 &&
				obj.getCentre().getY() > 10 && obj.getCentre().getY() < MainWindow.frame.getHeight() - 10) {
			//rotate the bullet trajectories to offset rotation of the player so that
			//enemies still shoot at the player
			//also give trajectories a bit of variation so they don't hit the player every time
			vec = MainWindow.canvas.turnBy(vec.byScalar(4.5f).NegateVector(),
					360 - MainWindow.canvas.getRotationAngle() + (r.nextInt(40) - 20));
			float x = vec.getX();
			vec.setX(-1 * x);
			//give a bit of variation to bullet trajectories so they don't hit the player every time
			enemyBulletList.add(new GameObject("res/enemybullet.png", 10, 10, new Point3f(obj.getCentre().getX() + 16, obj.getCentre().getY() + 16, 0.0f), vec));
			obj.lastBullet = System.currentTimeMillis();
		}
	}
	public void CreateEnemyBulletMultiplayer(GameObject obj, Vector3f vec) {
		if (System.currentTimeMillis() >= obj.lastBullet + obj.bulletDelay &&
				obj.getCentre().getX() > 10 && obj.getCentre().getX() < MainWindow.frame.getWidth() - 10 &&
				obj.getCentre().getY() > 10 && obj.getCentre().getY() < MainWindow.frame.getHeight() - 10) {
			
			vec = MainWindow.canvas.turnBy(vec.byScalar(4.5f).NegateVector(), (r.nextInt(40) - 20));
			float x = vec.getX();
			vec.setX(-1 * x);
			enemyBulletList.add(new GameObject("res/enemybullet.png", 10, 10, new Point3f(obj.getCentre().getX() + 16, obj.getCentre().getY() + 16, 0.0f), vec));
			obj.lastBullet = System.currentTimeMillis();
		}
	}
	

	private void CreatePlayerBullet() {
		playerBulletList.add(new GameObject("res/bullet.png", 12, 12 , new Point3f(Player.getCentre().getX(),  Player.getCentre().getY(),0.0f), MainWindow.canvas.turn(new Vector3f(0, 7, 0))));
	}
	
	private void CreatePlayerBulletMultiplayer(GameObject obj) {
		playerBulletList.add(new GameObject("res/bullet.png", 12, 12 , new Point3f(obj.getCentre().getX(), obj.getCentre().getY(),0.0f), MainWindow.canvas.turnBy(new Vector3f(0, 7, 0), obj.playerAngle)));
	}
	

	private void CreateMissile() {
		GameObject missile = new GameObject("res/blankSprite.png", 32, 32, new Point3f(MainWindow.frame.getWidth()/2, MainWindow.frame.getHeight()/2,0.0f), MainWindow.canvas.turn(new Vector3f(0, 3, 0)));
		GameObject closest = null;
		float minDist = Float.MAX_VALUE;
		for (GameObject temp : EnemiesList) {
			float dist = (Player.getCentre().getX() - temp.getCentre().getX()) * (Player.getCentre().getX() - temp.getCentre().getX()) +
					(Player.getCentre().getY() - temp.getCentre().getY()) * (Player.getCentre().getY() - temp.getCentre().getY());
			if (dist < minDist) {
				closest = temp;
				minDist = dist;
			}
		}

		missile.setTarget(closest);
		if (closest != null) {
			playerMissileList.add(missile);
		}
	}
	
	private void CreateMissileMultiplayer(GameObject obj) {
		GameObject missile = new GameObject("res/blankSprite.png", 32, 32, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(),0.0f), MainWindow.canvas.turnBy(new Vector3f(0, 3, 0), obj.playerAngle));
		GameObject closest = null;
		float minDist = Float.MAX_VALUE;
		for (GameObject temp : EnemiesList) {
			float dist = (obj.getCentre().getX() - temp.getCentre().getX()) * (obj.getCentre().getX() - temp.getCentre().getX()) +
					(obj.getCentre().getY() - temp.getCentre().getY()) * (obj.getCentre().getY() - temp.getCentre().getY());
			if (dist < minDist) {
				closest = temp;
				minDist = dist;
			}
		}

		missile.setTarget(closest);
		if (closest != null) {
			playerMissileList.add(missile);
		}
	}
	
	private void CreateFireball() {
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(0, 3, 0))));
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(0, -3, 0))));
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(3, 0, 0))));
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(-3, 0, 0))));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(1, 1, 0).Normal().byScalar(4))));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(1, -1, 0).Normal().byScalar(4))));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(-1, 1, 0).Normal().byScalar(4))));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f), MainWindow.canvas.turn(new Vector3f(-1, -1, 0).Normal().byScalar(4))));
	}
	
	private void CreateFireballMultiplayer(GameObject obj) {
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(0, 3, 0), obj.playerAngle)));
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(0, -3, 0), obj.playerAngle)));
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(3, 0, 0), obj.playerAngle)));
		playerBigFireballList.add(new GameObject("res/fireballbig.png", 30, 30, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(-3, 0, 0), obj.playerAngle)));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(1, 1, 0).Normal().byScalar(4), obj.playerAngle)));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(1, -1, 0).Normal().byScalar(4), obj.playerAngle)));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(-1, 1, 0).Normal().byScalar(4), obj.playerAngle)));
		playerSmallFireballList.add(new GameObject("res/fireballsmall.png", 25, 25, new Point3f(obj.getCentre().getX(), obj.getCentre().getY(), 0.0f), MainWindow.canvas.turnBy(new Vector3f(-1, -1, 0).Normal().byScalar(4), obj.playerAngle)));
	}


	
	private void CreateExplosion(GameObject temp) {
		GameObject explosion = new GameObject("res/explosion1.png", 64, 64, new Point3f(temp.getCentre().getX(), temp.getCentre().getY(),0.0f), new Vector3f(0, 0, 0));
		ExplosionList.add(explosion);
	}
	
	private void spawnEnemy() {
		if (System.currentTimeMillis() >= lastEnemySpawned + spawnDelay
				&& EnemiesList.size() <= 8) {
			float playerx = Player.getCentre().getX();
			float playery = Player.getCentre().getX();
			
			int q = r.nextInt(4);
			float enemyx;
			float enemyy;
			if (q == 0) { //spawn enemy above screen
				enemyx = r.nextInt(1000);
				enemyy = -40;
			} else if (q == 1) { //spawn enemy to the left of screen
				enemyx = -40;
				enemyy = r.nextInt(1000);
			} else if (q == 2) { //spawn enemy to the right of screen
				enemyx = MainWindow.frame.getWidth() + 40; 
				enemyy = r.nextInt(1000);
			} else { //spawn enemy below screen
				enemyx = r.nextInt(1000);
				enemyy = MainWindow.frame.getHeight() + 40;
			}
			
			GameObject enemy = new GameObject("res/blankSprite.png",50,50,
					new Point3f(enemyx, enemyy, 0), new Vector3f(playerx - enemyx, playery - enemyy, 0).Normal());
			enemy.setHealth(100);
			enemy.bulletDelay = enemyBulletDelay + r.nextInt(200);;
			EnemiesList.add(enemy);
			
			lastEnemySpawned = System.currentTimeMillis();
			spawnDelay = spawnTimes[r.nextInt(spawnTimes.length)];
			
		}
	}
	
	public boolean collision(GameObject a, GameObject b, float multiplier) {
		float sqdist = ((a.getCentre().getY() - b.getCentre().getY()) * (a.getCentre().getY() - b.getCentre().getY())) +
				((a.getCentre().getX() - b.getCentre().getX()) * (a.getCentre().getX() - b.getCentre().getX()));
		float sqSumRadii = ((a.getWidth()/2) * multiplier + (b.getWidth()/2) * multiplier) * ((a.getWidth()/2) * multiplier+ (b.getWidth()/2) * multiplier);
		if (sqdist < sqSumRadii) {
			return true;
		} else {
			return false;
		}
	}
	
	private void playerLogicMultiplayer() {
		//smoother animation is possible if we make a target position  // done but may try to change things for students  
		//check for movement and if you fired a bullet 
		
		if (Controller.getInstance().isKeyAPressed() && MainWindow.canvas.CurrentAnimationTime % 10 == 0) {
			Vector3f vec;
			switch(Player1.getTexture()) {
			case "res/player1_0.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_45.png");
				Player1.playerAngle = 45;
				break;
			case "res/player1_45.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_90.png");
				Player1.playerAngle = 90;
				break;
			case "res/player1_90.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_135.png");
				Player1.playerAngle = 135;
				break;
			case "res/player1_135.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_180.png");
				Player1.playerAngle = 180;
				break;
			case "res/player1_180.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_225.png");
				Player1.playerAngle = 225;
				break;
			case "res/player1_225.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_270.png");
				Player1.playerAngle = 270;
				break;
			case "res/player1_270.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_315.png");
				Player1.playerAngle = 315;
				break;
			case "res/player1_315.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), 45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_0.png");
				Player1.playerAngle = 0;
				break;
			default:
				Player1.getCentre().ApplyVector(Player1.getVec()); 
			}
		}
		
		if (Controller.getInstance().isKeyDPressed() && MainWindow.canvas.CurrentAnimationTime % 10 == 0) {
			Vector3f vec;
			switch(Player1.getTexture()) {
			case "res/player1_0.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_315.png");
				Player1.playerAngle = 315;
				break;
			case "res/player1_45.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_0.png");
				Player1.playerAngle = 0;
				break;
			case "res/player1_90.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_45.png");
				Player1.playerAngle = 45;
				break;
			case "res/player1_135.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_90.png");
				Player1.playerAngle = 90;
				break;
			case "res/player1_180.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_135.png");
				Player1.playerAngle = 135;
				break;
			case "res/player1_225.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_180.png");
				Player1.playerAngle = 180;
				break;
			case "res/player1_270.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_225.png");
				Player1.playerAngle = 225;
				break;
			case "res/player1_315.png":
				vec = MainWindow.canvas.turnBy(Player1.getVec(), -45);
				Player1.getCentre().ApplyVector(vec); 
				Player1.setVec(vec);
				Player1.setTexture("res/player1_270.png");
				Player1.playerAngle = 270;
				break;
			default:
				Player1.getCentre().ApplyVector(Player1.getVec()); 
			}
		}
		
		if (((!Controller.getInstance().isKeyAPressed() && !Controller.getInstance().isKeyDPressed()) ||
		MainWindow.canvas.CurrentAnimationTime % 10 != 0) && player1Alive) {
			Player1.getCentre().ApplyVector(Player1.getVec()); 
		}
		
		float player1x = Player1.getCentre().getX();
		float player1y = Player1.getCentre().getY();
		if (player1x < 0) {
			Player1.getCentre().setX(player1x + MainWindow.frame.getWidth());
		} else if (player1x > MainWindow.frame.getWidth()) {
			Player1.getCentre().setX(player1x - MainWindow.frame.getWidth());
		} else if (player1y < 0) {
			Player1.getCentre().setY(player1y + MainWindow.frame.getHeight());
		} else if (player1y > MainWindow.frame.getHeight()) {
			Player1.getCentre().setY(player1y - MainWindow.frame.getHeight());
		}
			
		
		if (Controller.getInstance().isKeyXPressed() && player1Alive) {
			if (System.currentTimeMillis() >= getPlayer1().lastBullet + getPlayer1().bulletDelay) {
				CreatePlayerBulletMultiplayer(Player1);
				getPlayer1().lastBullet = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyXPressed(false);
		} 
		
		if (Controller.getInstance().isKeyCPressed( )&& player1Alive) {
			if (System.currentTimeMillis() >= getPlayer1().lastFireball + getPlayer1().fireballDelay) {
				CreateFireballMultiplayer(Player1);
				getPlayer1().lastFireball = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyCPressed(false);
		}
		
		if (Controller.getInstance().isKeyVPressed() && player1Alive) {
			if (System.currentTimeMillis() >= getPlayer1().lastMissile + getPlayer1().missileDelay) {
				CreateMissileMultiplayer(Player1);
				getPlayer1().lastMissile = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyLPressed(false);
		} 
		
		if (Controller.getInstance().isKeyLeftBracketPressed() && MainWindow.canvas.CurrentAnimationTime % 10 == 0) {
			Vector3f vec;
			switch(Player2.getTexture()) {
			case "res/player2_0.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_45.png");
				Player2.playerAngle = 45;
				break;
			case "res/player2_45.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_90.png");
				Player2.playerAngle = 90;
				break;
			case "res/player2_90.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_135.png");
				Player2.playerAngle = 135;
				break;
			case "res/player2_135.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_180.png");
				Player2.playerAngle = 180;
				break;
			case "res/player2_180.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_225.png");
				Player2.playerAngle = 225;
				break;
			case "res/player2_225.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_270.png");
				Player2.playerAngle = 270;
				break;
			case "res/player2_270.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_315.png");
				Player2.playerAngle = 315;
				break;
			case "res/player2_315.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), 45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_0.png");
				Player2.playerAngle = 0;
				break;
			default:
				Player2.getCentre().ApplyVector(Player2.getVec()); 
			}
		}
		
		if (Controller.getInstance().isKeyRightBracketPressed() && MainWindow.canvas.CurrentAnimationTime % 10 == 0) {
			Vector3f vec;
			switch(Player2.getTexture()) {
			case "res/player2_0.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_315.png");
				Player2.playerAngle = 315;
				break;
			case "res/player2_45.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_0.png");
				Player2.playerAngle = 0;
				break;
			case "res/player2_90.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_45.png");
				Player2.playerAngle = 45;
				break;
			case "res/player2_135.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_90.png");
				Player2.playerAngle = 90;
				break;
			case "res/player2_180.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_135.png");
				Player2.playerAngle = 135;
				break;
			case "res/player2_225.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_180.png");
				Player2.playerAngle = 180;
				break;
			case "res/player2_270.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_225.png");
				Player2.playerAngle = 225;
				break;
			case "res/player2_315.png":
				vec = MainWindow.canvas.turnBy(Player2.getVec(), -45);
				Player2.getCentre().ApplyVector(vec); 
				Player2.setVec(vec);
				Player2.setTexture("res/player2_270.png");
				Player2.playerAngle = 270;
				break;
			default:
				Player2.getCentre().ApplyVector(Player2.getVec()); 
			}
		}
			
		if (((!Controller.getInstance().isKeyLeftBracketPressed() && !Controller.getInstance().isKeyRightBracketPressed()) ||
				MainWindow.canvas.CurrentAnimationTime % 10 != 0) && player2Alive) {
					Player2.getCentre().ApplyVector(Player2.getVec()); 
		}
		
		float player2x = Player2.getCentre().getX();
		float player2y = Player2.getCentre().getY();
		if (player2x < 0) {
			Player2.getCentre().setX(player2x + MainWindow.frame.getWidth());
		} else if (player2x > MainWindow.frame.getWidth()) {
			Player2.getCentre().setX(player2x - MainWindow.frame.getWidth());
		} else if (player2y < 0) {
			Player2.getCentre().setY(player2y + MainWindow.frame.getHeight());
		} else if (player2y > MainWindow.frame.getHeight()) {
			Player2.getCentre().setY(player2y - MainWindow.frame.getHeight());
		}
		
		
		if (Controller.getInstance().isKeyJPressed() && player2Alive) {
			if (System.currentTimeMillis() >= getPlayer2().lastBullet + getPlayer2().bulletDelay) {
				CreatePlayerBulletMultiplayer(Player2);
				getPlayer2().lastBullet = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyJPressed(false);
		} 
		
		if (Controller.getInstance().isKeyKPressed() && player2Alive) {
			if (System.currentTimeMillis() >= getPlayer2().lastFireball + getPlayer2().fireballDelay) {
				CreateFireballMultiplayer(Player2);
				getPlayer2().lastFireball = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyKPressed(false);
		}
		
		if (Controller.getInstance().isKeyLPressed() && player2Alive) {
			if (System.currentTimeMillis() >= getPlayer2().lastMissile + getPlayer2().missileDelay) {
				CreateMissileMultiplayer(Player2);
				getPlayer2().lastMissile = System.currentTimeMillis();
			}
			Controller.getInstance().setKeyLPressed(false);
		} 
		
	}
	
	private void enemyLogicMultiplayer() {
		for (GameObject temp : EnemiesList) {
		    // Move enemies 
			
			if (!PlayerList.contains(temp.getTarget()) && temp.getTarget() == Player1) {
				temp.setTarget(Player2);
			} else if (!PlayerList.contains(temp.getTarget()) && temp.getTarget() == Player2) {
				temp.setTarget(Player1);
			}
			int playerx = (int) temp.getTarget().getCentre().getX();
			int playery = (int) temp.getTarget().getCentre().getY();
			float enemyx = temp.getCentre().getX();
			float enemyy = temp.getCentre().getY();
			Vector3f vecToPlayer = new Vector3f(playerx - enemyx, enemyy - playery, 0).Normal().byScalar(0.5f);
			temp.getCentre().ApplyVector(temp.getVec());
			temp.setVec(vecToPlayer);
			
			//delete the enemy if it is offscreen
			if ((Math.abs(500 - enemyx)*Math.abs(500 - enemyx)) +
					(Math.abs(500 - enemyy)*Math.abs(500 - enemyy)) > MAXDISTANCE) {
				EnemiesList.remove(temp);
			} 
		}
	}
	
	private void spawnEnemyMultiplayer() {
		boolean bothAlive = false;
		boolean player1Alive = false;
		float playerx = 0;
		float playery = 0;
		float player1x = 0;
		float player1y =  0;
		float player2x = 0;
		float player2y = 0;
		if (System.currentTimeMillis() >= lastEnemySpawned + spawnDelay
				&& EnemiesList.size() <= 8) {
			if (isPlayer1Alive() && isPlayer2Alive()) {
				player1x = Player1.getCentre().getX();
				player1y = Player1.getCentre().getX();
				player2x = Player2.getCentre().getX();
				player2y = Player2.getCentre().getX();
				bothAlive = true;
			} else if (isPlayer1Alive()) {
				playerx = Player1.getCentre().getX();
				playery = Player1.getCentre().getX();
				player1Alive = true;
			} else {
				playerx = Player2.getCentre().getX();
				playery = Player2.getCentre().getX();
			}
			
			int target;
			GameObject t;
			if(bothAlive) {
				target = r.nextInt(2);
				if (target == 0) {
					playerx = player1x;
					playery = player1y;
					t = Player1;
				} else {
					playerx = player2x;
					playery = player2y;
					t = Player2;
				}
			} else if (player1Alive) {
				playerx = player1x;
				playery = player1y;
				t = Player1;
			} else {
				playerx = player2x;
				playery = player2y;
				t = Player2;
			}
			
			int q = r.nextInt(4);
			float enemyx;
			float enemyy;
			if (q == 0) { //spawn enemy above screen
				enemyx = r.nextInt(1000);
				enemyy = -40;
			} else if (q == 1) { //spawn enemy to the left of screen
				enemyx = -40;
				enemyy = r.nextInt(1000);
			} else if (q == 2) { //spawn enemy to the right of screen
				enemyx = MainWindow.frame.getWidth() + 40; 
				enemyy = r.nextInt(1000);
			} else { //spawn enemy below screen
				enemyx = r.nextInt(1000);
				enemyy = MainWindow.frame.getHeight() + 40;
			}
			
			GameObject enemy = new GameObject("res/blankSprite.png",50,50,
					new Point3f(enemyx, enemyy, 0), new Vector3f(playerx - enemyx, playery - enemyy, 0).Normal());
			enemy.setHealth(100);
			enemy.bulletDelay = enemyBulletDelay + r.nextInt(200);
			enemy.setTarget(t);
			EnemiesList.add(enemy);
			
			lastEnemySpawned = System.currentTimeMillis();
			spawnDelay = spawnTimes[r.nextInt(spawnTimes.length)];
			
		}
	}
	
	public GameObject getPlayer() {
		return Player;
	}
	
	public GameObject getPlayer1() {
		return Player1;
	}
	
	public GameObject getPlayer2() {
		return Player2;
	}

	public CopyOnWriteArrayList<GameObject> getEnemies() {
		return EnemiesList;
	}
	
	public CopyOnWriteArrayList<GameObject> getBullets() {
		return playerBulletList;
	}
	
	public CopyOnWriteArrayList<GameObject> getBigFireballs() {
		return playerBigFireballList;
	}
	
	public CopyOnWriteArrayList<GameObject> getSmallFireballs() {
		return playerSmallFireballList;
	}
	
	public CopyOnWriteArrayList<GameObject> getMissiles() {
		return playerMissileList;
	}
	
	public CopyOnWriteArrayList<GameObject> getEnemyBullets() {
		return enemyBulletList;
	}
	
	public CopyOnWriteArrayList<GameObject> getExplosions() {
		return ExplosionList;
	}

	public int getScore() { 
		return Score;
	}
	
	public boolean isPlayer1Alive() {
		return player1Alive;
	}
	
	public boolean isPlayer2Alive() {
		return player2Alive;
	}
	
	public boolean isPlayerAlive() {
		return playerAlive;
	}
	public void setScore(int x) { 
		Score = x;
	}
	
	public void setCanvas (Viewer c) {
		canvas = c;
	}
 
	public void restart(boolean s) {
		PlayerList  = new CopyOnWriteArrayList<GameObject>();
		EnemiesList  = new CopyOnWriteArrayList<GameObject>();
		ExplosionList  = new CopyOnWriteArrayList<GameObject>();
		
		playerBulletList  = new CopyOnWriteArrayList<GameObject>();
		playerMissileList  = new CopyOnWriteArrayList<GameObject>();
		playerBigFireballList  = new CopyOnWriteArrayList<GameObject>();
		playerSmallFireballList  = new CopyOnWriteArrayList<GameObject>();
		enemyBulletList  = new CopyOnWriteArrayList<GameObject>();
		
		Score = 0; 
		lastEnemySpawned = System.currentTimeMillis();
		playerAlive = true;
		player1Alive = true;
		player2Alive = true;
		gameOver = false;
		
		canvas.reset();
		singleplayer = s;

		Player = new GameObject("res/topviewnormal.png",50,50,new Point3f(500,500,0), new Vector3f(0, 0, 0));
		Player.setHealth(100);
		Player1 = new GameObject("res/player1_0.png",50,50,new Point3f(400,500,0), new Vector3f(0, 0, 0));
		Player1.setHealth(100);
		Player1.setVec(new Vector3f(0, 2, 0));
		Player1.playerAngle = 0;
		Player2 = new GameObject("res/player2_0.png",50,50,new Point3f(600,500,0), new Vector3f(0, 0, 0));
		Player2.setHealth(100);
		Player2.setVec(new Vector3f(0, 2, 0));
		Player2.playerAngle = 0;
		lastEnemySpawned = System.currentTimeMillis();
		PlayerList.add(Player1);
		PlayerList.add(Player2);
		
		Sound.playStartSound();

	}

}
