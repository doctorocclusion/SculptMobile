package net.eekysam.sculptmobile.mesh;

import java.util.ArrayList;

import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Ray;
import net.eekysam.sculptmobile.geo.Triangle;
import net.eekysam.sculptmobile.geo.Vector;
import net.eekysam.sculptmobile.mesh.Polygon.PolygonException;

public class Triangulator
{
	private ArrayList<Point> verticies;
	private ITriangleMesh out;
	private boolean isClockwise;
	
	public Triangulator(Polygon in, ITriangleMesh out) throws PolygonException
	{
		this.verticies = new ArrayList<Point>();
		this.verticies.addAll(in.verticies);
		this.isClockwise = in.isClockwise();
		this.out = out;
	}
	
	public void triangulate() throws PolygonException
	{
		if (this.verticies.size() < 3)
		{
			throw new PolygonException("Polygon must have at least 3 verticies.");
		}
		while (!this.run())
		{
			
		}
		if (this.verticies.size() == 3)
		{
			this.out.addTriangle(new Triangle(this.verticies.get(0), this.verticies.get(1), this.verticies.get(2)));
		}
	}
	
	private boolean run() throws PolygonException
	{
		if (this.verticies.size() < 4)
		{
			return true;
		}
		
		Point a;
		Point b;
		Point c;
		
		int mult = this.isClockwise ? -1 : 1;
		
		int size = this.verticies.size();
		for (int i = 0; i < size; i++)
		{
			a = this.verticies.get((i - 1 + size) % size);
			b = this.verticies.get(i);
			c = this.verticies.get((i + 1 + size) % size);
			
			Vector ray1 = (new Ray(a, Point.mean(a, c))).getVector();
			Vector ray2 = (new Ray(a, c)).getVector().getTransformed(new double[][] { { 0, 1 }, { -1, 0 } });
			
			if (Vector.dot(ray1, ray2) * mult > 0)
			{
				Triangle tri = new Triangle(a, b, c);
				
				boolean hasInside = false;
				
				for (Point p : this.verticies)
				{
					if (p != a && p != b && p != c)
					{
						if (tri.isPointInside(p))
						{
							hasInside = true;
							break;
						}
					}
				}
				
				if (!hasInside)
				{
					this.out.addTriangle(tri);
					this.verticies.remove(i);
					return this.verticies.size() < 4;
				}
			}
		}
		
		throw new PolygonException("All polygons should have at lest one ear.");
	}
}
