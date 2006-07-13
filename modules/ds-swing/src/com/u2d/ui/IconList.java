/*
 * Created on Oct 13, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Eitan Suez
 */
public class IconList extends IconPanel 
                      implements ListDataListener
{
	private ListModel _model;
	private FlexibleListCellRenderer _cellRenderer;
	private int _selectedIndex = -1;
	
	public IconList()
	{
		super();
		setOpaque(false);
      
		DefaultListSelectionModel lsModel = new DefaultListSelectionModel();
		lsModel.addListSelectionListener(new ListSelectionListener()
            {
               public void valueChanged(ListSelectionEvent evt)
               {
                  _selectedIndex = evt.getFirstIndex();
                  updateLayout();
               }
            });
	}
   
	public IconList(ListModel model)
	{
		this();
		setModel(model);
	}
	
	public void setModel(ListModel model)
	{
		_model = model;
		updateLayout();
		_model.addListDataListener(this);
	}

   // listdatalistener implementation:
   public void contentsChanged(ListDataEvent evt) { updateLayout(); }
   public void intervalAdded(ListDataEvent evt) { updateLayout(); }
   public void intervalRemoved(ListDataEvent evt) { updateLayout(); }
   
	public void setCellRenderer(FlexibleListCellRenderer r)
	{
		_cellRenderer = r;
		updateLayout();
	}
	
	private void updateLayout()
	{
		removeAll();
		for (int i=0; i<_model.getSize(); i++)
		{
			Object value = _model.getElementAt(i);
			Component c = null;
			if (_cellRenderer == null)
				c = new JLabel(value.toString());
			else
				c = _cellRenderer.getListCellRendererComponent(this, value, i, isSelected(i), hasFocus(i));
			add(c);
		}
		revalidate();
		repaint();
	}
	
	private boolean isSelected(int index)
	{
		return (_selectedIndex == index);
	}
	public boolean hasFocus(int index)
	{
		return false; // ??
	}
	
	
}
