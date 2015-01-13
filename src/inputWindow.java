import java.awt.Graphics;
import java.awt.Color;

public class inputWindow
{
	//Constants
	public final int width = 600, height = 400;
	
	private final int LEFTOFFSET	= 8;
	private final int RIGHTOFFSET	= 54;
	private final int TOPOFFSET		= 30;
	private final int BOTTOMOFFSET	= 10;
	private final int FIELDSPACING	= 40;
	private final int FIELDWIDTH	= 128;
	private final int FIELDHEIGHT	= 20;
	private final int WINDOWWIDTH	= LEFTOFFSET + FIELDWIDTH + RIGHTOFFSET;
	private final int WINDOWHEIGHT;
	private final int WINDOWMINIMIZEDHEIGHT = 16;
	
	private final int CLOSESIZE	= 10;
	private final int CLOSEX	= WINDOWWIDTH - CLOSESIZE - 4;
	private final int CLOSEY	= 4;
	
	private final int MINIMIZESIZE	= 10;
	private final int MINIMIZEX		= CLOSEX - CLOSESIZE - 4;
	private final int MINIMIZEY		= 4;
	
	private final int BARLEFT	= 0;
	private final int BARRIGHT	= MINIMIZEX - 4;
	private final int BARTOP	= 0;
	private final int BARBOTTOM	= 14;
	
	private final int BUTTONWIDTH		= 40;
	private final int BUTTONHEIGHT		= 20;
	private final int BUTTONX			= WINDOWWIDTH - BUTTONWIDTH - 6;
	private final int BUTTONYFROMBOTTOM	= 10;
	private final int BUTTONY;
	
	private final int BLINKRATE	= 30;
	
	private final int TEXT		= 0;
	private final int INT		= 1;
	private final int UFLOAT	= 2;
	private final int FLOAT		= 3;
	private final int ALL		= 4;
	
	//Vars
	private int x, y;
	private int mouseXOffset, mouseYOffset;
	public boolean moving = false;
	
	private int numFields;
	private String fieldLabels[];
	private String fields[];
	private int fieldTypes[];
	private String title;
	
	private int sField = 0;
	private boolean active = true;
	private boolean minimized = false;
	private int blink = 0;
	private bank.windowActionControler actionControler;
	private windowControler wC;
	
	private final customButton okBut;
	
	public inputWindow(String title, int numFields, String[] fieldLabels, int[] fieldTypes, int x, int y, bank.windowActionControler actionControler, windowControler wC)
	{
		this.title = title;
		this.numFields = numFields;
		this.fieldLabels = fieldLabels;
		this.fieldTypes = fieldTypes;
		this.x = x;
		this.y = y;
		this.actionControler = actionControler;
		this.wC = wC;
		
		fields = new String[numFields];
		for (int i=0; i < numFields; i++)
			fields[i] = "";
		
		WINDOWHEIGHT = TOPOFFSET + FIELDSPACING*(numFields - 1) + FIELDHEIGHT + BOTTOMOFFSET;
		BUTTONY = WINDOWHEIGHT - BUTTONHEIGHT - BUTTONYFROMBOTTOM;
		okBut = new customButton(x + BUTTONX, y + BUTTONY,  BUTTONWIDTH, BUTTONHEIGHT, new Color(222, 222, 222), new Color(191, 191, 191), "OK");
	}
	
	public inputWindow(String title, int numFields, String[] fieldLabels, int[] fieldTypes, String[] fields, int x, int y,  bank.windowActionControler actionControler, windowControler wC)
	{
		this.title = title;
		this.numFields = numFields;
		this.fieldLabels = fieldLabels;
		this.fieldTypes = fieldTypes;
		this.fields = fields;
		this.x = x;
		this.y = y;
		this.actionControler = actionControler;
		this.wC = wC;
		
		WINDOWHEIGHT = TOPOFFSET + FIELDSPACING*(numFields - 1) + FIELDHEIGHT + BOTTOMOFFSET;
		BUTTONY = WINDOWHEIGHT - BUTTONHEIGHT - BUTTONYFROMBOTTOM;
		okBut = new customButton(x + BUTTONX, y + BUTTONY,  BUTTONWIDTH, BUTTONHEIGHT, new Color(222, 222, 222), new Color(191, 191, 191), "OK");
	}
	
	//Run
	public void run()
	{
		if (active)
		{
			blink ++;
			if (blink > BLINKRATE*2)
				blink = 0;
		}
	}
	
	//Key press
	public void keyDown(int key)
	{
		if (active)
		{
			if (key == 96)
				randomize();
			
			if (sField > -1)
			{
				//Letters
				//If TEXT or ALL
				if (fieldTypes[sField] == TEXT || fieldTypes[sField] == ALL)
				{
					if ((key > 64 && key < 91) || (key > 96 && key < 123) || key == 32)
					{
						fields[sField] += (char)key;
						blink = 0;
					}
				}
				
				//Numbers
				//If INT, UFLOAT, FLOAT, or ALL
				if (fieldTypes[sField] > TEXT)
				{
					if (key > 48 && key < 58)
					{
						fields[sField] += (char)key;
						blink = 0;
					}
					
					//Zero
					if (key == 48)
					{
						//if INT, UFLOAT, or FLOAT prevent unnecisary 0's
						if (fieldTypes[sField] < ALL)
						{
							//0's cannot be the first char or the second char if the first is a negative
							if ((fields[sField].length() == 1 && fields[sField].charAt(0) != '-') || fields[sField].length() > 1)
							{
								fields[sField] += (char)key;
								blink = 0;
							}
						}
						else
						{
						fields[sField] += (char)key;
							blink = 0;
						}
					}
					
					//Point
					if (key == 46)
					{
						//If UFLOAT or FLOAT prevent multiple decimal points
						if (((fieldTypes[sField] == UFLOAT || fieldTypes[sField] == FLOAT) && !fields[sField].contains(".")) || fieldTypes[sField] == ALL)
						{
							fields[sField] += (char)key;
							blink = 0;
						}
					}
				}
				
				//Negative
				if (key == 45)
				{
					//If FLOAT only allow negative at the front
					if ((fieldTypes[sField] == FLOAT && fields[sField].length() == 0) ||fieldTypes[sField] == ALL)
					{
						fields[sField] += (char)key;
						blink = 0;
					}
				}
				
				//Backspace
				if (key == 8)
				{
					if (fields[sField].length() > 0)
						fields[sField] = fields[sField].substring(0, fields[sField].length() - 1);
					
					blink = 0;
				}
						
				
				//Enter
				if (key == 10)
				{
					if (sField < numFields - 1)
					{
						sField ++;
						blink = BLINKRATE;
					}
					else
						//If at end of fields check for completion
						checkFields();
				}
				
				//Arrows
				if (key == 1004)
				{
					if (sField > 0)
					{
						sField --;
						blink = BLINKRATE;
					}
				}
				if (key == 1005)
				{
					if (sField < numFields - 1)
					{
						sField ++;
						blink = BLINKRATE;
					}
				}
			}
		}
	}
	
	//Mouse click
	public void mouseDown(int xx, int yy)
	{
		if (active)
		{
			//Tile Bar
			if (xx > x + BARLEFT && xx < x + BARRIGHT && yy > y + BARTOP && yy < y + BARBOTTOM)
			{
				moving = true;
				mouseXOffset = xx - x;
				mouseYOffset = yy - y;
			}
			else
				moving = false;
			
			//Close
			if (xx > x + CLOSEX && xx < x + CLOSEX + CLOSESIZE && yy > y + CLOSEY && yy < y + CLOSEY + CLOSESIZE)
			{
				close();
				return;
			}
			
			//Minimize
			if (xx > x + MINIMIZEX && xx < x + MINIMIZEX + MINIMIZESIZE && yy > y + MINIMIZEY && yy < y + MINIMIZEY + MINIMIZESIZE)
			{
				active = false;
				minimized = true;
				return;
			}
			
			//OK Button
			if (okBut.check(xx, yy))
			{
				checkFields();
				return;
			}
			
			if (xx > x + LEFTOFFSET && xx < x + LEFTOFFSET + FIELDWIDTH)
			{
				for (int i=0; i < numFields; i++)
				{
					if (yy > y + TOPOFFSET + FIELDSPACING*i && yy < y + TOPOFFSET + FIELDSPACING*i + FIELDHEIGHT)
					{
						sField = i;
						blink = BLINKRATE;
						return;
					}
				}
			}
			
			sField = -1;
		}
		else if (minimized)
		{
			//Tile Bar
			if (xx > x + BARLEFT && xx < x + BARRIGHT && yy > y + BARTOP && yy < y + BARBOTTOM)
			{
				moving = true;
				mouseXOffset = xx - x;
				mouseYOffset = yy - y;
			}
			else
				moving = false;
				
			//Close
			if (xx > x + CLOSEX && xx < x + CLOSEX + CLOSESIZE && yy > y + CLOSEY && yy < y + CLOSEY + CLOSESIZE)
			{
				close();
				return;
			}
			
			//Maximize
			if (xx > x + MINIMIZEX && xx < x + MINIMIZEX + MINIMIZESIZE && yy > y + MINIMIZEY && yy < y + MINIMIZEY + MINIMIZESIZE)
			{
				active = true;
				minimized = false;
				return;
			}
		}
	}
	
	//Mouse drag
	public void mouseDrag(int xx, int yy)
	{
		if (active || minimized)
		{
			if (moving)
			{
				x = xx - mouseXOffset;
				y = yy - mouseYOffset;
				
				if (x + BARLEFT < 0)
					x = -BARLEFT;
				if (x + BARRIGHT > width)
					x = width - BARRIGHT;
				if (y + BARTOP < 0)
					y = -BARTOP;
				if (y + BARBOTTOM > height)
					y = height - BARBOTTOM;
				
				okBut.setPos(x + BUTTONX, y + BUTTONY);
			}
		}
	}
	
	//Mouse move
	public void mouseMove(int xx, int yy)
	{
		if (active)
			okBut.run(xx, yy);
	}
	
	//Check all fields
	private void checkFields()
	{
		//for (int i=0; i < numFields; i++)
		//{
		//	if (fields[i].length() == 0)
		//		return;
		//}
		
		actionControler.OK(this);
	}
	
	private void close()
	{
		actionControler.close();
		wC.closeWindow();
	}
	
	//Return if mouse is inside window
	public boolean mouseInside(int xx, int yy)
	{
		if (minimized)
			return (xx > x && xx < x + WINDOWWIDTH && yy > y && yy < y + WINDOWMINIMIZEDHEIGHT);
		
		return (xx > x && xx < x + WINDOWWIDTH && yy > y && yy < y + WINDOWHEIGHT);
	}
	
	//Paint
	public void paint(Graphics g)
	{
		if (minimized)
		{
			//Background
			g.setColor(Color.GRAY);
			g.fillRect(x, y, WINDOWWIDTH, WINDOWMINIMIZEDHEIGHT);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, WINDOWWIDTH, WINDOWMINIMIZEDHEIGHT);
			
			//Close
			g.setColor(Color.WHITE);
			g.fillRect(x + CLOSEX, y + CLOSEY, CLOSESIZE, CLOSESIZE);
			g.setColor(Color.BLACK);
			g.drawRect(x + CLOSEX, y + CLOSEY, CLOSESIZE, CLOSESIZE);
			g.drawLine(x + CLOSEX + 2, y + CLOSEY + 2, x + CLOSEX + CLOSESIZE - 2, y + CLOSEY + CLOSESIZE - 2);
			g.drawLine(x + CLOSEX + CLOSESIZE - 2, y + CLOSEY + 2, x + CLOSEX + 2, y + CLOSEY + CLOSESIZE - 2);
			
			//Maximize
			g.setColor(Color.WHITE);
			g.fillRect(x + MINIMIZEX, y + MINIMIZEY, MINIMIZESIZE, MINIMIZESIZE);
			g.setColor(Color.BLACK);
			g.drawRect(x + MINIMIZEX, y + MINIMIZEY, MINIMIZESIZE, MINIMIZESIZE);
			g.drawRect(x + MINIMIZEX + 2, y + MINIMIZEY + MINIMIZESIZE - 4 - 2, 4, 4);
			g.drawRect(x + MINIMIZEX + MINIMIZESIZE - 4 - 2, y + MINIMIZEY + 2, 4, 4);
			
			//Title Bar
			g.drawRect(x + BARLEFT, y + BARTOP, BARRIGHT - BARLEFT, BARBOTTOM - BARTOP);
			g.drawLine(x + BARLEFT + 6, y + BARTOP + 2, x + BARRIGHT - 6, y + BARTOP + 2);
			g.drawLine(x + BARLEFT + 6, y + BARTOP + 5, x + BARRIGHT - 6, y + BARTOP + 5);
			
			//Text
			g.setColor(Color.WHITE);
			g.drawString(title + " - " + fields[0], x + 2, y + 12);
		}
		else
		{
			//Background
			g.setColor(Color.GRAY);
			g.fillRect(x, y, WINDOWWIDTH, WINDOWHEIGHT);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, WINDOWWIDTH, WINDOWHEIGHT);
			
			//Close
			g.setColor(Color.WHITE);
			g.fillRect(x + CLOSEX, y + CLOSEY, CLOSESIZE, CLOSESIZE);
			g.setColor(Color.BLACK);
			g.drawRect(x + CLOSEX, y + CLOSEY, CLOSESIZE, CLOSESIZE);
			g.drawLine(x + CLOSEX + 2, y + CLOSEY + 2, x + CLOSEX + CLOSESIZE - 2, y + CLOSEY + CLOSESIZE - 2);
			g.drawLine(x + CLOSEX + CLOSESIZE - 2, y + CLOSEY + 2, x + CLOSEX + 2, y + CLOSEY + CLOSESIZE - 2);
			
			//Minimize
			g.setColor(Color.WHITE);
			g.fillRect(x + MINIMIZEX, y + MINIMIZEY, MINIMIZESIZE, MINIMIZESIZE);
			g.setColor(Color.BLACK);
			g.drawRect(x + MINIMIZEX, y + MINIMIZEY, MINIMIZESIZE, MINIMIZESIZE);
			g.drawLine(x + MINIMIZEX + 2, y + MINIMIZEY + MINIMIZESIZE/2, x + MINIMIZEX + MINIMIZESIZE - 2, y + MINIMIZEY + MINIMIZESIZE/2);
			
			//Title Bar
			g.drawRect(x + BARLEFT, y + BARTOP, BARRIGHT - BARLEFT, BARBOTTOM - BARTOP);
			g.drawLine(x + BARLEFT + 6, y + BARTOP + 2, x + BARRIGHT - 6, y + BARTOP + 2);
			g.drawLine(x + BARLEFT + 6, y + BARTOP + 5, x + BARRIGHT - 6, y + BARTOP + 5);
			g.setColor(Color.WHITE);
			g.drawString(title + " - " + fields[0], x + 2, y + 12);
			
			for (int i=0; i < numFields; i++)
			{
				//Field
				g.setColor(Color.WHITE);
				g.fillRect(x + LEFTOFFSET, y + TOPOFFSET + FIELDSPACING*i, FIELDWIDTH, FIELDHEIGHT);
				g.setColor(Color.BLACK);
				g.drawRect(x + LEFTOFFSET, y + TOPOFFSET + FIELDSPACING*i, FIELDWIDTH, FIELDHEIGHT);
				g.drawString(fieldLabels[i], x + LEFTOFFSET, y + TOPOFFSET + FIELDSPACING*i - 2);
				
				//Text
				if (sField == i && blink >= BLINKRATE)
					g.drawString(fields[i] + "|", x + LEFTOFFSET + 2, y + TOPOFFSET + FIELDSPACING*i + 15);
				else
					g.drawString(fields[i], x + LEFTOFFSET + 2, y + TOPOFFSET + FIELDSPACING*i + 15);
			}
			
			okBut.paint(g);
		}
			
	}
	
	//Return one field value
	public String getField(int i)
	{
		return fields[i];
	}
	//Return all field values
	public String[] getFields()
	{
		return fields;
	}
	
	
	//Insert Random Values
	private void randomize()
	{
		for (int i=0; i < numFields; i++)
		{
			int l = 2 + (int)(Math.random()*5);
			
			for (int j=0; j<l; j++)
			{
				if (fieldTypes[i] == TEXT)
					fields[i] += (char)((int)(97 + Math.random()*25));
				else
					fields[i] += (char)((int)(48 + Math.random()*10));
			}
		}
	}
}