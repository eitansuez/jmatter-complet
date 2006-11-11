/*
 * Created on Jan 22, 2004
 */
package com.u2d.element;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.Localized;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public abstract class ProgrammingElement extends AbstractComplexEObject
{
   protected final StringEO _name = new StringEO();
   protected final StringEO _label = new StringEO();
   
   public ProgrammingElement()
   {
      getName().addChangeListener(new javax.swing.event.ChangeListener()
         {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
               deriveLabel();
            }
         });
   }
   
   public StringEO getName() { return _name; }
   public StringEO getLabel() { return _label; }
   
   public String name() { return getName().stringValue(); }
   public String label() { return getLabel().stringValue(); }

   public void deriveLabel()
   {
      String labelValue = deriveLabel(getName().stringValue());
      getLabel().setValue(labelValue);
   }
   
   public abstract String localizedLabel(Localized l);
   public void localize(Localized l)
   {
      String value = localizedLabel(l);
      if (value != null)
         _label.setValue(value);
   }
   
   public static String deriveLabel(String name)
   {
      //  1. capitalize first letter
      //  2. insert a space before other capital letters in string
      if (name == null || name.length() == 0)
         return "";
      
      StringBuffer s = new StringBuffer(name.length());
      char previous = name.charAt(0);
      char next;
      s.append(Character.toUpperCase(previous));
      for (int i=1; i<name.length(); i++)
      {
         next = name.charAt(i);
         if (Character.isUpperCase(next) && Character.isLowerCase(previous))
            s.append(' ');
         
         if (next == '_')  // subst underscores with spaces..
            s.append(' ');
         else
            s.append(next);
         
         previous = next;
      }
      return s.toString();
   }
   
   public String toString() { return name(); }
   public Title title() { return getName().title(); }

   public boolean isMeta() { return true; }

}
