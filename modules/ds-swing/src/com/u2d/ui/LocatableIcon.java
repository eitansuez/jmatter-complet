/*
 * Created on Oct 27, 2003
 */
package com.u2d.ui;

import javax.swing.ImageIcon;

/**
 * @author Eitan Suez
 */
public class LocatableIcon extends ImageIcon implements java.io.Serializable
{
	private String _path = "";
	
	public LocatableIcon(String path)
	{
		super(path);
		_path = path;
	}
   public LocatableIcon(java.net.URL url)
   {
      super(url);
      _path = url.toString();
   }
   
	public String getPath() { return _path; }
   
}
