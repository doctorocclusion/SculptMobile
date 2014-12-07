package net.eekysam.sculptmobile.mesh;

import java.util.ArrayList;

import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Triangle;

public class TriangleArrayMesh implements ITriangleMesh
{
	public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	
	@Override
	public void addTriangle(Triangle tri)
	{
		this.triangles.add(tri);
	}
	
	public double getTotalArea()
	{
		double a = 0;
		for (Triangle t : this.triangles)
		{
			a += t.getArea();
		}
		return a;
	}
	
	public Point getCenterOfMass()
	{
		double x = 0;
		double y = 0;
		double m = 0;
		for (Triangle t : this.triangles)
		{
			double a = t.getArea();
			m += a;
			Point c = t.getCentroid();
			x += c.x * a;
			y += c.y * a;
		}
		x /= m;
		y /= m;
		return new Point(x, y);
	}
}
