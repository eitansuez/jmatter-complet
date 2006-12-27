package com.u2d.view.wings;

import com.u2d.view.View;
import org.wings.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Eitan Suez
 */
public class GenericFrame
      extends SInternalFrame
{
   private transient View _view;
   
   public GenericFrame(View view)
   {
      this(view, view.withTitlePane());
   }
   
   public GenericFrame(View view, boolean withTitlePane)
   {
      super();
      _view = view;
      
      setTitle(view.getTitle());
      Icon icon = view.iconSm();
      if (icon != null)
         setIcon(new SImageIcon((ImageIcon) icon));

      SPanel contentPane = (SPanel) getContentPane();

      if (withTitlePane)
      {
         GenericTitleView titleView = new GenericTitleView(_view);
         contentPane.add((SComponent) titleView, SBorderLayout.NORTH);
      }
      contentPane.add((SComponent) _view, SBorderLayout.CENTER);
      
//      setResizable(true);
      setMaximizable(true); setIconifyable(true); setClosable(true);
   }
   
   public View getView() { return _view; }
   
}
