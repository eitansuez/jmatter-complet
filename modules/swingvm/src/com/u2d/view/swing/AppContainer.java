package com.u2d.view.swing;

import com.u2d.app.AppSession;
import com.u2d.ui.desktop.Positioning;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 28, 2008
 * Time: 5:10:37 PM
 */
public interface AppContainer
{
   void appUnloaded();
   void appLoaded(AppSession appSession);
   void setVisible(boolean b);
   void addLoginDialog(LoginDialog loginDialog);
   void contributeToHeader(JComponent component);
   void popup(JPopupMenu menu);
   JInternalFrame addFrame(JInternalFrame jInternalFrame);
   JInternalFrame addFrame(JInternalFrame frame, Positioning positioning);
   boolean isVisible();
   void onMessage(String msg);
   void setWaitCursor();
   void setDefaultCursor();
   boolean focusFrameForObject(EObject eo);
}
