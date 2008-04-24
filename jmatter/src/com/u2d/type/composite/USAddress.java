/*
 * Created on Jul 24, 2003
 */
package com.u2d.type.composite;

import com.u2d.element.CommandInfo;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.reflection.Cmd;
import com.u2d.type.USState;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.USZipCode;
import com.u2d.utils.Launcher;

/**
 * @author Eitan Suez
 */
public class USAddress extends AbstractComplexEObject
{
   private final StringEO _addressLine1 = new StringEO();
   private final StringEO _addressLine2 = new StringEO();
   private final StringEO _city = new StringEO();
   private final USState _stateCode = new USState();
   private final USZipCode _zipCode = new USZipCode();
   
   public static String[] fieldOrder = {"addressLine1", "addressLine2", "city", "stateCode", "zipCode"};
   
   public USAddress() {}

   public void initialize()
   {
      _stateCode.setValue(_stateCode.get("TX"));
   }
   
   public USAddress(String addr1, String city, String statecode, String zipcode)
   {
      this(addr1, "", city, statecode, zipcode);
   }
   public USAddress(String addr1, String addr2, String city, String statecode, String zipcode)
   {
      _addressLine1.setValue(addr1);
      _addressLine2.setValue(addr2);
      _city.setValue(city);
      _stateCode.setValue(_stateCode.get(statecode));
      _zipCode.setValue(new USZipCode(zipcode));
   }
   
   public StringEO getAddressLine1() { return _addressLine1; }
   public StringEO getAddressLine2() { return _addressLine2; }
   public StringEO getCity() { return _city; }
   public USState getStateCode() { return _stateCode; }
   public USZipCode getZipCode() { return _zipCode; }
   
// template for implementing validate methods on complexobjects
//   public int validate()
//   {
//      int count = super.validate();
//      String msg = "";
//      if (_zipCode.stringValue().startsWith("8") && _city.stringValue().equals("Austin"))
//      {
//         msg = "Zip Code does not match city";
//         count++;
//      }
//      fireValidationException(msg);
//      return count;
//   }
   
   
   @Cmd(mnemonic='a',iconref="compass")
   public Object ViewOnMap(CommandInfo cmdInfo)
   {
      return vmech().getAddressViewOnMap(this);
   }
   
   public static final String googleMapsUrlPattern = "http://maps.google.com/maps?q=%s";

   @Cmd
   public void ViewInGoogleMaps(CommandInfo cmdInfo)
   {
      String address = String.format("%s, %s %s %s", _addressLine1.stringValue(), _city.stringValue(),
            _stateCode.code(), _zipCode.stringValue());
      String url = String.format(googleMapsUrlPattern, EmailMessage.htmlEscape(address));
      Launcher.openInBrowser(url);
   }
   
   public Title title()
	{
		return _addressLine1.title().append(",", _city).append(_stateCode.getCode()).append(_zipCode);
	}
   
   public static String pluralName() { return "Addresses"; }
   
   public String streetCombined()
   {
      Title title = _addressLine1.title().append(",", _addressLine2);
      return title.toString();
   }
   
   public String toString()
   {
      String line1 = _addressLine1.toString();
      String line2 = _addressLine2.toString();
      String line3 = _city.title().append(",", _stateCode.code()).append(_zipCode).toString();
      
      if ( line2 == null || "".equals(line2.trim()) )
         return line1 + "\n" + line3;
      
      return line1 + "\n" + line2 + "\n" + line3;
   }
   
   public String googlemapString()
   {
      return _addressLine1.stringValue() + " " + _addressLine2.stringValue() + " " +
            _city.stringValue() + " " + _stateCode.code() + " " + _zipCode.stringValue();
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof USAddress)) return false;
      USAddress addr = (USAddress) obj;
      return _addressLine1.equals(addr.getAddressLine1()) &&
             _addressLine2.equals(addr.getAddressLine2()) &&
             _zipCode.equals(addr.getZipCode());
   }

   public int hashCode()
   {
      return _addressLine1.hashCode() + 31 * _zipCode.hashCode();
   }
   
}
