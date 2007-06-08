package com.u2d.customui;

import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 23, 2006
 * Time: 2:40:47 PM
 */
public class CustomAddressView extends JPanel
      implements ComplexEView, Editor
{
   private Address _addr;
   JComponent line1View, line2View, cityView, stateView, zipView;
   
   public CustomAddressView(Address address)
   {
      _addr = address;
      buildUI();
   }

   private void buildUI()
   {
      line1View = (JComponent) _addr.getLine1().getView();
      line2View = (JComponent) _addr.getLine2().getView();
      cityView = (JComponent) _addr.getCity().getView();
      stateView = (JComponent) _addr.getStateCode().getView();
      zipView = (JComponent) _addr.getZipCode().getView();
      
      FormLayout layout = new FormLayout("pref, 10px, pref, 10px, pref", 
                                         "pref, pref, 10px, pref, pref, 10px, pref, pref");
      CellConstraints cc = new CellConstraints();
      DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
      // add caption..
      builder.add(new JLabel("Line 1:"), cc.xyw(1, 1, 5));
      builder.add(line1View, cc.xyw(1, 2, 5));
      
      builder.add(new JLabel("Line 2:"), cc.xyw(1, 4, 5));
      builder.add(line2View, cc.xyw(1, 5, 5));
      
      builder.add(new JLabel("City:"), cc.xy(1, 7));
      builder.add(new JLabel("State:"), cc.xy(3, 7));
      builder.add(new JLabel("Zip:"), cc.xy(5, 7));
      
      builder.add(cityView, cc.xy(1,8));
      builder.add(stateView, cc.xy(3, 8));
      builder.add(zipView, cc.xy(5, 8));
   }

   public EObject getEObject() { return _addr; }
   
   // as a composite view, this particular class may not
   // necessarily be interested in binding to the model
   // and listen to changes.  to the extent that i use
   // the jmatter views for the subparts of the address,
   // they will be listening directly to the parts.
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }

   public boolean isMinimized() { return false; }

   public int transferValue()
   {
      int result = 0;
      result += ((Editor) line1View).transferValue();
      result += ((Editor) line2View).transferValue();
      result += ((Editor) cityView).transferValue();
      result += ((Editor) stateView).transferValue();
      result += ((Editor) zipView).transferValue();
      return result;
   }

   public int validateValue() { return _addr.validate(); }

   public void setEditable(boolean editable)
   {
      ((Editor) line1View).setEditable(editable);
      ((Editor) line2View).setEditable(editable);
      ((Editor) cityView).setEditable(editable);
      ((Editor) stateView).setEditable(editable);
      ((Editor) zipView).setEditable(editable);
   }

   public boolean isEditable()
   {
      return ((Editor) line1View).isEditable();
   }
}
