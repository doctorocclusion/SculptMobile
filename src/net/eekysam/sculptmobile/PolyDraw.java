package net.eekysam.sculptmobile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.eekysam.sculptmobile.geo.Box;
import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Ray;

public class PolyDraw
{
	private static class PolyCross
	{
		int sideNum;
		Point cross;
	}

	private LinkedList<Point> poly = new LinkedList<Point>();
	private double nextDist;

	public PolyDraw(double res)
	{
		this.nextDist = res * res;
	}

	public List<Point> getPoints()
	{
		return this.poly;
	}

	public boolean canEnd(Point end)
	{
		if (this.poly.size() < 3)
		{
			return false;
		}
		if (end == null)
		{
			end = this.poly.peekLast();
		}
		Ray ray = new Ray(end, this.poly.getFirst());
		return this.rayCrossAny(ray, 0, this.poly.size() - 2) == null;
	}

	public boolean tick(Point p)
	{
		if (this.shouldAddPoint(p))
		{
			if (this.poly.size() > 2)
			{
				Ray next = new Ray(this.poly.peekLast(), p);
				PolyCross cross = this.rayCrossAny(next, this.poly.size() - 2);
				if (cross != null)
				{
					for (int i = 0; i <= cross.sideNum; i++)
					{
						this.poly.removeFirst();
					}
					this.poly.addFirst(cross.cross);
					return false;
				}
			}
			this.poly.addLast(p);
		}
		return true;
	}

	public PolyCross rayCrossAny(Ray ray, int... exsides)
	{
		HashSet<Integer> exclude = new HashSet<Integer>();
		for (int i = 0; i < exsides.length; i++)
		{
			exclude.add(exsides[i]);
		}
		if (this.poly.size() > 2)
		{
			Box raybox = ray.getBox();

			Iterator<Point> it = this.poly.iterator();

			Point a;
			Point b = it.next();

			int i = 0;

			while (it.hasNext())
			{
				a = b;
				b = it.next();
				Ray iray = new Ray(a, b);
				Box iraybox = iray.getBox();
				if (!exclude.contains(i) && Box.intersects(raybox, iraybox))
				{
					if (Ray.doLinesCross(ray, iray))
					{
						PolyCross cross = new PolyCross();
						cross.cross = Ray.getIntersection(ray, iray);
						cross.sideNum = i;
						return cross;
					}
				}
				i++;
			}
		}
		return null;
	}

	public boolean shouldAddPoint(Point p)
	{
		boolean flag = false;
		if (this.poly.size() > 0)
		{
			Ray r = new Ray(this.poly.peekLast(), p);
			if (r.getLengthSqr() >= this.nextDist)
			{
				flag = true;
			}
		}
		else
		{
			flag = true;
		}
		return flag;
	}
}
