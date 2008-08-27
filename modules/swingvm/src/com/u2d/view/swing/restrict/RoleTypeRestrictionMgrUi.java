package com.u2d.view.swing.restrict;

import com.u2d.pattern.Onion;
import com.u2d.element.Command;
import com.u2d.restrict.CommandRestriction;
import com.u2d.view.swing.CommandButton;
import com.u2d.view.swing.FormPane;
import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.app.RoleTypeRestrictionMgr;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.*;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 20, 2007
 * Time: 11:41:46 PM
 */
public class RoleTypeRestrictionMgrUi extends JPanel implements ComplexEView, Editor, Runnable
{
   private RoleTypeRestrictionMgr _mgr;
   
   public RoleTypeRestrictionMgrUi(RoleTypeRestrictionMgr mgr)
   {
      _mgr = mgr;
      SwingUtilities.invokeLater(this);
   }

   public void run()
   {
      setLayout(new BorderLayout());
      JPanel centerPane = buildUI();
      add(new JScrollPane(centerPane), BorderLayout.CENTER);
      
      Command cmd = _mgr.command("ApplyChanges");
      CommandButton cmdBtn = new CommandButton(cmd, _mgr, this, true);
      JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      btnPnl.add(cmdBtn);
      add(btnPnl, BorderLayout.PAGE_END);
   }

   private void addSeparator(JPanel panel, String text)
   {
      panel.add(new JLabel(text), "gapbottom 1, span, split 2, aligny center");
      panel.add(new JSeparator(), "gapleft rel, growx");
   }

   private JPanel addCommandSet(String title, Iterator cmdsIterator)
   {
      MigLayout layout = new MigLayout("insets 3 6 3 6, wrap 2", "[right][center]", "");
      FormPane formPane = new FormPane(layout);

      addSeparator(formPane, title);

      while (cmdsIterator.hasNext())
      {
         Command cmd = (Command) cmdsIterator.next();
         formPane.add(new JLabel(cmd.label()));
         formPane.add(checkbox(cmd));
      }

      return formPane;
   }
   private JPanel buildUI()
   {
      Onion typeCommands = _mgr.getType().commands();
      Map<Class, Onion> instanceCmds = _mgr.getType().instanceCommands();

      MigLayout mainLayout = new MigLayout("insets panel, gapx unrel", "", "[top]");
      JPanel mainPnl = new FormPane(mainLayout);

      JPanel childPane = addCommandSet("Type Commands", typeCommands.deepIterator());
      mainPnl.add(childPane);

      for (Iterator itr = instanceCmds.keySet().iterator(); itr.hasNext(); )
      {
         Class stateCls = (Class) itr.next();
         
         if ((stateCls == AbstractComplexEObject.NullState.class) ||
             (stateCls == AbstractComplexEObject.EditState.class) ||
             (stateCls == AbstractComplexEObject.TransientState.class) ||
             (stateCls == AbstractComplexEObject.ReadState.class && _mgr.getType().hasSubstates())
            )
         {
            continue;
         }
         
         Onion stateCmds = instanceCmds.get(stateCls);

         String title = "Instance Commands";
         if (stateCls != AbstractComplexEObject.ReadState.class)
         {
            title = stateName(stateCls);
         }
         
         childPane = addCommandSet(title, stateCmds.deepIterator());
         mainPnl.add(childPane);
      }
      
      return mainPnl;
   }
   
   private List<CommandRestrictionCheckbox> _cbs = new ArrayList<CommandRestrictionCheckbox>();

   private JCheckBox checkbox(Command cmd)
   {
      CommandRestrictionCheckbox cb = new CommandRestrictionCheckbox(cmd);
      _cbs.add(cb);
      return cb;
   }
   
   class CommandRestrictionCheckbox extends JCheckBox
   {
      Command cmd;
      public CommandRestrictionCheckbox(Command cmd)
      {
         super();
         setOpaque(false);
         this.cmd = cmd;
         setSelected(_mgr.getRole().hasRestrictionOnCmd(this.cmd));
      }
   }


   public EObject getEObject() { return _mgr; }
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }
   public void setEditable(boolean editable) { } 
   public boolean isEditable() { return false; }
   public int validateValue() { return 0; }
   public void propertyChange(PropertyChangeEvent evt) { }
   public boolean isMinimized() { return false; }

   public int transferValue()
   {
      List<CommandRestriction> addedRestrictions = new ArrayList<CommandRestriction>();
      List<CommandRestriction> removedRestrictions = new ArrayList<CommandRestriction>();
      
      for (CommandRestrictionCheckbox cb : _cbs)
      {
         CommandRestriction existingRestriction = _mgr.getRole().restrictionOnCmd(cb.cmd);
         if (cb.isSelected() && (existingRestriction == null))
         {
            CommandRestriction restriction = new CommandRestriction(_mgr.getRole(), cb.cmd);
            addedRestrictions.add(restriction);
         }
         else if (!cb.isSelected() && (existingRestriction!=null))
         {
            removedRestrictions.add(existingRestriction);
         }
      }
      _mgr.setAddedRestrictions(addedRestrictions);
      _mgr.setRemovedRestrictions(removedRestrictions);

      return 0;
   }



   private String stateName(Class stateCls)
   {
      String clsName = stateCls.getName();
      int idx = clsName.lastIndexOf(".");
      String stateName = clsName.substring(idx+1);
      if (stateName.contains("$"))
      {
         idx = stateName.lastIndexOf('$');
         stateName = stateName.substring(idx + 1);
      }
      return stateName.substring(0, stateName.length() - 5)+ " State";
   }
   
}
