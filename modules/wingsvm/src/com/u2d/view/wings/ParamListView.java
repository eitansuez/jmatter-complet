package com.u2d.view.wings;

import com.u2d.view.View;
import com.u2d.view.EView;
import com.u2d.element.EOCommand;
import com.u2d.element.CommandInfo;
import com.u2d.element.ParameterInfo;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.model.Editor;
import com.u2d.validation.ValidationNotifier;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import javax.swing.Icon;
import org.wings.*;

/**
 * @author Eitan Suez
 */
public class ParamListView
      extends SForm
      implements View
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
      
      SPanel mainPane = new SPanel();
      mainPane.setLayout(new SGridBagLayout());
      GridBagConstraints cc = new GridBagConstraints();
      cc.gridy = 0;

      try
      {
         ParameterInfo[] paramInfo = cmd.paramInfo();

         // skip first parameter (commandInfo)
         for (int i=1; i<paramInfo.length; i++)
         {
            SLabel label = new SLabel(paramInfo[i].caption());

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
               ComplexType itype = ComplexType.forClass(paramInfo[i].type());
               eo = itype.instance();
               view = eo.getView();
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
            SComponent comp = (SComponent) view;

            ValidationNotifier notifier = (ValidationNotifier) eo;
            SComponent vPnl = new ValidationNoticePanel(notifier, true);

            appendRow(mainPane, cc, label, comp, vPnl);
         }
      
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
      }

      setLayout(new SBorderLayout());

      add(mainPane, SBorderLayout.CENTER);
      
      SPanel btnPnl = new SPanel(new SFlowLayout(SConstants.RIGHT_ALIGN));
      btnPnl.add(okBtn());
      btnPnl.add(cancelBtn());
      add(btnPnl, SBorderLayout.SOUTH);
   }

   private void appendRow(SContainer container, GridBagConstraints cc,
                          SComponent caption, SComponent comp, SComponent vPnl)
   {
      cc.gridx = 0;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      container.add(vPnl, cc);
      cc.gridy++;
      
      cc.gridx = 0;
      cc.gridwidth = 1;
      container.add(caption, cc);
      
      cc.gridx = 1;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      container.add(comp, cc);

      cc.gridy++;
   }


   private SButton okBtn()
   {
      SButton okBtn = new DefaultButton("OK");
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

               transferErrorCount += ((Editor) view).transferValue();

               if (transferErrorCount > 0)
               {
                  haveAllParms = false;
                  continue;
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
            
            if (!haveAllParms)
            {
               return;
            }
            
            try
            {
               _cmd.execute(_value, parms.toArray());
            }
            catch (java.lang.reflect.InvocationTargetException ex)
            {
               System.err.println("InvocationTargetException: "+ex.getMessage());
               ex.printStackTrace();
            }
               
            WingSViewMechanism.getParentInternalFrame(ParamListView.this).dispose();
         }
      });
      return okBtn;
   }
   
   private SButton cancelBtn()
   {
      SButton cancelBtn = new NormalButton("Cancel");
      cancelBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt)
         {
            WingSViewMechanism.getParentInternalFrame(ParamListView.this).dispose();
         }
      });
      
      return cancelBtn;
   }

   // would be nice if _cmd were an eobject..then wouldn't have to do this.
   public String getTitle() { return _cmd.label(); }
   public Icon iconSm() { return _cmd.iconSm(); }
   public Icon iconLg() { return _cmd.iconLg(); }
   public boolean withTitlePane() { return true; }   
   
}
