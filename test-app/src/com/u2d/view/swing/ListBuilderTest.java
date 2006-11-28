package com.u2d.view.swing;

import com.u2d.view.swing.list.CheckboxListBuilder;
import com.u2d.list.SimpleListEO;
import com.u2d.domain.Shipment;
import javax.swing.*;
import java.awt.*;

/**
 * Date: Jun 16, 2005
 * Time: 3:16:09 PM
 *
 * @author Eitan Suez
 */
public class ListBuilderTest extends JPanel
{
   public ListBuilderTest()
   {
      SimpleListEO leo = new SimpleListEO(Shipment.class);
      leo.add(new Shipment("One", 1.0f));
      leo.add(new Shipment("Two", 1.1f));
      leo.add(new Shipment("Three", 1.2f));
      leo.add(new Shipment("Four", 1.3f));
      leo.add(new Shipment("Five", 1.4f));

      SimpleListEO subset = new SimpleListEO(Shipment.class);
      subset.add((Shipment) leo.getElementAt(0));
      subset.add((Shipment) leo.getElementAt(2));

      CheckboxListBuilder builder = new
            CheckboxListBuilder(leo, subset, 3);

      JScrollPane jsp = new JScrollPane(builder);
      add(jsp, BorderLayout.CENTER);
   }

   public static void main(String[] args)
   {
      JFrame f = new JFrame();
      JPanel cp = (JPanel) f.getContentPane();


      cp.add(new ListBuilderTest(), BorderLayout.CENTER);


      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocation(100, 100);
      f.pack();
      f.setVisible(true);

   }
}
