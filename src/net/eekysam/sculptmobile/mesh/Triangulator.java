package net.eekysam.sculptmobile.mesh;

import java.util.ArrayList;

import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Triangle;
import net.eekysam.sculptmobile.mesh.Polygon.PolygonException;

public class Triangulator
{
	private Point[] verticies;
	private short[] vertexType;
	private boolean[] isEar;
	private ArrayList<Integer> indicies = new ArrayList<Integer>();
	private ITriangleMesh out;

	public Triangulator(Polygon in, ITriangleMesh out) throws PolygonException
	{
		int num = in.verticies.size();
		this.verticies = new Point[num];
		in.verticies.toArray(this.verticies);
		this.indicies.ensureCapacity(num);
		if (in.isClockwise())
		{
			for (int i = num - 1; i >= 0; i--)
			{
				this.indicies.add(i);
			}
		}
		else
		{
			for (int i = 0; i < num; i++)
			{
				this.indicies.add(i);
			}
		}

		this.vertexType = new short[num];
		this.isEar = new boolean[num];

		for (int i = 0; i < num; i++)
		{
			this.update(i);
		}
		this.out = out;
	}

	private int getIndex(int vert)
	{
		vert += this.indicies.size();
		vert %= this.indicies.size();
		return this.indicies.get(vert);
	}

	private void remove(int vert)
	{
		vert += this.indicies.size();
		vert %= this.indicies.size();
		this.indicies.remove(vert);
	}

	private void clipEar(int vert)
	{
		int ai = this.getIndex(vert - 1);
		int bi = this.getIndex(vert);
		int ci = this.getIndex(vert + 1);

		Point a = this.verticies[ai];
		Point b = this.verticies[bi];
		Point c = this.verticies[ci];

		this.remove(vert);
		this.update(vert - 1);
		this.update(vert);

		this.out.addTriangle(new Triangle(a, b, c));
	}

	private int update(int vert)
	{
		int ai = this.getIndex(vert - 1);
		int bi = this.getIndex(vert);
		int ci = this.getIndex(vert + 1);

		Point a = this.verticies[ai];
		Point b = this.verticies[bi];
		Point c = this.verticies[ci];

		Triangle t = new Triangle(a, b, c);

		if (this.vertexType[bi] != 1)
		{
			this.updateType(a, b, c, bi);
		}

		if (this.vertexType[bi] == 1)
		{
			this.isEar[bi] = true;
			for (int ind : this.indicies)
			{
				if (ind != ai && ind != bi && ind != ci && t.isPointInside(this.verticies[ind]))
				{
					this.isEar[bi] = false;
					break;
				}
			}
		}
		else
		{
			this.isEar[bi] = false;
		}
		return bi;
	}

	private short spannedAreaSign(Point a, Point b, Point c)
	{
		double area = a.x * (c.y - b.y);
		area += b.x * (a.y - c.y);
		area += c.x * (b.y - a.y);
		return (short) Math.signum(-area);
	}

	private void updateType(Point a, Point b, Point c, int index)
	{
		this.vertexType[index] = this.spannedAreaSign(a, b, c);
	}

	public void triangulate() throws PolygonException
	{
		while (this.indicies.size() >= 3)
		{
			boolean flag = false;
			for (int vert = 0; vert < this.indicies.size(); vert++)
			{
				int ind = this.getIndex(vert);

				if (this.isEar[ind])
				{
					flag = true;
					this.clipEar(vert);
					break;
				}
			}

			if (!flag)
			{
				throw new PolygonException("A polygon must have at least one ear.");
			}
		}
	}
}
