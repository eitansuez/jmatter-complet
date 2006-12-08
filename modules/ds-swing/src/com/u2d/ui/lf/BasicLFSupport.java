/*
 * Created on Sep 5, 2003
 */
package com.u2d.ui.lf;

import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.metal.MetalLookAndFeelAddons;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

/**
 * @author Eitan Suez
 */
public class BasicLFSupport implements LookAndFeelSupport
{
   private JMenu _lfMenu;
   private Map _lfMap;
   private LookAndFeel _currentLF;
   private ButtonGroup _group;
   private Component[] _topLevelContainers;
   private Frame _mainFrame;
   private LookAndFeelSupport _lfsupport;

   public BasicLFSupport(Component[] topLevelContainers,
                         LFProvider provider,
                         LookAndFeelSupport lfsupport)
   {
      _topLevelContainers = topLevelContainers;
      _lfsupport = lfsupport;
      if (_lfsupport == null)
         _lfsupport = this;

      for (int i=0; i<_topLevelContainers.length; i++)
      {
         if (_topLevelContainers[i] instanceof Frame)
         {
            _mainFrame = (Frame) _topLevelContainers[i];
            break;
         }
      }

      //_lfMenu = new JMenu(provider.getMenuCaption());
      _lfMenu = new JMenu("Theme");
      _lfMenu.setMnemonic('t');
      _group = new ButtonGroup();

      ActionListener lfAction = new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               _lfsupport.setLF(evt.getActionCommand());
            }
         };


      _lfMap = new HashMap();

      LookAndFeel lf = null;
      for (int i=0; i<provider.lfs().length; i++)
      {
         lf = provider.lfs()[i];

         if (lf.isSupportedLookAndFeel())
         {
            //System.out.println("loading look and feel "+lf.getName());
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(lf.getName());
            _group.add(item);
            item.addActionListener(lfAction);
            _lfMenu.add(item);
            _lfMap.put(lf.getName(), lf);
            if (lf.isNativeLookAndFeel())
               setLF(lf);  // make the native look and feel the default
         }
      }

      if (_currentLF == null)
      {
         _currentLF = UIManager.getLookAndFeel();
         selectRadioForLF();
      }
   }


   public JMenu getMenu() { return _lfMenu; }
   public LookAndFeel getCurrentLF() { return _currentLF; }
   public String getCurrentLFName()
   {
      return _currentLF.getName();
   }

   public void setLF(String lfName)
   {
      if (!hasLF(lfName))
      {
         System.err.println("Invalid look and feel name: "+lfName);
         return;
      }
      LookAndFeel lf = (LookAndFeel) _lfMap.get(lfName);
      setLF(lf);
   }

   public boolean hasLF(String lfName)
   {
      return (_lfMap.get(lfName) != null);
   }

   public void setLF(LookAndFeel lf)
   {
      try
      {
         _mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         UIManager.setLookAndFeel(lf);
         LookAndFeelAddons.setAddon(MetalLookAndFeelAddons.class);
         
         _currentLF = lf;
         selectRadioForLF();

         for (int i=0; i<_topLevelContainers.length; i++)
            SwingUtilities.updateComponentTreeUI(_topLevelContainers[i]);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      finally
      {
         _mainFrame.setCursor(Cursor.getDefaultCursor());
      }
   }

   private void selectRadioForLF()
   {
      if (_currentLF == null) return;
      Enumeration elements = _group.getElements();
      while (elements.hasMoreElements())
      {
         AbstractButton b = (AbstractButton) elements.nextElement();
         if (b.getText().equals(_currentLF.getName()))
         {
            b.setSelected(true);
         }
      }
   }

   interface LFProvider
   {
      public LookAndFeel[] lfs();
      public String getMenuCaption();
   }
   public static class JGoodiesLooks implements LFProvider
   {
      private static String[] clsNames = {
            "com.jgoodies.looks.windows.WindowsLookAndFeel",
            "com.jgoodies.looks.plastic.PlasticLookAndFeel",
            "com.jgoodies.looks.plastic.Plastic3DLookAndFeel",
            "com.jgoodies.looks.plastic.PlasticXPLookAndFeel"
         };

      public String getMenuCaption() { return "JGoodies"; }

      private LookAndFeel[] _lfs;
      public LookAndFeel[] lfs()
      {
         if (_lfs == null)
            loadLFs();
         return _lfs;
      }
      
      private void loadLFs()
      {
         List lfs = new ArrayList();
         for (int i=0; i<clsNames.length; i++)
         {
            LookAndFeel lf = lookAndFeel(clsNames[i]);
            if (lf != null)
               lfs.add(lf);
         }
         _lfs = new LookAndFeel[lfs.size()];
         _lfs = (LookAndFeel[]) lfs.toArray(_lfs);
      }
   }
   
   public static class SystemLFProvider implements LFProvider
   {
      public String getMenuCaption() { return "Swing"; }

      private LookAndFeel[] _lfs;
      public LookAndFeel[] lfs()
      {
         if (_lfs == null)
            _lfs = installedLookAndFeels();
         return _lfs;
      }
   }


   public static LookAndFeel lookAndFeel(String clsName)
   {
      try
      {
         Class lfClass = Class.forName(clsName);
         return (LookAndFeel) lfClass.newInstance();
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println(ex.getMessage());
      }
      catch (InstantiationException ex)
      {
         System.err.println(ex.getMessage());
      }
      catch (IllegalAccessException ex)
      {
         System.err.println(ex.getMessage());
      }
      return null;
   }

   public static LookAndFeel[] installedLookAndFeels()
   {
      UIManager.LookAndFeelInfo[] lfinfo = UIManager.getInstalledLookAndFeels();
      LookAndFeel[] lfs = new LookAndFeel[lfinfo.length];
      for (int i=0; i<lfinfo.length; i++)
      {
         lfs[i] = lookAndFeel(lfinfo[i].getClassName());
      }
      return lfs;
   }

   // === tbd..
   public void addLFChangeListener(LFChangeListener l) {}
   public void fireLFChanged() {}
   public void removeLFChangeListener(LFChangeListener l) {}

}
