package net.eekysam.sculptmobile.geo;

public class Ray
{
	public Point start;
	public Point end;
	
	public Ray(Point start, Point end)
	{
		this.start = start;
		this.end = end;
	}
	
	public double xLength()
	{
		return this.end.x - this.start.x;
	}
	
	public double yLength()
	{
		return this.end.y - this.start.y;
	}
	
	public Vector getVector()
	{
		return new Vector(this.xLength(), this.yLength());
	}
	
	public static Point getIntersection(Ray a, Ray b)
	{
		double denom = a.xLength() * b.yLength() - a.yLength() * b.xLength();
		double adet = a.start.x * a.end.y + a.start.y * a.end.x;
		double bdet = b.start.x * b.end.y + b.start.y * b.end.x;
		double x = adet * b.xLength() - bdet * a.xLength();
		x /= denom;
		double y = adet * b.yLength() - bdet * a.yLength();
		y /= denom;
		return new Point(x, y);
	}
}
