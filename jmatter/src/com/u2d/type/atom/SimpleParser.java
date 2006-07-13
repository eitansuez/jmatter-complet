/*
 * Created on Nov 28, 2003
 */
package com.u2d.type.atom;

/**
 * @author Eitan Suez
 */
public class SimpleParser
{

	static String parseValue(String omit, String valid, String text)
	{
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<text.length(); i++)
		{
			char c = text.charAt(i);
			if (omit.indexOf(c) != -1) continue;
			if (valid.indexOf(c) == -1) return null;
			buf.append(c);
		}
		return buf.toString();
	}

}
