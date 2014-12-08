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

	public Point getMidpoint()
	{
		return Point.mean(this.start, this.end);
	}

	public double getLengthSqr()
	{
		return this.getVector().getLengthSqr();
	}

	public double getLength()
	{
		return this.getVector().getLength();
	}

	public Vector getVector()
	{
		return new Vector(this.xLength(), this.yLength());
	}

	public Vector getNormalizedVector()
	{
		return new Vector(this.xLength(), this.yLength()).getNormalized();
	}

	public static Point getIntersection(Ray a, Ray b)
	{
		double denom = -a.xLength() * -b.yLength() - -a.yLength() * -b.xLength();
		double adet = a.start.x * a.end.y - a.start.y * a.end.x;
		double bdet = b.start.x * b.end.y - b.start.y * b.end.x;
		double x = adet * -b.xLength() - bdet * -a.xLength();
		x /= denom;
		double y = adet * -b.yLength() - bdet * -a.yLength();
		y /= denom;
		if (Double.isFinite(x) && Double.isFinite(y))
		{
			return new Point(x, y);
		}
		return null;
	}

	public double crossMagnitude(Point p)
	{
		Vector a = this.getVector();
		Vector b = (new Ray(this.start, p)).getVector();
		return Vector.crossMagnitude(a, b);
	}

	public Box getBox()
	{
		return new Box(this.start, this.end);
	}

	public static boolean doRaysCross(Ray a, Ray b)
	{
		if (Box.intersects(a.getBox(), b.getBox()))
		{
			return a.crossMagnitude(b.start) * a.crossMagnitude(b.end) < 0;
		}
		return false;
	}

	public static boolean doLinesCross(Ray a, Ray b)
	{
		return a.crossMagnitude(b.start) * a.crossMagnitude(b.end) < 0;
	}
}
