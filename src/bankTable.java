import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

public class bankTable
{
	//Constants
	private final int FIRSTNAME =	0;
	private final int LASTNAME =	1;
	private final int BALANCE =		2;
	private final int RATE =		3;
	private final int STATE =		4;
	private final int CITY =		5;
	private final int ZIPCODE =		6;
	private final int PHONE =		7;
	private final int ADDRESS =		8;
	
	//Vars
	private int s = -1;
	private int lineSelected = -1;
	private int[] line = new int[7];
	private final int numLines = 7;
	private int sort = 0;
	
	//Size
	private final int x, y, w, h;
	
	//Parent
	private bank p;
	
	//Counters
	private int numPeople = 0;
	
	//Arrays
	private String people[][] = new String[1000][9];
	private int id[] = new int[1000];
	private final String label[] = {"First Name", "Last Name", "Balance", "Rate", "State", "City", "Zip Code", "Phone"};
	
	//Components
	private Rectangle2D rect;
	
	
	//Init
	public bankTable(int x, int y, int w, int h, bank p)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.p = p;
		
		for (int i=0; i < 1000; i++)
			id[i] = i;
		
		line[0] = (int)(1.0/8 * w);
		line[1] = (int)(2.0/8 * w);
		line[2] = (int)(3.0/8 * w);
		line[3] = (int)(4.0/8 * w);
		line[4] = (int)(5.0/8 * w);
		line[5] = (int)(6.0/8 * w);
		line[6] = (int)(7.0/8 * w);
	}
	
	//Check Mouse click
	public void check(int mx, int my)
	{
		if (mx > x && mx < x + w && my > y - 16 && my < y + h)
		{
			for (int i=0; i < numLines; i++)
			{
				if (mx > x + line[i] - 4 && mx < x + line[i] + 4)
					lineSelected = i;
			}
			
			if (lineSelected == -1)
			{
				if (my > y)
				{
					s = ((my - y) - p.scrollBar.getOffset()) / 20;
					
					if (s > numPeople - 1)
						s = -1;
					else
					{
						if (s*20 < -p.scrollBar.getOffset())
							p.scrollBar.setOffset(-s*20);
						if (s*20 + 20 > -p.scrollBar.getOffset() + h)
							p.scrollBar.setOffset(h - (s*20 + 20));
					}
				}
				else
				{
					if (mx < line[0])
						sort = 0;
					else
					{
						//for (int i=0; i < numLines; i++)
					}
				}
			}
		}
	}
	
	//UnClick
	public void unclick()
	{
		lineSelected = -1;
	}
	
	//Run
	public void run(int mx)
	{
		if (lineSelected > -1)
		{
			line[lineSelected] = mx - x;
			
			if (lineSelected > 0)
			{
				if (line[lineSelected] < line[lineSelected - 1] + 2)
					line[lineSelected] = line[lineSelected - 1] + 2;
			}
			else
			{
				if (line[0] < 2)
					line[0] = 2;
			}
			
			if (lineSelected < numLines - 1)
			{
				if (line[lineSelected] > line[lineSelected + 1] - 2)
					line[lineSelected] = line[lineSelected + 1] - 2;
			}
			else
			{
				if (line[numLines - 1] > w - 2)
					line[numLines - 1] = w - 2;
			}
		}
	}
	//Add person by array
	public void addPerson(String[] v)
	{
		addPerson(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8]);
	}
	//Add person by values
	public void addPerson(String firstName, String lastName, String balance, String rate, String state, String city,
						String zipCode, String phone, String address)
	{
		people[numPeople][FIRSTNAME] = firstName;
		people[numPeople][LASTNAME] = lastName;
		people[numPeople][ADDRESS] = address;
		people[numPeople][CITY] = city;
		people[numPeople][STATE] = state;
		people[numPeople][ZIPCODE] = zipCode;
		people[numPeople][PHONE] = phone;
		people[numPeople][BALANCE] = balance;
		people[numPeople][RATE] = rate;
		
		numPeople ++;
		
		sort(0);
		
		p.scrollBar.setContentHeight(numPeople * 20);
	}
	
	//Add array of people
	public void addArray(String array[][], int n)
	{
		for (int i=0; i < n; i++)
			people[numPeople + i] = array[i];
		
		numPeople += n;
		
		sort(0);
		
		p.scrollBar.setContentHeight(numPeople * 20);
	}
	
	//Delete person
	public void delete(int line)
	{
		if (line < 0)
			return;
		
		for (int i = id[line]; i < numPeople - 1; i++)
			people[i] = people[i + 1];
		
		int num = id[line];
		
		for (int i = line; i < numPeople - 1; i++)
			id[i] = id[i+1];
		
		for (int i=0; i < numPeople - 1; i++)
		{
			if (id[i] > num)
				id[i] --;
		}
		
		numPeople --;
		
		people[numPeople] = new String[] {null, null, null, null, null, null, null, null, null};
		id[numPeople] = numPeople;
		
		if (s > numPeople - 1)
			s --;
		
		p.scrollBar.setContentHeight(numPeople * 20);
	}
	
	//Clear table
	public void clear()
	{
		people = new String[1000][8];
		id = new int[1000];
		
		for (int i=0; i < 1000; i++)
			id[i] = i;
		
		numPeople = 0;
	}
	
	//Return line selected
	public int getLineSelected()
	{
		return s;
	}
	
	//Return one of a person's atributes
	public String getPerson(int p, int a)
	{
		return people[p][a];
	}
	//Return all of a person's atributes
	public String[] getPerson(int p)
	{
		return people[p];
	}
	
	//Set one of a person's attribues
	public void setPerson(int p, int a, String v)
	{
		people[p][a] = v;
	}
	//Set all of a person's attribues
	public void setPerson(int p, String[] v)
	{
		people[p] = v;
	}
	
	//Return a person's index in the array from their line position
	public int getPersonIndexFromLine(int l)
	{
		return id[l];
	}
	
	//Shorten strings
	/*public void shortenStrings(int a)
	{
		display = people;
		
		for (int i=0; i < numPeople; i++)
		{
			for (int j = people[i][0].length(); j > 0; j--)
			{
				rect = p.fm.getStringBounds(people[i][0].substring(0, j), p.dbg);
				
				if (rect.getWidth() < a)
				{
					display[i][0] = people[i][0].substring(0, j);
					break;
				}
			}
		}
	}*/
	
	//Sort
	public void sort(int f)
	{
		for (int i=0; i < numPeople; i++)
		{
			int x = 1;
			char c, c1, c2;
	
			c1 = people[id[i]][f].charAt(0); if (c1 > 96 && c1 < 123) c1-=32;
	
			for (int a=i; a > 0; a--)
			{
				c2 = 0;
				
				for (int v=0; v < x; v++)
				{
					c = people[id[a - 1]][f].charAt(v); if (c > 96 && c < 123) c-=32;
					c2 += c;
				}
				
				if (c1 < c2)
				{
					int temp = id[a];
					id[a] = id[a - 1];
					id[a - 1] = temp;

	
					c1 = people[id[a - 1]][f].charAt(0); if (c1 > 96 && c1 < 123) c1-=32;
					x = 1;
				}
				else if (c1 == c2)
				{
					c = people[id[a]][f].charAt(x); if (c > 96 && c < 123) c-=32;
					c1 += c;
					x++;
	
					if (x > people[id[a]][f].length())
					{
						int temp = id[a];
						id[a] = id[a - 1];
						id[a - 1] = temp;
						
						c1 = people[id[a - 1]][f].charAt(0); if (c1 > 96 && c1 < 123) c1-=32;
						x = 1;
					}
					else if (x > people[id[a - 1]][f].length()-1)
						break;
					else
						a++;
				}
				else
					break;
			}
		}
	}
	
	//Draw
	public void paint(Graphics g)
	{
		int o = p.scrollBar.getOffset();
		
		g.setColor(Color.BLUE);
		g.fillRect(x, y + 20*s + o, line[0], 20);
		
		g.setColor(Color.BLACK);
		
		for (int i=0; i < numPeople; i++)
		{
			if (i == s)
				g.setColor(Color.WHITE);
			
			g.drawString(people[id[i]][0], x + 5, y + 14 + 20*i + o);
			
			for (int j=0; j < numLines - 1; j++)
			{
				if (i == s)
				{
					g.setColor(Color.BLUE);
					g.fillRect(x + line[j] - 5, y + 20*i + o, line[j + 1] - line[j] + 5, 20);
					
					g.setColor(Color.WHITE);
				}
				else
				{
					g.setColor(Color.WHITE);
					g.fillRect(x + line[j] - 5, y + 20*i + o, line[j + 1] - line[j] + 5, 20);
					
					g.setColor(Color.BLACK);
				}
				
				g.drawString(people[id[i]][j + 1], x + line[j] + 5, y + 14 + 20*i + o);
			}
			

			if (i == s)
			{
				g.setColor(Color.BLUE);
				g.fillRect(x + line[numLines - 1] - 5, y + 20*i + o, w, 20);
				
				g.setColor(Color.WHITE);
			}
			else
			{
				g.setColor(Color.WHITE);
				g.fillRect(x + line[numLines - 1] - 5, y + 20*i + o, w, 20);
				
				g.setColor(Color.BLACK);
			}
			
			g.drawString(people[id[i]][numLines], x + line[numLines - 1] + 5, y + 14 + 20*i + o);
			
			g.setColor(Color.BLACK);
			g.drawLine(x, y + 20*i + o, x + w, y + 20*i + o);
			g.drawLine(x, y + 20 + 20*i + o, x + w, y + 20 + 20*i + o);
		}
		
		g.setColor(Color.WHITE);
		g.fillRect(x, 0, w + 1, y);
		g.fillRect(x, y + h + 1, w + 1, p.height - (y + h));
		g.fillRect(x + w, 0, p.width - (x + w), p.height);
		
		if (sort == 0)
		{
			g.setColor(Color.BLUE);
			g.fillRect(x, y - 16, line[0], 4);
			g.setColor(Color.BLACK);
		}
		else
			g.setColor(Color.BLACK);
			
		g.drawString(label[0], x + 5, y - 2);
		
		for (int j=0; j < numLines - 1; j++)
		{
			if (sort == j + 1)
			{
				g.setColor(Color.BLUE);
				g.fillRect(x + line[j] - 5, y - 16, line[j + 1] - line[j] + 5, 16);
				
				g.setColor(Color.WHITE);
			}
			else
			{
				g.setColor(Color.WHITE);
				g.fillRect(x + line[j] - 5, y - 16, line[j + 1] - line[j] + 5, 16);
				
				g.setColor(Color.BLACK);
			}
			
			g.drawString(label[j + 1], x + line[j] + 5, y - 2);
		}
		

		if (sort == numLines - 1)
		{
			g.setColor(Color.BLUE);
			g.fillRect(x + line[numLines - 1] - 5, y - 16, w, 16);
			
			g.setColor(Color.WHITE);
		}
		else
		{
			g.setColor(Color.WHITE);
			g.fillRect(x + line[numLines - 1] - 5, y - 16, w, 20);
			
			g.setColor(Color.BLACK);
		}
		
		g.drawString(label[numLines], x + line[numLines - 1] + 5, y - 2);
		
		g.setColor(Color.BLACK);
		g.drawRect(x, y, w, h);
		g.drawRect(x, y-16, w, 16);
		
		for (int i=0; i < numLines; i++)
			g.drawLine(x + line[i], y - 16, x + line[i], y + h);
	}
}