package com.u2d.type.composite;

import com.u2d.type.*;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class DetailedPerson extends Person
{
   private final SSN _ssn = new SSN();
//   private final DateEO _dob = new DateEO();
   private final DateWithAge _dob = new DateWithAge();
   private final Sex _sex = new Sex();
   private final MarritalStatus _marritalStatus = new MarritalStatus();
   private final EmploymentInfo _employmentInfo = new EmploymentInfo();
   
   public static String[] fieldOrder = {"name", "contact", "dateOfBirth", "ssn", "sex", 
         "marritalStatus", "profession", "employment"};
   public static String[] tabViews = {"contact", "employment"};

//   public static String[] identities = {"ssn"};
   
   public DetailedPerson() {}

   public DetailedPerson(Person person)
   {
      initialize();
      setPersonValue(person);
   }

   public void initialize()
   {
      super.initialize();
      _marritalStatus.setValue(_marritalStatus.get("m"));
      _sex.setValue(_sex.get("m"));
      _employmentInfo.initialize();
   }

   public void setPersonValue(Person person)
   {
      _name.setValue(person.getName());
      _contact.setValue(person.getContact());
   }

   //public static String dateOfBirthPreferredView = "DateWithAge";
   public DateWithAge getDateOfBirth() { return _dob; }
   public MarritalStatus getMarritalStatus() { return _marritalStatus; }
   public Sex getSex() { return _sex; }
   public SSN getSsn() { return _ssn; }
   public EmploymentInfo getEmployment() { return _employmentInfo; }


   public int validate()
   {
      int errorCount = super.validate();
      if (_ssn.isEmpty() && ssnRequired())
      {
         errorCount += 1;
         String msg = "(required)";
         _ssn.fireValidationException(msg);
      }
      return errorCount;
   }

   private boolean ssnRequired()
   {
      return _dob.isEmpty() || _dob.getAge() >= 18;
   }

}
