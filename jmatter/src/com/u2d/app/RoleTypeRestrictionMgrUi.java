package com.u2d.app;

import com.u2d.pattern.Onion;
import com.u2d.element.Command;
import com.u2d.restrict.CommandRestriction;
import com.u2d.view.swing.CommandButton;
import com.u2d.view.swing.FormPane;
import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.model.AbstractComplexEObject;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.PanelBuilder;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.*;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 20, 2007
 * Time: 11:41:46 PM
 */
public class RoleTypeRestrictionMgrUi extends JPanel implements ComplexEView, Editor
{
   private RoleTypeRestrictionMgr _mgr;
   
   public RoleTypeRestrictionMgrUi(RoleTypeRestrictionMgr mgr)
   {
      _mgr = mgr;
      
      setLayout(new BorderLayout());
      JPanel centerPane = buildUI();
      add(new JScrollPane(centerPane), BorderLayout.CENTER);
      
      Command cmd = _mgr.command("ApplyChanges");
      CommandButton cmdBtn = new CommandButton(cmd, _mgr, this, true);
      JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      btnPnl.add(cmdBtn);
      add(btnPnl, BorderLayout.SOUTH);
   }

   private JPanel buildUI()
   {
      Onion typeCommands = _mgr.getType().commands();
      Map<Class, Onion> instanceCmds = _mgr.getType().instanceCommands();

      FormLayout layout = new FormLayout("right:pref, 3dlu, center:pref");
//      layout.setColumnGroups(new int[][]{{1, 5}, {3, 7}});
      
      PanelBuilder builder = new PanelBuilder(layout, new FormPane());
      builder.setDefaultDialogBorder();
      
      builder.appendRow("pref");
      builder.addSeparator("Type commands", 3);
      builder.appendRelatedComponentsGapRow();
      builder.nextLine(2);
      
      for (Iterator itr = typeCommands.deepIterator(); itr.hasNext(); )
      {
         builder.appendRow("pref");
         Command cmd = (Command) itr.next();
         builder.addLabel(cmd.label());
         builder.nextColumn(2);
         builder.add(checkbox(cmd));
         builder.appendRelatedComponentsGapRow();
         builder.nextLine(2);
      }

      int colIdx = 1;
      
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
         
         builder.appendColumn("6dlu");
         builder.appendColumn("right:pref");
         builder.appendColumn("3dlu");
         builder.appendColumn("center:pref");

         builder.setRow(1);
         builder.setColumn(4*colIdx+1);
         if (stateCls == AbstractComplexEObject.ReadState.class)
         {
            builder.addSeparator("Instance commands", 3);
         }
         else
         {
            builder.addSeparator(stateName(stateCls), 3);
         }
         builder.nextLine(2);
         
         for (Iterator itr2 = stateCmds.deepIterator(); itr2.hasNext(); )
         {
            Command cmd = (Command) itr2.next();
            if (builder.getRow() > builder.getRowCount())
            {
               builder.appendRow("pref");
               builder.appendRelatedComponentsGapRow();
            }
            builder.setColumn(4*colIdx+1);
            builder.addLabel(cmd.label());
            builder.nextColumn(2);
            builder.add(checkbox(cmd));
            builder.nextLine(2);
         }
         
         colIdx++;
      }
      
      return builder.getPanel();
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
