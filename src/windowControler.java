import java.awt.Graphics;

public class windowControler
{
	private int numWindows = 0;
	private int activeWindow = -1;
	private inputWindow[] windows = new inputWindow[100];
	private int order[] = new int[100];
	private final bank driver;
	
	public windowControler(bank driver)
	{
		this.driver = driver;
		for (int i=0; i < 100; i++)
			order[i] = i;
	}
	
	//Add window
	public int addWindow(String title, int numFields, String[] fieldLabels, int[] fieldTypes, int x, int y, bank.windowActionControler actionControler)
	{
		if (numWindows == 100)
			return -1;
			
		windows[numWindows] = new inputWindow(title, numFields, fieldLabels, fieldTypes, x, y, actionControler, this);
		activeWindow = numWindows;
		order[numWindows] = numWindows;
		
		numWindows ++;
		
		moveWindowToFront(numWindows - 1);
		
		return numWindows-1;
	}
	public int addWindow(String title, int numFields, String[] fieldLabels, int[] fieldTypes, String[] fields, int x, int y, bank.windowActionControler actionControler)
	{
		if (numWindows == 100)
			return - 1;
			
		windows[numWindows] = new inputWindow(title, numFields, fieldLabels, fieldTypes, fields, x, y, actionControler, this);
		activeWindow = numWindows;
		order[numWindows] = numWindows;
		
		numWindows ++;
		
		moveWindowToFront(numWindows - 1);
		return numWindows-1;
	}
	
	//Delete Window
	public void deleteWindow(int w)
	{
		if (w < 0)
			return;
		
		for (int i = order[w]; i < numWindows - 1; i++)
			windows[i] = windows[i + 1];
		
		int val = order[w];
		
		for (int i = w; i < numWindows - 1; i++)
			order[i] = order[i+1];
		
		for (int i=0; i < numWindows - 1; i++)
		{
			if (order[i] > val)
				order[i] --;
		}
		
		numWindows --;
		
		windows[numWindows] = null;
		order[numWindows] = numWindows;
	}
	
	//Close window
	public void closeWindow()
	{
		deleteWindow(0);
		
		if (numWindows > 0)
			activeWindow = order[0];
		else
			activeWindow = -1;
		
		if (activeWindow > -1)
			windows[activeWindow].moving = false;
	}
	
	//Return active window
	public int getActiveWindow()
	{
		return activeWindow;
	}
	
	//Mouse move
	public void mouseMove(int x, int y)
	{
		if (activeWindow > -1)
			windows[activeWindow].mouseMove(x, y);
	}
	
	//Mouse down
	public void mouseDown(int x, int y)
	{
		for (int i=0; i < numWindows; i++)
		{
			if (windows[order[i]].mouseInside(x, y))
			{
				activeWindow = order[i];
				moveWindowToFront(i);
				
				windows[order[0]].mouseDown(x, y);
				return;
			}
		}
		
		activeWindow = -1;
	}
	
	//Mouse drag
	public void mouseDrag(int x, int y)
	{
		if (activeWindow > -1)
			windows[activeWindow].mouseDrag(x, y);
	}
	
	//Key down
	public void keyDown(int key)
	{
		if (activeWindow > -1)
			windows[activeWindow].keyDown(key);
	}
	
	//Run
	public void run()
	{
		if (activeWindow > -1)
			windows[activeWindow].run();
	}
	
	//Paint
	public void paint(Graphics g)
	{
		for (int i = numWindows - 1; i > -1; i--)
			windows[order[i]].paint(g);
	}
	
	//Move window to front
	private void moveWindowToFront(int w)
	{
		int val = order[w];
		
		for (int i = w; i > 0; i--)
			order[i] = order[i - 1];
		
		order[0] = val;
	}
}