/*
 * Created on Sep 22, 2003
 */
package com.u2d.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

/**
 * @author Eitan Suez
 */
public class FancyTextArea extends javax.swing.JTextArea
{
	/*
	 * goals:
	 *   text should auto-wrap
	 *   custom border
	 *   custom background color
	 *   font size = fn(componentsize)
	 *   component vertically resizable, should probably fire some kind of event to notify change in height (propertychangeevent)
	 */
	 
	 private Color _bgCol, _editBgCol;
//	 private Border _border;
    private Border _borderSm;

	private static Font _twelvept, _tenpt;
	static {
	  _twelvept = new Font("SansSerif", Font.PLAIN, 12);
	  _tenpt = _twelvept.deriveFont(10.0f);
	}
	 
	 public FancyTextArea()
	 {
	 	this(Color.red);
	 }
	 
	 public FancyTextArea(Color bgCol)
	 {
      _editBgCol = Color.white;
      setupColor(bgCol);
	 	
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);

		setEditable(false);
		
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					fireActionEvent(EDIT_ACTION, "edit item");
				}
				else if (evt.getClickCount() == 1 && !isEditable())
				{
					setEditable(true);
				}
			}
		});
		
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent evt)
			{
				if (evt.getKeyCode() == KeyEvent.VK_ENTER)
				{
					fireActionEvent(TEXTCHANGED_ACTION, "text changed");
					setEditable(!isEditable());
					evt.consume();
				}
				else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE && !isEditable())
				{
					fireActionEvent(DELETE_ACTION, "delete event");
					evt.consume();
				}
			}
		});
		
		addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				setEditable(false);
			}
		});
		
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
	 
	 public void setEditable(boolean editable)
	 {
		super.setEditable(editable);
		Caret caret = getCaret();
	 	if (editable)
 		{
			setBackground(_editBgCol);
			if (caret != null)
			{
				caret.setVisible(true);
			}
 		}
 		else
		{
			if (caret != null)
			{
				caret.setVisible(false);
			}
			setBackground(_bgCol);
		}
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
	 		setFont(_tenpt);
			//setBorder(_borderSm);
	 	}
	 	else if (area > threshold2 && getFont().getSize() != 12)
	 	{
	 		setFont(_twelvept);
			//setBorder(_border);
	 	}
	 }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Testing");
		f.setBounds(300,200,300,300);
		JPanel p = (JPanel) f.getContentPane();
		p.setBackground(Color.white);
		
		FancyTextArea fta = new FancyTextArea();
		fta.setText("This is a test");
		p.add(fta, BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}


	public static final int TEXTCHANGED_ACTION = 1;
	public static final int EDIT_ACTION = 2;
	public static final int DELETE_ACTION = 3;
	/************************************************************************
	 * List of observers.
	 */

	private ActionListener subscribers = null;

	/** Add a listener that's notified when the user scrolls the
	 *  selector or picks a date.
	 *  @see Date_selector
	 */
	 public synchronized void addActionListener(ActionListener l)
	 {
		subscribers = AWTEventMulticaster.add(subscribers, l);
	 }

	/** Remove a listener.
	 *  @see Date_selector
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
