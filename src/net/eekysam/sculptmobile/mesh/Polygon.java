package net.eekysam.sculptmobile.mesh;

import java.util.Iterator;
import java.util.LinkedList;

import net.eekysam.sculptmobile.geo.Point;

public class Polygon
{
	public static class PolygonException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		public PolygonException()
		{
			super();
		}
		
		public PolygonException(String msg)
		{
			super(msg);
		}
	}
	
	public LinkedList<Point> verticies = new LinkedList<Point>();
	
	public boolean isClockwise() throws PolygonException
	{
		int size = this.verticies.size();
		
		if (size < 3)
		{
			throw new PolygonException("Polygon must have at least 3 verticies.");
		}
		
		int count = 0;
		double z;
		
		Iterator<Point> it = this.verticies.listIterator();
		Point a = it.next();
		Point b = it.next();
		Point c = it.next();
		
		do
		{
			z = (b.x - a.x) * (c.y - b.y);
			z -= (b.y - a.y) * (c.x - b.x);
			if (z < 0)
			{
				count--;
			}
			else if (z > 0)
			{
				count++;
			}
			a = b;
			b = c;
			c = it.next();
		}
		while (it.hasNext());
		
		if (count > 0)
		{
			return false;
		}
		else if (count < 0)
		{
			return true;
		}
		else
		{
			throw new PolygonException("Polygon is not solid.");
		}
	}
}
