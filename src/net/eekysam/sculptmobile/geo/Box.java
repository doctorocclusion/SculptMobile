package net.eekysam.sculptmobile.geo;

public class Box
{
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;

	public Box(Point min, Point max)
	{
		this(min.x, min.y, max.x, max.y);
	}

	public Box(double minX, double minY, double maxX, double maxY)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.fixVerts();
	}

	public void fixVerts()
	{
		if (this.maxX < this.minX)
		{
			this.flipX();
		}
		if (this.maxY < this.minY)
		{
			this.flipY();
		}
	}

	protected void flipX()
	{
		double max = this.maxX;
		double min = this.minX;
		this.maxX = min;
		this.minX = max;
	}

	protected void flipY()
	{
		double max = this.maxY;
		double min = this.minY;
		this.maxY = min;
		this.minY = max;
	}

	public boolean intersectsWith(Box box)
	{
		return box.maxX > this.minX && box.minX < this.maxX ? (box.maxY > this.minY && box.minY < this.maxY) : false;
	}

	public static boolean intersects(Box a, Box b)
	{
		return a.intersectsWith(b);
	}
}
