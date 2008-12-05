package com.u2d.view.echo;

import com.u2d.view.View;
import javax.swing.*;
import nextapp.echo.app.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 5, 2008
 * Time: 12:37:25 AM
 */
public class GenericFrame extends WindowPane
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
      ImageIcon icon = (ImageIcon) view.iconSm();
      if (icon != null)
      {
         ImageReference imgRef = new AwtImageReference(icon.getImage());
         setIcon(imgRef);
      }


      SplitPane pane = new SplitPane(SplitPane.ORIENTATION_VERTICAL);

      if (withTitlePane)
      {
//         GenericTitleView titleView = new GenericTitleView(_view);
//         pane.add((Component) titleView);
      }
      pane.add((Component) _view);

      add(pane);
      
      setResizable(true);
//      setMaximizable(true);
//      setIconifiable(true);
      setClosable(true);
   }

   public View getView() { return _view; }

}
