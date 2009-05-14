package com.u2d.wizard.ui;

import com.u2d.wizard.abstractions.Step;
import com.u2d.view.EView;
import com.u2d.view.swing.ValidationNoticePanel;
import com.u2d.ui.UIUtils;
import com.u2d.model.EObject;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

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
   private ValidationNoticePanel _validationPnl;
   private JPanel _stepsPnl;

   public CenterPane(Dimension preferredSize)
   {
      setLayout(new BorderLayout());

      _stepsPnl = new JPanel();
      _cardLayout = new CardLayout();
      _stepsPnl.setLayout(_cardLayout);
      _stepsPnl.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      _stepsPnl.setPreferredSize(preferredSize);

      _validationPnl = new ValidationNoticePanel();
      add(_validationPnl, BorderLayout.NORTH);
      add(_stepsPnl, BorderLayout.CENTER);
   }

   public void updateStep(Step step)
   {
      _cardLayout.show(_stepsPnl, viewName(step));

      if (_currentView instanceof EView)
      {
         EObject eo = ((EView) _currentView).getEObject();
         _validationPnl.setTarget(eo);
      }

      // must delay this otherwise may not have any effect..
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            UIUtils.focusFirstEditableField(_currentView);
         }
      });
   }

   private String viewName(Step step)
   {
      JComponent view = _stepViewMap.get(step.title());
      if ( (view!=null) && (step.viewDirty()) )
      {
         _stepsPnl.remove(view);
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
         _stepsPnl.add(view, step.title());
      }
      _currentView = view;
      return step.title();
   }

   public JComponent getCurrentView() { return _currentView; }

   public void detach()
   {
      for (Object next : _stepViewMap.values())
      {
         if (next instanceof EView)
         {
            ((EView) next).detach();
         }
      }
      _validationPnl.stopListening();
   }

}
