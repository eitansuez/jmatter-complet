package com.u2d.wizard.ui;

import com.u2d.wizard.abstractions.Step;
import com.u2d.view.EView;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 26, 2005
 * Time: 3:51:17 PM
 */
public class CenterPane extends JPanel
{
   private CardLayout _cardLayout;
   private Map<String,JComponent> _stepViewMap = new HashMap<String,JComponent>();
   private JComponent _currentView;

   public static final Dimension PREFERRED_SIZE = new Dimension(350,400);


   public CenterPane()
   {
      _cardLayout = new CardLayout();
      setLayout(_cardLayout);
      setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
   }

   public void updateStep(Step step) { _cardLayout.show(this, viewName(step)); }

   private String viewName(Step step)
   {
      JComponent view = (JComponent) _stepViewMap.get(step.title());
      if ( (view!=null) && (step.viewDirty()) )
      {
         remove(view);
         _stepViewMap.remove(step.title());
         if (view instanceof EView)
            ((EView) view).detach();
         view = null;
      }

      if (view == null)
      {
         //System.out.println("getting view for "+step.title());
         view = step.getView();
         _stepViewMap.put(step.title(), view);
         add(view, step.title());
      }
      _currentView = view;
      return step.title();
   }

   public JComponent getCurrentView() { return _currentView; }

   public Dimension getPreferredSize() { return PREFERRED_SIZE; }

   public void detach()
   {
      Iterator views = _stepViewMap.values().iterator();
      Object next;
      while (views.hasNext())
      {
         next = views.next();
         if (next instanceof EView)
         {
            ((EView) next).detach();
         }
      }
   }

}
