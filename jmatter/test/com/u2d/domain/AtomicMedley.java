/*
 * Created on Jan 30, 2004
 */
package com.u2d.domain;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.MarritalStatus;
import com.u2d.type.Sex;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class AtomicMedley extends AbstractComplexEObject 
{
   private final StringEO name = new StringEO();
   private final IntEO age = new IntEO();
   private final Email email = new Email();
   private final BooleanEO veritee = new BooleanEO();
   private final FloatEO x = new FloatEO();
   private final Percent p = new Percent();
   private final TextEO comments = new TextEO();
   private final URI homePage = new URI();
   private final USDollar money = new USDollar();
   private final USPhone phone = new USPhone();
   private final USZipCode zip = new USZipCode();
   private final Sex sex = new Sex();
   private final MarritalStatus mstat = new MarritalStatus();
   private final DateEO dob = new DateEO();
   private final Photo photo = new Photo();
   
   public static String[] fieldOrder = { "dob", "name" , "email" , "age" , "veritee", "x", "p",
         "comments", "homePage", "photo", "money", "phone", "zip", "sex", "marritalStatus"};
   
   public AtomicMedley()
   {
   }
   
   public AtomicMedley(int age, String name, Email email, boolean veritee, double x)
   {
      this.age.setValue(age);
      this.name.setValue(name);
      this.email.setValue(email);
      this.veritee.setValue(veritee);
      this.x.setValue(x);
   }
   
   public IntEO getAge() { return age; }
   public StringEO getName() { return name; }
   public Email getEmail() { return email; }
   public BooleanEO getVeritee() { return veritee; }
   public FloatEO getX() { return x; }
   public Percent getP() { return p; }
   public TextEO getComments() { return comments; }
   public URI getHomePage() { return homePage; }
   public USDollar getMoney() { return money; }
   public USPhone getPhone() { return phone; }
   public USZipCode getZip() { return zip; }
   public Sex getSex() { return sex; }
   public MarritalStatus getMarritalStatus() { return mstat; }
   public DateEO getDob() { return dob; }
   public Photo getPhoto() { return photo; }

   public Title title()
   {
      return name.title().appendParens(age).append(",", email);
   }

}
