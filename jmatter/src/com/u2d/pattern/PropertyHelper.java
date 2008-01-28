/*
 * Created on Oct 7, 2003
 */
package com.u2d.pattern;

import java.util.*;
import java.io.*;

/**
 * @author Eitan Suez
 */
public class PropertyHelper
{
	private Map _propMap;
	
	private static Map _cache;
	static
	{
		_cache = new HashMap();
	}
	
	// PropertyHelper Factory..
	public static PropertyHelper getPropertyHelper(String propertyResource) throws IOException
	{
		if (_cache.get(propertyResource) == null)
		{
			PropertyHelper helper = new PropertyHelper(propertyResource);
			_cache.put(propertyResource, helper);
		}
		return (PropertyHelper) _cache.get(propertyResource);
	}
	
	private PropertyHelper(String propertyResource) throws IOException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream(propertyResource);
      if (is == null)
      {
         throw new RuntimeException("No such resource: "+propertyResource);
      }
		Properties properties = new Properties();
		properties.load(is);
		_propMap = properties;
	}
	
	public void mash(String propertyName, ClassMasher masher)
	{
		String classListStr = (String) _propMap.get(propertyName);
		String[] classNames = classListStr.split(",");
      
		for (int i=0; i<classNames.length; i++)
		{
			if ("".equals(classNames[i].trim())) continue;
			try
			{
				Class cls = Class.forName(classNames[i]);
				masher.mash(cls);
			}
			catch (ClassNotFoundException ex)
			{
				System.err.println("Class Not Found: "+ex.getMessage());
			}
		}
	}
	
	public String getStringValue(String propertyName)
	{
		return (String) _propMap.get(propertyName);
	}
	
	public int getIntValue(String propertyName)
	{
		String value = (String) _propMap.get(propertyName);
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ex)
		{
			System.err.println("value for property "+propertyName+" ("+value+") cannot be parsed as an integer");
			System.err.println(ex.getMessage());
		}
		return 0;
	}
	
	public Class getClassValue(String propertyName)
	{
		String clsName = (String) _propMap.get(propertyName);
		if (clsName == null || "".equals(clsName.trim()))
			return null;
		try
		{
         return Class.forName(clsName);
		}
		catch (ClassNotFoundException ex)
		{
			System.err.println("Class Not Found: "+ex.getMessage());
		}
		return null;  // TODO:  maybe throw classnotfoundexception instead
	}
	
	public Class getClassValue(String propertyName, Class defaultCls)
	{
		Class cls = getClassValue(propertyName);
		return (cls == null) ? defaultCls : cls;
	}

}
