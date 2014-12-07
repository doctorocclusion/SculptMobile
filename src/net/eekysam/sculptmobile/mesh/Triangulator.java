package net.eekysam.sculptmobile.mesh;

import java.util.ArrayList;

import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Triangle;
import net.eekysam.sculptmobile.mesh.Polygon.PolygonException;

public class Triangulator
{
	private ArrayList<Point> verticies;
	private ITriangleMesh out;
	
	public Triangulator(Polygon in, ITriangleMesh out)
	{
		this.verticies = new ArrayList<Point>();
		this.verticies.addAll(in.verticies);
		this.out = out;
	}
	
	public void triangulate() throws PolygonException
	{
		if (this.verticies.size() < 3)
		{
			throw new PolygonException("Polygon must have at least 3 verticies.");
		}
		while (this.run())
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
		
		int size = this.verticies.size();
		for (int i = 0; i < size; i++)
		{
			a = this.verticies.get((i - 1 + size) % size);
			b = this.verticies.get(i);
			c = this.verticies.get((i + 1 + size) % size);
			
			Triangle tri = new Triangle(a, b, c);
			
			boolean hasInside = false;
			
			for (Point p : this.verticies)
			{
				if (tri.isPointInside(p))
				{
					hasInside = true;
					break;
				}
			}
			
			if (!hasInside)
			{
				this.out.addTriangle(tri);
				this.verticies.remove(i);
				return this.verticies.size() < 4;
			}
		}
		
		throw new PolygonException("All polygons should have at lest one convex vertex.");
	}
}
