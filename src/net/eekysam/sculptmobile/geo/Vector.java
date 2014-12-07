package net.eekysam.sculptmobile.geo;

public class Vector extends Point
{
	public Vector(double x, double y)
	{
		super(x, y);
	}
	
	public double getLengthSqr()
	{
		return this.x * this.x + this.y * this.y;
	}
	
	public double getLength()
	{
		return Math.sqrt(this.getLength());
	}
	
	public Vector getNormalized()
	{
		double length = this.getLength();
		return new Vector(this.x / length, this.y / length);
	}
	
	public static double dot(Vector a, Vector b)
	{
		return a.x * b.x + a.y * b.y;
	}
}
