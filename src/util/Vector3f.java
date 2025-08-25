package util;

public class Vector3f {
	private float x = 0;
	private  float y = 0;
	private  float z = 0;
	
	public Vector3f() {  
		setX(0.0f);
		setY(0.0f);
		setZ(0.0f);
	}
	 
	public Vector3f(float x, float y, float z) { 
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	public Vector3f PlusVector(Vector3f Additonal) { 
		return new Vector3f(this.getX() + Additonal.getX(), this.getY() + Additonal.getY(), this.getZ() + Additonal.getZ());
	} 
	
	public Vector3f MinusVector(Vector3f Minus) { 
		return new Vector3f(this.getX() - Minus.getX(), this.getY() - Minus.getY(), this.getZ() - Minus.getZ());
	}
	
	public Point3f PlusPoint(Point3f Additonal) { 
		return new Point3f(this.getX() + Additonal.getX(), this.getY() + Additonal.getY(), this.getZ() + Additonal.getZ());
	} 
	
	public Vector3f byScalar(float scale ) {
		return new Vector3f(this.getX() * scale, this.getY() * scale, this.getZ() * scale);
	}
	
	public Vector3f  NegateVector() {
		return new Vector3f(-this.getX(), -this.getY(), -this.getZ());
	}
	
	public float length() {
	    return (float) Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
	}
	
	public Vector3f Normal() {
		float LengthOfTheVector = this.length();
		return this.byScalar(1.0f / LengthOfTheVector); 
	} 
	
	//implement getting the dot product of Vector.Vector and comment what the method does 
	public float dot(Vector3f v) { 
		return (this.getX() * v.getX() + this.getY() * v.getY() + this.getZ() * v.getZ());
	}
	
	public Vector3f cross(Vector3f v) {
	    float u0 = (this.getY() * v.getZ() - getZ() * v.getY());
	    float u1 = (getZ() * v.getX() - getX() * v.getZ());
	    float u2 = (getX() * v.getY() - getY() * v.getX());
	    return new Vector3f(u0,u1,u2);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
 
}
	