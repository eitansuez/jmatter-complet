package com.u2d.view.wings;

import com.u2d.model.AtomicEObject;
import com.u2d.view.EView;
import com.u2d.view.wings.atom.StatusView;
import java.util.ArrayList;
import org.wings.SPanel;
import org.wings.SBoxLayout;
import org.wings.SComponent;
import org.wings.border.SBevelBorder;
import java.awt.Color;

/**
 * Date: May 16, 2005
 * Time: 10:48:49 PM
 *
 * Some kind of simple/primitive status panel typical of status
 * panels for frames..
 *
 * @author Eitan Suez
 */
public class StatusPanel extends SPanel
{
   java.util.List _views = new ArrayList();

   public StatusPanel()
   {
      setBackground(new Color(0xd3d3d3));
      setLayout(new SBoxLayout(SBoxLayout.X_AXIS));
      setBorder(new SBevelBorder(SBevelBorder.LOWERED));
   }

   public void addEO(AtomicEObject eo)
   {
      EView view = new StatusView(eo);
      add((SComponent) view);
//      add(Box.createHorizontalStrut(30));
      _views.add(view);
   }

   public void detach()
   {
      for (int i=0; i<_views.size(); i++)
      {
         ((EView) _views.get(i)).detach();
      }
   }

}
