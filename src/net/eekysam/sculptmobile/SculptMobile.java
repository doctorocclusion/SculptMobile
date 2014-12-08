package net.eekysam.sculptmobile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Ray;
import net.eekysam.sculptmobile.geo.Triangle;
import net.eekysam.sculptmobile.mesh.Polygon;
import net.eekysam.sculptmobile.mesh.Polygon.PolygonException;
import net.eekysam.sculptmobile.mesh.TriangleArrayMesh;
import net.eekysam.sculptmobile.mesh.Triangulator;

public class SculptMobile
{
	public static void main(String[] args)
	{
		Polygon gon = new Polygon();
		
		gon.verticies.add(new Point(31, 27));
		gon.verticies.add(new Point(27, 36));
		gon.verticies.add(new Point(17, 33));
		gon.verticies.add(new Point(8, 30));
		gon.verticies.add(new Point(14, 24));
		gon.verticies.add(new Point(8, 15));
		gon.verticies.add(new Point(4, 9));
		gon.verticies.add(new Point(14, 3));
		gon.verticies.add(new Point(15, 9));
		gon.verticies.add(new Point(18, 18));
		gon.verticies.add(new Point(18, 27));
		gon.verticies.add(new Point(23, 18));
		gon.verticies.add(new Point(24, 9));
		gon.verticies.add(new Point(31, 21));
		
		TriangleArrayMesh mesh = new TriangleArrayMesh();
		
		Triangulator tritor = new Triangulator(gon, mesh);
		try
		{
			tritor.triangulate();
		}
		catch (PolygonException e)
		{
			e.printStackTrace();
		}
		
		BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
		
		double sx = img.getWidth() / 40.0D;
		double sy = img.getHeight() / 40.0D;
		
		for (int i = 0; i < img.getWidth(); i++)
		{
			for (int j = 0; j < img.getHeight(); j++)
			{
				double x = i / sx;
				double y = j / sy;
				img.setRGB(i, j, 0xFF000000);
				Point p = new Point(x, y);
				int c = 0;
				for (Triangle t : mesh.triangles)
				{
					c++;
					int C = 255 - (int) ((c / (float) mesh.triangles.size()) * 230);
					if (t.isPointInside(p))
					{
						img.setRGB(i, j, 0xFF000000 | C << 16 | C << 8 | C);
						break;
					}
				}
			}
		}
		
		for (int i = 0; i < gon.verticies.size(); i++)
		{
			Point a = gon.verticies.get(i);
			Point b = gon.verticies.get((i + 1) % gon.verticies.size());
			Ray r = new Ray(a, b);
			double l = r.getVector().getLength();
			l *= sx * 4;
			double x = a.x;
			double y = a.y;
			double dx = r.xLength() / l;
			double dy = r.yLength() / l;
			for (int j = 0; j < l; j++)
			{
				img.setRGB((int) (x * sx), (int) (y * sy), 0xFF0000FF);
				x += dx;
				y += dy;
			}
			
			img.setRGB((int) (a.x * sx), (int) (a.y * sy), 0xFFFF0000);
		}
		
		try
		{
			ImageIO.write(img, "PNG", new File("testOut.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
