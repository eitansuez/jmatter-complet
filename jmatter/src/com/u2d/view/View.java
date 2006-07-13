/*
 * Created on May 8, 2004
 */
package com.u2d.view;

/**
 * @author Eitan Suez
 */
public interface View
{
   public String getTitle();
   public javax.swing.Icon iconSm();
   public javax.swing.Icon iconLg();
   public boolean withTitlePane();
}
