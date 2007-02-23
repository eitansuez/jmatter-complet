/*
 * Created on May 10, 2004
 */
package com.u2d.view.swing.find;

import javax.swing.*;
import com.u2d.element.Command;
import com.u2d.field.Association;
import com.u2d.list.CriteriaListEO;
import com.u2d.model.ComplexType;
import com.u2d.model.Editor;
import com.u2d.view.*;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.SwingViewMechanism;
import java.awt.*;
import java.awt.event.*;
import com.u2d.ui.*;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.find.CompositeQuery;
import com.jgoodies.forms.builder.ButtonBarBuilder;


/**
 * @author Eitan Suez
 */
public class FindView2 extends JSplitPane implements View
{
   private ComplexType _type;
   private FindForm _findForm;

   static int TOP_COMPONENT = 1;
   static int BOTTOM_COMPONENT = 2;

   public FindView2(ComplexType type)
   {
      _type = type;
      _findForm = new FindForm(_type);

      setOrientation(VERTICAL_SPLIT);
      setDividerSize(8);
      setOneTouchExpandable(true);

      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.add(_findForm, BorderLayout.CENTER);

      ButtonBarBuilder builder = new ButtonBarBuilder();
      builder.addGlue();
      builder.addGridded(saveBtn());
      builder.addUnrelatedGap();
      builder.addGridded(findBtn());
      builder.addRelatedGap();
      builder.addGridded(cancelBtn());
      topPanel.add(builder.getPanel(), BorderLayout.SOUTH);

      setTopComponent(topPanel);
      setDividerLocation(topPanel.getPreferredSize().height);
      setBottomComponent(null);
   }

   public String getTitle() { return "Find " + _type; }
   public Icon iconSm() { return ICONSM; }
   public Icon iconLg() { return ICONLG; }
   public boolean withTitlePane() { return true; }

   public ComplexType getType() { return _type; }

   private static Icon ICONSM, ICONLG;
   static
   {
      ClassLoader loader = FindForm.class.getClassLoader();
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
            CloseableJInternalFrame.close(FindView2.this);
         }
      });

      return cancelBtn;
   }

   private JButton saveBtn()
   {
      Command cmd = new Command()
      {
         public void execute(Object value, EView source)
         {
            final CompositeQuery query = _findForm.query();
            query.setTransientState();

            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  EView view = query.getMainView();
                  query.setEditor((Editor) view);
                  SwingViewMechanism.getInstance().displayView(view, null);
               }
            });

         }

         {
            _name.setValue("Save Query");
            _mnemonic.setValue('s');
         }
      };
      Action action = new CommandAdapter(cmd, null, null);
      return new NormalButton(action);
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
            cleo.setPickState(_association);

            if (cleo.isEmpty())
            {
               SwingViewMechanism.getInstance().message("No matches found");
            }
//            else if (cleo.getSize() == 1)
//            {
//               detach();
//               ComplexEObject ceo = (ComplexEObject) cleo.getElementAt(0);
//               ceo.onLoad();
//               setBottomComponent((JComponent) ceo.getMainView());
//            }
            else
            {
               detach();
               setBottomComponent((JComponent) cleo.getView());
               CloseableJInternalFrame.updateSize(FindView2.this);
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

   private void detach()
   {
      EView view = (EView) getBottomComponent();
      if (view == null) return;
      view.detach();
   }

   private Association _association;
   public void setPickState(Association association)
   {
      _association = association;
   }

}

