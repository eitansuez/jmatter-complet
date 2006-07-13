/*
 * Created on Feb 15, 2004
 */
package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class EmploymentInfo extends AbstractComplexEObject
{
   private final StringEO _occupation = new StringEO();
   private final Business _employer = new Business();
   
   public static String[] fieldOrder = {"occupation", "employer"};

   public EmploymentInfo() {}

   public void initialize()
   {
      _employer.initialize();
   }

   public Title title()
   {
      return _occupation.title().append(" @", _employer);
   }

   public StringEO getOccupation() { return _occupation; }
   public Business getEmployer() { return _employer; }

}
