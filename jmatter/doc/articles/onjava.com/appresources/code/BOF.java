package org.jmatter.j1mgr;

import com.u2d.persist.Persist;
import com.u2d.type.atom.StringEO;

@Persist
public class BOF extends Talk
{
   private final StringEO code = new StringEO();

   public static String[] identities = {"code"};

   public static String[] fieldOrder = {"code", "topic", "span", "speaker", "description", "location"};
   public static String[] tabViews = {"description"};

   public BOF()
   {
   }

   public StringEO getCode() { return code; }

}
