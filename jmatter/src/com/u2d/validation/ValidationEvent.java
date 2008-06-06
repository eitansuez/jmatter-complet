/*
 * Created on Feb 9, 2004
 */
package com.u2d.validation;

import javax.swing.*;
import java.util.EventObject;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class ValidationEvent extends EventObject
{
   private String _msg;
   private boolean _statusType = false;

   public static final String FAILED_VALIDATION = "failed_validation";
   public static final Color INVALID_COLOR = new Color(0xFFCCCC);
   public static final Color REQUIRED_COLOR = new Color(0xEBEBFF);
   public static Color normalColor(JComponent c)
   {
      String key = (c.isEnabled()) ? "TextField.background" : "ComboBox.disabledBackground";
      if (c instanceof JComboBox && c.isEnabled()) key = "ComboBox.background";
      return UIManager.getColor(key);
   }

   public ValidationEvent(Object source, String msg, boolean statusType)
   {
      super(source);
      _msg = msg;
      _statusType = statusType;
   }

   public ValidationEvent(Object source, String msg)
   {
      this(source, msg, false);
   }
   
   public String getMsg()    { return _msg; }
   public boolean isStatusMsg() { return _statusType; }

   public String toString()
   {
      return String.format("Validation Event [msg='%s', on source object: '%s']", _msg, getSource());
   }
}
