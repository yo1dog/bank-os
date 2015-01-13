import java.awt.Graphics;
import java.awt.Color;

public class customScrollBar
{
	private final int w = 16;
	private int x, y, h, cy, ch, myl, sch;
	private float sy, sh;
	private boolean clicked = false;
	
	private int[] arrowX1 = new int[3];
	private int[] arrowY1 = new int[3];
	private int[] arrowX2 = new int[3];
	private int[] arrowY2 = new int[3];
	
	public customScrollBar(int x, int y, int h)
	{
		this.x = x;
		this.y = y;
		this.h = h;
		
		cy = 0;
		ch = 0;
		
		sch = h - w*2;
		
		sy = 0.0f;
		sh = sch;
		
		arrowX1[0] = x + 4; arrowX1[1] = x + 8; arrowX1[2] = x + 13;
		arrowY1[0] = y + 11; arrowY1[1] = y + 7; arrowY1[2] = y + 11;
		arrowX2[0] = x + 5; arrowX2[1] = x + 8; arrowX2[2] = x + 12;
		arrowY2[0] = y + h - 10; arrowY2[1] = y + h - 6; arrowY2[2] = y + h - 10;
	}
	
	public boolean check(int mx, int my)
	{
		if (mx > x && mx < x + w && my > y + w + sy && my < y + w + sy + sh)
		{
			clicked = true;
			myl = my;
			
			return true;
		}
		else
			return false;
	}
	
	public void unclick()
	{
		clicked = false;
	}
	
	public void run(int mx, int my)
	{
		if (clicked)
		{
			sy += my - myl;
			
			if (sy + sh > sch)
				sy = sch - sh;
			if (sy < 0)
				sy = 0;
			
			if (ch > h)
				cy = (int)((sy / (sch - sh))*(ch - h));
			
			myl = my;
		}
	}
	
	public void setContentHeight(int a)
	{
		ch = a;
		
		float p = ch > 0? (float)h / ch : 1.0f;
		if (p > 1.0f)
			p = 1.0f;
		
		sh = p*sch;
		
		if (sy + sh > sch)
			sy = sch - sh;
		
		if (ch > h && cy + h > ch)
			cy = ch - h;
	}
	
	public int getOffset()
	{
		return -cy;
	}
	
	public void setOffset(int i)
	{
		cy = -i;
		sy = (cy * (sch - sh)) / (ch - h);
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.drawRect(x, y, w, h);
		g.fillPolygon(arrowX1, arrowY1, 3);
		g.fillPolygon(arrowX2, arrowY2, 3);
		g.drawLine(x, y + w, x + w, y + w);
		g.drawLine(x, y + h - w, x + w, y + h - w);
		g.fillRect(x, y + w + (int)sy + 1, w, (int)sh);
	}
}