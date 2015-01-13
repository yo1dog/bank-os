import java.awt.Color;
import java.awt.Graphics;

public class customCheckbox
{
	private final int x, y;
	private boolean state;
	private final String s;
	private final Color c1, c2;
	private Color c;
	
	
	public customCheckbox(int x, int y, boolean state, Color c1, Color c2, String s)
	{
		this.x = x;
		this.y = y;
		this.state = state;
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
		return (mx > x && my > y && mx < x + 12 && my < y + 12);
	}
	
	public void set(boolean state)
	{
		this.state = state;
	}
	
	public boolean get()
	{
		return state;
	}
	
	public boolean toggle()
	{
		if (state)
			state = false;
		else
			state = true;
		
		return state;
	}
	
	public void paint(Graphics g)
	{
		g.setColor(c);
		g.fillRect(x, y, 12, 12);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 12, 12);
		
		if (state)
			g.fillRect(x + 3, y + 3, 7, 7);
		
		g.drawString(s, x + 15, y + 11);
	}
}