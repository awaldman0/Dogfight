package util;
public class GameObject {
	
	private Point3f centre = new Point3f(0, 0, 0);	// Center of object, using 3D as objects may be scaled  
	private int width = 10;
	private int height = 10;
	private boolean hasTextured = false;
	private String textureLocation; 
	private String blanktexture = "res/blankSprite.png";
	private int health;
	private Vector3f movementVec; 
	private GameObject target;
	public int bulletDelay = 100;
	public long lastBullet;
	public int missileDelay = 4000;
	public long lastMissile;
	public int fireballDelay = 2000;
	public long lastFireball;
	public long creationTime;
	public boolean playerExplosion = false;
	public boolean player1Explosion = false;
	public boolean player2Explosion = false;
	public int playerAngle;
	
	public GameObject() {  
		
	}
	
    public GameObject(String textureLocation, int width, int height, Point3f centre, Vector3f movementVec) { 
    	 hasTextured=true;
    	 this.textureLocation = textureLocation;
    	 this.width = width;
		 this.height = height;
		 this.centre = centre;
		 this.movementVec = movementVec;
		 this.creationTime = System.currentTimeMillis();
	}

	public Point3f getCentre() {
		return centre;
	}

	public Vector3f getVec() {
		return movementVec;
	}
	
	public void setVec(Vector3f newVec) {
		this.movementVec = newVec;
	}
	
	public void setCentre(Point3f centre) {
		this.centre = centre;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTexture() {
		if(hasTextured) {
			return textureLocation;
		}
		return blanktexture; 
	}
	
	public void setTexture(String filepath) {
		this.textureLocation = filepath;
		return;
	}
  
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int x) {
		this.health = x;
	}
	
	public GameObject getTarget() {
		return target;
	}
	
	public void setTarget(GameObject obj) {
		this.target = obj;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
}