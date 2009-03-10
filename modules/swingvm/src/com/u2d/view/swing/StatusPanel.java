package com.u2d.view.swing;

import com.u2d.model.AtomicEObject;
import com.u2d.view.EView;
import com.u2d.view.swing.atom.StatusView;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.ArrayList;
import org.jdesktop.swingx.JXPanel;

/**
 * Date: May 16, 2005
 * Time: 10:48:49 PM
 *
 * Some kind of simple/primitive status panel typical of status
 * panels for frames..
 *
 * @author Eitan Suez
 */
public class StatusPanel extends JXPanel
{
   java.util.List<EView> _views = new ArrayList<EView>();

   public StatusPanel()
   {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
   }

   public void addEO(AtomicEObject eo)
   {
      EView view = new StatusView(eo);
      add((JComponent) view);
      add(Box.createHorizontalStrut(30));
      _views.add(view);
   }

   public void detach()
   {
      for (EView view : _views)
      {
         view.detach();
      }
   }
}
