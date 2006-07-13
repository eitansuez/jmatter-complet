package com.u2d.wizard;

import com.u2d.wizard.details.BasicStep;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 10:55:07 AM
 */
public class MockBasicStep extends BasicStep
{
   String _text = "";

   public MockBasicStep() { }

   // with optional caption..
   public MockBasicStep(String text)
   {
      _text = text;
   }

   int invokeCount = 0;
   public JComponent getView()
   {
      invokeCount++;
      System.out.println(_text);
      return null;
   }

   public String title() { return toString(); }
   public String description() { return toString(); }

   public String toString() { return _text; }
}
