package com.u2d.view.swing.find;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.u2d.ui.DefaultButton;
import com.u2d.ui.Platform;
import com.u2d.find.QueryReceiver;
import com.u2d.model.ComplexType;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 3:59:51 PM
 */
public class FindPanel extends JPanel
{
   private FieldFilter _filter;
   private QueryReceiver _queryReceiver;

   public FindPanel(QueryReceiver receiver)
   {
      _queryReceiver = receiver;
      
      FormLayout layout = new FormLayout("left:pref:grow, 3dlu, right:pref, 1dlu", "pref");
      PanelBuilder builder = new PanelBuilder(layout, this);
      CellConstraints cc = new CellConstraints();
      
      _filter = new FieldFilter(_queryReceiver.queryType());
      
      updateResultsDynamically();
      
      builder.add(_filter, cc.xy(1, 1));
      // trying to get rid of go button and instead dynamically update search results
      // by attaching listeners to value editor.
      // see fieldfilter.ineqactionlistener
      builder.add(findBtn(), cc.xy(3, 1));

      setBorder(BorderFactory.createEtchedBorder());
      setupKeyStrokes();
   }
   
   static String MAP_KEY = "focus-findpanel";
   static KeyStroke COMMAND_F = 
         KeyStroke.getKeyStroke(KeyEvent.VK_F, Platform.mask());

   private void setupKeyStrokes()
   {
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(COMMAND_F, MAP_KEY);
      getActionMap().put(MAP_KEY, new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            _filter.focusValueComponent();
         }
      });
   }
   

   private void updateResultsDynamically()
   {
      _filter.addValueChangeListener(_changeListener);
   }

   public void detach()
   {
      _filter.removeValueChangeListener(_changeListener);
      _filter.detach();
   }


   private JButton findBtn()
   {
      JButton findBtn = new DefaultButton(ComplexType.localeLookupStatic("go"));
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
            _queryReceiver.setQuery(_filter.getQuery());
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
