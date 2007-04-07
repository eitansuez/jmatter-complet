package com.u2d.view.swing.list;

import com.u2d.ui.IconButton;
import com.u2d.view.swing.calendar.NavPanel;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.view.swing.SwingAction;
import com.u2d.list.Paginable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 18, 2006
 * Time: 6:33:36 PM
 */
public class SimplePagePanel extends JPanel
{
   private Paginable _paginable;
   private JButton _prevBtn = new IconButton(NavPanel.PREV_ICON, NavPanel.PREV_ROLLOVER);
   private JButton _nextBtn = new IconButton(NavPanel.NEXT_ICON, NavPanel.NEXT_ROLLOVER);
   private JLabel _pageNumCaption = new JLabel();
   {
      _pageNumCaption.setHorizontalAlignment(JLabel.CENTER);
   }

   public SimplePagePanel(Paginable leo)
   {
      _paginable = leo;
      
      setLayout(new BorderLayout());
      add(_prevBtn, BorderLayout.WEST);
      add(_pageNumCaption, BorderLayout.CENTER);
      add(_nextBtn, BorderLayout.EAST);

      contentsChanged();
      
      _prevBtn.addActionListener( new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  SwingViewMechanism.invokeSwingAction(new SwingAction()
                  {
                     public void offEDT() { _paginable.previousPage(); }
                     public void backOnEDT() { }
                  });
               }
            });
      _nextBtn.addActionListener( new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  SwingViewMechanism.invokeSwingAction(new SwingAction()
                  {
                     public void offEDT() { _paginable.nextPage(); }
                     public void backOnEDT() { }
                  });
               }
            });
   }
   
   public void contentsChanged()
   {
      _prevBtn.setVisible(_paginable.hasPreviousPage());
      _pageNumCaption.setText(_paginable.getPageTitleInfo());
      _nextBtn.setVisible(_paginable.hasNextPage());
   }
   
}
