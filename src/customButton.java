import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

public class customButton
{
	private final int w, h;
	private int x, y, tx, ty;
	private boolean init = true;
	private final Color c1, c2;
	private Color c;
	private final String s;
	
	public customButton(int x, int y, int w, int h, Color c1, Color c2, String s)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.c1 = c1;
		this.c2 = c2;
		this.s = s;
		c = c1;
	}
	
	public void run(int mx, int my)
	{
		if (check(mx, my))
			c = c2;
		else
			c = c1;
	}
	
	public boolean check(int mx, int my)
	{
		return (mx > x && my > y && mx < x + w && my < y + h);
	}
	
	public void setPos(int xx, int yy)
	{
		x = xx;
		y = yy;
	}
	
	public void setHover(boolean on)
	{
		if (on)
			c = c2;
		else
			c = c1;
	}
	
	public void paint(Graphics g)
	{
		if (init)
		{
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(s, g);
			
			int tw = (int)(rect.getWidth());
			int th = (int)(rect.getHeight());
			tx = w/2 - tw/2;
			ty = h/2 + th/2 - 2;
			
			init = false;
		}
		
		g.setColor(c);
		g.fillRect(x, y, w, h);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, w, h);
		g.drawString(s, x + tx, y + ty);
	}
}