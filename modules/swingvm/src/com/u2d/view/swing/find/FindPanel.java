package com.u2d.view.swing.find;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.u2d.ui.DefaultButton;
import com.u2d.find.QueryList;
import com.u2d.model.AbstractListEO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 3:59:51 PM
 */
public class FindPanel extends JPanel
{
   private FieldFilter _filter;
   private AbstractListEO _leo;

   public FindPanel(AbstractListEO leo)
   {
      _leo = leo;
      
      FormLayout layout = new FormLayout("left:pref:grow, 3dlu, right:pref, 1dlu", "pref");
      PanelBuilder builder = new PanelBuilder(layout, this);
      CellConstraints cc = new CellConstraints();
      
      _filter = new FieldFilter(_leo.type());
      
      updateResultsDynamically();
      
      builder.add(_filter, cc.xy(1, 1));
      builder.add(findBtn(), cc.xy(3, 1));

      setBorder(BorderFactory.createEtchedBorder());
   }

   private void updateResultsDynamically()
   {
      _filter.addValueChangeListener(_changeListener);
   }

   public void detach()
   {
      _filter.removeValueChangeListener(_changeListener);
      _filter.detach();
      _leo = null;
   }


   private JButton findBtn()
   {
      JButton findBtn = new DefaultButton("Go");
      findBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               updateResults();
            }
         });
      return findBtn;
   }
   
   private void updateResults()
   {
      new Thread()
      {
         public void run()
         {
            ((QueryList) _leo).setQuery(_filter.getQuery());
         }
      }.start();
   }
   
   ChangeListener _changeListener = new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            updateResults();
         }
      };
   
}
