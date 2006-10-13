package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.type.AbstractChoiceEO;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import org.wings.SLabel;
import org.wings.SComboBox;

/**
 * The reason this class does not extend JComboBox directly
 * is because of a namespace conflict between JComboBox and
 * my Editor interface (editable property)
 * 
 * @author Eitan Suez
 */
public class ChoiceView
      extends CardPanel
      implements ComplexEView, Editor
{
   private AbstractChoiceEO _choice;
   private SComboBox _cb;
   private SLabel _label = new SLabel();

   public ChoiceView(AbstractChoiceEO choice)
   {
      _choice = choice;
      _cb = new SComboBox(_choice);

      _choice.addChangeListener(this);
      stateChanged(null);  // initialize the label text..

      add(_cb, "edit");
      add(_label, "view");

      setEditable(false); // start out read-only by default
   }
   
   public void stateChanged(ChangeEvent evt)
   {
      _label.setText(_choice.title().toString());
   }
   public void propertyChange(PropertyChangeEvent evt) {}

   public int transferValue() { return 0; }

   public void setEditable(boolean editable)
   {
      _cb.setEnabled(editable);
      show((editable) ? "edit" : "view");
   }
   public boolean isEditable() { return _cb.isEnabled(); }
   
   public EObject getEObject() { return _choice; }
   public boolean isMinimized() { return false; }
   
   public void detach()
   {
      _choice.removeChangeListener(this);
   }
   
}
