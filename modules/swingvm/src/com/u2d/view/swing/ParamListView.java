/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.u2d.element.CommandInfo;
import com.u2d.element.EOCommand;
import com.u2d.element.ListCommand;
import com.u2d.element.ParameterInfo;
import com.u2d.model.*;
import com.u2d.view.*;
import com.u2d.view.swing.atom.TypePicker;
import com.u2d.ui.Caption;
import com.u2d.field.Association;
import com.u2d.field.DynaAssociationStrategy;
import java.util.*;
import java.lang.reflect.Method;
import net.miginfocom.swing.MigLayout;

/**
 * @author Eitan Suez
 * 
 * TODO: ParamListView is a sort of informal FormView.  Whereas formview
 *  operates on a type that has as its parameters a list of fields, here
 *  the list of fields is the method's parameters.  Perhaps some of the
 *  common logic can be refactored?
 */
public class ParamListView extends JPanel implements View 
{
   private EOCommand _cmd;
   private CommandInfo _cmdInfo;
   private Object _value;
   
   private java.util.List<EView> _views = new ArrayList<EView>();
   
   public ParamListView(EOCommand cmd, Object value, CommandInfo cmdInfo)
   {
      _cmd = cmd;
      _value = value;
      _cmdInfo = cmdInfo;
      
      MigLayout layout = new MigLayout("insets 0 5 0 5, wrap 2, gapy 2", "[trailing][grow]", "");
      JPanel pnl = new JPanel(layout);

      try
      {
         for (ParameterInfo paramInfo : cmd.paramInfo())
         {
            JLabel label = new Caption(paramInfo.caption());

            EView view;
            EObject eo;
            if ( paramInfo.type() == ComplexType.class)
            {
               try
               {
                  Class[] parameterTypes = new Class[0];
                  Method itypeMethod =
                        _value.getClass().getMethod("baseType", parameterTypes);
                  Object[] args = new Object[0];
                  ComplexType itype =
                        (ComplexType) itypeMethod.invoke(_value, args);
                  eo = itype;

                  if ("Type: ".equals(label.getText()))
                  {
                     label.setText(itype.getNaturalName() + " Type: ");
                  }

                  view = new TypePicker(itype);
               }
               catch (Exception ex)
               {
                  System.err.println("No specific baseType so go with complexeobject..");
                  ComplexType itype = ComplexType.forClass(ComplexEObject.class);
                  eo = itype;
                  view = new TypePicker(itype);
//                  System.err.println("Exception: "+ex);
//                  ex.printStackTrace();
               }
            }
            else if (ComplexEObject.class.isAssignableFrom(paramInfo.type()))
            {
               ComplexType type = ComplexType.forClass(paramInfo.type());
               DynaAssociationStrategy das = new DynaAssociationStrategy(type);
               Association association = new Association(das);
               view = SwingViewMechanism.getInstance().getAssociationView(association);
               eo = das;
            }
            else if (ComplexType.isAbstract(paramInfo.type()))
            {
               ComplexType itype = ComplexType.forClass(paramInfo.type());
               eo = itype;
               view = new TypePicker(itype);
            }
            else
            {
               eo = (EObject) paramInfo.type().newInstance();
               view = eo.getView();
            }
            
            if (view instanceof Editor)
            {
               ((Editor) view).setEditable(true);
            }
            _views.add(view);
            JComponent comp = (JComponent) view;
            label.setLabelFor(comp);

            JComponent vPnl = new ValidationNoticePanel(eo, true);

            pnl.add(vPnl, "skip, wrap");
            pnl.add(label);
            pnl.add(comp);
         }
      
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
      }

      setLayout(new BorderLayout());

      add(new JScrollPane(pnl), BorderLayout.CENTER);
      
      JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      btnPnl.add(okBtn());
      btnPnl.add(cancelBtn());
      add(btnPnl, BorderLayout.PAGE_END);
   }

   private JButton okBtn()
   {
      JButton okBtn = new com.u2d.ui.DefaultButton("OK");
      okBtn.addActionListener( new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            // check for blank fields and firevalidation exceptions
            // if all fields ok, then new thread().start(_cmd.execute(value, params);
            // how to construct params?
            // walk eviews:  eview.getEObject --> add to list
            final java.util.List<Object> parms = new ArrayList<Object>();
            parms.add(_cmdInfo);
            if (_cmd instanceof ListCommand)
            {
               parms.add(_value);
            }
            
            EObject eo;
            EView view;
            int transferErrorCount = 0;
            boolean haveAllParms = true;

            for (int i=0; i<_views.size(); i++)
            {
               view = _views.get(i);

               if (view instanceof Editor)
               {
                  transferErrorCount += ((Editor) view).transferValue();

                  if (transferErrorCount > 0)
                  {
                     haveAllParms = false;
                     continue;
                  }
               }

               eo = view.getEObject();
               // in addition, make sure that value is not empty (all arguments are required)
               if (eo.isEmpty())
               {
                  eo.fireValidationException("Value cannot be blank");
                  haveAllParms = false;
               }
               else
               {
                  eo.fireValidationException("");  // to clear any previous message
               }

               if (eo instanceof ComplexType &&
                     ComplexType.isAbstract(_cmd.paramInfo()[i].type()))
               {
                  // now that user has picked the concrete type,
                  // go ahead and create the instance
                  eo = ((ComplexType) eo).instance();
               }

               parms.add(eo);
            }
            
            if (transferErrorCount > 0) haveAllParms = false;
            
            final JInternalFrame jif = (JInternalFrame)
                  SwingUtilities.getAncestorOfClass(JInternalFrame.class, ParamListView.this);

            if (!haveAllParms)
            {
               if (jif instanceof com.u2d.ui.desktop.CloseableJInternalFrame)
               {
                  SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           ((com.u2d.ui.desktop.CloseableJInternalFrame) jif).updateSize();
                        }
                     });
               }
               return;
            }
            
            AppLoader.getInstance().newThread(new Runnable()
            {
               public void run()
               {
                  try
                  {
                     _cmd.execute(_value, parms.toArray());
                  }
                  catch (java.lang.reflect.InvocationTargetException ex)
                  {
                     System.err.println("InvocationTargetException: "+ex.getMessage());
                     ex.printStackTrace();
                  }
               }
            }).start();
               
            jif.dispose();
               
         }
      });
      return okBtn;
   }
   
   private JButton cancelBtn()
   {
      JButton cancelBtn = new com.u2d.ui.NormalButton("Cancel");
      cancelBtn.addActionListener(new ActionListener()
            {
         public void actionPerformed(ActionEvent evt)
         {
            final JInternalFrame jif = (JInternalFrame)
            SwingUtilities.getAncestorOfClass(JInternalFrame.class, ParamListView.this);
            jif.dispose();
         }
            });
      
      return cancelBtn;
   }
   
   // would be nice if _cmd were an eobject..then wouldn't have to do this.
   public String getTitle() { return _cmd.label(); }
   public Icon iconSm() { return _cmd.iconSm(); }
   public Icon iconLg() { return _cmd.iconLg(); }
   public boolean withTitlePane() { return true; }   
   
   public Dimension getMinimumSize() { return getPreferredSize(); }
   
}
