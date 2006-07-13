package com.u2d.customui;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.USZipCode;
import com.u2d.type.USState;
import com.u2d.view.EView;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 23, 2006
 * Time: 2:31:53 PM
 */
public class Address extends AbstractComplexEObject
{
   private final StringEO _line1 = new StringEO();
   private final StringEO _line2 = new StringEO();
   private final StringEO _city = new StringEO();
   private final USState _stateCode = new USState();
   private final USZipCode _zipCode = new USZipCode();

   public static String[] fieldOrder = {"line1", "line2", "city", "stateCode", "zipCode"};

   public Address() {}
   public Address(String addr1, String city, String statecode, String zipcode)
   {
      this(addr1, "", city, statecode, zipcode);
   }
   public Address(String addr1, String addr2, String city, String statecode, String zipcode)
   {
      _line1.setValue(addr1);
      _line2.setValue(addr2);
      _city.setValue(city);
      _stateCode.setValue(_stateCode.get(statecode));
      _zipCode.setValue(new USZipCode(zipcode));
   }

   public void initialize()
   {
      _stateCode.setValue(_stateCode.get("TX"));
   }

   public StringEO getLine1() { return _line1; }
   public StringEO getLine2() { return _line2; }
   public StringEO getCity() { return _city; }
   public USState getStateCode() { return _stateCode; }
   public USZipCode getZipCode() { return _zipCode; }

   public Title title()
   {
      return _line1.title().append(",", _city).append(_stateCode.getCode()).append(_zipCode);
   }

   public static String pluralName() { return "Addresses"; }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Address)) return false;
      Address addr = (Address) obj;
      return _line1.equals(addr.getLine1()) &&
             _line2.equals(addr.getLine2()) &&
             _zipCode.equals(addr.getZipCode());
   }

   public int hashCode()
   {
      return _line1.hashCode() + 31 * _zipCode.hashCode();
   }

   // uncomment this method to override the view that used to render addresses.
   public EView getMainView()
   {
      return new CustomAddressView(this);
   }

}
