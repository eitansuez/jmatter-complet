/*
 * Created on Nov 17, 2003
 */
package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class Contact extends AbstractComplexEObject
{
	private final USPhone _homePhone = new USPhone();
   private final USPhone _workPhone = new USPhone();
   private final USPhone _mobilePhone = new USPhone();
	private final USPhone _fax = new USPhone();
	private final Email _email = new Email();
	private final USAddress _address = new USAddress();
   private final ContactMethod _preferredContactMethod = new ContactMethod();
	
   public static String[] fieldOrder = { "homePhone" , "workPhone", 
         "mobilePhone" , "email" , "address" , "fax" , "preferredContactMethod" };
   
	public Contact() {}

   public void initialize()
   {
      _preferredContactMethod.setValue(_preferredContactMethod.get("homePhone"));
      _address.initialize();
   }

	public Contact(String homephone, String workphone, String mobilephone, String fax,
							   String email, USAddress address)
	{
      _preferredContactMethod.setValue(_preferredContactMethod.get("homePhone"));
      _homePhone.setValue(homephone);
      _workPhone.setValue(workphone);
      _mobilePhone.setValue(mobilephone);
		_fax.setValue(fax);
      _email.setValue(email);
      _address.setValue(address);
	}
	
	public Title title()
	{
      String fieldName = _preferredContactMethod.code();
      EObject value = field(fieldName).get(this);
      return value.title();
	}

   public Email getEmail() { return _email; }
	public USPhone getHomePhone() { return _homePhone; }
	public USPhone getWorkPhone() { return _workPhone; }
	public USPhone getMobilePhone() { return _mobilePhone; }
   public USAddress getAddress() { return _address; }
	public USPhone getFax() { return _fax; }
   public ContactMethod getPreferredContactMethod() { return _preferredContactMethod; }


   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Contact)) return false;
      Contact c = (Contact) obj;
      return _homePhone.equals(c.getHomePhone()) &&
            _address.equals(c.getAddress());
   }

   public int hashCode()
   {
      return _homePhone.hashCode() + 31 * _address.hashCode();
   }

}
