/*
 * Created on Dec 12, 2003
 */
package com.u2d.view.swing.calendar;

import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import java.awt.*;
import javax.swing.*;
import java.beans.*;

/**
 * @author Eitan Suez
 */
public class CalendarFrame extends CloseableJInternalFrame implements ComplexEView
{
   private transient EView _view;
   private ComplexEObject _ceo;
   
   public CalendarFrame(EView view)
   {
      super();
      _view = view;
      _ceo = ((ComplexEObject) _view.getEObject());
      
      _ceo.addChangeListener(this);
      _ceo.addPropertyChangeListener(this);
      
      if (_ceo.isTransientState())
         setTitle("New "+_ceo.type().getNaturalName());
      else
         setTitle(_ceo.title().toString());
      
      setFrameIcon(_ceo.iconSm());
      
      JPanel contentPane = (JPanel) getContentPane();
      contentPane.add((JComponent) _view, BorderLayout.CENTER);
      
      setResizable(true); setMaximizable(true); setIconifiable(true); setClosable(true);
      setupToFocusOnDragEnter();
      pack();
   }
   
   public EView getView() { return _view; }
   
   public void propertyChange(PropertyChangeEvent evt) {}

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (_ceo.isNullState())
            {
               closeFrame(CalendarFrame.this);
               return;
            }
            
            if (!_ceo.isEditableState())
               setTitle(_ceo.title().toString());
            
            updateSize();
         }
      });
   }
   
   public EObject getEObject() { return _view.getEObject(); }
   public boolean isMinimized() { return false; }
   
   // need to ensure this is called both on keyboard shortcut for closewindow 
   // as well as normal mouse gesture close window (click on 'x')
   public void close()
   {
      super.close();
      // window can be closed directly via 'x' decorator button -- it's semantically the same as 'cancel' but
      // short-circuits it
      _ceo.cancelTransition();
   }

   public void addNotify()
   {
      super.addNotify();
      updateSize();
   }

   
   public void dispose()
   {
      super.dispose();
      detach();
   }
   
   public void detach()
   {
      _ceo.removePropertyChangeListener(this);
      _ceo.removeChangeListener(this);
      _view.detach();
   }

}
