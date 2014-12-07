package net.eekysam.sculptmobile.mesh;

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
}
