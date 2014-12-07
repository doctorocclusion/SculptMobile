package net.eekysam.sculptmobile.geo;

public class Triangle
{
	public final Point[] points = new Point[3];
	
	public Triangle(Point a, Point b, Point c)
	{
		this.points[0] = a;
		this.points[1] = b;
		this.points[2] = c;
	}
	
	public boolean isPointInside(Point p)
	{
		double[] b = this.getBarycentric(p);
		return (b[0] >= 0) && (b[1] >= 0) && (b[0] + b[1] < 1);
	}
	
	public Point getCentroid()
	{
		Ray a = new Ray(Point.mean(this.points[0], this.points[1]), this.points[2]);
		Ray b = new Ray(Point.mean(this.points[1], this.points[2]), this.points[0]);
		return Ray.getIntersection(a, b);
	}
	
	public double getArea()
	{
		return 0.5D * Math.abs((this.points[0].x - this.points[2].x) * (this.points[1].y - this.points[0].y) - (this.points[0].x - this.points[1].x) * (this.points[2].y - this.points[0].y));
	}
	
	public double[] getBarycentric(Point p)
	{
		Vector v0 = (new Ray(this.points[0], this.points[2])).getVector();
		Vector v1 = (new Ray(this.points[0], this.points[1])).getVector();
		Vector v2 = (new Ray(this.points[0], p)).getVector();
		
		double dot00 = Vector.dot(v0, v0);
		double dot01 = Vector.dot(v0, v1);
		double dot02 = Vector.dot(v0, v2);
		double dot11 = Vector.dot(v1, v1);
		double dot12 = Vector.dot(v1, v2);
		
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
		
		return new double[] { u, v };
	}
}
