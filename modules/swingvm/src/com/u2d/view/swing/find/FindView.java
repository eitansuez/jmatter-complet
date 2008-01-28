/*
 * Created on May 10, 2004
 */
package com.u2d.view.swing.find;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.u2d.element.Command;
import com.u2d.list.CriteriaListEO;
import com.u2d.model.ComplexType;
import com.u2d.view.*;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.app.*;
import com.u2d.ui.*;


/**
 * @author Eitan Suez
 */
public class FindView extends JPanel implements View
{
   private ComplexType _type;
   private FindForm _findForm;
   
   public FindView(ComplexType type)
   {
      _type = type;
      _findForm = new FindForm(_type);
      
      setLayout(new BorderLayout());
      add(_findForm, BorderLayout.CENTER);
      
      JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      btnPnl.add(findBtn());
      btnPnl.add(cancelBtn());
      add(btnPnl, BorderLayout.SOUTH);
   }
   
   public String getTitle() { return "Find " + _type; }
   public Icon iconSm() { return ICONSM; }
   public Icon iconLg() { return ICONLG; }
   public boolean withTitlePane() { return true; }
   
   public ComplexType getType() { return _type; }
   
   private static Icon ICONSM, ICONLG;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/find16.png");
      ICONSM = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/find32.png");
      ICONLG = new ImageIcon(imgURL);
   }

   private JButton cancelBtn()
   {
      JButton cancelBtn = new NormalButton("Cancel");
      cancelBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            final JInternalFrame jif = (JInternalFrame)
               SwingUtilities.getAncestorOfClass(JInternalFrame.class, FindView.this);
            jif.dispose();
         }
      });
      
      return cancelBtn;
   }
   
   private JButton findBtn()
   {
      // by using the CommandAdapter, get WAIT_CURSOR and execution outside ui 
      // thread for free
      
      Command findCmd = new Command()
       {
         public void execute(Object value, EView source)
         {
            CriteriaListEO cleo = _findForm.doFind();
            
            ViewMechanism vmech = Context.getInstance().getViewMechanism();
            if (cleo.isEmpty())
            {
               vmech.message("No matches found");
            }
            else
            {
               vmech.displayView(cleo.getView(), null);
            }
            
         } // end execute method
         
         {
            _name.setValue("Find");
            _mnemonic.setValue('f');
         }
//         public String getName() { return "Find"; }
//         public String getLabel() { return "Find"; }
       };
            
      CommandAdapter cmdAdapter = new CommandAdapter(findCmd, null, null);
      return new DefaultButton(cmdAdapter);
   }

}

