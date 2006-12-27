/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.u2d.element.CommandInfo;
import com.u2d.element.EOCommand;
import com.u2d.element.ParameterInfo;
import com.u2d.model.*;
import com.u2d.validation.ValidationNotifier;
import com.u2d.view.*;
import com.u2d.view.swing.atom.TypePicker;
import com.u2d.ui.Caption;
import com.u2d.field.Association;
import com.u2d.field.DynaAssociationStrategy;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import java.util.*;
import java.lang.reflect.Method;

/**
 * @author Eitan Suez
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
      
      FormLayout layout =
         new FormLayout("right:pref, 5px, left:pref:grow", "");
      DefaultFormBuilder builder = new DefaultFormBuilder(layout);
      CellConstraints cc = new CellConstraints();

      try
      {
         ParameterInfo[] paramInfo = cmd.paramInfo();

         // skip first parameter (commandInfo)
         for (int i=1; i<paramInfo.length; i++)
         {
            JLabel label = new Caption(paramInfo[i].caption());

            EView view = null;
            EObject eo = null;
            if ( paramInfo[i].type() == ComplexType.class)
            {
               try
               {
                  Method itypeMethod =
                        _value.getClass().getMethod("abstractType", null);
                  ComplexType itype =
                        (ComplexType) itypeMethod.invoke(_value, null);
                  eo = itype;
                  view = new TypePicker(itype);
                  label.setText(itype.getNaturalName() + " Type: ");
               }
               catch (Exception ex)
               {
                  System.err.println("No specific abstractType so go with complexeobject..");
                  ComplexType itype = ComplexType.forClass(ComplexEObject.class);
                  eo = itype;
                  view = new TypePicker(itype);
                  label.setText("Type: ");
//                  System.err.println("Exception: "+ex);
//                  ex.printStackTrace();
               }
            }
            else if (ComplexType.isAbstract(paramInfo[i].type()))
            {
               ComplexType itype = ComplexType.forClass(paramInfo[i].type());
               eo = itype;
               view = new TypePicker(itype);
            }
            else if (ComplexEObject.class.isAssignableFrom(paramInfo[i].type()))
            {
               ComplexType type = ComplexType.forClass(paramInfo[i].type());
               DynaAssociationStrategy das = new DynaAssociationStrategy(type);
               Association association = new Association(das);
               view = SwingViewMechanism.getInstance().getAssociationView(association);
               eo = das;
            }
            else
            {
               eo = (EObject) paramInfo[i].type().newInstance();
               view = eo.getView();
            }
            
            if (view instanceof Editor)
            {
               ((Editor) view).setEditable(true);
            }
            _views.add(view);
            JComponent comp = (JComponent) view;
            label.setLabelFor(comp);

            ValidationNotifier notifier = (ValidationNotifier) eo;
            JComponent vPnl = new ValidationNoticePanel(notifier, true);

            appendRow(builder, cc, label, comp, vPnl);
         }
      
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
      }

      setLayout(new BorderLayout());

      add(new JScrollPane(builder.getPanel()), BorderLayout.CENTER);
      
      JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      btnPnl.add(okBtn());
      btnPnl.add(cancelBtn());
      add(btnPnl, BorderLayout.SOUTH);
   }

   private void appendRow(DefaultFormBuilder builder, CellConstraints cc,
                          JComponent caption, JComponent comp, JComponent vPnl)
   {
      builder.appendRow("pref");
      builder.add(vPnl, cc.xy(3, builder.getRow()));
      builder.nextLine();

      builder.appendRow("pref");
      builder.add(caption, cc.xy(1, builder.getRow()));
      builder.add(comp, cc.xy(3, builder.getRow()));
      builder.nextLine();

      builder.appendRow("3px"); // a vertical gap
      builder.nextLine();
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
            final java.util.List parms = new ArrayList();
            parms.add(_cmdInfo);
            
            EObject eo;
            EView view;
            int transferErrorCount = 0;
            boolean haveAllParms = true;

            for (int i=0; i<_views.size(); i++)
            {
               view = (EView) _views.get(i);

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
                     ComplexType.isAbstract(_cmd.paramInfo()[i+1].type()))
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
            
            new Thread()
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
            }.start();
               
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
