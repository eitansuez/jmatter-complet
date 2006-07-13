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
public class BusinessContact extends AbstractComplexEObject
{
	private final USPhone _phone = new USPhone();
   private final USPhone _fax = new USPhone();
	private final Email _email = new Email();
	private final USAddress _address = new USAddress();
	private final StringEO _contactName = new StringEO();
	private final URI _url = new URI();
	
   public static String[] fieldOrder = { "contactName", "phone", "fax", "email", "address", "url" };
   
   public BusinessContact() {}

   public void initialize()
   {
      _address.initialize();
   }

	public BusinessContact(String phone, String fax, String email, USAddress address, String contactName)
	{
      _phone.setValue(phone);
      _fax.setValue(fax);
      _email.setValue(email);
      _address.setValue(address);
      _contactName.setValue(contactName);
	}
	
	public Title title()
	{
      return _contactName.title().appendParens(_phone);
	}

	public StringEO getContactName() { return _contactName; }
	public USPhone getPhone() { return _phone; }
	public USPhone getFax() { return _fax; }
	public Email getEmail() { return _email; }
	public URI getUrl() { return _url; }
	public USAddress getAddress() { return _address; }

}
