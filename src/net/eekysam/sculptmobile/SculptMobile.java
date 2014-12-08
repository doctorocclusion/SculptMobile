package net.eekysam.sculptmobile;

import java.util.Iterator;
import java.util.List;

import net.eekysam.sculptmobile.geo.Point;
import net.eekysam.sculptmobile.geo.Triangle;
import net.eekysam.sculptmobile.mesh.Polygon;
import net.eekysam.sculptmobile.mesh.Polygon.PolygonException;
import net.eekysam.sculptmobile.mesh.TriangleArrayMesh;
import net.eekysam.sculptmobile.mesh.Triangulator;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class SculptMobile
{
	public static void main(String[] args)
	{
		SculptMobile instance = new SculptMobile();
		instance.run();
	}

	// {Outline, Fill, Center of Mass, Mesh, All Centers}
	public static boolean[][] modes = new boolean[][] { { true, true, true, false, false }, { false, true, false, true, false }, { false, true, false, true, true } };

	public Polygon poly = null;
	public TriangleArrayMesh mesh = null;
	public Point centerOfMass = null;
	public PolyDraw draw = null;
	public int renderMode = 1;

	protected void run()
	{
		this.updateDisplay(500, 500, false, true);

		Keyboard.enableRepeatEvents(false);

		try
		{
			Display.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}

		this.runloop();
	}

	private void renderPoint(Point p)
	{
		GL11.glVertex2d(p.x, p.y);
	}

	private void renderTriangle(Triangle t)
	{
		for (int i = 0; i < 3; i++)
		{
			this.renderPoint(t.points[i]);
		}
	}

	private void renderPoint(Point p, double x, double y)
	{
		GL11.glVertex2d(p.x + x, p.y + y);
	}

	public void tick()
	{
		if (Mouse.isInsideWindow())
		{
			Point loc = new Point(Mouse.getX(), Mouse.getY());
			if (this.draw != null)
			{
				if (!this.draw.tick(loc))
				{
					this.endDraw();
				}
			}
		}
	}

	public boolean[] getMode()
	{
		return SculptMobile.modes[(this.renderMode - 1 + SculptMobile.modes.length) % SculptMobile.modes.length];
	}

	public void render()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glClearColor(0.9F, 0.9F, 0.9F, 1.0F);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		if (this.draw != null)
		{
			List<Point> points = this.draw.getPoints();

			if (points.size() >= 2)
			{
				GL11.glColor3f(0.0f, 0.0f, 0.0f);
				GL11.glLineWidth(1.0f);

				GL11.glBegin(GL11.GL_LINE_STRIP);

				for (Point p : points)
				{
					this.renderPoint(p);
				}

				Point loc = new Point(Mouse.getX(), Mouse.getY());
				this.renderPoint(loc);

				if (this.draw.canEnd(loc))
				{
					this.renderPoint(points.get(0));
				}

				GL11.glEnd();
			}
		}

		boolean[] mode = this.getMode();

		if (this.mesh != null)
		{
			if (mode[1])
			{
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
				GL11.glColor3f(0.5f, 0.5f, 0.5f);

				GL11.glBegin(GL11.GL_TRIANGLES);

				for (Triangle t : this.mesh.triangles)
				{
					this.renderTriangle(t);
				}

				GL11.glEnd();
			}
			if (mode[3])
			{
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
				GL11.glColor3f(0.0f, 0.0f, 0.0f);
				GL11.glLineWidth(1.0f);

				GL11.glBegin(GL11.GL_TRIANGLES);

				for (Triangle t : this.mesh.triangles)
				{
					this.renderTriangle(t);
				}

				GL11.glEnd();
			}
		}

		if (mode[0] && this.poly != null && !this.poly.verticies.isEmpty())
		{
			GL11.glColor3f(0.0f, 0.0f, 0.0f);
			GL11.glLineWidth(1.0f);

			GL11.glBegin(GL11.GL_LINE_STRIP);

			Iterator<Point> it = this.poly.verticies.iterator();

			while (it.hasNext())
			{
				this.renderPoint(it.next());
			}

			this.renderPoint(this.poly.verticies.getFirst());

			GL11.glEnd();
		}

		if (mode[2] && this.centerOfMass != null)
		{
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			GL11.glLineWidth(1.0f);

			GL11.glBegin(GL11.GL_LINE_STRIP);

			this.renderPoint(this.centerOfMass, -3, 0);
			this.renderPoint(this.centerOfMass, 0, 3);
			this.renderPoint(this.centerOfMass, 3, 0);
			this.renderPoint(this.centerOfMass, 0, -3);
			this.renderPoint(this.centerOfMass, -3, 0);

			GL11.glEnd();
		}
	}

	public void runloop()
	{
		while (!Display.isCloseRequested())
		{
			try
			{
				this.doInputEvents();
				this.tick();
				this.render();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.shutdown();
				return;
			}

			Display.update();
			Display.sync(60);
		}
		this.shutdown();
	}

	private void endDraw()
	{
		this.poly = new Polygon();
		this.poly.verticies.addAll(this.draw.getPoints());
		this.draw = null;
		this.mesh = new TriangleArrayMesh();
		try
		{
			Triangulator tri = new Triangulator(this.poly, this.mesh);
			tri.triangulate();
		}
		catch (PolygonException e)
		{
			e.printStackTrace();
		}
		this.centerOfMass = this.mesh.getCenterOfMass();
	}

	private void doInputEvents()
	{
		while (Mouse.next())
		{
			if (Mouse.getEventButtonState())
			{
				Point loc = new Point(Mouse.getEventX(), Mouse.getEventY());
				int but = Mouse.getEventButton();
				if (but == 0)
				{
					if (this.draw == null)
					{
						this.centerOfMass = null;
						this.mesh = null;
						this.poly = null;
						this.draw = new PolyDraw(10.0F);
					}
					else
					{
						if (this.draw.canEnd(loc))
						{
							this.endDraw();
						}
					}
				}
			}
		}
		while (Keyboard.next())
		{
			boolean num = false;
			try
			{
				this.renderMode = Integer.parseUnsignedInt("" + Keyboard.getEventCharacter());
				num = true;
			}
			catch (NumberFormatException e)
			{

			}
			if (!num)
			{

			}
		}
	}

	public void setDisplayMode(int width, int height, boolean fullscreen)
	{
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen))
		{
			return;
		}

		try
		{
			DisplayMode targetDisplayMode = null;

			if (fullscreen)
			{
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++)
				{
					DisplayMode current = modes[i];

					if ((current.getWidth() == width) && (current.getHeight() == height))
					{
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq))
						{
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))
							{
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency()))
						{
							targetDisplayMode = current;
							break;
						}
					}
				}
			}
			else
			{
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null)
			{
				System.err.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		}
		catch (LWJGLException e)
		{
			System.err.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}

	public void shutdown()
	{
		Display.destroy();
		System.exit(0);
	}

	public void updateDisplay(int width, int height, boolean full, boolean vsync)
	{
		this.setDisplayMode(width, height, full);
		Display.setTitle("Sculpt Mobile");
		Display.setVSyncEnabled(vsync);
	}
}
