/*
 * Created on Jan 26, 2004
 */
package com.u2d.view;

import com.u2d.model.EObject;

/**
 * @author Eitan Suez
 */
public interface EView extends javax.swing.event.ChangeListener
{
   public static final int MAXHEIGHT = 400;
   public static final int MINHEIGHT = 50;
   public static final int MAXWIDTH = 400;

   public EObject getEObject();
   public void detach();
}
