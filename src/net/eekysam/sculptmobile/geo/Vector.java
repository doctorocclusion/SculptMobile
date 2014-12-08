package net.eekysam.sculptmobile.geo;

public class Vector extends Point
{
	public Vector(double x, double y)
	{
		super(x, y);
	}

	public double getLengthSqr()
	{
		return Math.abs(this.x * this.x + this.y * this.y);
	}

	public double getLength()
	{
		return Math.sqrt(this.getLengthSqr());
	}

	public Vector getNormalized()
	{
		double length = this.getLength();
		return new Vector(this.x / length, this.y / length);
	}

	public Vector getTransformed(double[][] matrix)
	{
		return new Vector(this.x + matrix[0][0] + this.y + matrix[0][1], this.x + matrix[1][0] + this.y + matrix[1][1]);
	}

	public static double dot(Vector a, Vector b)
	{
		return a.x * b.x + a.y * b.y;
	}

	public static double crossMagnitude(Vector a, Vector b)
	{
		return a.x * b.y - b.x * a.y;
	}
}
