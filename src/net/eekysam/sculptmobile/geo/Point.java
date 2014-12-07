package net.eekysam.sculptmobile.geo;

public class Point
{
	public final double x;
	public final double y;
	
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public static Point mean(Point a, Point b)
	{
		return new Point((b.x + a.x) / 2, (b.y + a.y) / 2);
	}
}
