package com.u2d.view.swing;

import com.u2d.model.Editor;
import com.u2d.view.ComplexEView;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: May 6, 2008
 * Time: 11:02:47 AM
 */
public interface IFormView extends ComplexEView, Editor
{
   void focusFirstEditableField();
}
