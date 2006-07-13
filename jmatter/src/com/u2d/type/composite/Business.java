/*
 * Created on Nov 17, 2003
 */
package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class Business extends AbstractComplexEObject
{
	protected final StringEO _name = new StringEO();
	protected final BusinessContact _contact = new BusinessContact();
   protected final Logo _logo = new Logo();
   protected final StringEO _ein = new StringEO();  // business's federal tax id number

   public static String[] fieldOrder = {"name", "contact", "logo", "ein"};
   
   public Business() {}

   public void initialize()
   {
      _contact.initialize();
   }

   public Business(String name)
   {
      initialize();
      _name.setValue(name);
   }
	public Business(String name, BusinessContact contact)
	{
      this(name);
      _contact.setValue(contact);
	}
	
	public StringEO getName() { return _name; }
	public BusinessContact getContact() { return _contact; }
   public Logo getLogo() { return _logo; }
   public StringEO getEIN() { return _ein; }
   
	public Title title() { return _name.title(); }
	
   // a convenience for hcfa printing..
   public String nameAndAddress()
   {
      String name = getName().toString();
      String address = getContact().getAddress().toString();
      return name + "\n" + address;
   }

}
