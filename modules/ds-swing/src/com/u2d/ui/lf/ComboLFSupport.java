/*
 * Created on Dec 29, 2003
 */
package com.u2d.ui.lf;

import java.awt.*;
import javax.swing.*;

/**
 * Combines BasicLFSupport and SkinLFSupport.
 * 
 * @author Eitan Suez
 */
public class ComboLFSupport implements LookAndFeelSupport
{
   private LookAndFeelSupport _system, _skin, _jgoodies;
   private JMenu _menu;
   private LookAndFeelSupport _selectedLF;
   private ButtonGroup _group;
   private Component[] _topLevelContainers;
   
   public ComboLFSupport(Component[] topLevelContainers)
   {
      _topLevelContainers = topLevelContainers;
      _menu = new JMenu("Theme");
      _menu.setMnemonic('t');
      _group = new ButtonGroup();
      
      _system = new BasicLFSupport(_topLevelContainers,
            new BasicLFSupport.SystemLFProvider(), this);
      _skin = new SkinLFSupport(_topLevelContainers, this);
//      _jgoodies = new BasicLFSupport(_topLevelContainers, 
//            new BasicLFSupport.JGoodiesLooks(), this);
      
      addMenu(_system.getMenu());
      addMenu(_skin.getMenu());
//      addMenu(_jgoodies.getMenu());
      
      _selectedLF = _system;
   }
   
   private void addMenu(JMenu from)
   {
      for (int i=0; i<from.getItemCount(); i++)
      {
         JMenuItem item = from.getItem(i);
         _group.add(item);
      }
      _menu.add(from);
   }
   
   public JMenu getMenu() { return _menu; }
   
   public String getCurrentLFName()
   {
      return _selectedLF.getCurrentLFName();
   }
   
   public void setLF(String lfname)
   {
      LookAndFeelSupport which = getLFSupportForLF(lfname);
      if (which == null)
      {
         System.err.println("no look and feel support for lf name: "+lfname);
         return;
      }
      which.setLF(lfname);
      _selectedLF = which;
      
      for (int i=0; i<_topLevelContainers.length; i++)
         SwingUtilities.updateComponentTreeUI(_topLevelContainers[i]);
      
      fireLFChanged();
   }
   
   private LookAndFeelSupport getLFSupportForLF(String lfname)
   {
      if (_system.hasLF(lfname)) return _system;
      if (_skin.hasLF(lfname)) return _skin;
      if (_jgoodies.hasLF(lfname)) return _jgoodies;
      return null;
   }
   
   public boolean hasLF(String lfName)
   {
      return ( (_system.hasLF(lfName)) || (_skin.hasLF(lfName)) || 
               (_jgoodies.hasLF(lfName)) );
   }
   
   
   
   // === lf change notifier implementation === //
   private LFChangeSupport _support = new LFChangeSupport();
   
   public void addLFChangeListener(LFChangeListener l)
   {
      _support.addLFChangeListener(l);
   }
   public void fireLFChanged()
   {
      _support.fireLFChanged();
   }
   public void removeLFChangeListener(LFChangeListener l)
   {
      _support.removeLFChangeListener(l);
   }
}
