/*
 * Created on Sep 5, 2003
 */
package com.u2d.ui.lf;


import javax.swing.*;

import java.awt.event.*;
import java.util.*;
import com.l2fprod.gui.plaf.skin.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class SkinLFSupport implements LookAndFeelSupport
{
	private JMenu _skinlfMenu;
	private Map _skinMap;
	private String _currentSkinName;
   private ButtonGroup _group;
   private Component[] _topLevelContainers;
   private SkinLookAndFeel _skinlf = new SkinLookAndFeel();
   private Frame _mainFrame;
   private LookAndFeelSupport _lfsupport;

   /* hard code because don't yet know of a way to use classloader to load
    * resources without knowing about them a priori.  with files, can simply
    * list contents of a designated themes directory.  since this app is
    * to be delivered using webstart, would rather not require security
    * permissions, certificates, etc..
    * TODO:  fix this.. should not be hard-coded..
    */
   private static String[] themenames = 
      {"aqua", "bbj", "beos", "cellshaded", "macos", 
         "modern", "default", "whistler", "xpluna", "toxic"};
   
   public SkinLFSupport(Component[] topLevelContainers, 
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

      _skinlfMenu = new JMenu("SkinLF");
      _skinlfMenu.setMnemonic('s');
      _group = new ButtonGroup();
      
		ActionListener lfAction = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				_lfsupport.setLF(evt.getActionCommand());
			}
		};
		
      _skinMap = new HashMap();

      JRadioButtonMenuItem skinItem = null;
      String resourceName;
      ClassLoader loader = getClass().getClassLoader();
      /*
       * problem: loading themepacks takes much time and resources and many
       * themes may never be used. furthermore delays startup time considerably.
       * solution: themes must be lazy loaded
       */
      for (int i = 0; i < themenames.length; i++)
      {
         try
         {
            String themename = themenames[i];
            if ("default".equals(themename)) themename = "";
            resourceName = "themes/" + themename + "themepack.zip";
            java.net.URL themeURL = loader.getResource(resourceName);
            _skinMap.put(themenames[i], themeURL);

            //System.out.println("creating menu item for theme:
            // "+themenames[i]);
            skinItem = new JRadioButtonMenuItem(themenames[i]);
            _group.add(skinItem);
            _skinlfMenu.add(skinItem);
            skinItem.addActionListener(lfAction);
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
	}

   
	public JMenu getMenu() { return _skinlfMenu; }
	public String getCurrentLFName() { return _currentSkinName; }
   
	private void setSkin(String skinName)
	{
      if (!hasSkin(skinName))
         return;
		
      _mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      
      Skin skin = null;
      
      Object mapped = _skinMap.get(skinName);
      if (mapped instanceof java.net.URL)
      {
         java.net.URL themeURL = (java.net.URL) mapped;
         System.out.println("loading themepack "+skinName);
         try
         {
            skin = SkinLookAndFeel.loadThemePack(themeURL);
            _skinMap.put(skinName, skin);
         }
         catch (Exception ex)
         {
            _mainFrame.setCursor(Cursor.getDefaultCursor());
            System.err.println("Failed to load themepack;  err msg: "+ex.getMessage());
            return;
         }
      }
      else
      {
         skin = (Skin) mapped;
      }
      
      try
      {
         SkinLookAndFeel.setSkin(skin);
         UIManager.setLookAndFeel(_skinlf);
         _currentSkinName = skinName;
         
         Enumeration elements = _group.getElements();
         while (elements.hasMoreElements())
         {
            AbstractButton b = (AbstractButton) elements.nextElement();
            if (b.getText().equals(skinName))
            {
               b.setSelected(true);
            }
         }  // or make a map at setup time..

         for (int i=0; i<_topLevelContainers.length; i++)
            SwingUtilities.updateComponentTreeUI(_topLevelContainers[i]);
      }
      catch (UnsupportedLookAndFeelException ex)
      {
         System.err.println("Unsupported Look and Feel Exception: "+ex.getMessage());
         ex.printStackTrace();
      }
      finally
      {
         _mainFrame.setCursor(Cursor.getDefaultCursor());
      }
	}
   
   private boolean hasSkin(String skinName)
   {
      Object skin = _skinMap.get(skinName);
      return (skin != null);
   }
   
	public void setLF(String name)
	{
		setSkin(name);  // alias for interface compliance
	}
   public boolean hasLF(String name)
   {
      return hasSkin(name);
   }

   
   // === tbd..
   public void addLFChangeListener(LFChangeListener l) {}
   public void fireLFChanged() {}
   public void removeLFChangeListener(LFChangeListener l) {}

   
}
