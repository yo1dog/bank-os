import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Event;
import java.awt.FontMetrics;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Locale;

public class bank extends Applet implements Runnable
{
	public interface windowActionControler { void OK(inputWindow win); void close();}
	
	//Constants
	public final int width = 600, height = 400;
	
	private final int FIRSTNAME =	0;
	private final int LASTNAME =	1;
	private final int BALANCE =		2;
	private final int RATE =		3;
	private final int STATE =		4;
	private final int CITY =		5;
	private final int ZIPCODE =		6;
	private final int PHONE =		7;
	private final int ADDRESS =		8;
	
	private final int TEXT		= 0;
	private final int INT		= 1;
	private final int UFLOAT	= 2;
	private final int FLOAT		= 3;
	private final int ALL		= 4;
	
	private final int WINX			= 100;
	private final int WINY			= 5;
	private final int WINXOFFSETINC	= 10;
	private final int WINYOFFSETINC	= 5;
	private final int MAXWINXOFFSET	= 400;
	private final int MAXWINYOFFSET	= 30;
	
	//Counters
	private boolean init = true;
	
	//Vars
	private final Color butColor1 = new Color(222, 222, 222);
	private final Color butColor2 = new Color(191, 191, 191);
	private int activeWindow = -1;
	private int winXOffset = 0, winYOffset = 0;
	
	//Components
	private final Color backgroundColor = Color.WHITE;
	private final Thread th = new Thread(this);
	private Graphics dbg;
	private Image dbImage;
	private FontMetrics fm;
	private final NumberFormat formatDollars = NumberFormat.getCurrencyInstance(Locale.US);
	private final NumberFormat formatPercent = NumberFormat.getPercentInstance();
	
	private final bankTable table = new bankTable(120, 46, 460, 300, this);
	public final customScrollBar scrollBar = new customScrollBar(580, 46, 300);
	
	private final customButton addPersonBut = new customButton(4, 4,  100, 20, butColor1, butColor2, "Add Person");
	private final customButton deleteBut = new customButton(4, 28, 100, 20, butColor1, butColor2, "Delete Entry");
	private final customButton editBut = new customButton(4, 52, 100, 20, butColor1, butColor2, "Edit Entry");
	private final customButton depositBut = new customButton(4, 76, 100, 20, butColor1, butColor2, "Deposit");
	private final customButton withdrawBut = new customButton(4, 100, 100, 20, butColor1, butColor2, "Withdraw");
	
	private final windowControler wC = new windowControler(this);
	
	//public final customCheckbox teacherCheck = new customCheckbox(120, 10, true, butColor1, butColor2, "Teachers");
	//public final customCheckbox studentCheck = new customCheckbox(220, 10, true, butColor1, butColor2, "Student");
	

//***************************************************************************
//Start of System Methods
//
//***************************************************************************
	
	//--------------------------
	//Initiate
	//--------------------------
	public void init()
	{
		setBackground(backgroundColor);
		setSize(width, height);
	}
	
	//--------------------------
	//Thread
	//--------------------------
	public void start()
	{
		th.start();
	}
	public void stop()
	{
		th.stop();
	}
	
	//--------------------------
	//Run
	//--------------------------
	public void run()
	{
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		while (true)
		{
			wC.run();
			
			repaint();
			
			try
			{
				Thread.sleep(16);
			}
			catch (InterruptedException ex) {}
			
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
	}
	
	//--------------------------
	//Mouse
	//--------------------------
	//Move
	public boolean mouseMove(Event e, int x, int y)
	{
		wC.mouseMove(x, y);
		
		addPersonBut.run(x, y);
		deleteBut.run(x, y);
		editBut.run(x, y);
		depositBut.run(x, y);
		withdrawBut.run(x, y);
		
		//teacherCheck.run(x, y);
		//studentCheck.run(x, y);
		
		return true;	
	}
	
	//Press
	public boolean mouseDown(Event e, int x, int y)
	{
		wC.mouseDown(x, y);
		
		if (wC.getActiveWindow() == -1)
		{
		
			//Add Person Button
			if (addPersonBut.check(x, y))
			{
				wC.addWindow("Add Person", 9, new String[] {"First Name", "Last Name", "Balance", "Rate", "State", "City", "Zip Code", "Phone Number", "Address"},
									  		  new int[] {TEXT, TEXT, UFLOAT, FLOAT, TEXT, TEXT, INT, INT, ALL}, WINX + winXOffset, WINY + winYOffset,
									  		  new windowActionControler(){
											  	public void OK(inputWindow win)
											  	{
												  	String[] fields = win.getFields();
												  	fields[BALANCE] = formatDollars.format(Float.valueOf(fields[BALANCE]));
												  	fields[RATE] = formatPercent.format(Float.valueOf(fields[RATE]));
												  	fields[PHONE] = formatPhoneNumber(fields[PHONE]);
													table.addPerson(fields);
													wC.closeWindow();
											  	}
											  	public void close(){}});
				incrementWindows();
			}
			
			//Delete Button
			if (deleteBut.check(x, y))
				table.delete(table.getLineSelected());
			
			//Edit Button
			if (editBut.check(x, y))
			{
				if (table.getLineSelected() > -1)
				{
					String[] fields = table.getPerson(table.getPersonIndexFromLine(table.getLineSelected()));
					fields[BALANCE] = dollarToFloat(fields[BALANCE]) + "";
					fields[RATE] = percentToFloat(fields[RATE]) + "";
					fields[PHONE] = phoneNumberToIntString(fields[PHONE]);
					wC.addWindow("Edit Person", 9, new String[] {"First Name", "Last Name", "Balance", "Rate", "State", "City", "Zip Code", "Phone Number", "Address"},
												   new int[] {TEXT, TEXT, UFLOAT, FLOAT, TEXT, TEXT, INT, INT, ALL},
												   fields, WINX + winXOffset, WINY + winYOffset,
												   new windowActionControler(){
													public void OK(inputWindow win)
												  	{
													  	if (table.getLineSelected() > -1)
													  	{
															String[] fields = win.getFields();
														  	fields[BALANCE] = formatDollars.format(Float.valueOf(fields[BALANCE]));
														  	fields[RATE] = formatPercent.format(Float.valueOf(fields[RATE]));
														  	fields[PHONE] = formatPhoneNumber(fields[PHONE]);
															table.setPerson(table.getPersonIndexFromLine(table.getLineSelected()), fields);
															wC.closeWindow();
														}
												  	}
												  	public void close(){}});
					incrementWindows();
				}
			}
			
			//Withdraw Button
			if (withdrawBut.check(x, y))
			{
				wC.addWindow("Withdraw", 1, new String[] {"Ammount"}, new int[] {UFLOAT}, WINX + winXOffset, WINY + winYOffset,
							 new windowActionControler(){
					  			public void OK(inputWindow win)
							  	{
								  	if (table.getLineSelected() > -1)
									{
										float bal = dollarToFloat(table.getPerson(table.getPersonIndexFromLine(table.getLineSelected()), BALANCE));
										float amt = dollarToFloat(win.getField(0));
										table.setPerson(table.getPersonIndexFromLine(table.getLineSelected()), BALANCE, formatDollars.format(bal-amt));
									}
							  	}
							  	public void close(){}});
				incrementWindows();
			}
			
			//Deposit Button
			if (depositBut.check(x, y))
			{
				wC.addWindow("Deposit", 1, new String[] {"Ammount"}, new int[] {UFLOAT}, WINX + winXOffset, WINY + winYOffset,
							 new windowActionControler(){
								public void OK(inputWindow win)
							  	{
								  	if (table.getLineSelected() > -1)
									{
										float bal = dollarToFloat(table.getPerson(table.getPersonIndexFromLine(table.getLineSelected()), BALANCE));
										float amt = dollarToFloat(win.getField(0));
										table.setPerson(table.getPersonIndexFromLine(table.getLineSelected()), BALANCE, formatDollars.format(bal+amt));
									}
							  	}
							  	public void close(){}});
				incrementWindows();
			}
			
			
			//Table
			table.check(x, y);
			
			//Scoll Bar
			scrollBar.check(x, y);
		}
		
		return true;
	}
	//Drag
	public boolean mouseDrag(Event e, int x, int y)
	{
		if (wC.getActiveWindow() > -1)
			wC.mouseDrag(x, y);	
		else
		{
			scrollBar.run(x, y);
			table.run(x);
		}
		
		return true;
	}
	//Release
	public boolean mouseUp(Event e, int x, int y)
	{
		
		if (wC.getActiveWindow() > -1)
		{
			scrollBar.unclick();
			table.unclick();
		}
		
		return true;
	}
	
	//Increment windows
	private void incrementWindows()
	{
		winXOffset += WINXOFFSETINC;
		winYOffset += WINYOFFSETINC;
		
		if (winXOffset > MAXWINXOFFSET)
			winXOffset = 0;
		if (winYOffset > MAXWINYOFFSET)
			winYOffset = 0;
	}
	
	//--------------------------
	//Keyboard
	//--------------------------
	//Press
	public boolean keyDown(Event e, int key)
	{
		//Pass to Input Windows
		wC.keyDown(key);
		
		return true;
	}
	
	//--------------------------
	//Draw
	//--------------------------
	public void paint (Graphics g)
	{
		if (init)
		{
			fm = g.getFontMetrics();
			init = false;
		}
		
		table.paint(g);
		scrollBar.paint(g);
		
		addPersonBut.paint(g);
		deleteBut.paint(g);
		editBut.paint(g);
		withdrawBut.paint(g);
		depositBut.paint(g);
		
		wC.paint(g);
		
		//teacherCheck.paint(g);
		//studentCheck.paint(g);
	}
	
	//--------------------------
	//Update
	//--------------------------
	public void update(Graphics g)
	{
		if (dbImage==null)
		{
			dbImage=createImage(this.getSize().width,this.getSize().height);
			dbg=dbImage.getGraphics();
		}
		
		dbg.setColor(backgroundColor);
		dbg.fillRect(0,0,width,height);
		
		dbg.setColor(getForeground());
		paint(dbg);
		
		g.drawImage(dbImage,0,0,this);
	}
	
	//Format Phone Numbers
	private String formatPhoneNumber(String s)
	{
		if (s.length() < 7)
			return s;
		
		s = s.substring(0, s.length() - 4) + "-" + s.substring(s.length() - 4, s.length());
		
		if (s.length() > 11)
		{
			s = s.substring(0, s.length() - 8) + ")" + s.substring(s.length() - 8, s.length());
			s = s.substring(0, s.length() - 12) + "(" + s.substring(s.length() - 12, s.length());
		}
		
		return s;
	}
	
	//Convert dollar string to foat
	private float dollarToFloat(String s)
	{
		s = s.replace("$", "");
		s = s.replaceAll(",", "");
		return Float.valueOf(s);
	}
	
	//Convert percent string to foat
	private float percentToFloat(String s)
	{
		s = s.replace("%", "");
		s = s.replaceAll(",", "");
		return Float.valueOf(s)/100;
	}
	
	//Convert phone number string to int
	private String phoneNumberToIntString(String s)
	{
		s = s.replace("(", "");
		s = s.replace(")", "");
		s = s.replace("-", "");
		
		return s;
	}
}