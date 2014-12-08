package net.eekysam.sculptmobile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

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
		
		double[][] verts = new double[][] { { 0.957023, 0.756766 }, { 0.693305, 1.05442 }, { 0.505813, 1.26947 }, { 0.0189531, 1.24461 }, { -0.384784, 1.16841 }, { -0.620767, 0.876489 }, { -0.925177, 0.231389 }, { -0.732801, 0.0940934 }, { -0.708597, -0.280715 }, { -0.182796, -0.804179 }, { 0.131973, -0.990042 }, { 0.492099, -0.62132 }, { 0.835736, -0.582399 }, { 1.19965, -0.128073 }, { 1.04042, 0.206185 } };
		
		for (int i = 0; i < verts.length; i++)
		{
			gon.verticies.add(new Point(verts[i][0], verts[i][1]));
		}
		
		TriangleArrayMesh mesh = new TriangleArrayMesh();
		
		try
		{
			Triangulator tritor = new Triangulator(gon, mesh);
			tritor.triangulate();
		}
		catch (PolygonException e)
		{
			e.printStackTrace();
		}
		
		BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
		
		int ox = img.getWidth() / 2;
		int oy = img.getWidth() / 2;
		double sx = img.getWidth() / 4;
		double sy = img.getHeight() / 4;
		
		int[] colors = new int[mesh.triangles.size()];
		
		Random rand = new Random();
		for (int i = 0; i < colors.length; i++)
		{
			colors[i] = 0xFF000000 | rand.nextInt(0xFFFFFF);
		}
		
		for (int i = 0; i < img.getWidth(); i++)
		{
			for (int j = 0; j < img.getHeight(); j++)
			{
				double x = (i - ox) / sx;
				double y = (j - oy) / sy;
				Point p = new Point(x, y);
				int tn = 0;
				for (Triangle t : mesh.triangles)
				{
					if (t.isPointInside(p))
					{
						img.setRGB(i, j, colors[tn]);
						break;
					}
					tn++;
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
				img.setRGB((int) (x * sx) + ox, (int) (y * sy) + oy, 0xFF0000FF);
				x += dx;
				y += dy;
			}
			
			img.setRGB((int) (a.x * sx) + ox, (int) (a.y * sy) + oy, 0xFFFF0000);
		}
		
		for (Triangle t : mesh.triangles)
		{
			Point c = t.getCentroid();
			img.setRGB((int) (c.x * sx) + ox, (int) (c.y * sy) + oy, 0xFF00FF00);
		}
		
		Point c = mesh.getCenterOfMass();
		img.setRGB((int) (c.x * sx) + ox, (int) (c.y * sy) + oy, 0xFFFFFFFF);
		img.setRGB((int) (c.x * sx) + ox + 1, (int) (c.y * sy) + oy, 0xFFFFFFFF);
		img.setRGB((int) (c.x * sx) + ox, (int) (c.y * sy) + oy + 1, 0xFFFFFFFF);
		img.setRGB((int) (c.x * sx) + ox - 1, (int) (c.y * sy) + oy, 0xFFFFFFFF);
		img.setRGB((int) (c.x * sx) + ox, (int) (c.y * sy) + oy - 1, 0xFFFFFFFF);
		
		System.out.printf("Center: x = %.2f y = %.2f%n", c.x, c.y);
		System.out.printf("Total Area = %.2f%n", mesh.getTotalArea());
		
		try
		{
			ImageIO.write(img, "PNG", new File("out.test.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
