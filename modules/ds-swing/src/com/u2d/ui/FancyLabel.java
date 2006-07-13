/*
 * Created on Sep 22, 2003
 */
package com.u2d.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A Version of FancyTextArea that is not editable
 * 
 * @author Eitan Suez
 */
public class FancyLabel extends javax.swing.JTextArea
{
	/*
	 * goals:
	 *   text should auto-wrap
	 *   custom border
	 *   custom background color
	 *   font size = fn(componentsize)
	 *   component vertically resizable, should probably fire some kind of event 
    *      to notify change in height (propertychangeevent)
	 */
	 
	 private Color _bgCol;
//    private Border _border;
    private Border _borderSm;

	public static Font TWELVE_PT, TEN_PT;
	static {
      TWELVE_PT = new Font("SansSerif", Font.PLAIN, 12);
      TEN_PT = TWELVE_PT.deriveFont(10.0f);
	}
	 
	 public FancyLabel()
	 {
	 	this(Color.red);
	 }
	 
	 public FancyLabel(Color bgCol)
	 {
      setupColor(bgCol);
	 	
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
      
      setHighlighter(null);
		setEditable(false);
		
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					fireActionEvent(EDIT_ACTION, "edit item"); 
				}
			}
		});
		
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent evt)
			{
				if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				{
					fireActionEvent(DELETE_ACTION, "delete event");
					evt.consume();
				}
			}
		});
		
      revalidate(); repaint();
	 }
    
   public void setupColor(Color bgCol)
   {
      _bgCol = introduceAlpha(bgCol);
      setBackground(_bgCol);
      
//      Border lineBorder = BorderFactory.createLineBorder(bgCol, 3);
//      Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
//      _border = new CompoundBorder(lineBorder, emptyBorder);

      Border emptyBorderSm = BorderFactory.createEmptyBorder(1, 1, 1, 1);
      Border lineBorderSm = BorderFactory.createLineBorder(bgCol, 1);
      _borderSm = new CompoundBorder(lineBorderSm, emptyBorderSm);
      
      setBorder(_borderSm);
   }
   
   private Color introduceAlpha(Color col)
   {
      return new Color(col.getRed(), col.getGreen(), col.getBlue(), 128);
   }
	 
	 public void setBounds(int x, int y, int width, int height)
	 {
	 	super.setBounds(x, y, width, height);
		if (getParent() != null && getParent() instanceof JViewport)
		{
		 	JViewport viewport = (JViewport) getParent();
		 	adjustCellSize(viewport.getWidth() * viewport.getHeight());
		}
		else
		{
			adjustCellSize(width*height);
		}
	 }
	 
	 private final static int threshold = 5000;
	 private final static int threshold2 = 30000;
	 
	 private void adjustCellSize(int area)
	 {
	 	//System.out.println("area: "+area);
	 	if (area < threshold && getFont().getSize() != 10)
	 	{
	 		setFont(TEN_PT);
			//setBorder(_borderSm);
	 	}
	 	else if (area > threshold2 && getFont().getSize() != 12)
	 	{
	 		setFont(TWELVE_PT);
			//setBorder(_border);
	 	}
	 }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Testing");
		f.setBounds(300,200,300,300);
		JPanel p = (JPanel) f.getContentPane();
		p.setBackground(Color.white);
		
		FancyLabel fta = new FancyLabel();
		fta.setText("This is a test");
		p.add(fta, BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}


	public static final int EDIT_ACTION = 2;
	public static final int DELETE_ACTION = 3;
	/************************************************************************
	 * List of observers.
	 */

	private ActionListener subscribers = null;

	/** Add a listener that's notified when the user scrolls the
	 *  selector or picks a date.
	 *  @see com.holub.ui.Date_selector
	 */
	 public synchronized void addActionListener(ActionListener l)
	 {
		subscribers = AWTEventMulticaster.add(subscribers, l);
	 }

	/** Remove a listener.
	 *  @see com.holub.ui.Date_selector
	 */
	 public synchronized void removeActionListener(ActionListener l)
	 {
		subscribers = AWTEventMulticaster.remove(subscribers, l);
	 }

	/** Notify the listeners of a scroll or select
	 */
	private void fireActionEvent( int id, String command)
	{
		if (subscribers != null)
			 subscribers.actionPerformed(new ActionEvent(this, id, command) );
	}
	/*****************************************************************/

}
