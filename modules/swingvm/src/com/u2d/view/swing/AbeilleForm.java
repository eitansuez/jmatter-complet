package com.u2d.view.swing;

import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.view.EView;
import com.u2d.element.Field;
import com.u2d.field.CompositeField;
import com.u2d.ui.UIUtils;
import com.jeta.forms.components.panel.FormPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 29, 2008
 * Time: 12:36:48 PM
 */
public abstract class AbeilleForm extends FormPane implements IFormView
{
   protected java.util.List<EView> _views = new ArrayList<EView>();

   protected FormPanel formPanel()
   {
      String clsName = getEObject().getClass().getName();
      String formName = clsName.replace('.', File.separatorChar) + ".jfrm";
      return new FormPanel(formName);
   }

   protected JComponent getView(EObject eo)
   {
      EView view = eo.getMainView();
      _views.add(view);
      return (JComponent) view;
   }

   public void detach()
   {
      for (EView view : _views)
      {
         view.detach();
      }
   }

   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }

   private ComplexEObject eo() { return (ComplexEObject) getEObject(); }

   protected boolean _editable = false;
   public void setEditable(boolean editable)
   {
      _editable = editable;
      for (EView view : _views)
      {
         if (!(view instanceof Editor)) continue;

         EObject eo = view.getEObject();
         if (eo == null) return;  // see comment in FormView
         Field field = eo.field();

         if (field.isComposite() && editable && !field.isIndexed())
         {
            CompositeField cfield = ((CompositeField) field);
            if (cfield.isReadOnly() ||
                  ( cfield.isIdentity() && !(eo().isTransientState()) )
               )
               continue;
         }

         ((Editor) view).setEditable(editable);
      }
   }
   public boolean isEditable() { return _editable; }

   public int transferValue()
   {
      int count = 0;
      for (EView view : _views)
      {
         if (view instanceof Editor)
         {
            Field field = view.getEObject().field();

            if (field.isComposite() && !field.isIndexed())
            {
               CompositeField cfield = ((CompositeField) field);
               if (cfield.isReadOnly() ||
                     ( cfield.isIdentity() && !(eo().isTransientState()) )
                  )
                  continue;
            }

            count += ((Editor) view).transferValue();
         }
      }

      return count;
   }

   public int validateValue()
   {
      return eo().validate();
   }

   public boolean isMinimized() { return false; }

   public void focusField()
   {
      UIUtils.focusFirstEditableField(AbeilleForm.this);
   }
}
